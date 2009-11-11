package com.aionemu.loginserver.network.netty;

import java.net.InetSocketAddress;
import java.nio.ByteOrder;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.HeapChannelBufferFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.aionemu.loginserver.network.netty.pipeline.LoginServerPipeLineFactory;

/**
 * @author ATracer
 */

public class LoginNettyServer 
{
	private static final Logger logger = Logger.getLogger(LoginNettyServer.class);
	
	private static final LoginNettyServer instance = new LoginNettyServer();
	
	/**
	 *  Initialize listening on login port
	 */
	public void initialize() 
	{
		// Create the global ChannelGroup
		ChannelGroup channelGroup = new DefaultChannelGroup(
				LoginNettyServer.class.getName());
		
		ChannelFactory factory = new NioServerSocketChannelFactory(Executors
				.newCachedThreadPool(), Executors.newCachedThreadPool(),
				Runtime.getRuntime().availableProcessors() * 2 + 1);
		
		ServerBootstrap bootstrap = new ServerBootstrap(factory);
		
		configure(bootstrap);

		// Add the parent channel to the group
		Channel channel = bootstrap.bind(new InetSocketAddress(2106));
		channelGroup.add(channel);

		logger.info("LoginNettyServer started");
	}
	
	/**
	 *  Shutdown server
	 */
	public void shutdown()
	{
		
	}

	/**
	 * @param bootstrap
	 */
	private void configure(ServerBootstrap bootstrap)
	{
		bootstrap.setPipelineFactory(new LoginServerPipeLineFactory());
		bootstrap.setOption("child.bufferFactory", HeapChannelBufferFactory.getInstance(ByteOrder.LITTLE_ENDIAN));	
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);
		bootstrap.setOption("child.reuseAddress", true);
		bootstrap.setOption("child.connectTimeoutMillis", 100);
		bootstrap.setOption("readWriteFair", true);
	}
	
	/**
	 * @return LoginNettyServer instance
	 */
	public static LoginNettyServer getInstance()
	{
		return instance;
	}
}
