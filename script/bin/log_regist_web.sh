#!/bin/bash
#-----------------------------------------------------------------------
# name   : log_regist_web.sh
# param  : none
# return : 0 - normal
#        : 1 - error
#
# 処理概要 : WebServerログを管理DBに登録する.
#-----------------------------------------------------------------------

. $(dirname $0)/../conf/script.conf

MODE=$1
TODAY=`date +"%Y%m%d%H%M%S"`
COM="",ERR="",ONL="",CSS="";

LOG_DATA_DIR=${WEB_LOG_DIR}

for file in $(ls ${LOG_DATA_DIR})
do
	LOG_CD="";LOG_OUTPUT_DATE="";UPDW_FLG="";CL_CD="";OPE_CD="";DENBUN_CD="";CLIENT_SERIAL_NUMBER="";CONTINUE_DENBUN_FLG="";TRANSACTION_NUMBER="";FUNCTION_CD="";MULTI_DENBUN_TYPE="";DENBUN_KIND=""

	# -------------------------------
	# 上り下りの判定
	# -------------------------------
	if echo "${file}" | grep "1.dat$" > /dev/null 2>&1
	then
		UPDW_FLG="1"
	else
		UPDW_FLG="2"
	fi
	
	while IFS= read LINE
	do
		# -------------------------------
		# ログ時間取得
		# -------------------------------
		if echo "${LINE}" | grep " TRACE " > /dev/null 2>&1
		then
			LOG_OUTPUT_DATE=$(echo "${LINE}" | awk '{print $1 $2}' | sed -e "s/\///g" -e "s/://g")
			continue
		fi

		# -------------------------------
		# 機能ID取得
		# -------------------------------
		if echo "${LINE}" | grep "<ns1:RequestMessage" > /dev/null 2>&1
		then
			FUNCTION_CD=$(echo "${LINE}" | awk -F/ '{print $(NF-1)}' | sed -e "s/Service//")
			continue
		fi

		# -------------------------------
		# ヘッダ情報取得
		# -------------------------------
		if echo "${LINE}" | grep "<strComUpHeadDt>" > /dev/null 2>&1
		then
			COM=$(echo "${LINE}" | sed -e "s/.*<strComUpHeadDt>//" | sed -e "s/<\/strComUpHeadDt>//")
			LOG_CD="2"
			CL_CD=$(echo "${COM}" | cut -b1-5)
			OPE_CD=$(echo "${COM}" | cut -b6-6)
			DENBUN_CD=$(echo "${COM}" | cut -b7-13)
			CLIENT_SERIAL_NUMBER=$(echo "${COM}" | cut -b14-17)
			CONTINUE_DENBUN_FLG=$(echo "${COM}" | cut -b18-18)
			MULTI_DENBUN_TYPE=$(echo "${COM}" | cut -b19-19)
			# 下りの場合
			if [ $UPDW_FLG == "2" ];then
				# トランザクション処理連番
				TRANSACTION_NUMBER=$(echo "${COM}" | cut -b23-30)
				# 電文種別
				DENBUN_KIND=$(echo "${COM}" | cut -b21-22)
			fi
		else
			continue
		fi

	done < ${LOG_DATA_DIR}/$file
	
	# -------------------------------
	# 管理DBに登録
	# -------------------------------
	java -jar ${JAVA_NORIN_JAR} RegistLogData "log_cd=${LOG_CD}" "log_output_date=${LOG_OUTPUT_DATE}" "function_cd=${FUNCTION_CD}" "up_down_cd=${UPDW_FLG}" "cl_cd=${CL_CD}" "ope_cd=${OPE_CD}" "denbun_cd=${DENBUN_CD}" "client_serial_number=${CLIENT_SERIAL_NUMBER}" "continue_denbun_flg=${CONTINUE_DENBUN_FLG}" "multi_denbun_type=${MULTI_DENBUN_TYPE}" "denbun_kind=${DENBUN_KIND}" "transaction_number=${TRANSACTION_NUMBER}" "log_data_file=${file}"

done
