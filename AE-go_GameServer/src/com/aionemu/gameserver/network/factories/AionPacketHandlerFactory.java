/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.factories;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionPacketHandler;
import com.aionemu.gameserver.network.aion.Version;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.clientpackets.*;
//import com.aionemu.gameserver.network.aion.clientpackets.CM_TELEPORT;
import com.google.inject.Injector;

/**
 * This factory is responsible for creating {@link AionPacketHandler} object. It also initializes created handler with a
 * set of packet prototypes.<br>
 * Object of this classes uses <tt>Injector</tt> for injecting dependencies into prototype objects.<br>
 * <br>
 * 
 * @author Luno
 * 
 */
public class AionPacketHandlerFactory
{
	private Injector			injector;
	private AionPacketHandler	handler	= new AionPacketHandler();

	/**
	 * Creates new instance of <tt>AionPacketHandlerFactory</tt><br>
	 * 
	 * @param injector
	 */
	public AionPacketHandlerFactory(Injector injector)
	{
		this.injector = injector;

		addPacket(new CM_RECONNECT_AUTH(Version.Chiness ? 0x9B : 0x21), State.AUTHED);
		addPacket(new CM_L2AUTH_LOGIN_CHECK(Version.Chiness ? 0xB9 : 0x7F), State.CONNECTED);
		addPacket(new CM_VERSION_CHECK(Version.Chiness ? 0x4c : 0xEA), State.CONNECTED);
		addPacket(new CM_TIME_CHECK(Version.Chiness ? 0x3e : 0xFC), State.CONNECTED, State.AUTHED, State.IN_GAME);
		addPacket(new CM_ATTACK(Version.Chiness ? -1 : 0x8A), State.IN_GAME); // maybe 0x13
		addPacket(new CM_SET_NOTE(Version.Chiness? -1 : 0x30), State.IN_GAME); //0x1A on 1.2>
		addPacket(new CM_TARGET_SELECT(Version.Chiness ? 0x33 : 0x89), State.IN_GAME);
		addPacket(new CM_SHOW_FRIENDLIST(Version.Chiness ? 0xE2 : 0x58), State.IN_GAME);
		addPacket(new CM_SHOW_BLOCKLIST(Version.Chiness ? 0xA4 : 0x12), State.IN_GAME);
		addPacket(new CM_SEARCH_STATUS(Version.Chiness ? 0xEC : 0x4A), State.IN_GAME);
		addPacket(new CM_RESTORE_CHARACTER(Version.Chiness ? 0xB5 : 0x03), State.AUTHED);
		addPacket(new CM_QUIT(Version.Chiness ? 0x4F : 0xED), State.AUTHED, State.IN_GAME);
		addPacket(new CM_QUESTION_RESPONSE(Version.Chiness ? -1 : 0x22), State.IN_GAME);
		addPacket(new CM_PLAYER_SEARCH(Version.Chiness ? -1 : 0xB1), State.IN_GAME);
		addPacket(new CM_PING(Version.Chiness ? 0x20 : 0x96), State.AUTHED, State.IN_GAME);
		addPacket(new CM_MOVE(Version.Chiness ? 0x1C : 0x9A), State.IN_GAME);
		addPacket(new CM_MAY_LOGIN_INTO_GAME(Version.Chiness ? 0x96 : 0x24), State.AUTHED);
		addPacket(new CM_MACRO_DELETE(Version.Chiness ? 0x9C : 0xA0), State.IN_GAME);// 1.5.x unknown
		addPacket(new CM_MACRO_CREATE(Version.Chiness ? 0xA3 : 0xA1), State.IN_GAME);// 1.5.x unknown
		addPacket(new CM_MAC_ADDRESS2(Version.Chiness ? 0xA2 : 0x18), State.IN_GAME);// not sure
		addPacket(new CM_MAC_ADDRESS(Version.Chiness ? 0x91 : 0x27), State.CONNECTED, State.AUTHED, State.IN_GAME);
		//addPacket(new CM_LOGIN_OUT(Version.Chiness ? 0x4E : 0xEE), State.AUTHED, State.IN_GAME); // 1.5.x unknown (maybe ED)
		addPacket(new CM_LOGIN_OUT(Version.Chiness ? 0x4E : 0xEC), State.AUTHED, State.IN_GAME);
		addPacket(new CM_LEVEL_READY(Version.Chiness ? 0x45 : 0xF3), State.IN_GAME);
		addPacket(new CM_FRIEND_STATUS(Version.Chiness ? 0xA6 : 0x14), State.IN_GAME);// 1.5.x unknown
		addPacket(new CM_FRIEND_DEL(Version.Chiness ? -1 : 0xE0), State.IN_GAME);// 1.5.x unknown
		addPacket(new CM_FRIEND_ADD(Version.Chiness ? -1 : 0xE1), State.IN_GAME);// 1.5.x unknown
		addPacket(new CM_ENTER_WORLD(Version.Chiness ? 0x44 : 0xF2), State.AUTHED);
		addPacket(new CM_EMOTION(Version.Chiness ? 0x27 : 0x95), State.IN_GAME);
		addPacket(new CM_DELETE_CHARACTER(Version.Chiness ? 0xB4 : 0x02), State.AUTHED);// 1.5.x unknown
		addPacket(new CM_CREATE_CHARACTER(Version.Chiness ? 0xBB : 0x01), State.AUTHED);// 1.5.x unknown
		addPacket(new CM_CLIENT_COMMAND_LOC(Version.Chiness ? 0xC2 : 0xC6), State.IN_GAME);// 1.5.x unknown
		addPacket(new CM_CHECK_NICKNAME(Version.Chiness ? 0x9D : 0x1B), State.AUTHED);// 1.5.x unknown
		addPacket(new CM_CHAT_MESSAGE_WHISPER(Version.Chiness ? 0x30 : 0x34), State.IN_GAME);// 1.5.x unknown
		addPacket(new CM_CHAT_MESSAGE_PUBLIC(Version.Chiness ? 0x37 : 0x85), State.IN_GAME);
		addPacket(new CM_CHARACTER_LIST(Version.Chiness ? 0xBA : 0x00), State.AUTHED);
		addPacket(new CM_BLOCK_SET_REASON(Version.Chiness ? -1 : 0x9D),State.IN_GAME);
		addPacket(new CM_BLOCK_DEL(Version.Chiness ? -1 : 0xA9), State.IN_GAME);
		addPacket(new CM_BLOCK_ADD(Version.Chiness ? -1 : 0xAE), State.IN_GAME);
		addPacket(new CM_TRADE_REQUEST(Version.Chiness ? 0x11 : 0x11), State.IN_GAME);
		addPacket(new CM_TRADE_LOCK(Version.Chiness ? 0x0D : 0x0D), State.IN_GAME);
		addPacket(new CM_TRADE_CANCEL(Version.Chiness ? 0x0F : 0x0F), State.IN_GAME);
		addPacket(new CM_TRADE_OK(Version.Chiness ? 0x0C : 0x0C), State.IN_GAME);
		addPacket(new CM_TERRITORY(Version.Chiness ? -1 : 0x4A), State.IN_GAME);
		addPacket(new CM_START_LOOT(Version.Chiness ? -1 : 0x04), State.IN_GAME);
		addPacket(new CM_LOOT_ITEM(Version.Chiness ? -1 : 0x05), State.IN_GAME);
		addPacket(new CM_CLOSE_LOOT(Version.Chiness ? -1 : 0x06), State.IN_GAME);
		addPacket(new CM_CASTSPELL(Version.Chiness ? -1 : 0x8B), State.IN_GAME);
		//addPacket(new CM_TELEPORT(Version.Chiness ? -1 : 0x9E), State.IN_GAME);
		addPacket(new CM_SHOW_DIALOG(Version.Chiness ? -1 : 0x9E), State.IN_GAME);
		addPacket(new CM_DIALOG_SELECT(Version.Chiness ? -1 : 0xA0), State.IN_GAME);
		addPacket(new CM_CLOSE_DIALOG(Version.Chiness ? -1 :0x9f ), State.IN_GAME);//0x9F

	}

	public AionPacketHandler getPacketHandler()
	{
		return handler;
	}

	private void addPacket(AionClientPacket prototype, State... states)
	{
		injector.injectMembers(prototype);
		handler.addPacketPrototype(prototype, states);
	}

}