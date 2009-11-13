package com.aionemu.loginserver.network.gameserver;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;

import com.aionemu.loginserver.network.BaseServerPacket;
import com.aionemu.loginserver.network.netty.handler.GameChannelHandler;

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
	 * @param gameChannelHandler
	 * @param buf
	 */
	public void write(GameChannelHandler gameChannelHandler, ChannelBuffer buf)
	{
		writeH(buf, 0);
		writeImpl(gameChannelHandler, buf);
	}
	
	/**
	 * 
	 * @param gameChannelHandler
	 * @param buf
	 */
	protected abstract void writeImpl(GameChannelHandler gameChannelHandler, ChannelBuffer buf);
	
}
