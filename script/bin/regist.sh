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
			${BASEDIR}/bin/regist_client.sh
			mv ${CLIENT_LOG_DIR}/* ${CLIENT_LOG_DIR_REGIST}/
			;;

		"2")
			# WEBServerログ登録
			${BASEDIR}/bin/regist_web.sh
			mv ${WEB_LOG_DIR}/* ${WEB_LOG_DIR_REGIST}/
			;;

		"3" | "4" | "5" | "6")
			# 登録 OTX-CSS,OTX-ONL,AP-INFO,AP-HOST
			${BASEDIR}/bin/regist_otx.sh $MODE

			if [ $MODE == "3" ];then
				mv ${OTXCSS_LOG_DIR}/* ${OTXCSS_LOG_DIR_REGIST}/
			elif [ $MODE == "4" ];then
				mv ${OTXONL_LOG_DIR}/* ${OTXONL_LOG_DIR_REGIST}/
			elif [ $MODE == "5" ];then
				mv ${APINFO_LOG_DIR}/* ${APINFO_LOG_DIR_REGIST}/
			elif [ $MODE == "6" ];then
				mv ${APHOST_LOG_DIR}/* ${APHOST_LOG_DIR_REGIST}/
			fi
			;;

		"7")
			# 登録 Tracelog
			${BASEDIR}/bin/regist_trace.sh
			mv ${OTXCSS_LOG_DIR}/* ${OTXCSS_LOG_DIR_REGIST}/
			;;

		"8")
			# 登録 DBIOログ
			${BASEDIR}/bin/regist_dbio.sh
			;;

		"9" | "10")
			# 登録 OTX-SQL,AP-SQL
			${BASEDIR}/bin/regist_sql.sh $MODE
			;;

		*)
			echo "Usage : conv.sh [1-10]"
			return -1
	esac

	return 0

}
