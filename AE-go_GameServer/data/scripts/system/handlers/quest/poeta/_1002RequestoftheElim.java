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

import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.google.inject.Inject;

/**
 * @author MrPoke
 * 
 */
public class _1002RequestoftheElim extends QuestHandler
{
	private final TeleportService teleportService;
	private final static int	questId	= 1002;

	@Inject
	public _1002RequestoftheElim(TeleportService teleportService)
	{
		super(questId);
		this.teleportService = teleportService;
		QuestEngine.getInstance().addQuestLvlUp(questId);
		QuestEngine.getInstance().setNpcQuestData(203076).addOnTalkEvent(questId);
		QuestEngine.getInstance().setNpcQuestData(730007).addOnTalkEvent(questId);
		QuestEngine.getInstance().setNpcQuestData(730010).addOnTalkEvent(questId);
		QuestEngine.getInstance().setNpcQuestData(730008).addOnTalkEvent(questId);
		QuestEngine.getInstance().setNpcQuestData(205000).addOnTalkEvent(questId);
		QuestEngine.getInstance().setNpcQuestData(203067).addOnTalkEvent(questId);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.LOCKED)
			return false;

		QuestState qs2 = player.getQuestStateList().getQuestState(1001);
		if(qs2 == null || qs2.getStatus() != QuestStatus.COMPLITE)
			return false;
		qs.setStatus(QuestStatus.START);
		updateQuestStatus(player, qs);
		return true;
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		int var = qs.getQuestVars().getQuestVarById(0);
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if(qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 203067)
			{
				if(env.getDialogId() == -1)
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2716);
				else if(env.getDialogId() == 1009)
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 5);
				else
					return defaultQuestEndDialog(env);
			}
			return false;
		}
		else if(qs.getStatus() != QuestStatus.START)
		{
			return false;
		}
		if(targetId == 203076)
		{
			switch(env.getDialogId())
			{
				case 25:
					if(var == 0)
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
					return false;
				case 10000:
					if(var == 0)
					{
						qs.getQuestVars().setQuestVarById(0, var + 1);
						updateQuestStatus(player, qs);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(),
							10));
						return true;
					}
					return false;
				default:
					return false;
			}
		}
		else if(targetId == 730007)
		{
			switch(env.getDialogId())
			{
				case 25:
					if(var == 1)
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
					else if(var == 5)
					{
						player.getInventory().removeFromBagByItemId(182200002, 1);
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1693);
					}
					else if(var == 6)
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2034);
					return false;
				case 33:
					if(var == 6)
					{
						if(collectItemCheck(env))
						{
							qs.getQuestVars().setQuestVarById(0, 12);
							updateQuestStatus(player, qs);
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2120);
						}
						else
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2205);
					}
					else if(var == 12)
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2120);
					return false;
				case 1353:
					if(var == 1)
						PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 20));
					return false;
				case 10001:
					if(var == 1)
					{
						if(player.getInventory().getItemCountByItemId(182200002) == 0)
							if (!QuestEngine.getInstance().addItem(player, new QuestItems(182200002, 1)))
								return true;
						qs.getQuestVars().setQuestVarById(0, var + 1);
						updateQuestStatus(player, qs);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(),
							10));
						return true;
					}
					return false;
				case 10002:
					if(var == 5)
					{
						qs.getQuestVars().setQuestVarById(0, var + 1);
						updateQuestStatus(player, qs);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(),
							10));
						return true;
					}
					return false;
				case 10003:
					if(var == 12)
					{
						qs.getQuestVars().setQuestVarById(0, var + 1);
						updateQuestStatus(player, qs);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(),
							10));
						return true;
					}
					return false;
				default:
					return false;
			}
		}
		else if(targetId == 730010 && var >= 2 && var < 5)
		{
			switch(env.getDialogId())
			{
				case -1:
					List<Item> items = player.getInventory().getItemsByItemId(182200002);
					if(items.isEmpty())
						return false;
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
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 10));
						}
					}, 3000);
					return true;
				case 25:
					if(var == 2)
						var++;
					qs.getQuestVars().setQuestVarById(0, var + 1);
					updateQuestStatus(player, qs);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
					((Npc) env.getVisibleObject()).getController().onDie();
					return true;
				default:
					return false;
			}
		}
		else if(targetId == 730008)
		{
			switch(env.getDialogId())
			{
				case 25:
					if(var == 13)
					{
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(),
							2375, questId));
						return true;
					}
					else if(var == 14)
					{
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(),
							2461, questId));
						return true;
					}
					return false;
				case 10004:
					if(var == 13)
					{
						qs.getQuestVars().setQuestVarById(0, 20);
						updateQuestStatus(player, qs);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(),
							0));
						WorldMapInstance newInstance = player.getPosition().getWorld().getNextAvailableInstance(310010000);
						newInstance.setDestroyTime(60 * 5);
						teleportService.teleportTo(player, 310010000, newInstance.getInstanceId(), 52, 174, 229, 0);
						return true;
					}
					return false;
				case 10005:
					if(var == 14)
					{
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(player, qs);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(),
							10));
						return true;
					}
					return false;
				default:
					return false;
			}
		}
		else if(targetId == 205000)
		{
			switch(env.getDialogId())
			{
				case 25:
					if(var == 20)
					{
						PacketSendUtility.sendPacket(player, new SM_EMOTION(player, 6, 1001, 0));
						ThreadPoolManager.getInstance().schedule(new Runnable(){
							@Override
							public void run()
							{
								qs.getQuestVars().setQuestVarById(0, 14);
								updateQuestStatus(player, qs);
								teleportService.teleportTo(player, 210010000, 1, 603, 1537, 116, (byte) 20, 0);
							}
						}, 43000);
						return true;
					}
					return false;
				default:
					return false;
			}
		}
		return false;
	}

}
