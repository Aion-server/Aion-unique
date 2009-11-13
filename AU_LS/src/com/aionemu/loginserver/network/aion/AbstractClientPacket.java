package com.aionemu.loginserver.network.aion;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;

import com.aionemu.loginserver.network.BaseClientPacket;
import com.aionemu.loginserver.network.netty.handler.ClientChannelHandler;

/**
 * @author ATracer
 */
public abstract class AbstractClientPacket extends BaseClientPacket
{
	private static final Logger log = Logger.getLogger(AbstractClientPacket.class);

	protected ClientChannelHandler clientChannelHandler;

	/**
	 * 
	 * @param channelBuffer
	 * @param clientChannelHandler
	 * @param opCode
	 */
	public AbstractClientPacket(ChannelBuffer channelBuffer, ClientChannelHandler clientChannelHandler,
		int opCode)
	{
		super(channelBuffer, opCode);
		this.clientChannelHandler = clientChannelHandler;
	}
}
