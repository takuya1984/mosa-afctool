#!/bin/bash
#-----------------------------------------------------------------------
# name   : regist.sh
#
# 処理概要 : 管理テーブルへの登録funcitonを定義
#-----------------------------------------------------------------------

. $(dirname $0)/../conf/script.conf


#----------------------------------------------
# function:func_regist_log
#
# param $1:MODE ログ種別コード
#
# return : 0 - normal
#        : 0以外 - error
#
# 処理概要:
#    ログ登録のラッパーfunciton.
#    パラメータのログ種別コードに応じて、各処理をcallする.
#----------------------------------------------
regist_log_data() {

	MODE=$1
	TODAY=`date +"%Y%m%d%H%M%S"`

	if [ $# -ne 1 ]
	then
		echo "Usage : <command> [1-10]"
		return -1
	fi

	#---------------------------------------------
	# モードごとの処理
	#---------------------------------------------
	case "$MODE" in
		"1")
			# Clientログ登録
			java -Dfile.encoding=utf-8 -cp ${JAVA_NORIN_JAR} com.jbcc.MQTool.create.ClientLogCreator
			;;

		"2")
			# WEBServerログ登録
			java -Dfile.encoding=utf-8 -cp ${JAVA_NORIN_JAR} com.jbcc.MQTool.create.WebServerLogCreator
			;;

		"3" | "4" | "5" | "6")
			# 登録 OTX-CSS,OTX-ONL,AP-INFO,AP-HOST
			java -Dfile.encoding=utf-8 -cp ${JAVA_NORIN_JAR} com.jbcc.MQTool.create.OtxLogCreator "$MODE"
			;;

		"7")
			# 登録 Tracelog
			java -Dfile.encoding=utf-8 -cp ${JAVA_NORIN_JAR} com.jbcc.MQTool.create.TraceLogCreator
			;;

		"8")
			# 登録 DBIOログ
			java -Dfile.encoding=utf-8 -cp ${JAVA_NORIN_JAR} com.jbcc.MQTool.create.DbioLogCreator
			;;

		"9" | "10")
			# 登録 OTX-SQL,AP-SQL
			java -Dfile.encoding=utf-8 -cp ${JAVA_NORIN_JAR} com.jbcc.MQTool.create.SqlLogCreator "$MODE"
			;;

		*)
			echo "Usage : regist.sh [1-10]"
			return -1
	esac

	return 0

}
