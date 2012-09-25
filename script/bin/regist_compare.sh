#!/bin/bash
#-----------------------------------------------------------------------
# name   : regist_compare.sh
#
# 処理概要 : 比較除外項目マスタへのfunctionを定義.
#-----------------------------------------------------------------------

. $(dirname $0)/../conf/script.conf


#----------------------------------------------
# function:regist_compare
#
# param $1:ID(省略可能)
#       $2:MASTER_ID
#       $3:KEY
#       $4:ITEM_NAME
#       $5:UP_DOWN_CD
#
# return : 0 - normal
#        : 0以外 - error
#
# 処理概要:
#    比較除外項目マスタにデータを登録する.
#    IDを指定しなければIDを自動採番し、新規登録する.
#    IDを指定した場合、該当するレコードが存在しなければInsert
#    該当するレコードが存在すればUpdateを行う.
#----------------------------------------------
regist_compare() {

#	TODAY=`date +"%Y%m%d%H%M%S"`

	# アーギュメントチェック
	if [[ $# -ne 4 && $# -ne 5 ]];then
		echo "Usage : ID, MASTER_ID, KEY, ITEM_NAME, UP_DOWN_CD"
		return -1
	fi

	if [ $# -eq 5 ];then
		ID=$1
		shift 1
	fi

	MASTER_ID=$1
	KEY=$2
	ITEM_NAME=$3
	UP_DOWN_CD=$4

	# -------------------------------
	# 比較除外項目マスタDBに登録
	# -------------------------------
	java -jar ${JAVA_NORIN_JAR} RegistCompareData "id=${ID}" "master_id=${MASTER_ID}" "key=${KEY}" "item_name=${ITEM_NAME}" "up_down_cd=${UP_DOWN_CD}"
	RC=$?
	return $RC

}
