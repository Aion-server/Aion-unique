package com.aionemu.loginserver.network.aion;

import org.jboss.netty.buffer.ChannelBuffer;

import com.aionemu.loginserver.network.BaseServerPacket;
import com.aionemu.loginserver.network.netty.handler.ClientChannelHandler;

/**
 * @author ATracer
 */
public abstract class AbstractServerPacket extends BaseServerPacket
{
	/**
	 * 
	 * @param opCode
	 */
	public AbstractServerPacket(int opCode)
	{
		super(opCode);
	}
	
	public void write(ClientChannelHandler clientChannelHandler, ChannelBuffer buf)
	{
		buf.writeShort((short)0);
		writeImpl(clientChannelHandler, buf);
	}
	
	/**
	 * 
	 * @param cHandler
	 * @param buf
	 */
	protected abstract void writeImpl(ClientChannelHandler cHandler, ChannelBuffer buf);

}
