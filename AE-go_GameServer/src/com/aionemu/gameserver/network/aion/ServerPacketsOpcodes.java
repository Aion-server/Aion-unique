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

import com.aionemu.gameserver.network.aion.serverpackets.SM_ABNORMAL_EFFECT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABNORMAL_STATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_BLOCK_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_BLOCK_RESPONSE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_BUY_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL_END;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CHARACTER_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CREATE_CHARACTER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUSTOM_PACKET;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_CHARACTER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DUEL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ENTER_WORLD_CHECK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EXCHANGE_ADD_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EXCHANGE_ADD_KINAH;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EXCHANGE_CONFIRMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EXCHANGE_REQUEST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_NOTIFY;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_RESPONSE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GAME_TIME;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GATHERABLE_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GATHER_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GATHER_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GROUP_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GROUP_MEMBER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GUILD_DETAILS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GUILD_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GUILD_MEMBERS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INFLUENCE_RATIO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_KEY;
import com.aionemu.gameserver.network.aion.serverpackets.SM_L2AUTH_LOGIN_CHECK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEVEL_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOKATOBJECT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_ITEMLIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MACRO_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MACRO_RESULT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MAY_LOGIN_INTO_GAME;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOVE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NEARBY_QUESTS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NICKNAME_CHECK_RESPONSE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NPC_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PING_RESPONSE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_ID;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_SEARCH;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_SPAWN;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PONG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PRICES;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACCEPTED;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_DELETE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_STEP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUIT_RESPONSE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RECONNECT_KEY;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RESTORE_CHARACTER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SELL_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SET_BIND_POINT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SHOW_NPC_ON_MAP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATUPDATE_DP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATUPDATE_EXP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATUPDATE_HP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATUPDATE_MP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TARGET_SELECTED;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TELEPORT_LOC;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TELEPORT_MAP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TIME_CHECK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TITLE_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TITLE_SET;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TITLE_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TRADELIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UI_SETTINGS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_NOTE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_PLAYER_APPEARANCE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_VERSION_CHECK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_VIEW_PLAYER_DETAILS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_WEATHER;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNK04;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNK05;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNK07;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FLY_TIME;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNK32;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNK33;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNK42;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNK5E;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNK68;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNK6D;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNK73;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNK7B;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNK80;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNK89;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNK98;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKAE;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKB4;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKC8;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKD0;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKD3;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKDA;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKDC;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKE4;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKE6;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKE8;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKEB;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKEC;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKEE;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKF1;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKF7;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEAVE_GROUP_MEMBER;

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
		addPacketOpcode(SM_ABNORMAL_EFFECT.class, 0x4A, idSet);
		addPacketOpcode(SM_ABNORMAL_STATE.class, 0x4B, idSet);
		addPacketOpcode(SM_ATTACK_STATUS.class, 0xFF, idSet);
		addPacketOpcode(SM_ATTACK.class, 0x4E, idSet);
		addPacketOpcode(SM_BLOCK_LIST.class, 0xD8, idSet);
		addPacketOpcode(SM_BLOCK_RESPONSE.class, 0xD9, idSet);
		addPacketOpcode(SM_BUY_LIST.class, 0x7E, idSet);
		addPacketOpcode(SM_CASTSPELL_END.class, 0x45, idSet);
		addPacketOpcode(SM_CASTSPELL.class, 0x1B, idSet);
		addPacketOpcode(SM_CHARACTER_LIST.class, 0xC0, idSet);
		addPacketOpcode(SM_CREATE_CHARACTER.class, 0xE3, idSet);
		addPacketOpcode(SM_DELETE_CHARACTER.class, 0xE2, idSet);
		addPacketOpcode(SM_DELETE_ITEM.class, 0x14, idSet);
		addPacketOpcode(SM_DELETE.class, 0x2E, idSet);
		addPacketOpcode(SM_DIALOG_WINDOW.class, 0x34, idSet);
		addPacketOpcode(SM_DIE.class, 0xBB, idSet);
		addPacketOpcode(SM_DUEL.class, 0xB3, idSet);
		addPacketOpcode(SM_EMOTION_LIST.class, 0x69, idSet);
		addPacketOpcode(SM_EMOTION.class, 0x1F, idSet);
		addPacketOpcode(SM_ENTER_WORLD_CHECK.class, 0x27, idSet);
		addPacketOpcode(SM_FRIEND_LIST.class, 0x7C, idSet);
		addPacketOpcode(SM_FRIEND_NOTIFY.class, 0xD7, idSet);
        addPacketOpcode(SM_FRIEND_RESPONSE.class, 0xD6, idSet);
		addPacketOpcode(SM_FRIEND_UPDATE.class, 0x09, idSet);
		addPacketOpcode(SM_GAME_TIME.class, 0x1E, idSet);
		addPacketOpcode(SM_GATHER_STATUS.class, 0x1A, idSet);
		addPacketOpcode(SM_GATHER_UPDATE.class, 0x1D, idSet);
		addPacketOpcode(SM_GATHERABLE_INFO.class, 0x2B, idSet);
		addPacketOpcode(SM_GUILD_DETAILS.class, 0x24, idSet);
		addPacketOpcode(SM_GUILD_INFO.class, 0x86, idSet);
		addPacketOpcode(SM_GUILD_MEMBERS.class, 0x97, idSet);
		addPacketOpcode(SM_INVENTORY_INFO.class, 0x12, idSet);
		addPacketOpcode(SM_INVENTORY_UPDATE.class, 0x15, idSet);
		addPacketOpcode(SM_ITEM_USAGE_ANIMATION.class, 0xD1, idSet);
		addPacketOpcode(SM_KEY.class, 0x40, idSet);
		addPacketOpcode(SM_L2AUTH_LOGIN_CHECK.class, 0xC1, idSet);
		addPacketOpcode(SM_LEVEL_UPDATE.class, 0x3E, idSet);
		addPacketOpcode(SM_LOOKATOBJECT.class, 0x20, idSet);
		addPacketOpcode(SM_LOOT_ITEMLIST.class, 0xE6, idSet);
		addPacketOpcode(SM_LOOT_STATUS.class, 0xE7, idSet);
		addPacketOpcode(SM_MACRO_LIST.class, 0xE1, idSet);
		addPacketOpcode(SM_MACRO_RESULT.class, 0xE0, idSet);
		addPacketOpcode(SM_MAY_LOGIN_INTO_GAME.class, 0xA3, idSet);
		addPacketOpcode(SM_MESSAGE.class, 0x30, idSet);
		addPacketOpcode(SM_MOVE.class, 0x51, idSet);
		addPacketOpcode(SM_NICKNAME_CHECK_RESPONSE.class, 0x03, idSet);
		addPacketOpcode(SM_NPC_INFO.class, 0x26, idSet);
		addPacketOpcode(SM_PING_RESPONSE.class, 0x78, idSet);
		addPacketOpcode(SM_PLAY_MOVIE.class, 0x83, idSet);
		addPacketOpcode(SM_PLAYER_INFO.class, 0x18, idSet);
		addPacketOpcode(SM_PLAYER_SEARCH.class, 0xED, idSet);
		addPacketOpcode(SM_PLAYER_STATE.class, 0x3C, idSet);
		addPacketOpcode(SM_PONG.class, 0xA6, idSet);
		addPacketOpcode(SM_QUEST_ACCEPTED.class, 0x74, idSet);
		addPacketOpcode(SM_QUEST_LIST.class, 0x75, idSet);
		addPacketOpcode(SM_QUEST_DELETE.class,0x76, idSet);
		addPacketOpcode(SM_QUESTION_WINDOW.class, 0x4C, idSet);
		addPacketOpcode(SM_QUIT_RESPONSE.class, 0x5A, idSet);
		addPacketOpcode(SM_RECONNECT_KEY.class, 0xF9, idSet);
		addPacketOpcode(SM_RESTORE_CHARACTER.class, 0xE5, idSet);
		addPacketOpcode(SM_SELL_ITEM.class, 0x36, idSet);
		addPacketOpcode(SM_SKILL_LIST.class, 0x44, idSet);
		addPacketOpcode(SM_STATS_INFO.class, 0xFB, idSet);
		addPacketOpcode(SM_STATUPDATE_DP.class, 0xFE, idSet);
		addPacketOpcode(SM_STATUPDATE_EXP.class, 0x00, idSet);
		addPacketOpcode(SM_STATUPDATE_HP.class, 0xFD, idSet);
		addPacketOpcode(SM_STATUPDATE_MP.class, 0xFC, idSet);
		addPacketOpcode(SM_SYSTEM_MESSAGE.class, 0x13, idSet);
		addPacketOpcode(SM_TELEPORT_LOC.class, 0x2C, idSet);
		addPacketOpcode(SM_TELEPORT_MAP.class, 0xBC, idSet);
		addPacketOpcode(SM_TIME_CHECK.class, 0x21, idSet);
        addPacketOpcode(SM_TITLE_LIST.class, 0xC8, idSet);
        addPacketOpcode(SM_TITLE_SET.class, 0xCB, idSet);
        addPacketOpcode(SM_TITLE_UPDATE.class, 0xCD, idSet);
        addPacketOpcode(SM_TRADELIST.class, 0xF7, idSet);
		addPacketOpcode(SM_TARGET_SELECTED.class, 0x43, idSet);
		addPacketOpcode(SM_UI_SETTINGS.class, 0x16, idSet);
		addPacketOpcode(SM_USE_OBJECT.class, 0xBF, idSet);
		addPacketOpcode(SM_SET_BIND_POINT.class, 0x05, idSet);
		addPacketOpcode(SM_UNK04.class, 0x07, idSet);
		addPacketOpcode(SM_UNK05.class, 0x04, idSet);
		addPacketOpcode(SM_UNK07.class, 0x06, idSet);
		addPacketOpcode(SM_FLY_TIME.class, 0x0C, idSet);
		addPacketOpcode(SM_UNK32.class, 0x32, idSet);
		addPacketOpcode(SM_UNK33.class, 0x33, idSet);
		addPacketOpcode(SM_UNK42.class, 0x42, idSet);
		addPacketOpcode(SM_UNK5E.class, 0x61, idSet);
		addPacketOpcode(SM_UNK68.class, 0x6B, idSet);
		addPacketOpcode(SM_UNK6D.class, 0x6C, idSet);
		addPacketOpcode(SM_UNK73.class, 0x72, idSet);
		addPacketOpcode(SM_UNK7B.class, 0x7A, idSet);
		addPacketOpcode(SM_UNK80.class, 0x80, idSet);
		addPacketOpcode(SM_UNK89.class, 0x89, idSet);
		addPacketOpcode(SM_UNK98.class, 0x9B, idSet);
		addPacketOpcode(SM_PLAYER_ID.class, 0xA7, idSet);
		addPacketOpcode(SM_UNKAE.class, 0xB1, idSet);
		addPacketOpcode(SM_UNKB4.class, 0xB7, idSet);
		addPacketOpcode(SM_UNKC8.class, 0xCA, idSet);
		addPacketOpcode(SM_UNKD0.class, 0xD0, idSet);
		addPacketOpcode(SM_UNKD3.class, 0xD3, idSet);
		addPacketOpcode(SM_UNKDA.class, 0xDD, idSet);
		addPacketOpcode(SM_UNKDC.class, 0xDF, idSet);
		addPacketOpcode(SM_UNKE4.class, 0xE4, idSet);
		addPacketOpcode(SM_UNKE6.class, 0xE9, idSet);
		addPacketOpcode(SM_UNKE8.class, 0xE8, idSet);
		addPacketOpcode(SM_UNKEB.class, 0xEB, idSet);
		addPacketOpcode(SM_UNKEC.class, 0xEF, idSet);
		addPacketOpcode(SM_UNKEE.class, 0xEE, idSet);
		addPacketOpcode(SM_UNKF1.class, 0xF0, idSet);	
		addPacketOpcode(SM_PLAYER_SPAWN.class, 0x29, idSet);
		addPacketOpcode(SM_UNKF7.class, 0xF6, idSet);
		addPacketOpcode(SM_UPDATE_ITEM.class, 0x17, idSet);
		addPacketOpcode(SM_UPDATE_NOTE.class, 0x60, idSet);
		addPacketOpcode(SM_UPDATE_PLAYER_APPEARANCE.class, 0x1C, idSet);
		addPacketOpcode(SM_VERSION_CHECK.class, 0xF8, idSet);
		addPacketOpcode(SM_VIEW_PLAYER_DETAILS.class, 0x3B, idSet);
		addPacketOpcode(SM_WEATHER.class, 0x3D, idSet);
		addPacketOpcode(SM_EXCHANGE_CONFIRMATION.class, 0x66, idSet);
		addPacketOpcode(SM_EXCHANGE_REQUEST.class, 0x62, idSet);
		addPacketOpcode(SM_EXCHANGE_ADD_ITEM.class, 0x65, idSet);
		addPacketOpcode(SM_EXCHANGE_ADD_KINAH.class, 0x67, idSet);

		addPacketOpcode(SM_QUEST_STEP.class, 0x77, idSet);
		addPacketOpcode(SM_NEARBY_QUESTS.class, 0x79, idSet);
		addPacketOpcode(SM_INFLUENCE_RATIO.class, 0x6F, idSet);
		addPacketOpcode(SM_PRICES.class, 0xF4, idSet);
		addPacketOpcode(SM_SHOW_NPC_ON_MAP.class, 0x53, idSet);		
		addPacketOpcode(SM_GROUP_INFO.class, 0x52, idSet);
		addPacketOpcode(SM_GROUP_MEMBER_INFO.class, 0x55, idSet);
		addPacketOpcode(SM_LEAVE_GROUP_MEMBER.class, 0x11, idSet);
		//Unrecognized Opcodes from 1.5.0:
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
