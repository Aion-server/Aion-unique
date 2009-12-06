/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.questEngine.operations;

import static com.aionemu.gameserver.configs.Config.QUEST_XP_RATE;

import org.w3c.dom.NamedNodeMap;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.Quest;
import com.aionemu.gameserver.questEngine.QuestEngineException;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author MrPoke
 */
public class GiveExpOperation extends QuestOperation
{
	private static final String NAME = "give_exp";
	private final int count;

	public GiveExpOperation(NamedNodeMap attr, Quest quest)
	{
		super(attr, quest);
		count = Integer.parseInt(attr.getNamedItem("value").getNodeValue());
	}

	@Override
	protected void doOperate(Player player) throws QuestEngineException
	{
		int reward=(QUEST_XP_RATE * count);
		long currentExp = player.getCommonData().getExp();
		player.getCommonData().setExp(currentExp + reward);
		PacketSendUtility.sendPacket(player,SM_SYSTEM_MESSAGE.EXP(Integer.toString(reward)));
	}

	@Override
	public String getName()
	{
		return NAME;
	}
}
