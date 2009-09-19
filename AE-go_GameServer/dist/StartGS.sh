#!/bin/sh
java -Xms128m -Xmx1024m -ea -Xbootclasspath/p:./libs/jsr166.jar -javaagent:libs/ae_commons.jar -cp ./libs/*:ae_gameserver.jar com.aionemu.gameserver.GameServer

