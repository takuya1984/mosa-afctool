#!/bin/bash

. $(dirname $0)/../bin/regist_compare.sh

regist_compare 6 key6 item6 6
#regist_compare 5 5 key5 item5 5
#regist_compare 1 a key1 item1 1
#regist_compare 1

echo "return:$RC"