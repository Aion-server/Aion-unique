/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services;

import java.util.Arrays;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACCEPTED;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_STEP;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer, sweetkr
 * 
 */
public class ClassChangeService
{
	/**
	 * TODO remove after class change quest is done
	 * 
	 * @param player
	 */
	public static void showClassChangeDialog(Player player)
	{
		if(CustomConfig.ENABLE_SIMPLE_2NDCLASS)
		{
			PlayerClass playerClass = player.getPlayerClass();
			Race playerRace = player.getCommonData().getRace();
			if(player.getLevel() >= 9 && Arrays.asList(0, 3, 6, 9).contains(playerClass.ordinal()))
			{
				if(playerRace.ordinal() == 0)
				{
					switch(playerClass.ordinal())
					{
						case 0:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 2375, 1006));
							break;
						case 3:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 2716, 1006));
							break;
						case 6:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3057, 1006));
							break;
						case 9:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3398, 1006));
							break;
					}
				}
				else if(playerRace.ordinal() == 1)
				{
					switch(playerClass.ordinal())
					{
						case 0:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3057, 2008));
							break;
						case 3:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3398, 2008));
							break;
						case 6:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3739, 2008));
							break;
						case 9:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 4080, 2008));
							break;
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param player
	 * @param dialogId
	 */
	public static void changeClassToSelection(final Player player, final int dialogId)
	{
		if(CustomConfig.ENABLE_SIMPLE_2NDCLASS)
		{
			Race playerRace = player.getCommonData().getRace();
			if(playerRace.ordinal() == 0)
			{
				switch(dialogId)
				{
					case 2376:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("1")));
						break;
					case 2461:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("2")));
						break;
					case 2717:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("4")));
						break;
					case 2802:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("5")));
						break;
					case 3058:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("7")));
						break;
					case 3143:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("8")));
						break;
					case 3399:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("10")));
						break;
					case 3484:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("11")));
						break;
				}
				addCompliteQuest(player, 1006);
				addCompliteQuest(player, 1007);
			}
			else if(playerRace.ordinal() == 1)
			{
				switch(dialogId)
				{
					case 3058:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("1")));
						break;
					case 3143:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("2")));
						break;
					case 3399:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("4")));
						break;
					case 3484:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("5")));
						break;
					case 3740:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("7")));
						break;
					case 3825:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("8")));
						break;
					case 4081:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("10")));
						break;
					case 4166:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("11")));
						break;
				}
				addCompliteQuest(player, 2008);
				addCompliteQuest(player, 2009);
			}
		}
	}

	private static void addCompliteQuest(Player player, int questId)
	{
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
		{
			player.getQuestStateList().addQuest(questId, new QuestState(questId, QuestStatus.COMPLITE, 0, 0));
			PacketSendUtility.sendPacket(player, new SM_QUEST_ACCEPTED(questId, QuestStatus.COMPLITE.value(), 0));
		}
		else
		{
			qs.setStatus(QuestStatus.COMPLITE);
			PacketSendUtility.sendPacket(player, new SM_QUEST_STEP(questId, qs.getStatus(), qs.getQuestVars()
				.getQuestVars()));
		}
	}

	private static void setClass(Player player, PlayerClass playerClass)
	{
		player.getCommonData().setPlayerClass(playerClass);
		player.getCommonData().upgradePlayer();
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 0, 0));
	}
}
