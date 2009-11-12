package com.aionemu.loginserver.network.netty.coder;

import java.nio.ByteOrder;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.aionemu.loginserver.network.netty.handler.ClientChannelHandler;

/**
 * @author ATracer
 */
@ChannelPipelineCoverage("one")
public class LoginPacketEncoder extends OneToOneEncoder
{
	private static final Logger logger = Logger.getLogger(LoginPacketEncoder.class);

	
	/* (non-Javadoc)
	 * @see org.jboss.netty.handler.codec.oneone.OneToOneEncoder#encode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, java.lang.Object)
	 */
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel arg1,
			Object arg2) throws Exception 
	{
		ChannelBuffer message = (ChannelBuffer) arg2;
		
		logger.info("Before encoding: " + ChannelBuffers.hexDump(message));
		
		int size = message.readableBytes();
		/** extra 8 is for keys **/
		byte[] data = new byte[size + 8];
		message.readBytes(data, 0, size);
		
		ClientChannelHandler channelHandler = (ClientChannelHandler) ctx.getPipeline().getLast();
		int newSize = channelHandler.getCryptEngine().encrypt(data, 2, size - 4);
		
		ChannelBuffer encryptedMessage = ChannelBuffers.wrappedBuffer(ByteOrder.LITTLE_ENDIAN,data, 0 , newSize + 2);
		encryptedMessage.setShort(0, (short) (newSize + 2));
		
		logger.info("After encoding: " + ChannelBuffers.hexDump(encryptedMessage) + " size: " + encryptedMessage.capacity());

		return encryptedMessage;
	}
}
