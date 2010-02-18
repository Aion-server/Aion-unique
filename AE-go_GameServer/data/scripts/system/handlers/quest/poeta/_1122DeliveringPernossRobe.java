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
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
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
public class _1122DeliveringPernossRobe extends QuestHandler
{

	private final static int	questId	= 1122;

	public _1122DeliveringPernossRobe()
	{
		super(questId);
		QuestEngine.getInstance().setNpcQuestData(203060).addOnQuestStart(questId);
		QuestEngine.getInstance().setNpcQuestData(203060).addOnTalkEvent(questId);
		QuestEngine.getInstance().setNpcQuestData(790001).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(targetId == 203060)
		{
			if(qs == null)
			{
				if(env.getDialogId() == 25)
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
				else if(env.getDialogId() == 1002)
				{
					if (QuestEngine.getInstance().addItem(player, new QuestItems(182200216, 1)))
						return defaultQuestStartDialog(env);
					else
						return true;
				}
				else
					return defaultQuestStartDialog(env);
			}
		}
		else if(targetId == 790001)
		{
			if(qs != null && qs.getStatus() == QuestStatus.START)
			{
				int itemCount;
				switch(env.getDialogId())
				{
					case 25:
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
					case 10003:
						itemCount = player.getInventory().getItemCountByItemId(182200220);
						if (itemCount > 0)
						{
							qs.getQuestVars().setQuestVar(1);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(player, qs);
							player.getInventory().removeFromBagByItemId(182200220, 1);
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1353);
						}
						else
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1608);
					case 10002:
						itemCount = player.getInventory().getItemCountByItemId(182200219);
						if (itemCount > 0)
						{
							qs.getQuestVars().setQuestVar(2);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(player, qs);
							player.getInventory().removeFromBagByItemId(182200219, 1);
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1438);
						}
						else
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1608);
					case 10001:
						itemCount = player.getInventory().getItemCountByItemId(182200218);
						if (itemCount > 0)
						{
							qs.getQuestVars().setQuestVar(3);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(player, qs);
							player.getInventory().removeFromBagByItemId(182200218, 1);
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1523);
						}
						else
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1608);
					default:
						return defaultQuestStartDialog(env);
				}
			}
			else if (qs!= null && qs.getStatus()==QuestStatus.REWARD)
			{
				if (env.getDialogId() == -1 || env.getDialogId() == 1009)
				{
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 4+qs.getQuestVars().getQuestVars());
				}
				else if (env.getDialogId() == 17)
				{
					QuestEngine.getInstance().getQuest(env).questFinish(qs.getQuestVars().getQuestVars()-1);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
			}
		}
		return false;
	}
}
