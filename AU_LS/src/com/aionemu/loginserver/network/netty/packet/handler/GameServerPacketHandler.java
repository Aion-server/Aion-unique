package com.aionemu.loginserver.network.netty.packet.handler;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;

import com.aionemu.loginserver.network.netty.handler.GameChannelHandler;
import com.aionemu.loginserver.network.netty.handler.GameChannelHandler.State;
import com.aionemu.loginserver.network.netty.packet.l2g.AbstractGameClientPacket;
import com.aionemu.loginserver.network.netty.packet.l2g.client.CM_ACCOUNT_AUTH;
import com.aionemu.loginserver.network.netty.packet.l2g.client.CM_ACCOUNT_DISCONNECTED;
import com.aionemu.loginserver.network.netty.packet.l2g.client.CM_ACCOUNT_LIST;
import com.aionemu.loginserver.network.netty.packet.l2g.client.CM_ACCOUNT_RECONNECT_KEY;
import com.aionemu.loginserver.network.netty.packet.l2g.client.CM_GS_AUTH;

/**
 * @author ATracer
 */
public class GameServerPacketHandler
{
	private static final Logger log = Logger.getLogger(GameServerPacketHandler.class);

	/**
	 *  Reads one packet from ChannelBuffer
	 *  
	 * @param buf
	 * @param channelHandler
	 * @return AbstractGameClientPacket 
	 */
	public static AbstractGameClientPacket handle(ChannelBuffer buf, GameChannelHandler channelHandler)
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
						gamePacket = new CM_GS_AUTH(buf, channelHandler);
						break;
					default:
						unknownPacket(opCode, state);
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
						gamePacket = new CM_ACCOUNT_DISCONNECTED(buf, channelHandler);
						break;
					case 0x04:
						gamePacket = new CM_ACCOUNT_LIST(buf, channelHandler);
						break;
					default:
						unknownPacket(opCode, state);
				}
				break;
			}
		}

		return gamePacket;
	}

	/**
	 *  Unknown packet
	 *  
	 * @param id
	 * @param state
	 */
	private static void unknownPacket(int id, State state)
	{
		log.warn(String.format("Unknown packet recived from Game server: 0x%02X state=%s", id, state.toString()));
	}
}
