package com.aionemu.loginserver.network.netty.coder;

import java.nio.ByteOrder;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import com.aionemu.loginserver.network.netty.handler.ClientChannelHandler;

/**
 * @author ATracer
 */
@ChannelPipelineCoverage("one")
public class GameServerPacketDecoder extends OneToOneDecoder
{
	private static final Logger logger = Logger.getLogger(GameServerPacketDecoder.class);

	/* (non-Javadoc)
	 * @see org.jboss.netty.handler.codec.oneone.OneToOneDecoder#decode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, java.lang.Object)
	 */
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel arg1,
			Object arg2) throws Exception 
	{	
		ChannelBuffer message = (ChannelBuffer) arg2;

		logger.info("Packet from gameServer " + ChannelBuffers.hexDump(message));
		
		int dataSize = message.readShort();

		return message;
	}	
}
