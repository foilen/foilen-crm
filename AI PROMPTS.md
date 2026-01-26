# Upgrade libraries

Run `./report-latest-dependencies-versions.sh` and `./report-latest-npm-dependencies-versions.sh`. Then use the output to upgrade the Java and Javascript dependencies.

Then build and test to ensure everything works well:
- `./mariadb-start.sh` to get a database running
- `./gradlew clean build` to build and test
