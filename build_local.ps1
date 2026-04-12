# Local build helper: official run_tests.sh omits ds/ and lib/utils/; this matches a full compile.
$ErrorActionPreference = "Stop"
$root = $PSScriptRoot
Set-Location $root

$JUNIT_VERSION = "6.0.3"
$JUNIT_JAR = ".test-tools/junit-platform-console-standalone-${JUNIT_VERSION}.jar"
$CLASSES = "build/classes"
$TEST_CLASSES = "build/test-classes"

New-Item -ItemType Directory -Force -Path ".test-tools", $CLASSES, $TEST_CLASSES | Out-Null

if (-not (Test-Path $JUNIT_JAR)) {
    Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/${JUNIT_VERSION}/junit-platform-console-standalone-${JUNIT_VERSION}.jar" -OutFile $JUNIT_JAR -UseBasicParsing
}

$javac = $null
if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\javac.exe")) { $javac = "$env:JAVA_HOME\bin\javac.exe" }
if (-not $javac) { $javac = (Get-Command javac -ErrorAction SilentlyContinue).Source }
if (-not $javac) { throw "javac not found. Set JAVA_HOME to a JDK or add JDK bin to PATH." }

& $javac -Xlint:unchecked -d $CLASSES `
    ds/*.java `
    lib/utils/*.java `
    port/*.java `
    lib/expression/*.java `
    lib/dict/*.java `
    lib/visitors/*.java `
    lib/handlers/*.java

& $javac -cp "$CLASSES;$JUNIT_JAR" -d $TEST_CLASSES `
    testsupport/*.java `
    spec/visitors/*.java

& java -jar $JUNIT_JAR execute `
    --class-path "$CLASSES;$TEST_CLASSES" `
    --scan-class-path `
    --details tree `
    --disable-banner
