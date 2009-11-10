package com.aionemu.loginserver.network.netty.coder;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.aionemu.loginserver.network.netty.handler.ChannelHandler;

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
		
		byte[] data = new byte[size];
		message.readBytes(data);
		
		ChannelHandler channelHandler = (ChannelHandler) ctx.getHandler();
		int newSize = channelHandler.getCryptEngine().encrypt(data, 0, size);
		
		ChannelBuffer encryptedMessage = ChannelBuffers.wrappedBuffer(data);
		encryptedMessage.writeShort((short) newSize);
		
		logger.info("After encoding: " + ChannelBuffers.hexDump(message));
		
		return message;
	}

	
}
