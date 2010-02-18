/*
 * This file is part of aion-unique <aion-unique.org>.
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
package quest.poeta;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author MrPoke
 * 
 */
public class _1205ANewSkill extends QuestHandler
{
	private final static int	questId	= 1205;

	public _1205ANewSkill()
	{
		super(questId);
		QuestEngine.getInstance().addQuestLvlUp(questId);
		QuestEngine.getInstance().setNpcQuestData(203087).addOnTalkEvent(questId); // Warrior
		QuestEngine.getInstance().setNpcQuestData(203088).addOnTalkEvent(questId); // Scout
		QuestEngine.getInstance().setNpcQuestData(203089).addOnTalkEvent(questId); // Mage
		QuestEngine.getInstance().setNpcQuestData(203090).addOnTalkEvent(questId); // Priest
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		if (player.getCommonData().getLevel() < 3)
			return false;
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs != null)
			return false;
		env.setQuestId(questId);
		if (QuestEngine.getInstance().getQuest(env).startQuest(QuestStatus.START))
		{
			qs = player.getQuestStateList().getQuestState(questId);
			qs.setStatus(QuestStatus.REWARD);
			switch(player.getCommonData().getPlayerClass())
			{
				case WARRIOR:
					qs.getQuestVars().setQuestVar(1);
					break;
				case SCOUT:
					qs.getQuestVars().setQuestVar(2);
					break;
				case MAGE:
					qs.getQuestVars().setQuestVar(3);
					break;
				case PRIEST:
					qs.getQuestVars().setQuestVar(4);
					break;
			}
			updateQuestStatus(player, qs);
		}
		return true;
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.REWARD)
			return false;

		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		PlayerClass playerClass = player.getCommonData().getPlayerClass();
		switch(targetId)
		{
			case 203087:
				if(playerClass == PlayerClass.WARRIOR)
				{
					if(env.getDialogId() == -1)
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
					else if(env.getDialogId() == 1009)
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 5);
					else
						return this.defaultQuestEndDialog(env);
				}
				return false;
			case 203088:
				if(playerClass == PlayerClass.SCOUT)
				{
					if(env.getDialogId() == -1)
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
					else if(env.getDialogId() == 1009)
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 6);
					else
						return this.defaultQuestEndDialog(env);
				}
				return false;
			case 203089:
				if(playerClass == PlayerClass.MAGE)
				{
					if(env.getDialogId() == -1)
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1693);
					else if(env.getDialogId() == 1009)
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 7);
					else
						return this.defaultQuestEndDialog(env);
				}
				return false;
			case 203090:
				if(playerClass == PlayerClass.PRIEST)
				{
					if(env.getDialogId() == -1)
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2034);
					else if(env.getDialogId() == 1009)
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 8);
					else
						return this.defaultQuestEndDialog(env);
				}
				return false;
		}

		return false;
	}
}
