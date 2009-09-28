#!/bin/bash

# Catch kill signals
trap finish 1 2 15


# Select language

langselect()
{
clear
echo "Welcome to Setup Aion-Unique"
echo ""
echo ""
echo ""
echo " 0)  English"
echo " 1)  Spanish"
echo ""
echo -ne "Please select the language in which you want to display this application: "
read langoption
	case "$langoption" in
		"0") CMDLoadLang;;
		"1") CMDLoadLang;;
		*)       langselect;;
	esac
}

CMDLoadLang()
{
if [ "$langoption" = "0" ]; then
. ./lang/enGB.sh
fi

if [ "$langoption" = "1" ]; then
. ./lang/esES.sh
fi
}

DBRuteSet()
{
	MYSQLDUMPPATH=`which mysqldump 2>/dev/null`
	MYSQLPATH=`which mysql 2>/dev/null`
	if [ $? -ne 0 ]; then
		while :
		do
			clear
			echo -ne "\n$LANG_MySQLBinPath: "
			read MYSQLBINPATH
			if [ -e "$MYSQLBINPATH" ] && [ -d "$MYSQLBINPATH" ] && [ -e "$MYSQLBINPATH/mysqldump" ] && [ -e "$MYSQLBINPATH/mysql" ]; then
				MYSQLDUMPPATH="$MYSQLBINPATH/mysqldump"
				MYSQLPATH="$MYSQLBINPATH/mysql"
				break
			else
				echo "$LANG_InvalidData."
				exit 1
			fi
		done
	fi

}

DBSETING()
{
	clear
	echo -ne "\n$LANG_LSHost ($LANG_Default localhost): "
	read LSDBHOST
	if [ -z "$LSDBHOST" ]; then
		LSDBHOST="localhost"
	fi
	echo -ne "\n$LANG_LSDB ($LANG_Default au_server): "
	read LSDB
	if [ -z "$LSDB" ]; then
		LSDB="au_server"
	fi
	echo -ne "\n$LANG_LSUSER ($LANG_Default root): "
	read LSUSER
	if [ -z "$LSUSER" ]; then
		LSUSER="root"
	fi
	echo -ne "\n$LANG_LSPASS1 $LSUSER$LANG_LSPASS2 :"
	stty -echo
	read LSPASS
	stty echo
	echo ""

	# GameServer
	echo -ne "\n$LANG_GSHost ($LANG_Default $LSDBHOST): "
	read GSDBHOST
	if [ -z "$GSDBHOST" ]; then
		GSDBHOST="$LSDBHOST"
	fi
	echo -ne "\n$LANG_GSDB ($LANG_Default $LSDB): "
	read GSDB
	if [ -z "$GSDB" ]; then
		GSDB="$LSDB"
	fi
	echo -ne "\n$LANG_GSUSER ($LANG_Default $LSUSER): "
	read GSUSER
	if [ -z "$GSUSER" ]; then
		GSUSER="$LSUSER"
	fi
	echo -ne "\n$LANG_GSPASS1 $GSUSER's $LANG_LSPASS2 : "
	stty -echo
	read GSPASS
	stty echo
	echo ""
}

mainmenu()
{
clear
echo "Press key Enter to continue. . ."
	stty -echo
	read PAUSA
	stty echo
}

# End of the script
finish()
{
	echo ""
	echo "Script execution finished."
	exit 0
}

# Clear console
clear

# Call langselect function
langselect

# Call DBRuteSet function
DBRuteSet

# Call DBSETING function
DBSETING

# Call mainmenu function
mainmenu
