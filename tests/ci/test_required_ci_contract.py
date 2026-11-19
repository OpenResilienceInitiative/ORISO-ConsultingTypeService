import os
from pathlib import Path
import re
import subprocess
import tempfile
import unittest


ROOT = Path(__file__).resolve().parents[2]


def job_block(workflow: str, job_name: str) -> str:
    marker = f"  {job_name}:\n"
    start = workflow.index(marker)
    remainder = workflow[start + len(marker) :]
    next_job = re.search(r"\n  [a-zA-Z0-9_-]+:\n", remainder)
    return remainder if next_job is None else remainder[: next_job.start()]


class RequiredCiContractTest(unittest.TestCase):
    def run_required_runner_with_reports(self, reports):
        runner = ROOT / "scripts/ci/run-required-integration-tests.sh"
        with tempfile.TemporaryDirectory() as temp_dir:
            temp_root = Path(temp_dir)
            fake_maven = temp_root / "mvnw"
            fake_maven.write_text("#!/usr/bin/env bash\nexit 0\n")
            fake_maven.chmod(0o755)
            for report_path, report_xml in reports.items():
                destination = temp_root / report_path
                destination.parent.mkdir(parents=True, exist_ok=True)
                destination.write_text(report_xml)
            env = os.environ.copy()
            env["ORISO_MAVEN_WRAPPER"] = str(fake_maven)

            return subprocess.run(
                [runner], cwd=temp_root, env=env, check=False, capture_output=True, text=True
            )

    def test_required_runner_rejects_skipped_test_report(self):
        skipped_report = """\
<?xml version="1.0" encoding="UTF-8"?>
<testsuite name="SkippedIT" tests="1" failures="0" errors="0" skipped="1">
  <testcase name="isRequired" classname="SkippedIT">
    <skipped message="missing database"/>
  </testcase>
</testsuite>
"""
        result = self.run_required_runner_with_reports(
            {"target/surefire-reports/TEST-SkippedIT.xml": skipped_report}
        )

        self.assertNotEqual(0, result.returncode, result.stdout + result.stderr)
        self.assertIn("skipped=1", result.stdout + result.stderr)

    def test_required_runner_rejects_skipped_failsafe_report(self):
        passing_integration = '<testsuite tests="1" failures="0" errors="0" skipped="0"/>'
        skipped_failsafe = '<testsuite tests="1" failures="0" errors="0" skipped="1"/>'

        result = self.run_required_runner_with_reports(
            {
                "target/surefire-reports/TEST-PassingIT.xml": passing_integration,
                "module/target/failsafe-reports/TEST-SkippedIT.xml": skipped_failsafe,
            }
        )

        self.assertNotEqual(0, result.returncode, result.stdout + result.stderr)
        self.assertIn("skipped=1", result.stdout + result.stderr)

    def test_required_runner_rejects_zero_executed_failures_and_errors(self):
        invalid_reports = {
            "zero executed tests": '<testsuite tests="0" failures="0" errors="0" skipped="0"/>',
            "one failure": '<testsuite tests="1" failures="1" errors="0" skipped="0"/>',
            "one error": '<testsuite tests="1" failures="0" errors="1" skipped="0"/>',
        }
        for reason, report in invalid_reports.items():
            with self.subTest(reason=reason):
                result = self.run_required_runner_with_reports(
                    {"target/surefire-reports/TEST-FixtureIT.xml": report}
                )

                self.assertNotEqual(0, result.returncode, result.stdout + result.stderr)

    def test_required_runner_accepts_only_executed_passing_reports(self):
        passing_report = '<testsuite tests="2" failures="0" errors="0" skipped="0"/>'

        result = self.run_required_runner_with_reports(
            {"target/surefire-reports/TEST-PassingIT.xml": passing_report}
        )

        self.assertEqual(0, result.returncode, result.stdout + result.stderr)
        self.assertIn("tests=2 failures=0 errors=0 skipped=0", result.stdout)

    def test_required_runner_rejects_zero_reports(self):
        result = self.run_required_runner_with_reports({})

        self.assertNotEqual(0, result.returncode, result.stdout + result.stderr)
        self.assertIn("no integration-test reports", result.stderr)

    def test_required_runner_propagates_maven_failure(self):
        runner = ROOT / "scripts/ci/run-required-integration-tests.sh"
        with tempfile.TemporaryDirectory() as temp_dir:
            fake_maven = Path(temp_dir) / "mvnw"
            fake_maven.write_text("#!/usr/bin/env bash\nexit 23\n")
            fake_maven.chmod(0o755)
            env = os.environ.copy()
            env["ORISO_MAVEN_WRAPPER"] = str(fake_maven)

            result = subprocess.run([runner], cwd=ROOT, env=env, check=False)

        self.assertEqual(23, result.returncode)

    def test_pull_request_has_one_truthful_required_conclusion(self):
        workflow = (ROOT / ".github/workflows/ci-pull-request.yml").read_text()
        integration = job_block(workflow, "required-integration-tests")
        aggregate = job_block(workflow, "required-ci")

        self.assertIn("name: required integration tests", integration)
        self.assertNotIn("continue-on-error:", integration)
        self.assertIn("needs: [validate, required-integration-tests, contract-tests]", aggregate)
        self.assertIn("if: always()", aggregate)
        self.assertIn("name: required PreDev CI", aggregate)
        self.assertIn("needs.required-integration-tests.result", aggregate)
        self.assertIn("needs.contract-tests.result", aggregate)
        # Reading a result into the environment is not the same as acting on
        # it: the conclusion itself must consider every required job.
        self.assertIn('"${CONTRACT_RESULT}" != success', aggregate)

    def test_ci_contract_tests_are_executed_by_ci(self):
        # These assertions are worthless unless something runs them. Without a
        # job that invokes pytest, tests/ci is dead weight that can drift out
        # of sync with the workflows it claims to protect.
        workflow = (ROOT / ".github/workflows/ci-pull-request.yml").read_text()
        contract = job_block(workflow, "contract-tests")

        self.assertIn("pytest", contract)
        self.assertIn("tests/ci", contract)
        self.assertNotIn("continue-on-error:", contract)

    def test_publish_waits_for_required_integration_tests(self):
        workflow = (ROOT / ".github/workflows/ci-main.yml").read_text()
        publish = job_block(workflow, "publish")
        integration = job_block(workflow, "required-integration-tests")

        self.assertIn("needs: required-integration-tests", publish)
        self.assertIn("name: required integration tests", integration)
        self.assertNotIn("continue-on-error:", integration)

    def test_required_action_does_not_swallow_maven_failure(self):
        action = (ROOT / ".github/actions/maven-verify-required/action.yml").read_text()
        self.assertNotIn("continue-on-error:", action)
        self.assertNotIn("if ! ./mvnw", action)
        self.assertIn("scripts/ci/run-required-integration-tests.sh", action)
        self.assertIn("scripts/ci/assert-required-test-reports.py", action)


if __name__ == "__main__":
    unittest.main()
