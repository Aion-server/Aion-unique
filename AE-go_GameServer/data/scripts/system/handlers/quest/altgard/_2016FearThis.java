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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.services.ZoneService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.google.inject.Inject;

/**
 * @author Mr. Poke
 *
 */
public class _2016FearThis extends QuestHandler
{

	@Inject
	ItemService itemService;

	@Inject
	ZoneService zoneService;

	private final static int	questId	= 2016;

	public _2016FearThis()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.addQuestLvlUp(questId);
		qe.setNpcQuestData(203631).addOnTalkEvent(questId);
		qe.setNpcQuestData(210455).addOnKillEvent(questId);
		qe.setNpcQuestData(210458).addOnKillEvent(questId);
		qe.setNpcQuestData(214032).addOnKillEvent(questId);
		qe.setNpcQuestData(203621).addOnTalkEvent(questId);
		qe.setQuestItemIds(182203019).add(questId);
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
						case 25:
							if(var == 0)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
							else if (var == 6)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
						case 10000:
						case 10001:
							if (var == 0 || var == 6)
							{
								qs.setQuestVarById(0, var + 1);
								updateQuestStatus(player, qs);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
							}
					}
					break;
				case 203621:
					switch(env.getDialogId())
					{
						case 25:
							if(var == 7)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1693);
							else if (var == 8)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2034);
							break;
						case 10002:
						case 10003:
							if (var == 7 || var == 9)
							{
								if (var == 9)
									if (!itemService.addItems(player, Collections.singletonList(new QuestItems(182203019, 1))))
										return true;
								qs.setQuestVarById(0, var + 1);
								updateQuestStatus(player, qs);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
							}
							break;
						case 33:
							if(var == 8)
							{
								if(collectItemCheck(env))
								{
									qs.setQuestVarById(0, var + 1);
									updateQuestStatus(player, qs);
									return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2035);
								}
								else
									return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2120);
							}
					}
					
			}
		}
		else if(qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 203631)
			{
				if (env.getDialogId() == -1 )
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2375);
				else
					return defaultQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onItemUseEvent(QuestEnv env, Item item)
	{
		final Player player = env.getPlayer();
		final int id = item.getItemTemplate().getTemplateId();
		final int itemObjId = item.getObjectId();

		if(id != 182203019)
			return false;
		if(!zoneService.isInsideZone(player, ZoneName.Q2016))
			return false;
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 3000, 0, 0), true);
		ThreadPoolManager.getInstance().schedule(new Runnable(){
			@Override
			public void run()
			{
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0, 1, 0), true);
				player.getInventory().removeFromBagByObjectId(itemObjId, 1);
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(player, qs);
			}
		}, 3000);
		return true;
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
			case 210455:
			case 210458:
			case 214032:
				var = qs.getQuestVarById(0);
				if (var < 6)
				{
					qs.setQuestVarById(0, var+1);
					updateQuestStatus(player, qs);
				}
				break;
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
