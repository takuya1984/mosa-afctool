#!/bin/bash

. $(dirname $0)/../conf/script.conf

echo $BASEDIR

#-----------------------------
# ログ種別コード
# 1:client
# 2:web
# 3:otx-css
# 4:otx-onl
# 5:apinfo
# 6:aphost
# 7:trace
# 8:dbio
# 9:otx-sql
# 10:ap-sql
#-----------------------------

MODE=$1
TODAY=`date +"%Y%m%d%H%M%S"`

if [ $# -ne 1 ]
then
	echo "Usage : <command> [client | otx | gateway]"
	exit 1
fi

#---------------------------------
# モードごとの処理
#---------------------------------
case "$MODE" in
"1")
	echo "client"
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
			exit 1
		fi
	done

	# ログ情報登録
	${BASEDIR}/bin/log_regist_conv.sh

	;;
"2")
	echo "web"
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
			exit 1
		fi
	done

	# ログ情報登録
#	${BASEDIR}/bin/log_regist_web.sh
	;;
"3")
	#--------------------------------------
	# OTX-CSS 抽出処理
	#--------------------------------------
	MARGE_FILE=${TMP_DIR}/marge_otx.${TODAY}.tmp.$$
	MARGE_FILE_UTF8=${OTXCSS_LOG_DIR_UTF8}/marge_otx.utf8.${TODAY}.tmp.$$

	# 複数のファイルをマージ
	sort -m -k2 ${OTXCSS_LOG_DIR_TARGET}/*log* > ${MARGE_FILE}

	# UTF-8変換
	nkf -w8x --ms-ucs-map ${MARGE_FILE} > ${MARGE_FILE_UTF8}

	# ログ抽出
	${BASEDIR}/bin/log_conv_otxcss.sh "${MARGE_FILE_UTF8}"
	RC=$?
	if [ $RC -ne 0 ]
	then
		echo "error : ログ抽出エラー"
		exit 1
	fi

	# 後続電文抽出
	# ${BINDIR}/log_conv_otx_repeatlist.sh # 

	# 改行コード変換(LF -> CRLF)
	# for file in `ls ${OTX_MSG_DIR}/*`
	# do
	# 	nkf -w8x --ms-ucs-map -Lw --overwrite ${file}
	# done

	rm -f ${MARGE_FILE} ${MARGE_FILE_UTF8}
	;;
*)
	echo "Usage : log_conv.sh [client | otx | gateway]"
	exit 1
esac

echo "success"
exit 0
