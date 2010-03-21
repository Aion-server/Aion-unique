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
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author Mr. Poke
 *
 */
public class _2290GrokensEscape extends QuestHandler
{

	private final static int	questId	= 2290;

	public _2290GrokensEscape()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.setNpcQuestData(203608).addOnQuestStart(questId);
		qe.setNpcQuestData(203608).addOnTalkEvent(questId);
		qe.setNpcQuestData(700178).addOnTalkEvent(questId);
		qe.setNpcQuestData(203607).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() == QuestStatus.NONE)
		{
			if(targetId == 203608)
			{
				if(env.getDialogId() == 25)
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
				else if (env.getDialogId() == 1002)
				{
					if (questService.startQuest(env, QuestStatus.START))
					{
						Npc npc = (Npc)env.getVisibleObject();
						npc.getMoveController().setNewDirection(1219.15f, 1212f, 247.37f);
						npc.getMoveController().schedule();
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1003);
					}
				}
				else
					return defaultQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START)
		{
			switch(targetId)
			{
				case 700178:
				{
					if (qs.getQuestVarById(0) == 0 && env.getDialogId() == -1)
					{
						for (VisibleObject obj : player.getKnownList())
						{
							if (!(obj instanceof Npc))
								continue;
							if (((Npc)obj).getNpcId() != 203608)
								continue;
							if (MathUtil.getDistance(player.getX(), player.getY(), player.getZ(),obj.getX(), obj.getY(), obj.getZ()) > 4)
								return false;
							((Npc)obj).getController().onDie(null); //TODO check null or player
							((Npc)obj).getController().onDespawn(false);
						}
						final int targetObjectId = env.getVisibleObject().getObjectId();
						PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000,
							1));
						PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 37, 0,
							targetObjectId), true);
						ThreadPoolManager.getInstance().schedule(new Runnable(){
							@Override
							public void run()
							{
								if(player.getTarget() == null || player.getTarget().getObjectId() != targetObjectId)
									return;
								PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(),
									targetObjectId, 3000, 0));
								PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 38, 0,
									targetObjectId), true);
								qs.setQuestVarById(0, 3);
								updateQuestStatus(player, qs);
							}
						}, 3000);
					}
				}
				break;
				case 203607:
				{
					if (qs.getQuestVarById(0) == 3)
					{
						if(env.getDialogId() == 25)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1693);
						else if(env.getDialogId() == 1009)
							{
								player.getInventory().removeFromBagByItemId(182203208, 1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(player, qs);
								return defaultQuestEndDialog(env);
							}
							else
								return defaultQuestEndDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 203607)
				return defaultQuestEndDialog(env);
		}
		return false;
	}
}
