package com.aionemu.loginserver.network.aion;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aionemu.loginserver.GameServerService;
import com.aionemu.loginserver.network.AbstractPacketHandler;
import com.aionemu.loginserver.network.aion.clientpackets.CM_AUTH_GG;
import com.aionemu.loginserver.network.aion.clientpackets.CM_LOGIN;
import com.aionemu.loginserver.network.aion.clientpackets.CM_PLAY;
import com.aionemu.loginserver.network.aion.clientpackets.CM_SERVER_LIST;
import com.aionemu.loginserver.network.aion.clientpackets.CM_UPDATE_SESSION;
import com.aionemu.loginserver.network.netty.handler.ClientChannelHandler;
import com.aionemu.loginserver.network.netty.handler.ClientChannelHandler.State;
import com.aionemu.loginserver.service.AccountTimeService;

/**
 * @author ATracer
 */
@Component
public class ClientPacketHandler extends AbstractPacketHandler
{
	private static final Logger log = Logger.getLogger(ClientPacketHandler.class);

	private final AccountTimeService accountTimeService;
	
	private final GameServerService gameServerService;
	
	@Autowired
	public ClientPacketHandler(AccountTimeService accountTimeService, GameServerService gameServerService)
	{
		super();
		this.accountTimeService = accountTimeService;
		this.gameServerService = gameServerService;
	}
	/**
	 *  Reads one packet from ChannelBuffer
	 *  
	 * @param buf
	 * @param channelHandler
	 * @return AbstractClientPacket 
	 */
	public AbstractClientPacket handle(ChannelBuffer buf, ClientChannelHandler channelHandler)
	{
		byte opCode = buf.readByte();
		State state = channelHandler.getState();
		AbstractClientPacket clientPacket = null;
		
		switch(state)
		{
			case CONNECTED:
				switch(opCode)
				{
					case 0x07:
						clientPacket = new CM_AUTH_GG(buf, channelHandler);
						break;
					case 0x08:
						clientPacket = new CM_UPDATE_SESSION(buf, channelHandler);
						break;
					default :
						unknownPacket(opCode, state.toString());						
				}
				break;
			case AUTHED_GG:
				switch(opCode)
				{
					case 0x0B:
						clientPacket = new CM_LOGIN(buf, channelHandler);
						break;
					default :
						unknownPacket(opCode, state.toString());
				}
				break;
			case AUTHED_LOGIN:
				switch(opCode)
				{
					case 0x05:
						clientPacket = new CM_SERVER_LIST(buf, channelHandler, gameServerService);
						break;
					case 0x02:
						clientPacket = new CM_PLAY(buf, channelHandler, gameServerService);
						break;
					default:
						unknownPacket(opCode, state.toString());
				}
				break;
		}
		
		return clientPacket;
	}
}
