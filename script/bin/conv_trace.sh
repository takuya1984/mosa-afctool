#!/bin/bash
#-----------------------------------------------------------------------
# name   : conv_trace.sh
# param  : $1 - log_filename
# return : 0 - normal
#        : 1 - error
#
# 処理概要 : 指定されたログファイルからダンプファイルを抽出する
#-----------------------------------------------------------------------

. $(dirname $0)/../conf/script.conf

LOG=$1
TODAY=`date +"%Y%m%d%H%M%S"`

# ISPEC
ISPEC=$(echo $LOG | awk 'BEGIN{FS="_"}{print $(NF)}' | awk 'BEGIN{FS="."}{print $1}')

# 識別子
TMP_FILE=${TMP_DIR}/tracelog_.${TODAY}.tmp.$$

# アーギュメントチェック
if [ $# -ne 1 ]
then
	echo "error:引数は1つ指定してください" 1>&2
	exit 1
fi

# 識別子リスト生成
grep "DB:.*SELECT \|DB:.*UPDATE \|DB:.*INSERT \|DB:.*DELETE \|^BEFORE \|^AFTER \|^0" $LOG > ${TMP_FILE}

status=0;TMP_FILE_NAME=""
while IFS= read LINE
do
	# DBアクセス処理部分から情報を抽出
	if echo "${LINE}" | grep "DB:.*SELECT\|DB:.*UPDATE\|DB:.*INSERT\|DB:.*DELETE" > /dev/null 2>&1
	then

		# 1つ前の抽出情報を整形しファイル生成
		if [ $status -eq 2 ];then
			cat $TMP_FILE_NAME | awk -f ${BASEDIR}/bin/trace_conv.awk > $FILE_NAME
			rm -rf ${TMP_FILE_NAME}
		fi

		status=1
		LOG_OUTPUT_DATE=$(echo "$LINE" | awk '{print $1}' | sed -e "s/://g")
		LOG_TABLE_NAME=$(echo "$LINE" | awk '{print $(NF-1)}' | sed -e "s/.*\.//" -e "s/:.*//")
		NAME="${LOG_OUTPUT_DATE}_${ISPEC}_${LOG_TABLE_NAME}.log"
		TMP_FILE_NAME="${TMP_DIR}/$NAME"
		FILE_NAME="${TRACE_LOG_DIR}/$NAME"
		continue
	fi

	if echo "${LINE}" | grep "AFTER " > /dev/null 2>&1
	then
		status=2
		continue
	fi

	if [ $status -eq 2 ];then
		if echo "${LINE}" | grep "^0" > /dev/null 2>&1
		then
			echo "${LINE}"| cut -b1-79 >> $TMP_FILE_NAME
			continue
		fi
	fi
done < ${TMP_FILE}

	
# tempファイル削除
rm -rf ${TMP_FILE}

exit 0

