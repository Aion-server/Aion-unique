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

import java.util.Collections;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.google.inject.Inject;

/**
 * @author Mr. Poke
 *
 */
public class _2013ADangerousCrop extends QuestHandler
{
	@Inject
	ItemService itemService;

	private final static int	questId	= 2013;

	public _2013ADangerousCrop()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.addQuestLvlUp(questId);
		qe.setNpcQuestData(203605).addOnTalkEvent(questId);
		qe.setNpcQuestData(700096).addOnTalkEvent(questId);
		qe.setQuestEnterZone(ZoneName.MUMU_FARMLAND_220030000).add(questId);
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
				case 203605:
					switch(env.getDialogId())
					{
						case 25:
							if(var == 0)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
							else if(var == 2)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
							else if(var == 8)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1693);
							else if(var == 9)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2034);
						case 10000:
						case 10001:
						case 10002:
							if(var == 0 || var == 2 || var == 8)
							{
								if (var == 2)
									if (!itemService.addItems(player, Collections.singletonList(new QuestItems(182203012, 1))))
										return true;
								if (var == 8)
									player.getInventory().removeFromBagByItemId(182203012, 1);
								qs.setQuestVarById(0, var + 1);
								updateQuestStatus(player, qs);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
							}
						case 33:
							if(var == 9)
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
					break;
				case 700096:
					switch(env.getDialogId())
					{
						case -1:
							if(var >= 3 && var < 6)
							{
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
										if (var == 5)
											qs.setQuestVarById(0, 8);
										else
											qs.setQuestVarById(0, var + 1);
										updateQuestStatus(player, qs);
										
									}
								}, 3000);
								return true;
							}
					}		
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 203605)
				return defaultQuestEndDialog(env);
		}
		return false;
	}

	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName)
	{
		if(zoneName != ZoneName.MUMU_FARMLAND_220030000)
			return false;
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;
		if (qs.getQuestVarById(0) == 1)
		{
			qs.setQuestVarById(0, 2);
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
		if(qs == null || qs.getStatus() != QuestStatus.LOCKED)
			return false;
		qs.setStatus(QuestStatus.START);
		updateQuestStatus(player, qs);
		return true;
	}

}
