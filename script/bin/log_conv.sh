#!/bin/bash

. $(dirname $0)/../conf/script.conf

#----------------------------------------------
# function:func_conv_log
# param $1:MODE ログ種別コード
#
# 処理概要:
#    ログ抽出のラッパーfunciton.
#    パラメータのログ種別コードに応じて、
#    各処理をcallする.
#----------------------------------------------
func_conv_log() {

MODE=$1
TODAY=`date +"%Y%m%d%H%M%S"`

if [ $# -ne 1 ]
then
	echo "Usage : <command> [MODE:1-10]"
	return -1
fi

#---------------------------------
# ログ種別コードごとの処理
#---------------------------------
case "$MODE" in
"1")
	#---------------------------------------------
	# Clientログ抽出処理
	#---------------------------------------------

	for file in $(ls ${CLIENT_LOG_DIR_TARGET})
	do
		if [ -d $file ]
		then
			continue
		fi

		# UTF-8変換
		nkf -w8x --ms-ucs-map ${CLIENT_LOG_DIR_TARGET}/${file} > ${CLIENT_LOG_DIR_UTF8}/${file}

		# 改行対応
#		${BIN_DIR}/log_conv_linefeed.sh "${CLIENT_LOG_UTF8_DIR}/${file}"

		# ログ抽出
		${BASEDIR}/bin/log_conv_client.sh "${CLIENT_LOG_DIR_UTF8}/${file}"
		RC=$?
		if [ $RC -ne 0 ]
		then
			echo "error : ログ抽出エラー"
			return -1
		fi
	done

	# ログ情報登録
#	${BASEDIR}/bin/log_regist_conv.sh

	;;
"2")
	#---------------------------------------------
	# WEBServerログ抽出処理
	#---------------------------------------------

	for file in $(ls ${WEB_LOG_DIR_TARGET})
	do
		if [ -d $file ]
		then
			continue
		fi

		# UTF-8変換
		nkf -w8x --ms-ucs-map ${WEB_LOG_DIR_TARGET}/${file} > ${WEB_LOG_DIR_UTF8}/${file}

		# 改行対応
#		${BIN_DIR}/log_conv_linefeed.sh "${WEB_LOG_UTF8_DIR}/${file}"

		# ログ抽出
		${BASEDIR}/bin/log_conv_web.sh "${WEB_LOG_DIR_UTF8}/${file}"
		RC=$?
		if [ $RC -ne 0 ]
		then
			echo "error : ログ抽出エラー"
			return -1
		fi
	done

	# ログ情報登録
#	${BASEDIR}/bin/log_regist_web.sh
	;;

"3" | "4" | "5" | "6")

	LOG_DIR_TARGET=""
	# OTX-CSS
	if [ $MODE -eq 3 ];then
		LOG_DIR_TARGET=${OTXCSS_LOG_DIR_TARGET}
	# OTX-ONL
	elif [ $MODE -eq 4 ];then
		LOG_DIR_TARGET=${OTXONL_LOG_DIR_TARGET}
	# AP-INF
	elif [ $MODE -eq 5 ];then
		LOG_DIR_TARGET=${APINFO_LOG_DIR_TARGET}
	# AP-HOST
	elif [ $MODE -eq 6 ];then
		LOG_DIR_TARGET=${APHOST_LOG_DIR_TARGET}
	fi
	#--------------------------------------
	# 抽出処理
	#--------------------------------------
	MARGE_FILE=${TMP_DIR}/marge_otx.${TODAY}.tmp.$$
	MARGE_FILE_UTF8=${TMP_DIR}/marge_otx.utf8.${TODAY}.tmp.$$

	# 複数のファイルをマージ
	sort -m -k2 ${LOG_DIR_TARGET}/*log* > ${MARGE_FILE} 2>&1

	# UTF-8変換
	nkf -w8x --ms-ucs-map ${MARGE_FILE} > ${MARGE_FILE_UTF8}

	# ログ抽出
	${BASEDIR}/bin/log_conv_otx.sh "${MARGE_FILE_UTF8}" $MODE
	RC=$?
	if [ $RC -ne 0 ]
	then
		echo "error : ログ抽出エラー"
		return -1
	fi

	rm -f ${MARGE_FILE} ${MARGE_FILE_UTF8}
	;;
"7")
	#---------------------------------------------
	# Tracelog抽出処理
	#---------------------------------------------

	for file in $(ls ${TRACE_LOG_DIR_TARGET})
	do
		if [ -d $file ]
		then
			continue
		fi

		# ログ抽出
		${BASEDIR}/bin/log_conv_trace.sh "${TRACE_LOG_DIR_TARGET}/${file}"
		RC=$?
		if [ $RC -ne 0 ]
		then
			echo "error : ログ抽出エラー"
			return -1
		fi
	done
	;;
"9" | "10")

	LOG_DIR_TARGET=""
	# OTX-SQL
	if [ $MODE -eq 9 ];then
		LOG_DIR_TARGET=${OTXSQL_LOG_DIR_TARGET}
	# AP-SQL
	elif [ $MODE -eq 10 ];then
		LOG_DIR_TARGET=${APSQL_LOG_DIR_TARGET}
	fi
	#--------------------------------------
	# 抽出処理
	#--------------------------------------
	MARGE_FILE=${TMP_DIR}/marge_otx.${TODAY}.tmp.$$
	MARGE_FILE_UTF8=${TMP_DIR}/marge_otx.utf8.${TODAY}.tmp.$$

	# 複数のファイルをマージ
	sort -m -k2 ${LOG_DIR_TARGET}/*log* > ${MARGE_FILE} 2>&1

	# UTF-8変換
	nkf -w8x --ms-ucs-map ${MARGE_FILE} > ${MARGE_FILE_UTF8}

	# ログ抽出
	${BASEDIR}/bin/log_conv_sql.sh "${MARGE_FILE_UTF8}" $MODE
	RC=$?
	if [ $RC -ne 0 ]
	then
		echo "error : ログ抽出エラー"
		return -1
	fi

	rm -f ${MARGE_FILE} ${MARGE_FILE_UTF8}
	;;
*)
	echo "Usage : log_conv.sh [1-10]"
	return -1
esac

echo "success"
return 0

}
