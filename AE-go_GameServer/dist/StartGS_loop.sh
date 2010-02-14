#!/bin/bash

err=1
until [ $err == 0 ];
do
	[ -d log/ ] || mkdir log/
	[ -f log/console.log ] && mv log/console.log "log/console/`date +%Y-%m-%d_%H-%M-%S`_console.log"
	java -Xms128m -Xmx1024m -ea -Xbootclasspath/p:./libs/jsr166.jar -javaagent:libs/ae_commons.jar -cp ./libs/*:ae_gameserver.jar com.aionemu.gameserver.GameServer > log/console.log 2>&1
	err=$?
	sleep 10
done