package com.aionemu.loginserver.network;

import org.apache.log4j.Logger;

import com.aionemu.loginserver.network.netty.handler.GameChannelHandler.State;

/**
 * @author ATracer
 */
public abstract class AbstractPacketHandler
{
	private static final Logger log = Logger.getLogger(AbstractPacketHandler.class);
	
	/**
	 *  Unknown packet
	 *  
	 * @param id
	 * @param state
	 */
	protected void unknownPacket(int id, String state)
	{
		log.warn(String.format("Unknown packet received from Game server: 0x%02X state=%s", id, state));
	}
}
