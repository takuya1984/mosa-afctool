#!/bin/bash

. $(dirname $0)/../conf/script.conf

#----------------------------------------------
# function:log_get_list
# param $1:検索条件(key=value)
#          任意かつ複数指定可能
#
# 処理概要:
#    管理テーブルに検索条件を指定して検索し、IDを返す.
#----------------------------------------------
log_get_list() {

	PARAM=$@
	TMP_AWK=getloglist.awk.tmp.$$
	echo $PARAM | awk '
{
    for(i=1;i<=NF;i++) {
        printf("%s",$i);
        if(i!=NF) printf(" ");
    }
}' > $TMP_AWK
	TODAY=`date +"%Y%m%d%H%M%S"`
	TMP_FILE=${TMP_DIR}/getloglist.tmp.$$

	# 検索APIをcall
	java -jar ${JAVA_NORIN_JAR} GetLogList $(cat $TMP_AWK) > $TMP_FILE

	# 検索結果の件数
	line=$(cat $TMP_FILE | wc -l)
	line=$(expr $line - 1)
	# 結果を出力
	cat $TMP_FILE

	rm -f $TMP_FILE $TMP_AWK
	return $line

}

#----------------------------------------------
# function:log_get_data
# param $1:ID
#          複数指定可能
#
# 処理概要:
#    管理テーブルにIDを指定して検索し、結果を返す.
#----------------------------------------------
log_get_data() {

	PARAM=$@
	TMP_AWK=getloglist.awk.tmp.$$
	echo $PARAM | awk '
{
    for(i=1;i<=NF;i++) {
        printf("%s",$i);
        if(i!=NF) printf(" ");
    }
}' > $TMP_AWK
	TODAY=`date +"%Y%m%d%H%M%S"`
	TMP_FILE=${TMP_DIR}/getloglist.tmp.$$

	# 検索APIをcall
	java -jar ${JAVA_NORIN_JAR} GetLogData $(cat $TMP_AWK) > $TMP_FILE

	# 検索結果の件数
	line=$(cat $TMP_FILE | wc -l)
	line=$(expr $line - 2)
	# 結果を出力
	cat $TMP_FILE

	rm -f $TMP_FILE $TMP_AWK
	return $line

}
