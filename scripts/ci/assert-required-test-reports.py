#!/usr/bin/env python3
"""Fail closed when required Surefire/Failsafe reports are not fully executed and green."""

from pathlib import Path
import sys
import xml.etree.ElementTree as ET


REPORT_PATTERNS = (
    "**/target/surefire-reports/TEST-*.xml",
    "**/target/failsafe-reports/TEST-*.xml",
)


def main() -> int:
    report_root = Path(sys.argv[1] if len(sys.argv) > 1 else ".")
    reports = sorted(
        {report for pattern in REPORT_PATTERNS for report in report_root.glob(pattern)}
    )
    if not reports:
        print("Required test suite produced zero test reports.", file=sys.stderr)
        return 1

    totals = {"tests": 0, "failures": 0, "errors": 0, "skipped": 0}
    invalid_reports = []
    non_green_reports = []
    for report in reports:
        try:
            root = ET.parse(report).getroot()
            counts = {name: int(root.attrib.get(name, 0)) for name in totals}
        except (ET.ParseError, ValueError) as error:
            invalid_reports.append((report, error))
            continue

        for name, count in counts.items():
            totals[name] += count
        if counts["failures"] or counts["errors"] or counts["skipped"]:
            non_green_reports.append((report, counts))

    summary = " ".join(f"{name}={count}" for name, count in totals.items())
    print(f"Required test suite: reports={len(reports)} {summary}")

    for report, error in invalid_reports:
        print(f"Invalid test report {report}: {error}", file=sys.stderr)
    for report, counts in non_green_reports:
        report_summary = " ".join(f"{name}={count}" for name, count in counts.items())
        print(f"Non-green test report {report}: {report_summary}", file=sys.stderr)

    if invalid_reports:
        return 1
    if totals["tests"] == 0:
        print("Required test suite executed zero tests.", file=sys.stderr)
        return 1
    if totals["failures"] or totals["errors"] or totals["skipped"]:
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
