@echo off
rem ###########################################
set DBuser=root
set DBpass=
set DBname=au_server_gs
set DBhost=localhost
rem ###########################################

if  "%DBuser%"=="" goto Err
if  "%DBpass%"=="" goto Err
if  "%DBname%"=="" goto Err
if  "%DBhost%"=="" goto Err

set /p command=Do you want upgrade droplist(y/N) ?:
if /i %command%==y goto Upgrade_droplist
goto Break_install

:Upgrade_droplist
echo Clearing droplist...
mysql -p%DBpass% -u %DBuser% -D %DBname% -h %DBhost% --execute="delete from droplist"
if ERRORLEVEL 1 goto Err

for /R %%i in (drops\*.sql) DO (
	echo Loading %%i
    mysql -p%DBpass% -u %DBuser% -D %DBname% -h %DBhost% < %%i
	if ERRORLEVEL 1 goto Err
) 
echo Done!
pause
exit
:Err
echo Please check the correct settings of DBuser,DBpass,DBname,DBhost !
pause
:Break_install

