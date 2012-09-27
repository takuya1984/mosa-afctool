#!/bin/bash

. $(dirname $0)/../bin/compare.sh

compare_log $1 $2
RC=$?
echo "return:$RC"
