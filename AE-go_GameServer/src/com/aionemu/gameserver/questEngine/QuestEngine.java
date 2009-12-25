/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.questEngine;

import java.util.ArrayList;
import java.util.List;

import javolution.util.FastMap;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.quest.NpcQuestData;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.events.OnEnterZoneEvent;
import com.aionemu.gameserver.questEngine.events.OnItemUseEvent;
import com.aionemu.gameserver.questEngine.events.OnKillEvent;
import com.aionemu.gameserver.questEngine.events.OnLvlUpEvent;
import com.aionemu.gameserver.questEngine.events.OnTalkEvent;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * @author MrPoke
 * 
 */
public class QuestEngine
{
	private static final Logger					log	= Logger.getLogger(QuestEngine.class);

	private static QuestEngine					instance;
	private FastMap<Integer, NpcQuestData>		_npcQuestData;
	private FastMap<Integer, List<Integer>>		_questItemIds;
	private List<Integer>						_questLvlUp;
	private FastMap<ZoneName, List<Integer>>	_questEnterZone;

	private ItemService							itemService;

	private QuestEngine()
	{
		_npcQuestData = new FastMap<Integer, NpcQuestData>();
		_questItemIds = new FastMap<Integer, List<Integer>>();
		_questLvlUp = new ArrayList<Integer>();
		_questEnterZone = new FastMap<ZoneName, List<Integer>>();
	}

	public boolean onDialog(QuestEnv env)
	{
		Quest quest = null;
		Player player = env.getPlayer();
		Npc npc = (Npc) env.getVisibleObject();
		int npcObjId = 0;
		if(npc != null)
			npcObjId = npc.getObjectId();
		int dialogId = env.getDialogId();

		for(int id : getNpcQuestData(npc == null ? 0 : npc.getNpcId()).getOnTalkEvent())
			for(OnTalkEvent event : DataManager.QUEST_DATA.getQuestById(id).getOnTalkEvent())
				if(event.operate(new QuestEnv(npc, player, id, dialogId)))
					return true;

		int questId = env.getQuestId();
		if(questId > 0)
		{
			quest = getQuest(env);
		}

		switch(dialogId)
		{
			case 8:
			case 9:
			case 10:
				if(questId == 0)
				{
					for(int endQuestId : getNpcQuestData(npc.getNpcId()).getOnQuestEnd())
					{
						QuestState qs = player.getQuestStateList().getQuestState(endQuestId);
						if(qs == null)
							continue;
						if(qs.getStatus() != QuestStatus.REWARD)
							continue;

						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcObjId, 5, endQuestId));
						return true;

					}
					break;
				}
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
				if(quest == null)
					break;
				quest.questFinish();
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcObjId, 10));
				return true;
			case 25:
				QuestState qs = player.getQuestStateList().getQuestState(questId);
				if(qs == null || qs.getStatus() == QuestStatus.NONE)
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcObjId, 1011, questId));
				else
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcObjId, 2375, questId));
				return true;
			case 33:
				if(quest == null)
					break;
				if(quest.collectItemCheck())
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcObjId, 5, questId));
				else
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcObjId, 2716, questId));
				return true;
			case 1002:
				if(quest == null)
					break;
				if(quest.startQuest())
				{
					if (npc != null)
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcObjId, 1003, questId));
					return true;
				}
				else
				{
					if (npc != null)
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcObjId, dialogId));
					return true;
				}
			case 0:
			case 1003:
			case 1004:
			case 1005:
			case 1006:
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcObjId, dialogId + 1, questId));
				return true;
			case 1007:
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcObjId, 4, questId));
				return true;
			case 1009:
				if(quest == null)
					break;
				if(quest.questComplite())
				{
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcObjId, 5, questId));
					return true;
				}
				break;
			default:
				return false;
		}
		return false;
	}

	public void onEnterWorld(final QuestEnv env)
	{
		ThreadPoolManager.getInstance().schedule(new Runnable(){
			public void run()
			{
				playerInitQuest(env);
			}
		}, 10000);
		onLvlUp(env);
	}

	public boolean onKill(QuestEnv env)
	{
		Npc npc = (Npc) env.getVisibleObject();
		for(int questId : getNpcQuestData(npc.getNpcId()).getOnKillEvent())
		{
			for(OnKillEvent event : DataManager.QUEST_DATA.getQuestById(questId).getOnKillEvent())
			{
				env.setQuestId(questId);
				if(event.operate(env))
					return true;
			}
		}
		return false;
	}

	public boolean onLvlUp(QuestEnv env)
	{
		for(int questId : _questLvlUp)
		{
			for(OnLvlUpEvent event : DataManager.QUEST_DATA.getQuestById(questId).getOnLvlUpEvent())
			{
				env.setQuestId(questId);
				if(event.operate(env))
					return true;
			}
		}
		return false;
	}

	public boolean onItemUseUp(QuestEnv env, int itemId)
	{
		for(int questId : getQuestItemIds(itemId))
		{
			for(OnItemUseEvent event : DataManager.QUEST_DATA.getQuestById(questId).getOnItemUseEvent())
			{
				env.setQuestId(questId);
				if(event.operate(env))
					return true;
			}
		}
		return false;
	}

	public boolean onEnterZone(QuestEnv env, ZoneName zoneName)
	{
		for(int questId : getQuestEnterZone(zoneName))
		{
			for(OnEnterZoneEvent event : DataManager.QUEST_DATA.getQuestById(questId).getOnEnterZoneEvent())
			{
				env.setQuestId(questId);
				if(event.operate(env))
					return true;
			}
		}
		return false;
	}

	public void playerInitQuest(final QuestEnv env)
	{
		int id;
		Player player = env.getPlayer();
		if(player.getCommonData().getRace() == Race.ELYOS)
		{
			id = 1000;
		}
		else
		{
			id = 2000;
		}
		env.setQuestId(id);

		if(player.getQuestStateList().getQuestState(id) == null)
		{
			getQuest(env).startQuest();
		}

		QuestState qs = player.getQuestStateList().getQuestState(id);
		if(qs != null)
		{
			if(qs.getStatus() == QuestStatus.COMPLITE)
				return;
			qs.setStatus(QuestStatus.REWARD);
			ThreadPoolManager.getInstance().schedule(new Runnable(){
				public void run()
				{
					getQuest(env).questFinish();
				}
			}, 5000);
		}
	}

	public Quest getQuest(QuestEnv env)
	{
		QuestTemplate template = DataManager.QUEST_DATA.getQuestById(env.getQuestId());
		if(template == null)
		{
			log.warn("Missing QUEST_DATA questId: " + env.getQuestId());
			return null;
		}
		Quest quest = new Quest(template, env);
		if(quest == null)
		{
			log.warn("Error init quest questId: " + env.getQuestId());
			return null;
		}
		return quest;
	}

	public boolean deleteQuest(Player player, int questId)
	{
		if(DataManager.QUEST_DATA.getQuestById(questId).isCannotGiveup())
			return false;

		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if(qs == null)
			return false;
		qs.setStatus(QuestStatus.NONE);
		return true;
	}

	public NpcQuestData getNpcQuestData(int npcTemplateId)
	{
		if(_npcQuestData.containsKey(npcTemplateId))
		{
			return _npcQuestData.get(npcTemplateId);
		}
		return new NpcQuestData();
	}

	public NpcQuestData setNpcQuestData(int npcTemplateId)
	{
		if(!_npcQuestData.containsKey(npcTemplateId))
		{
			_npcQuestData.put(npcTemplateId, new NpcQuestData());
		}
		return _npcQuestData.get(npcTemplateId);
	}

	public List<Integer> getQuestItemIds(int itemId)
	{
		if(_questItemIds.containsKey(itemId))
		{
			return _questItemIds.get(itemId);
		}
		return new ArrayList<Integer>();
	}

	public List<Integer> setQuestItemIds(int itemId)
	{
		if(!_questItemIds.containsKey(itemId))
		{
			_questItemIds.put(itemId, new ArrayList<Integer>());
		}
		return _questItemIds.get(itemId);
	}

	public void addQuestLvlUp(int questId)
	{
		_questLvlUp.add(questId);
	}

	public List<Integer> getQuestEnterZone(ZoneName zoneName)
	{
		if(_questEnterZone.containsKey(zoneName))
		{
			return _questEnterZone.get(zoneName);
		}
		return new ArrayList<Integer>();
	}

	public List<Integer> setQuestEnterZone(ZoneName zoneName)
	{
		if(!_questEnterZone.containsKey(zoneName))
		{
			_questEnterZone.put(zoneName, new ArrayList<Integer>());
		}
		return _questEnterZone.get(zoneName);
	}

	public void setItemService(ItemService itemService)
	{
		this.itemService = itemService;
	}

	public void addItem(Player player, int itemId, int count)
	{
		itemService.addItem(player, itemId, count, false);
	}

	public void clear()
	{
		_npcQuestData.clear();
	}

	public static QuestEngine getInstance()
	{
		if(instance == null)
		{
			instance = new QuestEngine();
		}
		return instance;
	}
}