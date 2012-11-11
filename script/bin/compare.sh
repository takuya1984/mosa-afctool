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


	if [ $# -eq 2 ];then
		idold=$1
		idnew=$2

		java -Dfile.encoding=utf-8 -jar ${JAVA_NORIN_JAR} CompareData $idold $idnew
		RC=$?
	elif [ $# -eq 3 ];then
		masterid=$1
		idold=$2
		idnew=$3
		
		java -Dfile.encoding=utf-8 -jar ${JAVA_NORIN_JAR} CompareData $masterid $idold $idnew
		RC=$?
	else
		echo "Usage : <command> masterid ID-srcfilename ID-newfilename"
		return -1
	fi

	#---------------------------------------------
	# 比較処理実行
	#---------------------------------------------
	
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
			echo "ログの組み合わせが不正です."
			return $RC
			;;
		*)
			echo "error:exception"
			return -1
	esac

	return 0

}
