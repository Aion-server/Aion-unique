@echo off
title Aion-Unique GameServer Console

REM Start...
:start
echo Starting Aion-Unique Game Server.
echo.
REM -------------------------------------
REM Default parameters for a basic server.
java -Xms512m -Xmx1024m -ea -Xbootclasspath/p:./libs/jsr166.jar -javaagent:libs/ae_commons.jar -cp ./libs/*;ae_gameserver.jar com.aionemu.gameserver.GameServer
REM -------------------------------------

SET CLASSPATH=%OLDCLASSPATH%

if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
if ERRORLEVEL 0 goto end

REM Restart...
:restart
echo.
echo Administrator Restart ...
echo.
goto start

REM Error...
:error
echo.
echo Server is terminated abnormaly ...
echo.
goto end

REM End...
:end
echo.
echo Server is terminated ...
echo.
pause
