./gradlew allTests `
    "-Pgobley.projects.gradleTests=false" `
    "-Pgobley.projects.uniffiTests=false";
./.github/workflows/pr-build-test-copy-test-result.ps1;
./gradlew clean `
    "-Pgobley.projects.gradleTests=false" `
    "-Pgobley.projects.uniffiTests=false";
