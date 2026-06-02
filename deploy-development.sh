#!/usr/bin/env bash
set -euo pipefail

echo "Starting ConsultingTypeService build & deployment..."

# Set Java 17
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

# Go to project directory
cd /root/online-beratung/ORISO-Complete/caritas-workspace/ORISO-ConsultingTypeService

echo "Building Maven package..."
mvn -q -Dmaven.test.skip=true -Dspotless.check.skip=true package

echo "Copying JAR..."
cp -f target/ConsultingTypeService.jar ./ConsultingTypeService.jar

echo "Building Docker image..."
docker build -t oriso-consultingtypeservice:latest .

echo "Importing image into k3s..."
docker save oriso-consultingtypeservice:latest | sudo k3s ctr images import - > /dev/null 2>&1

echo "Restarting Kubernetes deployment..."
kubectl rollout restart deployment/oriso-platform-consultingtypeservice -n caritas

echo "Waiting for rollout to complete..."
kubectl rollout status deployment/oriso-platform-consultingtypeservice -n caritas --timeout=240s

echo "ConsultingTypeService deployed successfully ✅"

