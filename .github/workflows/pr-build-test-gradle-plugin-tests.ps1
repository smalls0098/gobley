$ErrorActionPreference = "Stop";
$PSNativeCommandUseErrorActionPreference = $true;

./gradlew :build-logic:gradle-plugin:test;