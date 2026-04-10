#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT_DIR"

JUNIT_VERSION="6.0.3"
JUNIT_JAR=".test-tools/junit-platform-console-standalone-${JUNIT_VERSION}.jar"
BUILD_DIR="build"
CLASSES_DIR="$BUILD_DIR/classes"
TEST_CLASSES_DIR="$BUILD_DIR/test-classes"

mkdir -p .test-tools "$CLASSES_DIR" "$TEST_CLASSES_DIR"

if [[ ! -f "$JUNIT_JAR" ]]; then
    curl -L -o "$JUNIT_JAR" "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/${JUNIT_VERSION}/junit-platform-console-standalone-${JUNIT_VERSION}.jar"
fi

find "$CLASSES_DIR" -type f -name '*.class' -delete
find "$TEST_CLASSES_DIR" -type f -name '*.class' -delete

javac -Xlint:unchecked -d "$CLASSES_DIR" \
    port/*.java \
    lib/expression/*.java \
    lib/dict/*.java \
    lib/visitors/*.java \
    lib/handlers/*.java

javac -cp "$CLASSES_DIR:$JUNIT_JAR" -d "$TEST_CLASSES_DIR" \
    testsupport/*.java \
    spec/visitors/*.java

java -jar "$JUNIT_JAR" execute \
    --class-path "$CLASSES_DIR:$TEST_CLASSES_DIR" \
    --scan-class-path \
    --details tree \
    --disable-banner
