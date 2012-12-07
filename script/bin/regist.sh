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
#			${BASEDIR}/bin/regist_client.sh
			java -Dfile.encoding=utf-8 -cp ${JAVA_NORIN_JAR} com.jbcc.MQTool.create.ClientLogCreator
#			mv ${CLIENT_LOG_DIR}/* ${CLIENT_LOG_DIR_REGIST}/ > /dev/null 2>&1
			;;

		"2")
			# WEBServerログ登録
#			${BASEDIR}/bin/regist_web.sh
			java -Dfile.encoding=utf-8 -cp ${JAVA_NORIN_JAR} com.jbcc.MQTool.create.WebServerLogCreator
#			mv ${WEB_LOG_DIR}/* ${WEB_LOG_DIR_REGIST}/ > /dev/null 2>&1
			;;

		"3" | "4" | "5" | "6")
			# 登録 OTX-CSS,OTX-ONL,AP-INFO,AP-HOST
#			${BASEDIR}/bin/regist_otx.sh $MODE
			java -Dfile.encoding=utf-8 -cp ${JAVA_NORIN_JAR} com.jbcc.MQTool.create.OtxLogCreator "$MODE"

			LOG_DIR="";LOG_DIR_REGIST=""
			if [ $MODE == "3" ];then
				LOG_DIR=${OTXCSS_LOG_DIR}
				LOG_DIR_REGIST=${OTXCSS_LOG_DIR_REGIST}
			elif [ $MODE == "4" ];then
				LOG_DIR=${OTXONL_LOG_DIR}
				LOG_DIR_REGIST=${OTXONL_LOG_DIR_REGIST}
			elif [ $MODE == "5" ];then
				LOG_DIR=${APINFO_LOG_DIR}
				LOG_DIR_REGIST=${APINFO_LOG_DIR_REGIST}
			elif [ $MODE == "6" ];then
				LOG_DIR=${APHOST_LOG_DIR}
				LOG_DIR_REGIST=${APHOST_LOG_DIR_REGIST}
			fi

#			mv ${LOG_DIR}/* ${LOG_DIR_REGIST}/ > /dev/null 2>&1
			;;

		"7")
			# 登録 Tracelog
#			${BASEDIR}/bin/regist_trace.sh
			java -Dfile.encoding=utf-8 -cp ${JAVA_NORIN_JAR} com.jbcc.MQTool.create.TraceLogCreator
#			mv ${TRACE_LOG_DIR}/* ${TRACE_LOG_DIR_REGIST}/ > /dev/null 2>&1
			;;

		"8")
			# 登録 DBIOログ
#			${BASEDIR}/bin/regist_dbio.sh
			java -Dfile.encoding=utf-8 -cp ${JAVA_NORIN_JAR} com.jbcc.MQTool.create.DbioLogCreator
#			mv ${DBIO_LOG_DIR}/* ${DBIO_LOG_DIR_REGIST}/ > /dev/null 2>&1
			;;

		"9" | "10")
			# 登録 OTX-SQL,AP-SQL
#			${BASEDIR}/bin/regist_sql.sh $MODE
			java -Dfile.encoding=utf-8 -cp ${JAVA_NORIN_JAR} com.jbcc.MQTool.create.SqlLogCreator "$MODE"
			# if [ $MODE == "9" ];then
			# 	mv ${OTXSQL_LOG_DIR}/* ${OTXSQL_LOG_DIR_REGIST}/ > /dev/null 2>&1
			# elif [ $MODE == "10" ];then
			# 	mv ${APSQL_LOG_DIR}/* ${APSQL_LOG_DIR_REGIST}/ > /dev/null 2>&1
			# fi
			;;

		*)
			echo "Usage : conv.sh [1-10]"
			return -1
	esac

	return 0

}
