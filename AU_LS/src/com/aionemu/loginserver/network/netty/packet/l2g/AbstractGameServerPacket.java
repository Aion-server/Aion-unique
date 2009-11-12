package com.aionemu.loginserver.network.netty.packet.l2g;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;

import com.aionemu.loginserver.network.netty.handler.GameChannelHandler;
import com.aionemu.loginserver.network.netty.packet.BaseServerPacket;

/**
 * @author ATracer
 */
public abstract class AbstractGameServerPacket extends BaseServerPacket
{
	private static final Logger log = Logger.getLogger(AbstractGameServerPacket.class);
	
	/**
	 * 
	 * @param opCode
	 */
	public AbstractGameServerPacket(int opCode)
	{
		super(opCode);
	}
	
	/**
	 * 
	 * @param channelHandler
	 * @param buf
	 */
	public void write(GameChannelHandler channelHandler, ChannelBuffer buf)
	{
		writeH(buf, 0);
		writeImpl(channelHandler, buf);
	}
	
	/**
	 * 
	 * @param channelHandler
	 * @param buf
	 */
	protected abstract void writeImpl(GameChannelHandler channelHandler, ChannelBuffer buf);
	
}
