#!/bin/sh
set -e

SOURCE=com/example/progress

if [ -n "$MY_LIB_DIR" ]; then
    javac -cp $MY_LIB_DIR/rationals.jar $SOURCE/*.java
    jar cf progress.jar $SOURCE/*.class
else
    echo "ERROR: \$MY_LIB_DIR not set"
    exit 1    
fi