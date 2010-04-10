#!/bin/sh

############################################
DBuser=root
DBpass=
DBname=au_server_gs
DBhost=localhost
###########################################

function show_err(){
  echo Please check the correct settings of DBuser,DBpass,DBname,DBhost !
  exit
}

clear

if [ "$DBuser" = "" ] || [ "$DBpass" = "" ] || [ "$DBname" = "" ] || [ "$DBhost" = "" ]; then 
    show_err
fi    

echo -n "Do you want upgrade droplist ?(y/N)"
read COMMAND
if [ "$COMMAND" != "y" ] && [ "$COMMAND" != "Y"  ]; then 
 exit
fi

echo Clearing droplist...
mysql -u ${DBuser} -p${DBpass} -D ${DBname} -h ${DBhost}  -e 'delete from droplist';

if [ $? -ne 0 ]; then 
 show_err
 exit
fi

FILES=`ls ./drops/*.sql`

for FILE in $FILES; do 
   echo Loading $FILE
   mysql -u ${DBuser} -p${DBpass} -D ${DBname} -h ${DBhost} < $FILE
   if [ $? -ne 0 ]; then 
        show_err
	exit
   fi
done   

echo Done!                                  

