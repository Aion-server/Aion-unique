package com.aionemu.loginserver.network.gameserver;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aionemu.loginserver.GameServerService;
import com.aionemu.loginserver.network.AbstractPacketHandler;
import com.aionemu.loginserver.network.gameserver.clientpackets.CM_ACCOUNT_AUTH;
import com.aionemu.loginserver.network.gameserver.clientpackets.CM_ACCOUNT_DISCONNECTED;
import com.aionemu.loginserver.network.gameserver.clientpackets.CM_ACCOUNT_LIST;
import com.aionemu.loginserver.network.gameserver.clientpackets.CM_ACCOUNT_RECONNECT_KEY;
import com.aionemu.loginserver.network.gameserver.clientpackets.CM_GS_AUTH;
import com.aionemu.loginserver.network.netty.handler.GameChannelHandler;
import com.aionemu.loginserver.network.netty.handler.GameChannelHandler.State;
import com.aionemu.loginserver.service.AccountTimeService;

/**
 * @author ATracer
 */
@Component
public class GameServerPacketHandler extends AbstractPacketHandler
{
	private static final Logger log = Logger.getLogger(GameServerPacketHandler.class);
	
	private final AccountTimeService accountTimeService;
	
	private final GameServerService gameServerService;
	
	@Autowired
	public GameServerPacketHandler(AccountTimeService accountTimeService, GameServerService gameServerService)
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
	 * @return AbstractGameClientPacket 
	 */
	public AbstractGameClientPacket handle(ChannelBuffer buf, GameChannelHandler channelHandler)
	{
		byte opCode = buf.readByte();
		State state = channelHandler.getState();
		AbstractGameClientPacket gamePacket = null;

		switch (state)
		{
			case CONNECTED:
			{
				switch (opCode)
				{
					case 0x00:
						gamePacket = new CM_GS_AUTH(buf, channelHandler, gameServerService);
						break;
					default:
						unknownPacket(opCode, state.toString());
				}
				break;
			}
			case AUTHED:
			{
				switch (opCode)
				{
					case 0x01:
						gamePacket = new CM_ACCOUNT_AUTH(buf, channelHandler);
						break;
					case 0x02:
						gamePacket = new CM_ACCOUNT_RECONNECT_KEY(buf, channelHandler);
						break;
					case 0x03:
						gamePacket = new CM_ACCOUNT_DISCONNECTED(buf, channelHandler, accountTimeService);
						break;
					case 0x04:
						gamePacket = new CM_ACCOUNT_LIST(buf, channelHandler, gameServerService);
						break;
					default:
						unknownPacket(opCode, state.toString());
				}
				break;
			}
		}

		return gamePacket;
	}

	
}
