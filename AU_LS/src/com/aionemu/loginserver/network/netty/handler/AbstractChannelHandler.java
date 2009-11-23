package com.aionemu.loginserver.network.netty.handler;

import java.net.InetAddress;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.aionemu.loginserver.network.BaseServerPacket;

/**
 * @author ATracer
 */
public abstract class AbstractChannelHandler extends SimpleChannelUpstreamHandler
{
	private static final Logger log = Logger.getLogger(AbstractChannelHandler.class);
	
	/**
	 *  IP address of channel client
	 */
	protected InetAddress inetAddress;
	/**
	 * Associated channel
	 */
	protected Channel channel;
	
	/**
	 * Invoked when a Channel was disconnected from its remote peer
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
		//noinspection ThrowableResultOfMethodCallIgnored
		log.error("Netty: Exception caught", e.getCause());
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
	 *  Closes the channel but ensures that packet is send before close
	 *  
	 * @param packet
	 */
	public void close(BaseServerPacket packet)
	{
		channel.write(packet).addListener(ChannelFutureListener.CLOSE);
	}
	
	/**
	 *  Closes the channel
	 */
	public void close()
	{
		channel.close();
	}
	
	/**
	 * @return the IP address string
	 */
	public String getIP()
	{
		return inetAddress.getHostAddress();
	}
}
