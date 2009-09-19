#!/bin/sh
java -Xms8m -Xmx32m -ea -Xbootclasspath/p:./libs/jsr166.jar -javaagent:libs/ae_commons.jar -cp ./libs/*:ae_login.jar com.aionemu.loginserver.LoginServer
