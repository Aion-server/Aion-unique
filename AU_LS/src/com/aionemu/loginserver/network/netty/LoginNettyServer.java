package com.aionemu.loginserver.network.netty;

import com.aionemu.loginserver.configs.LoginServerConfig;
import com.aionemu.loginserver.network.netty.pipeline.LoginToClientPipeLineFactory;
import com.aionemu.loginserver.network.netty.pipeline.LoginToGamePipelineFactory;
import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.HeapChannelBufferFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.nio.ByteOrder;
import java.util.concurrent.Executors;

/**
 * @author ATracer
 */
@Service(LoginNettyServer.SPRING_BEAN_NAME)
public class LoginNettyServer 
{
	private static final Logger logger = Logger.getLogger(LoginNettyServer.class);

	public static final String SPRING_BEAN_NAME = "loginNettyServer";

	private final ChannelGroup channelGroup = new DefaultChannelGroup(LoginNettyServer.class.getName());

	private final LoginToClientPipeLineFactory loginToClientPipeLineFactory;

	private final LoginToGamePipelineFactory loginToGamePipelineFactory;

	private final LoginServerConfig loginServerConfig;
	
	private ChannelFactory loginToClientChannelFactory;

	private ChannelFactory loginToGameChannelFactory;

	@Autowired
	public LoginNettyServer(LoginServerConfig loginServerConfig, LoginToClientPipeLineFactory loginToClientPipeLineFactory,
						 LoginToGamePipelineFactory loginToGamePipelineFactory){

		this.loginServerConfig = loginServerConfig;
		this.loginToClientPipeLineFactory = loginToClientPipeLineFactory;
		this.loginToGamePipelineFactory = loginToGamePipelineFactory;
	}

	/**
	 *  Initialize listening on login port
	 */
	public void initialize() 
	{
		loginToClientChannelFactory = initChannelFactory();
		loginToGameChannelFactory = initChannelFactory();

		Channel loginToClientChannel = initChannel(loginToClientChannelFactory, loginServerConfig.getClientListenAddress(), loginToClientPipeLineFactory);
		Channel loginToGameChannel = initChannel(loginToGameChannelFactory, loginServerConfig.getGameServerListenAddress(), loginToGamePipelineFactory);
		
		channelGroup.add(loginToClientChannel);
		channelGroup.add(loginToGameChannel);

		logger.info("Login Server started");
	}

	private NioServerSocketChannelFactory initChannelFactory(){
		return new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool(),
			Runtime.getRuntime().availableProcessors() * 2 + 1);
	}

	private Channel initChannel(ChannelFactory channelFactory, InetSocketAddress listenAddress, ChannelPipelineFactory channelPipelineFactory)
	{
		ServerBootstrap bootstrap = new ServerBootstrap(channelFactory);
		bootstrap.setPipelineFactory(channelPipelineFactory);
		bootstrap.setOption("child.bufferFactory", HeapChannelBufferFactory.getInstance(ByteOrder.LITTLE_ENDIAN));
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);
		bootstrap.setOption("child.reuseAddress", true);
		bootstrap.setOption("child.connectTimeoutMillis", 100);
		bootstrap.setOption("readWriteFair", true);
		return bootstrap.bind(listenAddress);
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
}
