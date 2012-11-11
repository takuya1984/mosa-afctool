#!/bin/bash

. $(dirname $0)/../bin/compare.sh

if [ $# -eq 2 ];then
	compare_log $1 $2
	RC=$?
elif [ $# -eq 3 ];then
	compare_log $1 $2 $3
	RC=$?
fi
echo "return:$RC"
