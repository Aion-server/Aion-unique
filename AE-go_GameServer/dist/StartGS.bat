@echo off
title Aion-Unique GameServer Console
:start
echo Starting Aion-Unique Game Server.
echo.
REM -------------------------------------
REM Default parameters for a basic server.
java -Xms128m -Xmx1024m -ea -Xbootclasspath/p:./libs/jsr166.jar -javaagent:libs/ae_commons.jar -cp ./libs/*;ae_gameserver.jar com.aionemu.gameserver.GameServer
REM
REM -------------------------------------

SET CLASSPATH=%OLDCLASSPATH%


if ERRORLEVEL 1 goto error
goto end
:error
echo.
echo GameServer Terminated Abnormaly, Please Verify Your Files.
echo.
:end
echo.
echo GameServer Terminated.
echo.
pause