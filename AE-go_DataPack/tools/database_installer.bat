@echo off
title Database Installer

REM ############################################
REM ## Select language                        ##
REM ############################################

:langselect
cls
echo Welcome to Setup Aion-Unique
echo.
echo.
echo.
echo  0)  English
echo  1)  Spanish
echo  2)  Russian
echo.
set langoption=0
set /p langoption=Please select the language in which you want to display this application: 
if /i %langoption%==0 goto CMDLoadLang
if /i %langoption%==1 goto CMDLoadLang
if /i %langoption%==2 goto CMDLoadLang
goto langselect

REM ############################################
REM ## We call the language file              ##
REM ############################################

:CMDLoadLang
if /i %langoption%==0 set cmdLANG=enGB
if /i %langoption%==1 set cmdLANG=esES
if /i %langoption%==2 set cmdLANG=ruRU
call lang\%cmdLANG%.bat

REM ############################################

:DBRuteSet
cls
set mysqlBinPath=C:\%ProgramFiles%\MySQL\MySQL Server 5.1\bin
echo %LANG_MySQLBinPath%
set /p mysqlBinPath=%LANG_Rute%: 

IF EXIST "%mysqlBinPath%\mysql.exe" GOTO DBSETING
echo.
echo.
echo %LANG_InvalidData%
pause
goto DBRuteSet

:DBSETING
cls
echo.
set LSDBHOST=localhost
set /p LSDBHOST=%LANG_LSHost% (%LANG_Default% %LSDBHOST%): 
echo.
set LSDB=au_server_ls
set /p LSDB=%LANG_LSDB% (%LANG_Default% %LSDB%): 
echo.
set LSUSER=root
set /p LSUSER=%LANG_LSUSER% (%LANG_Default% %LSUSER%): 
echo.
set /p LSPASS=%LANG_LSPASS1% %LSUSER%%LANG_LSPASS2%: 
echo.
set GSDBHOST=%LSDBHOST%
set /p GSDBHOST=%LANG_GSHost% (%LANG_Default% %GSDBHOST%): 
echo.
set GSDB=au_server_gs
set /p GSDB=%LANG_GSDB% (%LANG_Default% %GSDB%): 
echo.
set GSUSER=%LSUSER%
set /p GSUSER=%LANG_GSUSER% (%LANG_Default% %LSUSER%): 
echo.
set /p GSPASS=%LANG_GSPASS1% %GSUSER%%LANG_LSPASS2%: 
echo.

set mysqldumpPath="%mysqlBinPath%\mysqldump"
set mysqlPath="%mysqlBinPath%\mysql"

set MYL=%mysqlPath% -h %LSDBHOST% -u %LSUSER% --password=%LSPASS% -D %LSDB%
set MYG=%mysqlPath% -h %GSDBHOST% -u %GSUSER% --password=%GSPASS% -D %GSDB%

:mainmenu
cls
echo #################################################
echo #        Aiun-Unique Database Installer         #
echo #################################################
echo.
echo %LANG_MAINMENU_TITLE%
echo [b]  %LANG_Database_backup%
echo [r]  %LANG_Insert_backups%
echo [f]  %LANG_Full_install%
echo [q]  %LANG_QuitScript%          
set /p mmopt=%LANG_Choice%: 
if /i %mmopt%==b goto backup_db
if /i %mmopt%==r goto insert_backup
if /i %mmopt%==f goto full_install
if /i %mmopt%==q goto finish
goto mainmen

REM Make a backup of the LS and GS database
:backup_db
	echo #################################################
	echo #                Database Backup                #
	echo #################################################
	echo.
	echo LoginServer backup
	%MYSQLDUMPPATH% --add-drop-table -h %LSDBHOST% -u %LSUSER% --password=%LSPASS% %LSDB% > loginserver_backup.sql
	echo GameServer backup
	%MYSQLDUMPPATH% --add-drop-table -h %GSDBHOST% -u %GSUSER% --password=%GSPASS% %GSDB% > gameserver_backup.sql
goto finish

REM Insert backups
:insert_backup
	echo #################################################
	echo #                Database Backup                #
	echo #################################################
	echo.
	echo %LANG_BACKUP_MSG1%
	echo %LANG_BACKUP_MSG2%
	set /p LS_BACKUP=LoginServer backup: 
	set /p GS_BACKUP=GameServer backup: 
	echo Inserting Backups
	%MYL% < %LS_BACKUP%
	%MYG% < %GS_BACKUP%
	echo %LANG_BACKUP_MSG3%
goto finish

REM  Full installation (erase and insert all tables)
:full_install
echo #################################################
echo #          Full Database Installation           #
echo #################################################
echo.
echo LoginServer database
%MYL% < ../sql/loginserver/account_data.sql
%MYL% < ../sql/loginserver/account_time.sql
%MYL% < ../sql/loginserver/banned_ip.sql
%MYL% < ../sql/loginserver/gameservers.sql
echo GameServer database
%MYG% < ../sql/gameserver/players.sql
%MYG% < ../sql/gameserver/player_appearance.sql
%MYG% < ../sql/gameserver/player_macrosses.sql
%MYG% < ../sql/gameserver/player_skills.sql
%MYG% < ../sql/gameserver/server_variables.sql
%MYG%< ../sql/gameserver/blocks.sql
%MYG%< ../sql/gameserver/droplist.sql
%MYG%< ../sql/gameserver/friends.sql
%MYG% < ../sql/gameserver/inventory.sql
%MYG% < ../sql/gameserver/item_list.sql
%MYG% < ../sql/gameserver/skill_trees.sql

REM End of the script
:finish
echo.
echo Script execution finished.
