# Upgrade libraries

Run `./report-latest-dependencies-versions.sh` and `./report-latest-npm-dependencies-versions.sh`. Then use the output to upgrade the Java and Javascript dependencies.

Then build and test to ensure everything works well:
- `./gradlew clean build` to build and test
