#!/bin/sh
# ----------------------------------------------------------------------------
# Maven Wrapper Shell Script for Linux/macOS
# ----------------------------------------------------------------------------
DIR="$(cd "$(dirname "$0")" && pwd)"
WRAPPER_JAR="$DIR/.mvn/wrapper/maven-wrapper.jar"

if [ ! -f "$WRAPPER_JAR" ]; then
    echo "Downloading Maven Wrapper JAR..."
    curl -o "$WRAPPER_JAR" https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
fi

exec java -Dmaven.multiModuleProjectDirectory="$DIR" -jar "$WRAPPER_JAR" "$@"

