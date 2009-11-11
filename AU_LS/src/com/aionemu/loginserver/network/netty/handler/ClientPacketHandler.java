package com.aionemu.loginserver.network.netty.handler;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;

import com.aionemu.loginserver.network.netty.handler.ChannelHandler.State;
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
	private static final Logger log = Logger.getLogger(ClientPacketHandler.class);
	
	/**
	 *  Reads one packet from ChannelBuffer
	 *  
	 * @param buf
	 * @param channelHandler
	 * @return AbstractClientPacket 
	 */
	public static AbstractClientPacket handle(ChannelBuffer buf, ChannelHandler channelHandler)
	{
		byte opCode = buf.readByte();
		State state = channelHandler.getState();
		AbstractClientPacket clientPacket = null;
		
		switch(state)
		{
			case CONNECTED:
				switch(opCode)
				{
					case 0x07:
						clientPacket = new CM_AUTH_GG(buf, channelHandler);
						break;
					case 0x08:
						clientPacket = new CM_UPDATE_SESSION(buf, channelHandler);
						break;
					default :
						unknownPacket(opCode, state);						
				}
				break;
			case AUTHED_GG:
				switch(opCode)
				{
					case 0x0B:
						clientPacket = new CM_LOGIN(buf, channelHandler);
						break;
					default :
						unknownPacket(opCode, state);
				}
				break;
			case AUTHED_LOGIN:
				switch(opCode)
				{
					case 0x05:
						clientPacket = new CM_SERVER_LIST(buf, channelHandler);
						break;
					case 0x02:
						clientPacket = new CM_PLAY(buf, channelHandler);
						break;
					default:
						unknownPacket(opCode, state);
				}
				break;
		}
		
		return clientPacket;
	}
	
	/**
	 *  Unknown packet
	 *  
	 * @param id
	 * @param state
	 */
	private static void unknownPacket(int id, State state)
	{
		log.warn(String.format("Unknown packet recived from Aion client: 0x%02X state=%s", id, state.toString()));
	}
}
