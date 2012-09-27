#!/bin/bash
#-----------------------------------------------------------------------
# name   : compare.sh
#
# 処理概要 : ログ比較処理の関数を定義
#-----------------------------------------------------------------------

. $(dirname $0)/../conf/script.conf


#----------------------------------------------
# function:compare
#
# param $1:ID-old 比較元のログID
#       $2:ID-new 比較先のログID
#
# return : 0 - normal
#        : 0以外 - error
#
# 処理概要:
#    ログ比較のラッパーfunciton.
#    指定されたIDのログファイルの比較を行う.
#----------------------------------------------
compare_log() {

	MODE=$1
	TODAY=`date +"%Y%m%d%H%M%S"`

	if [ $# -ne 2 ]
	then
		echo "Usage : <command> ID-old ID-new"
		return -1
	fi

	idold=$1
	idnew=$2
	#---------------------------------------------
	# 比較処理実行
	#---------------------------------------------
	java -Dfile.encoding=utf-8 -jar ${JAVA_NORIN_JAR} CompareData $idold $idnew
	RC=$?
	
	#---------------------------------------------
	# 結果コードの処理
	#---------------------------------------------
	case "$RC" in
		0)
#			echo "success"
			;;
		1)
			# Clientログ登録
			${BASEDIR}/bin/regist_client.sh
			mv ${CLIENT_LOG_DIR}/* ${CLIENT_LOG_DIR_REGIST}/
			;;
		
		2)
			# ログファイルが取得できなかった場合
			echo "指定されたIDが取得できませんでした"
			return $RC
			;;
		3)
			# ログファイルが取得できなかった場合
			echo "ログファイルが取得できませんでした"
			return $RC
			;;
		*)
			echo "Usage : conv.sh [1-10]"
			return -1
	esac

	return 0

}
