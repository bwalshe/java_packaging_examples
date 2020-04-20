#!/bin/sh

set -e

SOURCE=com/example/progress/YearProgress

javac -cp $LIB_DIR/rationals.jar $SOURCE.java
TMP_DIR=`mktemp -d`
unzip -uo $LIB_DIR/rationals.jar -d $TMP_DIR
jar cfe progress.jar com.example.progress.YearProgress -C $TMP_DIR . $SOURCE.class
rm -r $TMP_DIR