#!/bin/bash
#-----------------------------------------------------------------------
# name   : regist_client.sh
# param  : none
# return : 0 - normal
#        : 1 - error
#
# 処理概要 : VBログを管理DBに登録する.
#-----------------------------------------------------------------------

. $(dirname $0)/../conf/script.conf

TODAY=`date +"%Y%m%d%H%M%S"`
COM="",ERR="",ONL="",CSS="";

for file in $(ls ${CLIENT_LOG_DIR})
do
	LOG_CD="";LOG_OUTPUT_DATE="";UPDW_FLG="";CL_CD="";OPE_CD="";DENBUN_CD="";CLIENT_SERIAL_NUMBER="";CONTINUE_DENBUN_FLG="";TRANSACTION_NUMBER=""
	while IFS= read LINE
	do

		opecd=$(echo "${LINE}" | awk '{print $5}')

		# -------------------------------
		# 上り下りの判定
		# -------------------------------
		if echo "${LINE}" | grep "送信" > /dev/null 2>&1
		then
			UPDW_FLG="1"
		else
			UPDW_FLG="2"
		fi

		# -------------------------------
		# ヘッダ情報取得
		# -------------------------------
		if [ "$opecd" == "Com" ];then
			COM=$(echo "${LINE}" | sed -e "s/.* Com //")
			LOG_OUTPUT_DATE=$(echo "${LINE}" | awk '{print $1 $2}' | sed -e "s/\///g" -e "s/://g")
			LOG_CD="1"
			CL_CD=$(echo "${COM}" | cut -b1-5)
			OPE_CD=$(echo "${COM}" | cut -b6-6)
			DENBUN_CD=$(echo "${COM}" | cut -b7-13)
			CLIENT_SERIAL_NUMBER=$(echo "${COM}" | cut -b14-17)
			CONTINUE_DENBUN_FLG=$(echo "${COM}" | cut -b18-18)

			# 下りの場合
			if [ $UPDW_FLG == "2" ];then
				# トランザクション処理連番
				TRANSACTION_NUMBER=$(echo "${COM}" | cut -b23-30)
			fi
		fi

	done < ${CLIENT_LOG_DIR}/$file
	
	# -------------------------------
	# 管理DBに登録
	# -------------------------------
	java -jar ${JAVA_NORIN_JAR} RegistLogData "log_cd=${LOG_CD}" "log_output_date=${LOG_OUTPUT_DATE}" "up_down_cd=${UPDW_FLG}" "cl_cd=${CL_CD}" "ope_cd=${OPE_CD}" "denbun_cd=${DENBUN_CD}" "client_serial_number=${CLIENT_SERIAL_NUMBER}" "continue_denbun_flg=${CONTINUE_DENBUN_FLG}" "transaction_number=${TRANSACTION_NUMBER}" "log_data_file=${file}"
done
