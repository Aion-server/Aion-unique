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
package quest.verteron;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author MrPoke + Dune11
 *
 */
public class _1017HeldSacred extends QuestHandler
{
	private final static int	questId	= 1017;
	
	public _1017HeldSacred()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.setNpcQuestData(203178).addOnTalkEvent(questId);
		qe.addQuestLvlUp(questId);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || player.getCommonData().getLevel() < 18 || qs.getStatus() != QuestStatus.LOCKED)
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

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if(qs.getStatus() == QuestStatus.START)
		{
			if(targetId == 203178)
			{
				switch(env.getDialogId())
				{
					case 25:
						if(var == 0)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
					case 10000:
						if(var == 0 || var == 0)
						{
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
								.getObjectId(), 10));
							return true;
						}
						else if(var == 1)
						{
							int itemCount = player.getInventory().getItemCountByItemId(182200025);
							if(itemCount >= 5)
							{
								if(env.getDialogId() == 33)
								{
									return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1694);
								}
								else
								{
									player.getInventory().removeFromBagByItemId(182200025, itemCount);
									qs.setQuestVarById(0, var + 1);
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
				}
			}
		}
		else if(qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 203178)
				return defaultQuestEndDialog(env);
		}
		return false;
	}	
}
