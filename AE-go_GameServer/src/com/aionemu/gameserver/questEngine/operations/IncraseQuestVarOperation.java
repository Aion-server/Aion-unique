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

import org.w3c.dom.NamedNodeMap;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_STEP;
import com.aionemu.gameserver.questEngine.Quest;
import com.aionemu.gameserver.questEngine.QuestEngineException;
import com.aionemu.gameserver.questEngine.QuestState;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author MrPoke
 */
public class IncraseQuestVarOperation extends QuestOperation
{

	private static final String NAME = "incrase_quest_var";
	private final int value;
	private final int varId;

	public IncraseQuestVarOperation(NamedNodeMap attr, Quest quest)
	{
		super(attr, quest);
		this.value = Integer.parseInt(attr.getNamedItem("value").getNodeValue());
		this.varId = Integer.parseInt(attr.getNamedItem("var_id").getNodeValue());
	}

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	protected void doOperate(Player player) throws QuestEngineException
	{
		QuestState qs = player.getQuestStateList().getQuestState(getQuest().getId());
		if (qs != null)
		{
			qs.getQuestVars().setQuestVarById(varId, qs.getQuestVars().getQuestVarById(varId)+value);
			PacketSendUtility.sendPacket(player, new SM_QUEST_STEP(getQuest().getId(), qs.getStatus() , qs.getQuestVars().getQuestVars()));
		}
	}
}
