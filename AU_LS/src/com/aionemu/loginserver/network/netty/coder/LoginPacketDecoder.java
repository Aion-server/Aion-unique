package com.aionemu.loginserver.network.netty.coder;

import java.nio.ByteOrder;

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
		
		int dataSize = message.readShort() - 2;
		byte[] data = new byte[dataSize];
		message.readBytes(data);
		
		ChannelHandler channelHandler = (ChannelHandler)ctx.getPipeline().getLast();
		
		if (!channelHandler.getCryptEngine().decrypt(data, 0, dataSize))
		{
			logger.warn("Wrong checksum from client: " + this);	
			arg1.disconnect();
		}
		
		ChannelBuffer decodedMessage = ChannelBuffers.wrappedBuffer(ByteOrder.LITTLE_ENDIAN, data);
		logger.info("After decoding: " + ChannelBuffers.hexDump(decodedMessage));	

		return decodedMessage;
	}	
}
