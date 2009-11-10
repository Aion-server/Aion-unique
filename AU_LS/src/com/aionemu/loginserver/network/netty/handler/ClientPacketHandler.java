package com.aionemu.loginserver.network.netty.handler;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;

import com.aionemu.loginserver.network.netty.packet.AbstractClientPacket;
import com.aionemu.loginserver.network.netty.packet.client.CM_AUTH_GG;
import com.aionemu.loginserver.network.netty.packet.client.CM_LOGIN;
import com.aionemu.loginserver.network.netty.packet.client.CM_PLAY;
import com.aionemu.loginserver.network.netty.packet.client.CM_SERVER_LIST;
import com.aionemu.loginserver.network.netty.packet.client.CM_UPDATE_SESSION;

/**
 * @author ATracer
 */
public class ClientPacketHandler
{
	private static final Logger log = Logger.getLogger(ChannelHandler.class);

	public AbstractClientPacket handle(ChannelBuffer buf, ChannelHandler channelHandler)
	{
		byte opCode = buf.getByte(0);
		AbstractClientPacket clientPacket = null;
		switch(opCode)
		{
			case 0x07:
				clientPacket = new CM_AUTH_GG(buf, channelHandler);
				break;
			case 0x08:
				clientPacket = new CM_UPDATE_SESSION(buf, channelHandler);
				break;
			case 0x0B:
				clientPacket = new CM_LOGIN(buf, channelHandler);
				break;
			case 0x05:
				clientPacket = new CM_SERVER_LIST(buf, channelHandler);
				break;
			case 0x02:
				clientPacket = new CM_PLAY(buf, channelHandler);
				break;
			default:
				unknownPacket(opCode);
		}
		return clientPacket;
	}
	
	private static void unknownPacket(int id)
	{
		log.warn(String.format("Unknown packet recived from Aion client: 0x%02X ", id));
	}
}
