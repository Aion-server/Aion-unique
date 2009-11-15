package com.aionemu.loginserver.network.netty.pipeline;

import com.aionemu.loginserver.network.gameserver.GameServerPacketHandler;
import com.aionemu.loginserver.network.netty.coder.GameServerPacketDecoder;
import com.aionemu.loginserver.network.netty.coder.GameServerPacketEncoder;
import com.aionemu.loginserver.network.netty.coder.PacketFrameDecoder;
import com.aionemu.loginserver.network.netty.handler.GameChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author ATracer
 */
@Component
public class LoginToGamePipelineFactory implements ChannelPipelineFactory
{
	private static final int THREADS_MAX = 10;
	private static final int MEMORY_PER_CHANNEL = 1048576;
	private static final int TOTAL_MEMORY = 134217728;
	private static final int TIMEOUT = 100;

	private final GameServerPacketHandler gameServerPacketHandler;

	@Autowired
	public LoginToGamePipelineFactory(GameServerPacketHandler gameServerPacketHandler) {
		this.gameServerPacketHandler = gameServerPacketHandler;
	}


	/**
	 *  Decoding process will include the following handlers:
	 *  - framedecoder
	 *  - packetdecoder
	 *  - handler
	 *  
	 *  Encoding process:
	 *  - packetencoder
	 *  
	 *  Please note the sequence of handlers 
	 */
	@Override
	public ChannelPipeline getPipeline() throws Exception 
	{
		ChannelPipeline pipeline = Channels.pipeline();

		pipeline.addLast("framedecoder", new PacketFrameDecoder());
		pipeline.addLast("packetdecoder", new GameServerPacketDecoder());
		pipeline.addLast("packetencoder", new GameServerPacketEncoder());

		OrderedMemoryAwareThreadPoolExecutor pipelineExecutor = new OrderedMemoryAwareThreadPoolExecutor(
				THREADS_MAX, MEMORY_PER_CHANNEL, TOTAL_MEMORY, TIMEOUT, TimeUnit.MILLISECONDS, Executors
				.defaultThreadFactory());

		pipeline.addLast("executor", new ExecutionHandler(pipelineExecutor)); 
		pipeline.addLast("handler", new GameChannelHandler(gameServerPacketHandler));

		return pipeline;
	}
}
