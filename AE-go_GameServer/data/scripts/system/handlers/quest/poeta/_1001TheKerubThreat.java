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

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author MrPoke
 * 
 */
public class _1001TheKerubThreat extends QuestHandler
{
	private final static int	questId	= 1001;

	public _1001TheKerubThreat()
	{
		super(questId);
		QuestEngine.getInstance().setNpcQuestData(210670).addOnKillEvent(questId);
		QuestEngine.getInstance().setNpcQuestData(203071).addOnTalkEvent(questId);
		QuestEngine.getInstance().setNpcQuestData(203067).addOnTalkEvent(questId);
		QuestEngine.getInstance().addQuestLvlUp(questId);
	}

	@Override
	public boolean onKillEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		int var = qs.getQuestVars().getQuestVarById(0);
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if(qs.getStatus() != QuestStatus.START)
			return false;
		if(targetId == 210670)
		{
			if(var > 0 && var < 6)
			{
				qs.getQuestVars().setQuestVarById(0, qs.getQuestVars().getQuestVarById(0) + 1);
				updateQuestStatus(player, qs);
				return true;
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
		qs.setStatus(QuestStatus.START);
		updateQuestStatus(player, qs);
		return true;
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		int var = qs.getQuestVars().getQuestVarById(0);
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if(qs.getStatus() == QuestStatus.START)
		{
			if(targetId == 203071)
			{
				switch(env.getDialogId())
				{
					case 1013:
						PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 15));
						return false;
					case 25:
						if(var == 0)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
						else if(var == 6)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
						else if(var == 7)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1693);
						return false;
					case 10002:
					case 33:
						if(var == 7)
						{
							int itemCount = player.getInventory().getItemCountByItemId(182200001);
							if(itemCount >= 5)
							{
								if(env.getDialogId() == 33)
								{
									return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1694);
								}
								else
								{
									player.getInventory().removeFromBagByItemId(182200001, itemCount);
									qs.getQuestVars().setQuestVarById(0, var + 1);
									qs.setStatus(QuestStatus.REWARD);
									updateQuestStatus(player, qs);
									PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
										.getObjectId(), 10));
									return true;
								}
							}
							else
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1779);
						}
						return true;
					case 10000:
					case 10001:
						if(var == 0 || var == 6)
						{
							qs.getQuestVars().setQuestVarById(0, var + 1);
							updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
								.getObjectId(), 10));
						}
						return true;
					default:
						return false;
				}
			}
		}
		else if(qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 203067)
			{
				return defaultQuestEndDialog(env);
			}
		}
		return false;
	}

}
