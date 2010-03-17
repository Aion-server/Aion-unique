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
package quest.ishalgen;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Mr. Poke
 *
 */
public class _2114TheInsectProblem extends QuestHandler
{
	private final static int	questId	= 2114;

	public _2114TheInsectProblem()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.setNpcQuestData(203533).addOnQuestStart(questId);
		qe.setNpcQuestData(203533).addOnTalkEvent(questId);
		qe.setNpcQuestData(210734).addOnKillEvent(questId);
		qe.setNpcQuestData(210380).addOnKillEvent(questId);
		qe.setNpcQuestData(210381).addOnKillEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(targetId == 203533)
		{
			if(qs == null || qs.getStatus() == QuestStatus.NONE)
			{
				switch (env.getDialogId())
				{
					case 25:
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
					case 10000:
						if (questService.startQuest(env, QuestStatus.START))
						{
							qs = player.getQuestStateList().getQuestState(questId);
							qs.setQuestVar(1);
							this.updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						}
					case 10001:
						if (questService.startQuest(env, QuestStatus.START))
						{
							qs = player.getQuestStateList().getQuestState(questId);
							qs.setQuestVar(11);
							this.updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						}
				}
			}
			else if (qs.getStatus() == QuestStatus.REWARD)
			{
				int var = qs.getQuestVarById(0);
				switch (env.getDialogId())
				{
					case -1:
						if (var == 10)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 5);
						else if (var == 20)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 6);
					case 17:
						if (questService.questFinish(env, var/10-1))
						{
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						}
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if(qs.getStatus() != QuestStatus.START)
			return false;
		switch(targetId)
		{
			case 210734:
				if(var >= 1 && var < 10)
				{
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(player, qs);
					return true;
				}
				else if (var == 10)
				{
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(player, qs);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
			case 210380:
			case 210381:
				if(var >= 11 && var < 20)
				{
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(player, qs);
					return true;
				}
				else if (var == 20)
				{
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(player, qs);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
		}
		return false;
	}
}
