#!/bin/bash
#-----------------------------------------------------------------------
# name   : conv_otx.sh
# param  : $1 - log_filename
# return : 0 - normal
#        : 1 - error
#
# 処理概要 : 指定されたログファイルから電文ログ形式ファイルを生成する
#-----------------------------------------------------------------------

. $(dirname $0)/../conf/script.conf

LOG=$1
TODAY=`date +"%Y%m%d%H%M%S"`
MODE=$2

# 識別子
OPE_LIST=${TMP_DIR}/otxlog_.${TODAY}.tmp.$$

# アーギュメントチェック
if [ $# -ne 2 ]
then
	echo "error:引数は2つ指定してください" 1>&2
	exit 1
fi

# 識別子リスト生成
grep "Execute" "$LOG" | awk '{printf("%s %s %s\n", $5,$6,$7)}' | sort | uniq | sed -e "s/ /_/g" > ${OPE_LIST}

#--------------------------------------------------
# 識別子単位に処理
#--------------------------------------------------
for ope in `cat "${OPE_LIST}"`
do

	# 識別子毎にデータ抽出
	TMP_FILE="${ope}.${TODAY}.tmp.$$"
	TMP_OPE_FILE="${TMP_DIR}/${TMP_FILE}"
	grep "Execute\|Ope=" $LOG | grep "`echo "${ope}" | sed -e "s/_/ /g"`" | sed -e "s/<CrLf>//g" > ${TMP_OPE_FILE}

	java -Dfile.encoding=utf-8 -cp ${JAVA_NORIN_JAR} com.jbcc.MQTool.converter.OtxLogConverter "${MODE}" "${TMP_FILE}"

	rm -f ${TMP_OPE_FILE}
done

# tempファイル削除
rm -rf ${OPE_LIST}

exit 0

