package com.aionemu.loginserver.network.netty.handler;

import java.net.InetAddress;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.aionemu.loginserver.network.netty.packet.BaseServerPacket;

/**
 * @author ATracer
 */
public abstract class AbstractChannelHandler extends SimpleChannelUpstreamHandler
{
	private static final Logger log = Logger.getLogger(AbstractChannelHandler.class);

	protected InetAddress inetAddress;
	protected Channel channel;
	
	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#channelDisconnected(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
	 */
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception 
	{
		log.info("Channel disconnected");
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#exceptionCaught(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ExceptionEvent)
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception 
	{
		e.getCause().printStackTrace();
		e.getChannel().close();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#handleUpstream(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelEvent)
	 */
	@Override
	public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception 
	{
		if (e instanceof ChannelStateEvent) 
		{
			log.info(e.toString());
		}
		super.handleUpstream(ctx, e);
	}
	
	/**
	 * 
	 * @param packet
	 * @param forced
	 */
	public void close(BaseServerPacket packet, boolean forced)
	{
		//TODO close channel
	}
	
	/**
	 * 
	 * @param forced
	 */
	public void close(boolean forced)
	{
		//TODO close channel
	}
	
	public String getIP()
	{
		return inetAddress.getHostAddress();
	}

}
