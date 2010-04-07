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
public class _2020KeepingtheBlackClawTribeinCheck extends QuestHandler
{

	private final static int	questId	= 2020;

	public _2020KeepingtheBlackClawTribeinCheck()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.addQuestLvlUp(questId);
		qe.setNpcQuestData(203665).addOnTalkEvent(questId);
		qe.setNpcQuestData(203668).addOnTalkEvent(questId);
		qe.setNpcQuestData(210562).addOnKillEvent(questId);
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
				case 203665:
					switch(env.getDialogId())
					{
						case 25:
							if(var == 0)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
							break;
						case 10000:
							if (var == 0)
							{
								qs.setQuestVarById(0, var+1);
								updateQuestStatus(player, qs);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
							}
					}
					break;
				case 203668:
					switch(env.getDialogId())
					{
						case 25:
							if(var == 1)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
							else if(var == 5)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1693);
							else if(var == 6)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2034);
							break;
						case 10001:
						case 10002:
							if (var == 1 || var== 5)
							{
								qs.setQuestVarById(0, var+1);
								updateQuestStatus(player, qs);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
							}
						case 33:
							if (var == 6)
							{
								if(collectItemCheck(env))
								{
									qs.setStatus(QuestStatus.REWARD);
									updateQuestStatus(player, qs);
									return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 5);
								}
								else
									return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2120);
							}
					}
			}
		}
		else if(qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 203668)
					return defaultQuestEndDialog(env);
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		
		if (targetId == 210562 && var >= 2 && var < 5)
		{
			qs.setQuestVarById(0, var+1);
			updateQuestStatus(player, qs);
			return true;
		}
		return false;
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.LOCKED || player.getLevel() < 14)
			return false;
		qs.setStatus(QuestStatus.START);
		updateQuestStatus(player, qs);
		return true;
	}
}
