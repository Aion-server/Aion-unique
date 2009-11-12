package com.aionemu.loginserver.network.netty.packet;

import java.nio.ByteOrder;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public abstract class BaseServerPacket extends AbstractPacket
{
	private static final Logger log = Logger.getLogger(BaseServerPacket.class);
	
	/**
	 * 
	 * @param opCode
	 */
	public BaseServerPacket(int opCode)
	{
		super(opCode);
	}
	
	/**
	 * 
	 * @param buf
	 * @param value
	 */
	protected final void writeD(ChannelBuffer buf, int value)
	{
		buf.writeInt(value);
	}
	
	/**
	 * 
	 * @param buf
	 * @param value
	 */
	protected final void writeH(ChannelBuffer buf, int value)
	{
		buf.writeShort((short)value);
	}

	/**
	 * 
	 * @param buf
	 * @param value
	 */
	protected final void writeC(ChannelBuffer buf, int value)
	{
		buf.writeByte((byte)value);
	}

	/**
	 * 
	 * @param buf
	 * @param data
	 */
	protected final void writeB(ChannelBuffer buf, byte[] data)
	{
		buf.writeBytes(data);
	}
	
	protected final void writeS(ChannelBuffer buf, String data)
	{
		ChannelBuffer copyBuf = ChannelBuffers.copiedBuffer(data, "UTF-16");
		buf.writeBytes(copyBuf, 2, copyBuf.capacity() - 2);
	}
	
	protected final void writeQ(ChannelBuffer buf, long data)
	{
		buf.writeLong(data);
	}
	

}
