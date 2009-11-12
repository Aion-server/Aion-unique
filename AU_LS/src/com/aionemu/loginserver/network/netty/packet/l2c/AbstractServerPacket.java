package com.aionemu.loginserver.network.netty.packet.l2c;

import org.jboss.netty.buffer.ChannelBuffer;

import com.aionemu.loginserver.network.netty.handler.ClientChannelHandler;
import com.aionemu.loginserver.network.netty.packet.BaseServerPacket;

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
	
	public void write(ClientChannelHandler cHandler, ChannelBuffer buf)
	{
		buf.writeShort((short)0);
		writeImpl(cHandler, buf);
	}
	
	/**
	 * 
	 * @param cHandler
	 * @param buf
	 */
	protected abstract void writeImpl(ClientChannelHandler cHandler, ChannelBuffer buf);

}
