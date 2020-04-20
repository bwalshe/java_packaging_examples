#!/bin/sh

set -e
javac  com/example/rationals/*.java
java com.example.rationals.RationalNumberTests
jar cf rationals.jar com/example/rationals/*.class
