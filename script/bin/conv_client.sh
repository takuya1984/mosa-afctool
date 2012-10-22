#!/bin/bash
#-----------------------------------------------------------------------
# name   : conv_client.sh
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


java -Dfile.encoding=utf-8 -cp ${JAVA_NORIN_JAR} com.jbcc.MQTool.converter.ClientLogConverter "ClientToday_3_20120409.log"


