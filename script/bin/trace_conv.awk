{
	linenum[NR] = ($1 + 0);
	item_6[NR] = $6;
}
substr($2,1,1)=="*"{
	do {
		# "*"の場合、直前wordを出力
		linenum[NR-1] += 5;
		print item_6[NR-1],item_6[NR-1],item_6[NR-1],item_6[NR-1],item_6[NR-1]
	} while ((linenum[NR-1] + 5) != linenum[NR])
}
{
	if (substr($2,1,1)=="*")
		item_2 = substr($2,2,12);
	else
		item_2 = $2;
	print item_2,$3,$4,$5,$6
}

