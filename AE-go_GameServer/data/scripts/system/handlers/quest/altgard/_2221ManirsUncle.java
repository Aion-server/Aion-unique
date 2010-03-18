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
public class _2221ManirsUncle extends QuestHandler
{

	private final static int	questId	= 2221;

	public _2221ManirsUncle()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.setNpcQuestData(203607).addOnQuestStart(questId);
		qe.setNpcQuestData(203607).addOnTalkEvent(questId);
		qe.setNpcQuestData(203608).addOnTalkEvent(questId);
		qe.setNpcQuestData(700214).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
		{
			if(targetId == 203607)
			{
				if(env.getDialogId() == 25)
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
				else
					return defaultQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START)
		{
			switch(targetId)
			{
				case 203608:
				{
					if (qs.getQuestVarById(0) == 0)
					{
						if(env.getDialogId() == 25)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
						else if(env.getDialogId() == 10000)
						{
							qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
							updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						}
					}
					if (qs.getQuestVarById(0) == 2)
					{
						if(env.getDialogId() == 25)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2375);
						else if(env.getDialogId() == 1009)
						{
							player.getInventory().removeFromBagByItemId(182203215, 1);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(player, qs);
							return defaultQuestEndDialog(env);
						}
						else
							return defaultQuestEndDialog(env);
					}
				}
				break;
				case 700214:
					if ((qs.getQuestVarById(0) == 1 || qs.getQuestVarById(0) == 2) && env.getDialogId() == -1)
					{
						qs.setQuestVarById(0, 2);
						updateQuestStatus(player, qs);
					}
						return true;
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 203608)
				return defaultQuestEndDialog(env);
		}
		return false;
	}
}
