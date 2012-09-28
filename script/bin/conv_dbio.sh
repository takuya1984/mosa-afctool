#!/bin/bash
#-----------------------------------------------------------------------
# name   : conv_dbio.sh
# param  : $1 - log_filename
# return : 0 - normal
#        : 1 - error
#
# 処理概要 : 指定されたログファイルからダンプファイルを抽出する
#-----------------------------------------------------------------------

. $(dirname $0)/../conf/script.conf

LOG=$1
TODAY=`date +"%Y%m%d%H%M%S"`
FILE=$(basename "${LOG}")
IOCOPY_CSV=$(dirname $0)/../conf/IOCOPY.csv

# ISPEC
ISPEC=$(echo $FILE | awk 'BEGIN{FS="_"}{print $1}')

# アーギュメントチェック
if [ $# -ne 1 ]
then
	echo "error:引数は1つ指定してください" 1>&2
	exit 1
fi

# 対象行抽出
TMP_FILE="${TMP_DIR}/${FILE}"
grep "INST\|UPDT\|DELE" ${LOG} > $TMP_FILE 2>&1

for logfile in $(ls ${TMP_FILE})
do
	index=0
	while IFS= read LINE
	do

		# ファイル名を生成
		if echo "${LINE}" | grep "^[0-9]" > /dev/null 2>&1
		then
			index=$(($index + 1))
			LOG_OUTPUT_DATE=$(echo "$LINE" | cut -b1-14)
			LOG_TABLE_NAME=$(echo "$LINE" | cut -b40-44)
			NAME="${LOG_OUTPUT_DATE}_${index}_${ISPEC}_${LOG_TABLE_NAME}.dat"

			FILE_NAME="${DBIO_LOG_DIR}/$NAME"

			if cat $IOCOPY_CSV | grep $LOG_TABLE_NAME > /dev/null 2>&1
			then
				start_index=$(cat $IOCOPY_CSV | grep $LOG_TABLE_NAME | awk '{print $2}' | sed -e "s/,.*//g")
			else
				continue
			fi

			s1=$(echo "${LINE}" | cut -b1-53)
			s2=$(echo "${LINE}" | cut -b$((54 + $start_index))-)
			echo -e "$s1$s2" >> $FILE_NAME

		fi
	done < ${logfile}
done

rm -f ${TMP_FILE}

exit 0

