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
	if [ -z "$langoption" ]; then
		langoption="0"
	fi

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
	echo -ne "\n$LANG_LSDB ($LANG_Default au_server_ls): "
	read LSDB
	if [ -z "$LSDB" ]; then
		LSDB="au_server_ls"
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
	echo -ne "\n$LANG_GSDB ($LANG_Default au_server_gs): "
	read GSDB
	if [ -z "$GSDB" ]; then
		GSDB="au_server_gs"
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
echo "#################################################"
echo "#        Aiun-Unique Database Installer         #"
echo "#################################################"
echo ""
echo "$LANG_MAINMENU_TITLE"
echo "[b]  $LANG_Database_backup"
echo "[r]  $LANG_Insert_backups"
echo "[f]  $LANG_Full_install"
echo "[q]  $LANG_QuitScript"
echo -ne "$LANG_Choice: "
read mmopt
	case "$mmopt" in
		"b") backup_db; finish;;
		"r") insert_backup; finish;;
		"f") full_install; finish;;
		"q") finish;;
		*)       mainmenu;;
	esac
}

# Make a backup of the LS and GS database
backup_db()
{
	echo "#################################################"
	echo "#                Database Backup                #"
	echo "#################################################"
	echo ""
	echo "LoginServer backup"
	$MYSQLDUMPPATH --add-drop-table -h $LSDBHOST -u $LSUSER --password=$LSPASS $LSDB > loginserver_backup.sql
	echo "GameServer backup"
	$MYSQLDUMPPATH --add-drop-table -h $GSDBHOST -u $GSUSER --password=$GSPASS $GSDB > gameserver_backup.sql
}

# Insert backups
insert_backup()
{
	echo "#################################################"
	echo "#                Database Backup                #"
	echo "#################################################"
	echo ""
	echo "$LANG_BACKUP_MSG1"
	echo "$LANG_BACKUP_MSG2"
	echo "LoginServer backup: "
	read LS_BACKUP
	echo "GameServer backup: "
	read GS_BACKUP
	echo "Inserting Backups"
	$MYL < $LS_BACKUP &> /dev/null
	$MYG < $GS_BACKUP &> /dev/null
	echo "$LANG_BACKUP_MSG3"
}


# Full installation (erase and insert all tables)
full_install()
{
	echo "#################################################"
	echo "#          Full Database Installation           #"
	echo "#################################################"
	echo ""
	echo "LoginServer database"
	$MYL < ../sql/loginserver/account_data.sql &> /dev/null
	$MYL < ../sql/loginserver/account_time.sql &> /dev/null
	$MYL < ../sql/loginserver/banned_ip.sql &> /dev/null
	$MYL < ../sql/loginserver/gameservers.sql &> /dev/null
	echo "GameServer database"
	$MYG < ../sql/gameserver/players.sql &> /dev/null
	$MYG < ../sql/gameserver/player_appearance.sql &> /dev/null
	$MYG < ../sql/gameserver/player_macrosses.sql &> /dev/null
	$MYG < ../sql/gameserver/player_skills.sql &> /dev/null
	$MYG < ../sql/gameserver/server_variables.sql &> /dev/null
	$MYG < ../sql/gameserver/blocks.sql &> /dev/null
	$MYG < ../sql/gameserver/droplist.sql &> /dev/null
	$MYG < ../sql/gameserver/friends.sql &> /dev/null
	$MYG < ../sql/gameserver/inventory.sql &> /dev/null
	$MYG < ../sql/gameserver/item_list.sql &> /dev/null
	$MYG < ../sql/gameserver/skill_trees.sql &> /dev/null
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

# Open MySQL connections
MYL="$MYSQLPATH -h $LSDBHOST -u $LSUSER --password=$LSPASS -D $LSDB"
MYG="$MYSQLPATH -h $GSDBHOST -u $GSUSER --password=$GSPASS -D $GSDB"

# Call mainmenu function
mainmenu
