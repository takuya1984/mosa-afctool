#!/bin/bash
#-----------------------------------------------------------------------
# name   : regist_sql.sh
# param  : none
# return : 0 - normal
#        : 1 - error
#
# 処理概要 : SQLログを管理DBに登録する.
#-----------------------------------------------------------------------

. $(dirname $0)/../conf/script.conf

MODE=$1
TODAY=`date +"%Y%m%d%H%M%S"`
COM="",ERR="",ONL="",CSS="";

# アーギュメントチェック
if [ $# -ne 1 ]
then
	echo "error:引数は1つ指定してください" 1>&2
	exit 1
fi

LOG_DATA_DIR=""
case "$MODE" in
"9")
	# OTX-SQL
    LOG_DATA_DIR="${OTXSQL_LOG_DIR}"
	;;
"10")
	# AP-SQL
    LOG_DATA_DIR="${APSQL_LOG_DIR}"
	;;
*)
    echo "Usage:regist_sql.sh [MODE:9|10]"
    exit 1
esac


for file in $(ls ${LOG_DATA_DIR})
do
	LOG_CD="";LOG_OUTPUT_DATE="";UPDW_FLG="";CL_CD="";OPE_CD="";DENBUN_CD="";CLIENT_SERIAL_NUMBER="";CONTINUE_DENBUN_FLG="";TRANSACTION_NUMBER="";FUNCTION_CD="";MULTI_DENBUN_TYPE="";DENBUN_KIND=""
	while IFS= read LINE
	do
		# -------------------------------
		# ヘッダ情報取得
		# -------------------------------
		if echo "${LINE}" | grep "共通ヘッダ部" > /dev/null 2>&1
		then
			COM=$(echo "${LINE}" | sed -e "s/.*共通ヘッダ部=//")
			LOG_OUTPUT_DATE=$(echo "${LINE}" | awk '{print $1 $2}' | sed -e "s/\///g" -e "s/://g" | sed -e "s/|.*//")
			LOG_CD="1"
			CL_CD=$(echo "${COM}" | cut -b1-5)
			OPE_CD=$(echo "${COM}" | cut -b6-6)
			DENBUN_CD=$(echo "${COM}" | cut -b7-13)
			CLIENT_SERIAL_NUMBER=$(echo "${COM}" | cut -b14-17)
			CONTINUE_DENBUN_FLG=$(echo "${COM}" | cut -b18-18)
			MULTI_DENBUN_TYPE=$(echo "${COM}" | cut -b19-19)
			# 下りの場合
			if echo "${LINE}" | grep "下り" > /dev/null 2>&1
			then
				# トランザクション処理連番
				TRANSACTION_NUMBER=$(echo "${COM}" | cut -b23-30)
				# 電文種別
				DENBUN_KIND=$(echo "${COM}" | cut -b21-22)
			fi
		fi

		# -------------------------------
		# ヘッダ情報取得
		# -------------------------------
		if echo "${LINE}" | grep " Ope=" > /dev/null 2>&1
		then
			FUNCTION_CD=$(echo "${LINE}" | sed -e "s/.*Ope=//" | awk '{print $1}')
		fi
	done < ${LOG_DATA_DIR}/$file
	
	# -------------------------------
	# 管理DBに登録
	# -------------------------------
	java -jar ${JAVA_NORIN_JAR} RegistLogData "log_cd=${LOG_CD}" "log_output_date=${LOG_OUTPUT_DATE}" "function_cd=${FUNCTION_CD}" "up_down_cd=${UPDW_FLG}" "cl_cd=${CL_CD}" "ope_cd=${OPE_CD}" "denbun_cd=${DENBUN_CD}" "client_serial_number=${CLIENT_SERIAL_NUMBER}" "continue_denbun_flg=${CONTINUE_DENBUN_FLG}" "multi_denbun_type=${MULTI_DENBUN_TYPE}" "denbun_kind=${DENBUN_KIND}" "transaction_number=${TRANSACTION_NUMBER}" "log_data_file=${file}"

done
