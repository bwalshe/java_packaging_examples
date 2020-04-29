#!/bin/sh
set -e

if [ -n "$MY_LIB_DIR" ]; then
  sh ./build.sh
  cp rationals.jar $MY_LIB_DIR
else
  echo "ERROR: \$MY_LIB_DIR not set"
  exit 1
fi
