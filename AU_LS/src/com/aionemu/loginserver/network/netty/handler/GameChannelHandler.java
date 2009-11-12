package com.aionemu.loginserver.network.netty.handler;

import java.net.InetSocketAddress;
import java.nio.ByteOrder;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;

import com.aionemu.loginserver.GameServer;
import com.aionemu.loginserver.network.netty.packet.handler.GameServerPacketHandler;
import com.aionemu.loginserver.network.netty.packet.l2g.AbstractGameClientPacket;
import com.aionemu.loginserver.network.netty.packet.l2g.AbstractGameServerPacket;

/**
 * @author ATracer
 */
@ChannelPipelineCoverage("one") 
public class GameChannelHandler extends AbstractChannelHandler
{
	private static final Logger logger = Logger.getLogger(GameChannelHandler.class);
	
	private State state;
	
	/**
	 * GameServer for this GsConnection.
	 */
	private GameServer gameServer = null;
	
	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#channelConnected(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
	 */
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception 
	{
		super.channelConnected(ctx, e);
		state = State.CONNECTED;
		channel = ctx.getChannel();
		inetAddress =  ((InetSocketAddress) e.getChannel().getRemoteAddress()).getAddress();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent)
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception 
	{
		super.messageReceived(ctx, e);
		/**
		 *  Packet is frame decoded and decrypted at this stage
		 *  Here packet will be read and submitted to execution
		 */
		logger.info("Message received");
		AbstractGameClientPacket gameServerPacket = GameServerPacketHandler.handle((ChannelBuffer) e.getMessage(), this);
		if(gameServerPacket != null && gameServerPacket.read())
		{
			gameServerPacket.run();
		}
	}
	
	/**
	 * 
	 * @param packet
	 */
	public void sendPacket(AbstractGameServerPacket packet)
	{
		ChannelBuffer cb = ChannelBuffers.buffer(ByteOrder.LITTLE_ENDIAN, 2*8192);
		packet.write(this, cb);
		channel.write(cb);
	}

	public static enum State
	{
		/**
		 * Means that GameServer just connect, but is not authenticated yet
		 */
		CONNECTED,
		/**
		 * GameServer is authenticated
		 */
		AUTHED
	}

	/**
	 * @return the state
	 */
	public State getState()
	{
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(State state)
	{
		this.state = state;
	}
	
	/**
	 * @return GameServerInfo for this ChannelHandler or null if this ChannelHandler is not authenticated yet.
	 */
	public GameServer getGameServerInfo()
	{
		return gameServer;
	}

	/**
	 * 
	 * @param gameServer
	 */
	public void setGameServerInfo(GameServer gameServer)
	{
		this.gameServer = gameServer;
	}

}
