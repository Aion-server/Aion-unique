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
package com.aionemu.gameserver.questEngine;

import java.util.ArrayList;
import java.util.List;

import javolution.util.FastMap;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.quest.NpcQuestData;
import com.aionemu.gameserver.model.templates.spawn.SpawnTemplate;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.handlers.QuestHandlers;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
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
	private FastMap<Integer, List<Integer>>		_questMovieEndIds;
	private ItemService							itemService;
	private SpawnEngine							spawnEngine;

	private QuestEngine()
	{
		_npcQuestData = new FastMap<Integer, NpcQuestData>();
		_questItemIds = new FastMap<Integer, List<Integer>>();
		_questLvlUp = new ArrayList<Integer>();
		_questEnterZone = new FastMap<ZoneName, List<Integer>>();
		_questMovieEndIds = new FastMap<Integer, List<Integer>>();
	}

	public boolean onDialog(QuestEnv env)
	{
		QuestHandler questHandler = null;
		if(env.getQuestId() != 0)
		{
			questHandler = QuestHandlers.getQuestHandlerByQuestId(env.getQuestId());
			if(questHandler != null)
				if(questHandler.onDialogEvent(env))
					return true;
		}
		else
		{
			Npc npc = (Npc) env.getVisibleObject();
			for(int questId : getNpcQuestData(npc == null ? 0 : npc.getNpcId()).getOnTalkEvent())
			{
				questHandler = QuestHandlers.getQuestHandlerByQuestId(questId);
				if(questHandler != null)
					if(questHandler.onDialogEvent(env))
						return true;
			}
		}
		return false;
	}

	public void onEnterWorld(QuestEnv env)
	{
		onLvlUp(env);
	}

	public boolean onKill(QuestEnv env)
	{
		Npc npc = (Npc) env.getVisibleObject();
		for(int questId : getNpcQuestData(npc.getNpcId()).getOnKillEvent())
		{
			QuestHandler questHandler = QuestHandlers.getQuestHandlerByQuestId(questId);
			if(questHandler != null)
				if(questHandler.onKillEvent(env))
					return true;
		}
		return false;
	}

	public boolean onAttack(QuestEnv env)
	{
		Npc npc = (Npc) env.getVisibleObject();
		for(int questId : getNpcQuestData(npc.getNpcId()).getOnAttackEvent())
		{
			QuestHandler questHandler = QuestHandlers.getQuestHandlerByQuestId(questId);
			if(questHandler != null)
				if(questHandler.onAttackEvent(env))
					return true;
		}
		return false;
	}

	public void onLvlUp(QuestEnv env)
	{
		for(int questId : _questLvlUp)
		{
			QuestHandler questHandler = QuestHandlers.getQuestHandlerByQuestId(questId);
			if(questHandler != null)
				questHandler.onLvlUpEvent(env);
		}
	}

	public boolean onItemUseEvent(QuestEnv env, Item item)
	{
		for(int questId : getQuestItemIds(item.getItemTemplate().getItemId()))
		{
			QuestHandler questHandler = QuestHandlers.getQuestHandlerByQuestId(questId);
			if(questHandler != null)
				if(questHandler.onItemUseEvent(env, item))
					return true;
		}
		return false;
	}

	public boolean onEnterZone(QuestEnv env, ZoneName zoneName)
	{
		for(int questId : getQuestEnterZone(zoneName))
		{
			QuestHandler questHandler = QuestHandlers.getQuestHandlerByQuestId(questId);
			if(questHandler != null)
				if(questHandler.onEnterZoneEvent(env, zoneName))
					return true;
		}
		return false;
	}

	public boolean onMovieEnd(QuestEnv env, int movieId)
	{
		for(int questId : getQuestMovieEndIds(movieId))
		{
			env.setQuestId(questId);
			QuestHandler questHandler = QuestHandlers.getQuestHandlerByQuestId(env.getQuestId());
			if(questHandler != null)
				if(questHandler.onMovieEndEvent(env, movieId))
					return true;
		}
		return false;
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
		if(!_questLvlUp.contains(questId))
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

	public List<Integer> getQuestMovieEndIds(int moveId)
	{
		if(_questMovieEndIds.containsKey(moveId))
		{
			return _questMovieEndIds.get(moveId);
		}
		return new ArrayList<Integer>();
	}

	public List<Integer> setQuestMovieEndIds(int moveId)
	{
		if(!_questMovieEndIds.containsKey(moveId))
		{
			_questMovieEndIds.put(moveId, new ArrayList<Integer>());
		}
		return _questMovieEndIds.get(moveId);
	}

	public void setItemService(ItemService itemService, SpawnEngine spawnEngine)
	{
		this.itemService = itemService;
		this.spawnEngine = spawnEngine;
	}

	public void addItem(Player player, int itemId, int count)
	{
		itemService.addItem(player, itemId, count, false);
	}

	public VisibleObject addNewSpawn(int worldId, int templateId, float x, float y, float z, byte heading, boolean respawn)
	{
		SpawnTemplate spawn = spawnEngine.addNewSpawn(worldId, 1, templateId, x, y, z, heading, 0, 0, respawn);
		return spawnEngine.spawnObject(spawn, 1);
	}

	public void clear()
	{
		_npcQuestData.clear();
		_questItemIds.clear();
		_questLvlUp.clear();
		_questEnterZone.clear();
		_questMovieEndIds.clear();
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