@echo off
start javaw -Xms512m -Xmx512m -cp ./libs/*;packetsamurai.jar com.aionemu.packetsamurai.PacketSamurai
exit