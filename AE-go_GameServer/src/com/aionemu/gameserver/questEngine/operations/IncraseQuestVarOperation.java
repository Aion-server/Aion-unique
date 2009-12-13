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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_STEP;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author MrPoke
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IncraseQuestVarOperation")
public class IncraseQuestVarOperation extends QuestOperation
{

	@XmlAttribute(name = "var_id", required = true)
	protected int	varId;
	@XmlAttribute(required = true)
	protected int	value;

	@Override
	public void doOperate(QuestEnv env)
	{
		Player player = env.getPlayer();
		int questId = env.getQuestId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs != null)
		{
			qs.getQuestVars().setQuestVarById(varId, qs.getQuestVars().getQuestVarById(varId) + value);
			PacketSendUtility.sendPacket(player, new SM_QUEST_STEP(questId, qs.getStatus(), qs.getQuestVars()
				.getQuestVars()));
		}
	}
}
