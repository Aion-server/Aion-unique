package com.aionemu.loginserver.network.netty.coder;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import com.aionemu.loginserver.network.netty.handler.ChannelHandler;

/**
 * @author ATracer
 */
@ChannelPipelineCoverage("one")
public class LoginPacketDecoder extends OneToOneDecoder
{
	private static final Logger logger = Logger.getLogger(LoginPacketDecoder.class);

	/* (non-Javadoc)
	 * @see org.jboss.netty.handler.codec.oneone.OneToOneDecoder#decode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, java.lang.Object)
	 */
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel arg1,
			Object arg2) throws Exception 
	{	
		ChannelBuffer message = (ChannelBuffer) arg2;
		
		logger.info("Before decoding: " + ChannelBuffers.hexDump(message));
		
		short size = message.getShort(0);
		byte[] data = new byte[size];
		message.readBytes(data);
		
		ChannelHandler channelHandler = (ChannelHandler)ctx.getHandler();
		boolean result = channelHandler.getCryptEngine().decrypt(data, 0, size);
		
		ChannelBuffer decodedMessage = ChannelBuffers.wrappedBuffer(data);
		
		logger.info("After decoding: " + ChannelBuffers.hexDump(decodedMessage));
		
		if (!result)
			logger.warn("Wrong checksum from client: " + this);	

		return decodedMessage;
	}	
}
