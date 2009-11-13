package com.aionemu.loginserver.network.gameserver;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;

import com.aionemu.loginserver.network.BaseClientPacket;
import com.aionemu.loginserver.network.netty.handler.GameChannelHandler;

/**
 * @author ATracer
 */
public abstract class AbstractGameClientPacket extends BaseClientPacket
{
	private static final Logger log = Logger.getLogger(AbstractGameClientPacket.class);

	protected GameChannelHandler gameChannelHandler;

	/**
	 * 
	 * @param channelBuffer
	 * @param gameChannelHandler
	 * @param opCode
	 */
	public AbstractGameClientPacket(ChannelBuffer channelBuffer,
		GameChannelHandler gameChannelHandler, int opCode)
	{
		super(channelBuffer, opCode);
		this.gameChannelHandler = gameChannelHandler;
	}
}
