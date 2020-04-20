#!/bin/sh

set -e

PACKAGE=com.example.progress
SOURCE=`echo $PACKAGE | tr . /` # Convert the package name into a file path 
javac -cp $LIB_DIR/rationals.jar $SOURCE/*.java
jar cfe progress.jar $PACKAGE.YearProgress -C $TMP_DIR . $SOURCE/*.class