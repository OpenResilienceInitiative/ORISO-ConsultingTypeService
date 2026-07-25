#!/usr/bin/env bash

set -euo pipefail

maven_wrapper="${ORISO_MAVEN_WRAPPER:-./mvnw}"
"${maven_wrapper}" -B -fae verify

reports=(target/surefire-reports/TEST-*IT.xml)
if [[ ! -e "${reports[0]}" ]]; then
  echo "Required integration suite produced no integration-test reports." >&2
  exit 1
fi

echo "Required integration suite produced ${#reports[@]} report(s)."
