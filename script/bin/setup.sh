#!/bin/bash

basedir=/Users/kohgami/tmp/test

logdirs=(log log_regist log_target log_target_utf8)
dirs=(01_client	02_web 03_otx-css 04_otx-onl 05_apinfo 06_aphost 07_trace 08_dbio 09_otx-sql 10_ap-sql)

cd $basedir
for logdir in ${logdirs[@]}
do
	for dir in ${dirs[@]}
	do
		mkdir -p ${basedir}/${logdir}/${dir}
	done
done
