package com.aionemu.loginserver.network.netty.packet.l2c;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;

import com.aionemu.loginserver.network.netty.handler.ClientChannelHandler;
import com.aionemu.loginserver.network.netty.packet.BaseClientPacket;

/**
 * @author ATracer
 */
public abstract class AbstractClientPacket extends BaseClientPacket
{
	private static final Logger log = Logger.getLogger(AbstractClientPacket.class);

	private ClientChannelHandler channelHandler;

	/**
	 * 
	 * @param channelBuffer
	 * @param channelHandler
	 * @param opCode
	 */
	public AbstractClientPacket(ChannelBuffer channelBuffer, ClientChannelHandler channelHandler,
		int opCode)
	{
		super(channelBuffer, opCode);
		this.channelHandler = channelHandler;
	}

	/**
	 * 
	 * @return
	 */
	protected ClientChannelHandler getHandler()
	{
		return channelHandler;
	}
	
	
}
