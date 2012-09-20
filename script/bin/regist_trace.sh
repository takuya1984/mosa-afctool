#!/bin/bash
#-----------------------------------------------------------------------
# name   : regist_trace.sh
# param  : none
# return : 0 - normal
#        : 1 - error
#
# 処理概要 : Tracelogを管理DBに登録する.
#-----------------------------------------------------------------------

. $(dirname $0)/../conf/script.conf

MODE=$1
TODAY=`date +"%Y%m%d%H%M%S"`

LOG_DATA_DIR="${TRACE_LOG_DIR}"

for file in $(ls ${LOG_DATA_DIR})
do
	# ファイル名から情報取得
	# ISPEC
	FUNCTION_CD=$(echo "$file" | awk 'BEGIN{FS="_"}{print $2}')
	# TABLE名
	LOG_TABLE_NAME=$(echo "$file" | cut -b18-22)
	# ログ時間
	LOG_OUTPUT_DATE=$(echo "$file" | awk 'BEGIN{FS="_"}{print $1}')
	# ログ種別コード
	LOG_CD="7"
	
	# -------------------------------
	# 管理DBに登録
	# -------------------------------
	java -jar ${JAVA_NORIN_JAR} RegistLogData "log_cd=${LOG_CD}" "log_output_date=${LOG_OUTPUT_DATE}" "function_cd=${FUNCTION_CD}" "up_down_cd=${UPDW_FLG}" "cl_cd=${CL_CD}" "ope_cd=${OPE_CD}" "denbun_cd=${DENBUN_CD}" "client_serial_number=${CLIENT_SERIAL_NUMBER}" "continue_denbun_flg=${CONTINUE_DENBUN_FLG}" "multi_denbun_type=${MULTI_DENBUN_TYPE}" "denbun_kind=${DENBUN_KIND}" "transaction_number=${TRANSACTION_NUMBER}" "log_table_name=${LOG_TABLE_NAME}" "log_data_file=${file}"

done
