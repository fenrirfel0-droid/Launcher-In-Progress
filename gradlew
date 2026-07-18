#!/usr/bin/env sh

# Ensure we can run the gradle wrapper script on Linux cloud instances
DIRNAME=`dirname "$0"`
if [ -z "$DIRNAME" ]; then
    DIRNAME="."
fi
APP_BASE_NAME=`basename "$0"`
APP_HOME=`cd "$DIRNAME" >/dev/null; pwd`

exec "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" "$@"
