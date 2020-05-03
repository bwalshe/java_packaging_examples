#!/bin/bash
set -e

if [[ ! -n "$MY_LIB_DIR" ]]; then
    echo "ERROR: \$MY_LIB_DIR not set"
    exit 1    
fi

SOURCE=com/example/progress
javac -cp $MY_LIB_DIR/rationals.jar $SOURCE/*.java
jar cf progress.jar $SOURCE/*.class