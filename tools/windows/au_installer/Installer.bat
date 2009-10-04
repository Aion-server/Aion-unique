@echo off

set user=root
set pass=
set LSDBname=au_server_ls
set GSDBname=au_server_gs
set AEDBname=aion_emu
set DBHost=localhost


REM ##############################################
REM ## AION UNIQUE Server Installer             ##
REM ##############################################
REM ## Created - by ErgoZ - www.ergoz.ru        ##
REM ##############################################
REM Copyright (C) 2009 ErgoZ Development Team & Aion Unique Team
REM This program is free software; you can redistribute it and/or modify 
REM it under the terms of the GNU General Public License as published by 
REM the Free Software Foundation; either version 2 of the License, or (at
REM your option) any later version.
REM
REM This program is distributed in the hope that it will be useful, but 
REM WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
REM or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
REM for more details.
REM
REM You should have received a copy of the GNU General Public License along 
REM with this program; if not, write to the Free Software Foundation, Inc., 
REM 675 Mass Ave, Cambridge, MA 02139, USA. Or contact the ErgoZ Development
REM Team at http://www.ergoz.ru, http://forum.ergoz.ru or
REM E-mail: dev@ergoz.ru

TITLE Aion Unique and ErgoZ Developers Team - installer
REM =========================================
goto answer%ERRORLEVEL%
:answerTrue
set fastend=yes
goto upgrade_db
:answer0
set fastend=no

REM ========================================= :create databases

mysql.exe -h %DBHost% -u %user% --password=%pass% --execute="CREATE DATABASE IF NOT EXISTS %LSDBname%"
mysql.exe -h %DBHost% -u %user% --password=%pass% --execute="CREATE DATABASE IF NOT EXISTS %GSDBname%"
mysql.exe -h %DBHost% -u %user% --password=%pass% --execute="CREATE DATABASE IF NOT EXISTS %AEDBname%"
if not exist backup (mkdir backup)

REM ========================================= :Main_menu
:Main_menu
cls
echo. Install DataBase.
echo.
echo.---  Main Menu  ---
echo.
echo.
echo.(1) = Install Login Server
echo.
echo.(2) = Install Game Server And Updates
echo.
echo.(3) = Backup DB
echo.
echo.(4) = Restore DB
echo.
echo.(q) = Quit
echo.
set button=x
set /p button=What do you want ?:
if /i %button%==1 goto Install_Login_menu
if /i %button%==2 goto Install_Game_menu
if /i %button%==3 goto Backup_menu
if /i %button%==4 goto Restore_menu
if /i %button%==q goto Exit
goto Main_menu

REM ========================================= :Install_Login_menu
:Install_Login_menu
cls
echo.Install DB Aion Unique and ErgoZ Developers Team.
echo.
echo.--- Install Login Server.. ---
echo.
echo.
echo.(i) = Install Login Server. Account_data, account_time, 
echo.gameservers, banned_ip,  will be deleted!!!
echo.
echo.(m) = Main menu
echo.
echo.(q) = Quit
echo.
set button=x
set /p button=Your choice ?:
if /i %button%==i goto Install_Login 
if /i %button%==m goto Main_menu
if /i %button%==q goto Exit
goto Install_Login_menu

REM ========================================= :Install_Game_menu
:Install_Game_menu
cls
echo.Install Aion Unique and ErgoZ Developers Team DB
echo.
echo.Install Game Server and Updates
echo.
echo.
echo.(i) = Install Game Server. All Game Server Database will be deleted !!!
echo.
echo.(m) = Main menu
echo.
echo.(q) = Quit
echo.
set button=x
set /p button=Your choice ?:
if /i %button%==i goto Install_Game 
if /i %button%==m goto Main_menu
if /i %button%==q goto Exit
goto Install_Game_menu

REM ========================================= :Backup_menu
:Backup_menu
cls
echo.Install Aion Unique and ErgoZ Developers Team DB
echo.
echo.--- Backup Menu ---
echo.
echo.
echo.(1) = Full backup
echo.
echo.(m) = Main menu
echo.
echo.(q) = Quit
echo.
set button=x
set /p button=Select backup type ?:
if /i %button%==1 goto full_backup
if /i %button%==m goto Main_menu
if /i %button%==q goto Exit
goto Backup_menu

REM ========================================= :Restore_menu
:Restore_menu
cls
echo.List all files in dir "/backup" !
echo.
dir backup /B /P
echo.
echo.Install Aion Unique and ErgoZ Developers Team DB
echo.
echo.--- Restore Menu ---
echo.
echo.
echo.Enter a full filename do you want to restore to the database !
echo.
echo.(m) = Main menu
echo.
echo.(q) = Quit
echo.
set filename=x
set /p filename=Enter filename ?:
if /i %filename%==m goto Main_menu
if /i %filename%==q goto Exit
if /i %filename%==%filename% goto restore_DB
goto Restore_menu


REM ========================================= :Install_Login 
:Install_Login
set ctime=%TIME:~0,2%
if "%ctime:~0,1%" == " " (set ctime=0%ctime:~1,1%)
set ctime=%ctime%'%TIME:~3,2%'%TIME:~6,2%
echo.
echo Making a full backup into %LSDBname%-%DATE%-%ctime%_backup_full.sql
echo.
mysqldump.exe  --add-drop-table -h %DBHost% -u %user% --password=%pass% %LSDBname% > backup/%LSDBname%-%DATE%-%ctime%_backup_full.sql
echo.
echo.[Drop Table]: account_data
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %LSDBname% --execute="SET FOREIGN_KEY_CHECKS=0; DROP TABLE IF EXISTS account_data;"
echo.[Drop Table]: account_time
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %LSDBname% --execute="SET FOREIGN_KEY_CHECKS=0; DROP TABLE IF EXISTS account_time;"
echo.[Drop Table]: banned_ip
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %LSDBname% --execute="SET FOREIGN_KEY_CHECKS=0; DROP TABLE IF EXISTS banned_ip;"
echo.[Drop Table]: gameservers
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %LSDBname% --execute="SET FOREIGN_KEY_CHECKS=0; DROP TABLE IF EXISTS gameservers;"
echo.
echo.Installing Login Server !!!
echo.
echo.[Installing Table]: account_data.sql
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %LSDBname% < ../sql/loginserver/ls_account_data.sql

echo.[Installing Table]: account_time.sql
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %LSDBname% < ../sql/loginserver/ls_account_time.sql

echo.[Installing Table]: banned_ip.sql
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %LSDBname% < ../sql/loginserver/ls_banned_ip.sql

echo.[Installing Table]: gameservers.sql
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %LSDBname% < ../sql/loginserver/ls_gameservers.sql
echo.
echo.Login Server Installed !!!  Now you are going to main menu.
echo.
pause
goto main_menu

REM ========================================= :Install_Game
:Install_Game
set ctime=%TIME:~0,2%
if "%ctime:~0,1%" == " " (set ctime=0%ctime:~1,1%)
set ctime=%ctime%'%TIME:~3,2%'%TIME:~6,2%
echo.
echo Making a full backup into %GSDBname%-%DATE%-%ctime%_backup_full.sql
echo.
mysqldump.exe  --add-drop-table -h %DBHost% -u %user% --password=%pass% %GSDBname% > backup/%GSDBname%-%DATE%-%ctime%_backup_full.sql
echo.
echo.[Drop Table]: blocks
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% --execute="SET FOREIGN_KEY_CHECKS=0; DROP TABLE IF EXISTS blocks;"
echo.[Drop Table]: droplist
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% --execute="SET FOREIGN_KEY_CHECKS=0; DROP TABLE IF EXISTS droplist;"
echo.[Drop Table]: friends
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% --execute="SET FOREIGN_KEY_CHECKS=0; DROP TABLE IF EXISTS friends;"
echo.[Drop Table]: inventory
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% --execute="SET FOREIGN_KEY_CHECKS=0; DROP TABLE IF EXISTS inventory;"
echo.[Drop Table]: item_list
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% --execute="SET FOREIGN_KEY_CHECKS=0; DROP TABLE IF EXISTS item_list;"
echo.[Drop Table]: player_appearance
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% --execute="SET FOREIGN_KEY_CHECKS=0; DROP TABLE IF EXISTS player_appearance;"
echo.[Drop Table]: player_equipment
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% --execute="SET FOREIGN_KEY_CHECKS=0; DROP TABLE IF EXISTS player_equipment;"
echo.[Drop Table]: player_macrosses
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% --execute="SET FOREIGN_KEY_CHECKS=0; DROP TABLE IF EXISTS player_macrosses;"
echo.[Drop Table]: player_skills
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% --execute="SET FOREIGN_KEY_CHECKS=0; DROP TABLE IF EXISTS player_skills;"
echo.[Drop Table]: players
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% --execute="SET FOREIGN_KEY_CHECKS=0; DROP TABLE IF EXISTS players;"
echo.[Drop Table]: server_variables
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% --execute="SET FOREIGN_KEY_CHECKS=0; DROP TABLE IF EXISTS server_variables;"
echo.[Drop Table]: skill_trees
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% --execute="SET FOREIGN_KEY_CHECKS=0; DROP TABLE IF EXISTS skill_trees;"
echo.
echo.Installing general tables !!!
echo.
echo [Installing Table]: blocks.sql
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% < ../sql/gameserver/gs_blocks.sql
echo [Installing Table]: gs_droplist.sql
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% < ../sql/gameserver/gs_droplist.sql
echo [Installing Table]: friends.sql
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% < ../sql/gameserver/gs_friends.sql
echo [Installing Table]: inventory.sql
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% < ../sql/gameserver/gs_inventory.sql
echo [Installing Table]: gs_item_list.sql
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% < ../sql/gameserver/gs_item_list.sql
echo [Installing Table]: player_appearance.sql
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% < ../sql/gameserver/gs_player_appearance.sql
echo [Installing Table]: gs_player_equipment.sql
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% < ../sql/gameserver/gs_player_equipment.sql
echo [Installing Table]: player_macrosses.sql
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% < ../sql/gameserver/gs_player_macrosses.sql
echo [Installing Table]: player_skills.sql
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% < ../sql/gameserver/gs_player_skills.sql
echo [Installing Table]: players.sql
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% < ../sql/gameserver/gs_players.sql
echo [Installing Table]: server_variables.sql
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% < ../sql/gameserver/gs_server_variables.sql
echo [Installing Table]: skill_trees.sql
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% < ../sql/gameserver/gs_skill_trees.sql
echo.
echo.Game Server Installed !!!  Now you are going to main menu.
echo.
if /I %fastend%==yes goto :EOF
pause
goto Main_menu

REM ========================================= :full_backup
:full_backup
set ctime=%TIME:~0,2%
if "%ctime:~0,1%" == " " (set ctime=0%ctime:~1,1%)
set ctime=%ctime%'%TIME:~3,2%'%TIME:~6,2%
echo.
echo Making a full backup into %LSDBname%-%DATE%-%ctime%_backup_full.sql
echo.
mysqldump.exe  --add-drop-table -h %DBHost% -u %user% --password=%pass% %LSDBname% > backup/%LSDBname%-%DATE%-%ctime%_backup_full.sql
echo.
echo Making a full backup into %GSDBname%-%DATE%-%ctime%_backup_full.sql
echo.
mysqldump.exe  --add-drop-table -h %DBHost% -u %user% --password=%pass% %GSDBname% > backup/%GSDBname%-%DATE%-%ctime%_backup_full.sql
echo.
echo Making a full backup into %AEDBname%-%DATE%-%ctime%_backup_full.sql
echo
mysqldump.exe  --add-drop-table -h %DBHost% -u %user% --password=%pass% %AEDBname% > backup/%AEDBname%-%DATE%-%ctime%_backup_full.sql
pause
goto Main_menu

REM ========================================= :restore_GSDB
:restore_GSDB
if not exist backup/%filename% (
echo.
echo.File not found !
echo.
pause
goto restore_menu
)
cls
echo.
echo.Restore from file  %filename% !
echo.
pause
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %GSDBname% < backup/%filename%
goto Main_menu

REM ========================================= :restore_LSDB
:restore_LSDB
if not exist backup/%filename% (
echo.
echo.File not found !
echo.
pause
goto restore_menu
)
cls
echo.
echo.Restore from file  %filename% !
echo.
pause
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %LSDBname% < backup/%filename%
goto Main_menu

REM ========================================= :end
:Exit
call :colors 03
title Aion Unique installer - Script execution finished
cls
echo.
echo Aion Unique installer version 0.0.0.2 beta
echo (C) 2009-2010 ErgoZ Development Team and Aion Unique Dev Team
echo Aion Unique installer comes with ABSOLUTELY NO WARRANTY;
echo This is free software, and you are welcome to redistribute it
echo under certain conditions; See the file gpl license.txt for further
echo details.
echo.
echo Thanks for using our software.
echo Visit http://www.aionunique.smfnew.com for more info about
echo the AION UNIQUE project.
echo.
pause