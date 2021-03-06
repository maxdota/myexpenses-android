#!/bin/bash
set -ev

./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew checkStyle
./gradlew installDebugAndroidTest
./gradlew connectedDebugAndroidTest -PdisablePreDex  -Pandroid.testInstrumentationRunnerArguments.size=small
./gradlew connectedDebugAndroidTest -PdisablePreDex  -Pandroid.testInstrumentationRunnerArguments.size=medium
./gradlew connectedDebugAndroidTest -PdisablePreDex  -Pandroid.testInstrumentationRunnerArguments.size=large

if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
	./gradlew testfairyJonathan
	./gradlew testfairyThainara
fi