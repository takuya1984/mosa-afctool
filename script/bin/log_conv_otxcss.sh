#!/bin/bash
#-----------------------------------------------------------------------
# name   : log_conv_otxcss.sh
# param  : $1 - log_filename
# return : 0 - normal
#        : 1 - error
#
# 処理概要 : 指定されたログファイルから電文ログ形式ファイルを生成する
#-----------------------------------------------------------------------
set -x
. $(dirname $0)/../conf/script.conf

LOG=$1
TODAY=`date +"%Y%m%d%H%M%S"`

# 識別子
OPE_LIST=${TMP_DIR}/otxcss_.${TODAY}.tmp.$$

# アーギュメントチェック
if [ $# -ne 1 ]
then
	echo "error:引数は1つ指定してください" 1>&2
	exit 1
fi

# 識別子リスト生成
#cat $LOG | awk '{printf("%s %s %s\n", $5,$6,$7)}' | sort | uniq | sed -e "s/ /_/g" > ${OPE_LIST}
grep "Execute" $LOG | awk '{printf("%s %s %s\n", $5,$6,$7)}' | sort | uniq | sed -e "s/ /_/g" > ${OPE_LIST}


#--------------------------------------------------
# 識別子単位に処理
#--------------------------------------------------
for ope in `cat "${OPE_LIST}"`
do

	# 識別子毎にデータ抽出
	TMP_OPE_FILE="${TMP_DIR}/${ope}.${TODAY}.tmp.$$"
	grep "Execute\|Ope=" $LOG | grep "`echo "${ope}" | sed -e "s/_/ /g"`" | sed -e "s/<CrLf>//g" > ${TMP_OPE_FILE}

	UpCom=""
	DownCom=""
	OpeId=""
	TMP_DATA_FILE="",TIME=""
	status=0
	#--------------------------------------------------
	# 電文抽出処理
	#--------------------------------------------------
	while IFS= read LINE
	do

		# 上り-共通ヘッダ
		if echo "${LINE}" | grep "上り共通ヘッダ部" > /dev/null 2>&1
		then
			UpCom=`echo "${LINE}" | sed -e "s/.*上り共通ヘッダ部=//"`
			status=1
			TIME=$(echo "${LINE}"| cut -b1-19 | sed -e "s/\//-/g" -e "s/ /_/g" -e "s/://g")
			DATA_FILE_UP="$(echo "${TIME}")_$(echo "${UpCom}" | cut -b1-5)_$(echo "${UpCom}" | cut -b6-6)_$(echo "${UpCom}" | cut -b7-13)_1.dat"
			TMP_DATA_FILE_UP="${TMP_DIR}/${DATA_FILE_DW}.$$"

			echo "${LINE}" >> $TMP_DATA_FILE_UP
			continue
		fi

		# 上り-オンライン
		# 上り-CSS
		if echo "${LINE}" | grep "上りオンライン業務固有部\|上りCSS業務固有部" > /dev/null 2>&1
		then
			echo "${LINE}" >> $TMP_DATA_FILE_UP

			continue
		fi

			
		# 下り-共通ヘッダ
		echo "${LINE}" | grep "下り共通ヘッダ部" > /dev/null 2>&1
		RC=$?
		if [ $RC -eq 0 ]; then
			DownCom=`echo "${LINE}" | sed -e "s/.*下り共通ヘッダ部=//"`
			status=1
			TIME=$(echo "${LINE}"| cut -b1-19 | sed -e "s/\//-/g" -e "s/ /_/g" -e "s/://g")
			DATA_FILE_DW="$(echo "${TIME}")_$(echo "${DownCom}" | cut -b1-5)_$(echo "${DownCom}" | cut -b6-6)_$(echo "${DownCom}" | cut -b7-13)_2.dat"
			TMP_DATA_FILE_DW="${TMP_DIR}/${DATA_FILE_DW}.$$"

			echo "${LINE}" >> $TMP_DATA_FILE_DW
			continue
		fi

		# 下り-エラー制御
		echo "${LINE}" | grep "エラー制御部" > /dev/null 2>&1
		RC=$?
		if [ $RC -eq 0 ]; then
			echo "${LINE}" >> $TMP_DATA_FILE_DW
			continue
		fi

		# 下り-オンライン
		# 下り-CSS
		if echo "${LINE}" | grep "下りオンライン業務固有部\|下りCSS業務固有部" > /dev/null 2>&1
		then
			echo "${LINE}" >> $TMP_DATA_FILE_DW
			continue
		fi

		# 画面ID_電文ID取得
		if echo "${LINE}" | tr -d ${LINE_FEED_CD} | grep "Ope=.*終了$" > /dev/null 2>&1
		then
			OpeId=`echo "${LINE}" | sed -e "s/.*Ope=//" | awk '{print $1}'`
			status=0

			cp -p ${TMP_DATA_FILE_UP} ${OTXCSS_LOG_DIR}/${DATA_FILE_UP}
			rm -f ${TMP_DATA_FILE_UP}
			cp -p ${TMP_DATA_FILE_DW} ${OTXCSS_LOG_DIR}/${DATA_FILE_DW}
			rm -f ${TMP_DATA_FILE_DW}

		fi
	done < ${TMP_OPE_FILE}
	
	# tempファイル削除
	rm -rf ${TMP_OPE_FILE}
done


# tempファイル削除
rm -rf ${OPE_LIST}

exit 0

