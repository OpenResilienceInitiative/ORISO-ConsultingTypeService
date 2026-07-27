import os
from pathlib import Path
import subprocess
import tempfile
import unittest

import yaml


ROOT = Path(__file__).resolve().parents[2]


class OpenApiContractGateTest(unittest.TestCase):
    def test_consumer_gate_propagates_oasdiff_failure(self):
        gate = ROOT / "scripts/contracts/verify-consumer-contract.sh"
        with tempfile.TemporaryDirectory() as temp_dir:
            temp = Path(temp_dir)
            provider_root = temp / "provider"
            provider_root.mkdir()
            (provider_root / "provider.yaml").write_text("openapi: 3.0.3\n")
            consumer = temp / "consumer.yaml"
            consumer.write_text("openapi: 3.0.3\n")

            fake_redocly = temp / "redocly"
            fake_redocly.write_text(
                "#!/usr/bin/env bash\n"
                'cp "$2" "$4"\n'
            )
            fake_redocly.chmod(0o755)

            fake_oasdiff = temp / "oasdiff"
            fake_oasdiff.write_text("#!/usr/bin/env bash\nexit 23\n")
            fake_oasdiff.chmod(0o755)

            env = os.environ.copy()
            env["REDOCLY_BIN"] = str(fake_redocly)
            env["REDOCLY_VERSION"] = "test"
            env["OASDIFF_BIN"] = str(fake_oasdiff)
            result = subprocess.run(
                [gate, consumer, provider_root, "provider.yaml"],
                cwd=ROOT,
                env=env,
                check=False,
            )

        self.assertEqual(23, result.returncode)

    def test_workflow_pins_tools_and_publishes_provider_artifact(self):
        workflow = (
            ROOT / ".github/workflows/openapi-contracts.yml"
        ).read_text()
        self.assertIn("REDOCLY_VERSION: 2.40.0", workflow)
        self.assertIn("OASDIFF_VERSION: v1.17.0", workflow)
        self.assertIn("name: openapi provider and consumer contracts", workflow)
        self.assertIn("actions/upload-artifact@v4", workflow)
        self.assertIn("provider-contracts-${{ github.sha }}", workflow)
        self.assertIn("repository: OpenResilienceInitiative/ORISO-TenantService", workflow)
        self.assertNotIn("continue-on-error:", workflow)
        self.assertIn("permissions:\n  contents: read", workflow)
        self.assertEqual(
            workflow.count("uses: actions/checkout@v6"),
            workflow.count("persist-credentials: false"),
        )
        self.assertIn("a213d5546e2cdbcbd1f641291661f11cbbca2cfc", workflow)

    def test_tenant_contract_is_checked_as_a_consumer_dependency(self):
        workflow = (
            ROOT / ".github/workflows/openapi-contracts.yml"
        ).read_text()
        self.assertIn("services/tenantservice.yaml", workflow)
        self.assertIn(".providers/tenant", workflow)
        self.assertIn("api/tenantservice.yaml", workflow)

    def test_topic_text_and_url_schemas_use_valid_string_formats(self):
        provider = yaml.safe_load((ROOT / "api/topicservice.yml").read_text())
        schemas = provider["components"]["schemas"]

        self.assertNotIn("format", schemas["WelcomeMessage"])
        self.assertNotIn("format", schemas["FallBackUrl"])

    def test_reviewed_contract_correction_uses_a_narrow_err_allowlist(self):
        gate = (
            ROOT / "scripts/contracts/verify-provider-compatibility.sh"
        ).read_text()
        workflow = (
            ROOT / ".github/workflows/openapi-contracts.yml"
        ).read_text()
        allowlist = (
            ROOT / "contracts/topic-format-correction-96.md"
        ).read_text()

        self.assertIn("--err-ignore", gate)
        self.assertIn("contracts/topic-format-correction-96.md", workflow)
        entries = [
            line for line in allowlist.splitlines()
            if line.startswith(("GET ", "POST ", "PUT "))
        ]
        self.assertEqual(14, len(entries))
        self.assertIn(
            "https://github.com/OpenResilienceInitiative/"
            "ORISO-ConsultingTypeService/issues/96",
            allowlist,
        )

    def test_contract_gate_tests_are_executed_by_ci(self):
        # A gate assertion that never runs protects nothing. Without a job that
        # invokes pytest, this file can drift away from the workflow and the
        # scripts it describes while every check stays green.
        workflow = (ROOT / ".github/workflows/openapi-contracts.yml").read_text()

        self.assertIn("contract-gate-tests:", workflow)
        self.assertIn("python -m pytest -q tests/contracts", workflow)


if __name__ == "__main__":
    unittest.main()
