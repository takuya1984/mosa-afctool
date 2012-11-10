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
	TMP_OPE_FILE="${TMP_DIR}/${ope}.${TODAY}.tmp.$$"
	grep "Execute\|Ope=\|=<select\|=<insert\|=<update\|=<delete" $LOG | grep "`echo "${ope}" | sed -e "s/_/ /g"`" > ${TMP_OPE_FILE}

	UpCom="";DownCom="";OpeId=""
	TMP_DATA_FILE="";TIME=""
	upstatus=0;dwstatus=0
	TMP_DATA_FILE_SQL="${TMP_DIR}/consql_${ope}.tmp.$$"
	#--------------------------------------------------
	# 電文抽出処理
	#--------------------------------------------------
	while IFS= read LINE
	do

		# 上り-共通ヘッダ
		if echo "${LINE}" | grep "上り共通ヘッダ部" > /dev/null 2>&1
		then
			upstatus=0;dwstatus=0
			UpCom=`echo "${LINE}" | sed -e "s/.*上り共通ヘッダ部=//"`
			upstatus=1
			TIME=$(echo "${LINE}"| cut -b1-19 | sed -e "s/\//-/g" -e "s/ /-/g" -e "s/://g")
			DATA_FILE="$(echo "${TIME}")_$(echo "${UpCom}" | cut -b1-5)_$(echo "${UpCom}" | cut -b6-6)_$(echo "${UpCom}" | cut -b7-13)_sql.dat"
#			TMP_DATA_FILE_SQL="${TMP_DIR}/${DATA_FILE}.$$"

			echo "${LINE}" >> $TMP_DATA_FILE_SQL
 			continue
		fi

		# 上り-オンライン
		# 上り-CSS
		if echo "${LINE}" | grep "上りオンライン業務固有部\|上りCSS業務固有部" > /dev/null 2>&1
		then
#			upstatus=0
			echo "${LINE}" >> $TMP_DATA_FILE_SQL
			continue
		fi

		#--------------------------------------
		# SQL抽出
		#--------------------------------------
		if [ $upstatus -eq 1 ];then
			echo "${LINE}" | awk '$9 ~ /=<select|=<insert|=<update|=<delete/{print $0}' >> $TMP_DATA_FILE_SQL
		fi

		# 下り-共通ヘッダ
		if echo "${LINE}" | grep "下り共通ヘッダ部" > /dev/null 2>&1
		then
			DownCom=`echo "${LINE}" | sed -e "s/.*下り共通ヘッダ部=//"`
			dwstatus=1
			echo "${LINE}" >> $TMP_DATA_FILE_SQL
			continue
		fi

		# 下り-エラー制御
		if echo "${LINE}" | grep "エラー制御部" > /dev/null 2>&1
		then
			echo "${LINE}" >> $TMP_DATA_FILE_SQL
			continue
		fi

		# 下り-オンライン
		# 下り-CSS
		if echo "${LINE}" | grep "下りオンライン業務固有部\|下りCSS業務固有部" > /dev/null 2>&1
		then
#			dwstatus=0
			echo "${LINE}" >> $TMP_DATA_FILE_SQL
			continue
		fi

		if [ $dwstatus -eq 1 ];then
			# 画面ID_電文ID取得
			if echo "${LINE}" | tr -d ${LINE_FEED_CD} | grep "Ope=.*終了$" > /dev/null 2>&1
			then
				echo "${LINE}" >> $TMP_DATA_FILE_SQL
			fi
		fi

		if [[ $upstatus -eq 1 && $dwstatus -eq 1 ]];then
			cp -p ${TMP_DATA_FILE_SQL} ${LOG_DATA_DIR}/${DATA_FILE}
			rm -f ${TMP_DATA_FILE_SQL}
			
			upstatus=0;dwstatus=0
		fi
	done < ${TMP_OPE_FILE}
	
	# tempファイル削除
	rm -rf ${TMP_OPE_FILE}
done


# tempファイル削除
rm -rf ${OPE_LIST}

exit 0

