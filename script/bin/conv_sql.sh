#!/bin/bash
#-----------------------------------------------------------------------
# name   : conv_sql.sh
# param  : $1 - log_filename
# return : 0 - normal
#        : 1 - error
#
# 処理概要 : 指定されたログファイルからSQLログ形式ファイルを生成する
#-----------------------------------------------------------------------

. $(dirname $0)/../conf/script.conf

LOG=$1
TODAY=`date +"%Y%m%d%H%M%S"`
MODE=$2

# 識別子
OPE_LIST=${TMP_DIR}/sqllog_.${TODAY}.tmp.$$

# アーギュメントチェック
if [ $# -ne 2 ]
then
	echo "error:引数は2つ指定してください" 1>&2
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
    echo "Usage:conv_sql.sh [logfilename] [MODE:9|10]"
    exit 1
esac

# 識別子リスト生成
grep "Execute" $LOG | awk '{printf("%s %s %s\n", $5,$6,$7)}' | sort | uniq | sed -e "s/ /_/g" > ${OPE_LIST}


#--------------------------------------------------
# 識別子単位に処理
#--------------------------------------------------
for ope in `cat "${OPE_LIST}"`
do

	# 識別子毎にデータ抽出
	TMP_FILE="${ope}.${TODAY}.tmp.$$"
	TMP_OPE_FILE="${TMP_DIR}/${TMP_FILE}"

	grep "Execute\|Ope=\|=<select\|=<insert\|=<update\|=<delete" $LOG | grep "`echo "${ope}" | sed -e "s/_/ /g"`" > ${TMP_OPE_FILE}

	#--------------------------------------------------
	# 電文抽出処理
	#--------------------------------------------------
	java -Dfile.encoding=utf-8 -cp ${JAVA_NORIN_JAR} com.jbcc.MQTool.converter.SqlLogConverter "${MODE}" "${TMP_FILE}"
	
	# tempファイル削除
	rm -rf ${TMP_OPE_FILE}
done


# tempファイル削除
rm -rf ${OPE_LIST}

exit 0

