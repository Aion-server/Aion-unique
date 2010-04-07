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
package quest.altgard;

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
public class _2015TaketheInitiative extends QuestHandler
{

	private final static int	questId	= 2015;

	public _2015TaketheInitiative()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.addQuestLvlUp(questId);
		qe.setNpcQuestData(203631).addOnTalkEvent(questId);
		qe.setNpcQuestData(210510).addOnKillEvent(questId);
		qe.setNpcQuestData(210504).addOnKillEvent(questId);
		qe.setNpcQuestData(210506).addOnKillEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		final int var = qs.getQuestVarById(0);
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if(qs.getStatus() == QuestStatus.START)
		{
			switch (targetId)
			{
				case 203631:
					switch(env.getDialogId())
					{
						case -1:
							if (qs.getQuestVarById(1) >= 1 && qs.getQuestVarById(2) >= 5 && qs.getQuestVarById(3) >= 5)
							{
								qs.setQuestVarById(0, var + 1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(player, qs);
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
							}
							break;
						case 25:
							if(var == 0)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
							break;
						case 10000:
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
					}
			}
		}
		else if(qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 203631)
			{
				return defaultQuestEndDialog(env);
			}
		}
		return false;
	}

	public boolean onKillEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.START)
			return false;


		int targetId = 0;
		int var = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		switch(targetId)
		{
			case 210510:
				var = qs.getQuestVarById(1);
				if (var == 0)
				{
					qs.setQuestVarById(1, 1);
					updateQuestStatus(player, qs);
				}
				break;
			case 210504:
				var = qs.getQuestVarById(2);
				if (var < 5)
				{
					qs.setQuestVarById(2, var+1);
					updateQuestStatus(player, qs);
				}
				break;
			case 210506:
				var = qs.getQuestVarById(3);
				if (var < 5)
				{
					qs.setQuestVarById(3, var+1);
					updateQuestStatus(player, qs);
				}
		}
		return false;
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.LOCKED)
			return false;
		QuestState qs2 = player.getQuestStateList().getQuestState(2014);
		if(qs2 == null || qs2.getStatus() != QuestStatus.COMPLITE)
			return false;
		qs.setStatus(QuestStatus.START);
		updateQuestStatus(player, qs);
		return true;
	}
}
