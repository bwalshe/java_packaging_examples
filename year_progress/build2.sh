#!/bin/sh

set -e

PACKAGE=com.example.progress

LIB=$LIB_DIR/rationals.jar

SOURCE=`echo $PACKAGE | tr . /` # Convert the package name into a file path 
javac -cp $LIB $SOURCE/*.java
TMP_DIR=`mktemp -d`
unzip -uo $LIB -d $TMP_DIR
jar cfe progress-executable.jar $PACKAGE.YearProgress -C $TMP_DIR . $SOURCE/*.class
rm -r $TMP_DIR