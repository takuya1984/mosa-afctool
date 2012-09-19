#!/bin/bash
#-----------------------------------------------------------------------
# name   : conv_web.sh
# param  : $1 - 抽出対象ログファイル
# return : 0 - normal
#        : 1 - error
#
# 処理概要 : 指定されたログファイルから電文ログ形式ファイルを生成する
#-----------------------------------------------------------------------

. $(dirname $0)/../conf/script.conf

TODAY=`date +"%Y%m%d%H%M%S"`
logfile=$1
TMPLOG="${TMP_DIR}/conv_web.${TODAY}.tmp.$$"

status=0    # 0:idle 1:prossecing 
updw_flg=0  # 1:up 2:dw
filename=""
COM="",TIME=""

# 対象行のみ抽出
grep " TRACE \|^ *<" "${logfile}" > "${TMPLOG}" 2>&1

while IFS= read LINE
do
	#--------------------------------
	# 電文単位に抽出を行う.
	#--------------------------------
	if [ $status -eq 0 ];then
		echo "${LINE}" | grep " TRACE " > /dev/null 2>&1
		RC=$?
		if [ $RC -ne 0 ];then
			continue
		else
			status=1
			COM="";TIME=""

			if echo "${LINE}" | grep "Outgoing Request Message" > /dev/null 2>&1
			then
				updw_flg=1
			else
				updw_flg=2
			fi
#			echo "${LINE}" | awk '$4 ~ /受信/{print $0}'

			tmpfile="${TMP_DIR}/$(echo "${LINE}"| cut -b1-19 | sed -e "s/\//-/g" -e "s/ /_/g" -e "s/://g")_$(echo "${LINE}" | cut -b21-23)_${updw_flg}.dat.$$"
			TIME=$(echo "${LINE}"| cut -b1-19 | sed -e "s/\//-/g" -e "s/ /_/g" -e "s/://g")
		fi
	fi
	
	echo "${LINE}" >> $tmpfile

	#--------------------------------
	# ヘッダーの内容チェック
	#--------------------------------
	if echo "${LINE}" | grep "<strComUpHeadDt>" > /dev/null 2>&1
	then
		COM=$(echo "${LINE}" | sed -e "s/.*<strComUpHeadDt>//" | sed -e "s/<\/strComUpHeadDt>.*//")
		
	fi

	#--------------------------------
	# Webサーバログかどうかの判定
	# 情報系APor勘定系APログの場合、処理を終了
	# (本番環境であればこの処理は該当しないはず)
	#--------------------------------
	if echo "${LINE}" | grep "<ns1:ResponseMessageList\|<bon:ResponseMessageList\|<faultcode>" > /dev/null 2>&1
	then
		status=0
		rm -f $tmpfile > /dev/null 2>&1
	fi

	#--------------------------------
	# 電文の最終処理行チェック
	#--------------------------------
	if echo "${LINE}" | grep "</env:Envelope>" > /dev/null 2>&1
	then
		status=0
		# ファイルコピー
		cp -p $tmpfile ${WEB_LOG_DIR}/$(echo "${TIME}")_$(echo "${COM}" | cut -b1-5)_$(echo "${COM}" | cut -b6-6)_$(echo "${COM}" | cut -b7-13)_${updw_flg}.dat

		rm -f $tmpfile > /dev/null 2>&1
		COM="";TIME=""
	fi

done < $TMPLOG

# rm -f $TMPLOG

exit 0
