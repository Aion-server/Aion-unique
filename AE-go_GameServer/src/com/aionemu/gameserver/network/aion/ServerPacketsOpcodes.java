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
package com.aionemu.gameserver.network.aion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.aionemu.gameserver.network.aion.serverpackets.unk.*;
import com.aionemu.gameserver.network.aion.serverpackets.*;

/**
 * This class is holding opcodes for all server packets. It's used only to have all opcodes in one place
 * 
 * @author Luno
 * @author alexa026
 * @author ATracer
 * @author avol
 * @author orz
 */
public class ServerPacketsOpcodes
{
	private static Map<Class<? extends AionServerPacket>, Integer>	opcodes	= new HashMap<Class<? extends AionServerPacket>, Integer>();

	static
	{
		Set<Integer> idSet = new HashSet<Integer>();

		addPacketOpcode(SM_CUSTOM_PACKET.class, 99999, idSet); // fake packet
		
		addPacketOpcode(SM_UNKF5.class, 0x26, idSet);
		addPacketOpcode(SM_UNKEF.class, -1, idSet);
		addPacketOpcode(SM_UNKE7.class, -1, idSet);
		addPacketOpcode(SM_UNKE1.class, -1, idSet);
		addPacketOpcode(SM_UNKD9.class, -1, idSet); // was 0xE3
		addPacketOpcode(SM_UNKD3.class, -1, idSet);
		addPacketOpcode(SM_UNKCB.class, -1, idSet);
		addPacketOpcode(SM_UNKC8.class, -1, idSet);
		addPacketOpcode(SM_UNKC7.class, -1, idSet);
		addPacketOpcode(SM_UNKC6.class, -1, idSet);
		addPacketOpcode(SM_UNKBD.class, 0xCF, idSet);
		addPacketOpcode(SM_UNK97.class, -1, idSet);
		addPacketOpcode(SM_UNK91.class, -1, idSet);
		addPacketOpcode(SM_UNK8D.class, -1, idSet);
		addPacketOpcode(SM_UNK66.class, -1, idSet);
		addPacketOpcode(SM_UNK64.class, -1, idSet);
		addPacketOpcode(SM_UNK60.class, -1, idSet);
		addPacketOpcode(SM_UNK5E.class, -1, idSet);
		addPacketOpcode(SM_UNK72.class, 0x72, idSet);//1.5.x
		addPacketOpcode(SM_UNK32.class, -1, idSet);
		addPacketOpcode(SM_UNK17.class, -1, idSet);
		addPacketOpcode(SM_UNK0C.class, 0x48, idSet);
		addPacketOpcode(SM_UNK0A.class, -1, idSet);
		addPacketOpcode(SM_VERSION_CHECK.class, 0xF9, idSet);
		addPacketOpcode(SM_SYSTEM_MESSAGE.class, 0x30, idSet);
		addPacketOpcode(SM_STATUPDATE_MP.class, 0xFD, idSet);
		addPacketOpcode(SM_STATUPDATE_HP.class, 0xFA, idSet); //0x12//need opcode for 1.5.x client
		addPacketOpcode(SM_STATUPDATE_EXP.class, 0x01, idSet); // 1.5.x
		addPacketOpcode(SM_STATUPDATE_DP.class, 0x17, idSet); // 0x17
		addPacketOpcode(SM_STATS_INFO.class, 0xF8, idSet);
		addPacketOpcode(SM_SKILL_LIST.class, 0x45, idSet); // 0x45
		addPacketOpcode(SM_RESTORE_CHARACTER.class, 0xE2, idSet);
		addPacketOpcode(SM_RECONNECT_KEY.class, 0xF6, idSet);
		addPacketOpcode(SM_QUIT_RESPONSE.class, 0x5B, idSet); // 63
		addPacketOpcode(SM_QUESTION_WINDOW.class, 0x4D, idSet);
		addPacketOpcode(SM_PONG.class, 0xA7, idSet);
		addPacketOpcode(SM_PLAYER_STATE.class, 0x3D, idSet);
		addPacketOpcode(SM_PLAYER_SEARCH.class, 0xEA, idSet);
		addPacketOpcode(SM_PLAYER_INFO.class, 0x19, idSet);
		addPacketOpcode(SM_NPC_INFO.class, 0x27, idSet);
		addPacketOpcode(SM_NICKNAME_CHECK_RESPONSE.class, 0xE0, idSet);
		addPacketOpcode(SM_MOVE.class, 0x4E, idSet);
		addPacketOpcode(SM_MESSAGE.class, 0x31, idSet);
		addPacketOpcode(SM_MAY_LOGIN_INTO_GAME.class, 0x80, idSet);
		addPacketOpcode(SM_MACRO_LIST.class, 0xDE, idSet);
		addPacketOpcode(SM_L2AUTH_LOGIN_CHECK.class, 0xBE, idSet);
		addPacketOpcode(SM_KEY.class, 0x41, idSet);
		addPacketOpcode(SM_INVENTORY_INFO.class, 0x13, idSet); //1.5.x**
		addPacketOpcode(SM_GAME_TIME.class, 0x1F, idSet);
		addPacketOpcode(SM_FRIEND_UPDATE.class, 0x09, idSet);
		addPacketOpcode(SM_FRIEND_RESPONSE.class, 0xD7, idSet);
		addPacketOpcode(SM_FRIEND_NOTIFY.class, 0xD8, idSet);
		addPacketOpcode(SM_FRIEND_LIST.class, 0x7D, idSet);
		addPacketOpcode(SM_ENTER_WORLD_CHECK.class, 0x24, idSet);
		addPacketOpcode(SM_EMOTION.class, 0x1C, idSet);
		addPacketOpcode(SM_DELETE.class, 0x2F, idSet);
		addPacketOpcode(SM_DELETE_CHARACTER.class, 0xE3, idSet);
		addPacketOpcode(SM_CREATE_CHARACTER.class, 0xC0, idSet);
		addPacketOpcode(SM_CHARACTER_LIST.class, 0xC1, idSet);
		addPacketOpcode(SM_BLOCK_RESPONSE.class, 0xD6 ,idSet);
		addPacketOpcode(SM_BLOCK_LIST.class, 0xD9, idSet);
		addPacketOpcode(SM_ATTACK.class, 0x4F, idSet);
		addPacketOpcode(SM_ATTACK_STATUS.class, 0xFC, idSet);
		addPacketOpcode(SM_LOOT_STATUS.class, 0xE4, idSet);
		
		addPacketOpcode(SM_WEATHER.class, 0x4A, idSet);
		
		addPacketOpcode(SM_CASTSPELL.class, 0x18, idSet);	//1.5.x 
		addPacketOpcode(SM_CASTSPELL_END.class, 0x42, idSet);	//1.5.x 
		addPacketOpcode(SM_INVENTORY_UPDATE.class, 0x12, idSet);	//1.5.x 
		addPacketOpcode(SM_LOOT_ITEMLIST.class, 0xE7, idSet);	//1.5.x 
		
		addPacketOpcode(SM_TRADELIST.class, 0xF4, idSet);   //1.5.x 
		addPacketOpcode(SM_DIALOG.class, 0x21, idSet);//1.5.x
		addPacketOpcode(SM_DIALOG_WINDOW.class, 0x35, idSet);//1.5.x
		addPacketOpcode(SM_SELL_ITEM.class, 0x37, idSet);//1.5.x
		addPacketOpcode(SM_DELETE_ITEM.class, 0x15, idSet);//1.5.x
		addPacketOpcode(SM_UPDATE_ITEM.class, 0x14, idSet);//1.5.x
		addPacketOpcode(SM_UPDATE_PLAYER_APPEARANCE.class, 0x1D, idSet);	//1.5.x

		addPacketOpcode(SM_LEVEL_UPDATE.class, 0x3f, idSet);//1.5.x
		
		addPacketOpcode(SM_FLY_TELEPORT.class, 0xC2, idSet);//1.5.x
		addPacketOpcode(SM_DIE.class, 0xb8, idSet);	//1.5.x
		
		



		//Unrecognized Opcodes:
		
		addPacketOpcode(SM_TELEPORT.class, 0xBD, idSet);
		//addPacketOpcode(SM_TIME_CHECK.class, 0x26, idSet);
		//addPacketOpcode(SM_VIRTUAL_AUTH.class, 0xE4, idSet);
		//addPacketOpcode(SM_WAITING_LIST.class, 0x18, idSet);		
	}

	static int getOpcode(Class<? extends AionServerPacket> packetClass)
	{
		Integer opcode = opcodes.get(packetClass);
		if(opcode == null)
			throw new IllegalArgumentException("There is no opcode for " + packetClass + " defined.");

		return opcode;
	}

	private static void addPacketOpcode(Class<? extends AionServerPacket> packetClass, int opcode, Set<Integer> idSet)
	{
		if(opcode < 0)
			return;

		if(idSet.contains(opcode))
			throw new IllegalArgumentException(String.format("There already exists another packet with id 0x%02X", opcode));

		
		idSet.add(opcode);
		opcodes.put(packetClass, opcode);
	}
}
