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
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.aionemu.loginserver.network.netty.pipeline.LoginToClientPipeLineFactory;
import com.aionemu.loginserver.network.netty.pipeline.LoginToGamePipelineFactory;

/**
 * @author ATracer
 */
public class LoginNettyServer 
{
	private static final Logger logger = Logger.getLogger(LoginNettyServer.class);

	private static final LoginNettyServer instance = new LoginNettyServer();

	private ChannelGroup channelGroup = new DefaultChannelGroup(LoginNettyServer.class.getName());
	
	private ChannelFactory loginToClientChannelFactory;
	private ChannelFactory loginToGameChannelFactory;
	
	/**
	 *  Initialize listening on login port
	 */
	public void initialize() 
	{
		Channel loginToClientChannel = initLoginToClientChannel();
		Channel loginToGameChannel = initLoginToGameChannel();
		
		channelGroup.add(loginToClientChannel);
		channelGroup.add(loginToGameChannel);

		logger.info("LoginNettyServer started");
	}

	private Channel initLoginToClientChannel()
	{
		loginToClientChannelFactory = new NioServerSocketChannelFactory(Executors
			.newCachedThreadPool(), Executors.newCachedThreadPool(),
			Runtime.getRuntime().availableProcessors() * 2 + 1);

		ServerBootstrap bootstrap = new ServerBootstrap(loginToClientChannelFactory);

		configureLoginToClient(bootstrap);

		Channel channel = bootstrap.bind(new InetSocketAddress(2106));
		return channel;
	}
	
	private Channel initLoginToGameChannel()
	{
		loginToGameChannelFactory = new NioServerSocketChannelFactory(Executors
			.newCachedThreadPool(), Executors.newCachedThreadPool(),
			Runtime.getRuntime().availableProcessors() * 2 + 1);

		ServerBootstrap bootstrap = new ServerBootstrap(loginToGameChannelFactory);

		configureLoginToGame(bootstrap);

		Channel channel = bootstrap.bind(new InetSocketAddress(9014));
		return channel;
	}

	/**
	 *  Shutdown server
	 */
	public void shutdownAll()
	{
		ChannelGroupFuture future = channelGroup.close();
		future.awaitUninterruptibly();
		loginToClientChannelFactory.releaseExternalResources();
		loginToGameChannelFactory.releaseExternalResources();
	}
	

	/**
	 * @param bootstrap
	 */
	private void configureLoginToClient(ServerBootstrap bootstrap)
	{
		bootstrap.setPipelineFactory(new LoginToClientPipeLineFactory());
		bootstrap.setOption("child.bufferFactory", HeapChannelBufferFactory.getInstance(ByteOrder.LITTLE_ENDIAN));	
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);
		bootstrap.setOption("child.reuseAddress", true);
		bootstrap.setOption("child.connectTimeoutMillis", 100);
		bootstrap.setOption("readWriteFair", true);
	}
	
	/**
	 * @param bootstrap
	 */
	private void configureLoginToGame(ServerBootstrap bootstrap)
	{
		bootstrap.setPipelineFactory(new LoginToGamePipelineFactory());
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
