package com.aionemu.loginserver.network;

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
	 * Write double to buffer.
	 * 
	 * @param buf
	 * @param value
	 */
	protected final void writeDF(ChannelBuffer buf, double value)
	{
		buf.writeDouble(value);
	}
	
	/**
	 * Write float to buffer.
	 * 
	 * @param buf
	 * @param value
	 */
	protected final void writeF(ChannelBuffer buf, float value)
	{
		buf.writeFloat(value);
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
	
	/**
	 * Write String to buffer
	 * 
	 * @param buf
	 * @param text
	 */
	protected final void writeS(ChannelBuffer buf, String text)
	{
		if (text == null)
		{
			buf.writeChar('\000');
		}
		else
		{
			final int len = text.length();
			for (int i = 0; i < len; i++)
				buf.writeChar(text.charAt(i));
			buf.writeChar('\000');
		}
	}
	
	/**
	 * 
	 * @param buf
	 * @param data
	 */
	protected final void writeQ(ChannelBuffer buf, long data)
	{
		buf.writeLong(data);
	}
	
}
