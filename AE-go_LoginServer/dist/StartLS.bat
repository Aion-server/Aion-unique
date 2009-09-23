@echo off
title Aion-Unique Login Server Console
:start
echo Starting Aion-Unique Login Server.
echo.
REM -------------------------------------
REM Default parameters for a basic server.
java -Xms8m -Xmx32m -ea -Xbootclasspath/p:./libs/jsr166.jar -javaagent:libs/ae_commons.jar -cp ./libs/*;ae_login.jar com.aionemu.loginserver.LoginServer
REM
REM -------------------------------------

SET CLASSPATH=%OLDCLASSPATH%


if ERRORLEVEL 1 goto error
goto end
:error
echo.
echo Login Server Terminated Abnormaly, Please Verify Your Files.
echo.
:end
echo.
echo Login Server Terminated.
echo.
pause