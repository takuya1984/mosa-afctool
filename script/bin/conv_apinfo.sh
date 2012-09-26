#!/bin/bash
#-----------------------------------------------------------------------
# name   : conv_apinfo.sh
# param  : $1 - 抽出対象ログファイル
# return : 0 - normal
#        : 1 - error
#
# 処理概要 : 指定されたログファイルから電文ログ形式ファイルを生成する
#-----------------------------------------------------------------------

. $(dirname $0)/../conf/script.conf

TODAY=`date +"%Y%m%d%H%M%S"`
logfile=$1

status=0    # 0:idle 1:prossecing 
updw_flg=0  # 1:up 2:dw
filename=""

while IFS= read LINE
do

	#--------------------------------
	# 電文単位に抽出を行う.
	#--------------------------------
	if [ $status -eq 0 ];then
		status=1

		if echo "${LINE}" | grep "Execute:上り" > /dev/null 2>&1
		then
			updw_flg=1
		else
			updw_flg=2
		fi
#			echo "${LINE}" | awk '$4 ~ /受信/{print $0}'

		# ファイル名
		# yyyymmddhhmmss_CL番号_オペレーション区分_電文ID_上り下り区分.dat
		COM=$(echo "${LINE}" | awk '{print $6}')
		filename="${CLIENT_LOG_DIR}/$(echo "${LINE}"| cut -b1-19 | sed -e "s/\//-/g" -e "s/ /_/g" -e "s/://g")_$(echo "${COM}" | cut -b1-5)_$(echo "${COM}" | cut -b6-6)_$(echo "${COM}" | cut -b7-13)_${updw_flg}.dat"
	fi

	echo "${LINE}" >> $filename

	#--------------------------------
	# 電文の最終処理行チェック
	#--------------------------------
	if echo "${LINE}" | grep " Css " > /dev/null 2>&1
	then
		status=0
	fi

done < $logfile
