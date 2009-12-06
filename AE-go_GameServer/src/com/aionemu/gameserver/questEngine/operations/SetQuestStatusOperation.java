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

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.QuestListDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_STEP;
import com.aionemu.gameserver.questEngine.Quest;
import com.aionemu.gameserver.questEngine.QuestEngineException;
import com.aionemu.gameserver.questEngine.QuestState;
import com.aionemu.gameserver.questEngine.types.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author MrPoke
 */
public class SetQuestStatusOperation extends QuestOperation
{

	private static final String NAME = "set_quest_status";
	private final QuestStatus value;

	public SetQuestStatusOperation(NamedNodeMap attr, Quest quest)
	{
		super(attr, quest);
		this.value = QuestStatus.valueOf(attr.getNamedItem("status").getNodeValue());
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
			qs.setStatus(value);
			PacketSendUtility.sendPacket(player, new SM_QUEST_STEP(getQuest().getId(), value , qs.getQuestVars().getQuestVars()));
			player.updateNearbyQuests();
	    	DAOManager.getDAO(QuestListDAO.class).store(player.getObjectId(), qs);
		}
	}
}
