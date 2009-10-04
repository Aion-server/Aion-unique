@echo off

set antpath=".\apache-ant\bin"



Rem CONFIG THIS PATHS:
set loginserverpath="C:\Aion\Aion-Unique\trunk\AE-go_LoginServer"
set gameserverpath="C:\Aion\Aion-Unique\trunk\AE-go_GameServer"
set commonspath="C:\Aion\Aion-Unique\trunk\AE-go_Commons"
set datapackpath="C:\Aion\Aion-Unique\trunk\AE-go_DataPack"
set trunkpath="C:\Aion\Aion-Unique\trunk\"

set ANT_BATCH_PAUSE=on

:menu
cls
echo.
echo         ###########################################################
echo                  Aion-Unique Server Emulator SVN Manager 
echo         ###########################################################
echo.
echo Your Options:
echo - c  = checkout source
echo - u  = update source
echo - 1  = compile Login Server
echo - 2  = compile Game Server
echo - 3  = compile Commons
echo - 4  = compile Datapack
echo - 5  = clean   Login Server
echo - 5  = clean   Game Server
echo - 7  = clean   Commons
echo - 8  = clean   Datapack
echo - q  = quit
echo.

:askfirst
set promptfirst=x
set /p promptfirst=Please make a Choice: 
if /i %promptfirst%==c goto checkout
if /i %promptfirst%==u goto update
if /i %promptfirst%==1 goto comploginserver
if /i %promptfirst%==2 goto compgameserver
if /i %promptfirst%==3 goto compcommons
if /i %promptfirst%==3 goto compdatapack
if /i %promptfirst%==5 goto cleanloginserver
if /i %promptfirst%==6 goto cleangameserver
if /i %promptfirst%==7 goto cleancommons
if /i %promptfirst%==8 goto cleandatapack
if /i %promptfirst%==q goto end
goto askfirst

:checkout
cd aion-emu
svn co http://my-svn.assembla.com/svn/ae-go/trunk/
pause
goto menu


:update 
cd %trunkpath%
svn update
pause
goto menu

:comploginserver
cd %loginserverpath%
%antpath%/ant
pause
goto menu

:cleanloginserver
cd %loginserverpath%
%antpath%/ant clean
pause
goto menu

:compgameserver
cd %gameserverpath%
%antpath%/ant
pause
goto menu

:cleangameserver
cd %gameserverpath%
%antpath%/ant clean
pause
goto menu

:cleancommons
cd %loginserverpath%
%antpath%/ant clean
pause
goto menu

:compcommons
cd %gameserverpath%
%antpath%/ant
pause
goto menu

:compdatapack
cd %datapackpath%
%antpath%/ant
pause
goto menu

:cleandatapack
cd %datapackpath%
%antpath%/ant clean
pause
goto menu

:end
