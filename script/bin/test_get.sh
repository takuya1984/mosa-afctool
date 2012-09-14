#!/bin/bash

. $(dirname $0)/../bin/log_get.sh

MODE=$1
TODAY=`date +"%Y%m%d%H%M%S"`

#---------------------------------
# モードごとの処理
#---------------------------------
case "$MODE" in
"1")
	echo "FUNCTION_CD=GeneAcc3301_0801030"
	log_get_list "FUNCTION_CD=GeneAcc3301_0801030"
	RC=$?
	echo "result=$RC"

	echo "FUNCTION_CD=GeneAcc3301_0801030 CLIENT_SERIAL_NUMBER=0002"
	log_get_list "FUNCTION_CD=GeneAcc3301_0801030" "CLIENT_SERIAL_NUMBER=0002"
	RC=$?
	echo "result=$RC"
	;;

"2")
	echo "2"
	log_get_data "2"
	RC=$?
	echo "result=$RC"

	echo "2 3 4"
	log_get_data 2 3 4
	RC=$?
	echo "result=$RC"
	;;

*)
	echo "Usage : test_get.sh [1-2]"
	exit -1
esac

exit 0

}
