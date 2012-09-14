#!/bin/bash

. $(dirname $0)/../conf/script.conf

#----------------------------------------------
# function:func_regist_log
# param $1:MODE ログ種別コード
#
# 処理概要:
#    ログ登録のラッパーfunciton.
#    パラメータのログ種別コードに応じて、
#    各処理をcallする.
#----------------------------------------------
func_regist_log() {

MODE=$1
TODAY=`date +"%Y%m%d%H%M%S"`

if [ $# -ne 1 ]
then
	echo "Usage : <command> [1-10]"
	return -1
fi

#---------------------------------
# モードごとの処理
#---------------------------------
case "$MODE" in
"1")
	#---------------------------------------------
	# Clientログ登録
	#---------------------------------------------
	${BASEDIR}/bin/log_regist_client.sh

	;;

"2")
	#---------------------------------------------
	# WEBServerログ登録
	#---------------------------------------------
	${BASEDIR}/bin/log_regist_web.sh
	;;

"3" | "4" | "5" | "6")
	#--------------------------------------
	# 登録 OTX-CSS,OTX-ONL,AP-INFO,AP-HOST
	#--------------------------------------
	${BASEDIR}/bin/log_regist_otx.sh $MODE
	;;

"9" | "10")
	#--------------------------------------
	# 登録 OTX-SQL,AP-SQL
	#--------------------------------------
	${BASEDIR}/bin/log_regist_sql.sh $MODE
	;;

*)
	echo "Usage : log_conv.sh [1-10]"
	return -1
esac

return 0

}
