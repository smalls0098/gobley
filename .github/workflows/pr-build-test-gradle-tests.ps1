$ErrorActionPreference = "Stop";
$PSNativeCommandUseErrorActionPreference = $true;

./gradlew allTests `
    "-Puniffi-kmm.projects.uniffiTests=false" `
    "-Puniffi-kmm.projects.examples=false";
./.github/workflows/pr-build-test-copy-test-result.ps1;
./gradlew clean `
    "-Puniffi-kmm.projects.uniffiTests=false" `
    "-Puniffi-kmm.projects.examples=false";
