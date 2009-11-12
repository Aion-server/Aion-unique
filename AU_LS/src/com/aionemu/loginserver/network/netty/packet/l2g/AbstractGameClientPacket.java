package com.aionemu.loginserver.network.netty.packet.l2g;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;

import com.aionemu.loginserver.network.netty.handler.GameChannelHandler;
import com.aionemu.loginserver.network.netty.packet.BaseClientPacket;

/**
 * @author ATracer
 */
public abstract class AbstractGameClientPacket extends BaseClientPacket
{
	private static final Logger log = Logger.getLogger(AbstractGameClientPacket.class);

	protected GameChannelHandler channelHandler;

	/**
	 * 
	 * @param channelBuffer
	 * @param channelHandler
	 * @param opCode
	 */
	public AbstractGameClientPacket(ChannelBuffer channelBuffer,
		GameChannelHandler channelHandler, int opCode)
	{
		super(channelBuffer, opCode);
		this.channelHandler = channelHandler;
	}

	/**
	 * @return the channelHandler
	 */
	public GameChannelHandler getChannelHandler()
	{
		return channelHandler;
	}
}
