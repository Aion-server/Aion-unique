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
echo.
set langoption=0
set /p langoption=Please select the language in which you want to display this application: 
if /i %langoption%==0 goto CMDLoadLang
if /i %langoption%==1 goto CMDLoadLang
goto langselect

REM ############################################
REM ## We call the language file              ##
REM ############################################

:CMDLoadLang
if /i %langoption%==0 set cmdLANG=enGB
if /i %langoption%==1 set cmdLANG=esES
call lang\%cmdLANG%.bat

REM ############################################

:DBRuteSet
cls
set mysqlBinPath=C:\%ProgramFiles%\MySQL\MySQL Server 5.1\bin
echo %LANG_MySQLBinPath%
set /p mysqlBinPath=%LANG_Rute%: 

IF EXIST %mysqlBinPath%\mysql.exe GOTO DBSETING
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
set LSDB=au_server
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
set GSDB=%LSDB%
set /p GSDB=%LANG_GSDB% (%LANG_Default% %GSDB%): 
echo.
set GSUSER=%LSUSER%
set /p GSUSER=%LANG_GSUSER% (%LANG_Default% %LSUSER%): 
echo.
set /p GSPASS=%LANG_GSPASS1% %GSUSER%%LANG_LSPASS2%: 
echo.

:mainmenu
cls
pause

