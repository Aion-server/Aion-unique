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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javolution.util.FastMap;

import org.apache.log4j.Logger;

import com.aionemu.commons.scripting.scriptmanager.ScriptManager;
import com.aionemu.gameserver.GameServerError;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.quest.NpcQuestData;
import com.aionemu.gameserver.model.templates.quest.QuestDrop;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.model.templates.quest.QuestWorkItems;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.handlers.QuestHandlerLoader;
import com.aionemu.gameserver.questEngine.handlers.models.QuestScriptData;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.google.inject.Injector;

/**
 * @author MrPoke
 * 
 */
public class QuestEngine
{
	private Injector injector;

	private static final Logger log = Logger.getLogger(QuestEngine.class);

	private static final FastMap<Integer, QuestHandler> questHandlers = new FastMap<Integer, QuestHandler>();

	private static ScriptManager 			scriptManager = new ScriptManager();

	public static final File QUEST_DESCRIPTOR_FILE = new File("./data/scripts/system/quest_handlers.xml");

	private FastMap<Integer, NpcQuestData>		_npcQuestData = new FastMap<Integer, NpcQuestData>();
	private FastMap<Integer, List<Integer>>		_questItemIds= new FastMap<Integer, List<Integer>>();
	private List<Integer>						_questLvlUp = new ArrayList<Integer>();
	private FastMap<ZoneName, List<Integer>>	_questEnterZone= new FastMap<ZoneName, List<Integer>>();
	private FastMap<Integer, List<Integer>>		_questMovieEndIds= new FastMap<Integer, List<Integer>>();
	private List<Integer>						_questOnDie= new ArrayList<Integer>();
	private List<Integer>						_questOnEnterWorld= new ArrayList<Integer>();
	private FastMap<Integer, List<QuestDrop>>	_questDrop= new FastMap<Integer, List<QuestDrop>>();

	public void load()
	{
		for (QuestTemplate data : DataManager.QUEST_DATA.getQuestData())
		{
			for (QuestDrop drop : data.getQuestDrop())
			{
				drop.setQuestId(data.getId());
				setQuestDrop(drop.getNpcId()).add(drop);
			}
		}

		scriptManager = new ScriptManager();
		scriptManager.setGlobalClassListener(new QuestHandlerLoader(injector));

		try
		{
			scriptManager.load(QUEST_DESCRIPTOR_FILE);
		}
		catch (Exception e)
		{
			throw new GameServerError("Can't initialize quest handlers.", e);
		}
		for (QuestScriptData data : DataManager.QUEST_SCRIPTS_DATA.getData())
		{
			data.register(this);
		}

		log.info("Loaded " + questHandlers.size() + " quest handler.");
	}

	public void shutdown()
	{
		scriptManager.shutdown();
		clear();
		scriptManager = null;
		log.info("Quests are shutdown...");
	}
	public boolean onDialog(QuestEnv env)
	{
		QuestHandler questHandler = null;
		if(env.getQuestId() != 0)
		{
			questHandler = getQuestHandlerByQuestId(env.getQuestId());
			if(questHandler != null)
				if(questHandler.onDialogEvent(env))
					return true;
		}
		else
		{
			Npc npc = (Npc) env.getVisibleObject();
			for(int questId : getNpcQuestData(npc == null ? 0 : npc.getNpcId()).getOnTalkEvent())
			{
				questHandler = getQuestHandlerByQuestId(questId);
				if(questHandler != null)
					if(questHandler.onDialogEvent(env))
						return true;
			}
		}
		return false;
	}

	public boolean onKill(QuestEnv env)
	{
		Npc npc = (Npc) env.getVisibleObject();
		for(int questId : getNpcQuestData(npc.getNpcId()).getOnKillEvent())
		{
			QuestHandler questHandler = getQuestHandlerByQuestId(questId);
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
			QuestHandler questHandler = getQuestHandlerByQuestId(questId);
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
			QuestHandler questHandler = getQuestHandlerByQuestId(questId);
			if(questHandler != null)
				questHandler.onLvlUpEvent(env);
		}
	}

	public void onDie(QuestEnv env)
	{
		for(int questId : _questOnDie)
		{
			QuestHandler questHandler = getQuestHandlerByQuestId(questId);
			if(questHandler != null)
				questHandler.onDieEvent(env);
		}
	}
	
	public void onEnterWorld(QuestEnv env)
	{
		for(int questId : _questOnEnterWorld)
		{
			QuestHandler questHandler = getQuestHandlerByQuestId(questId);
			if(questHandler != null)
				questHandler.onEnterWorldEvent(env);
		}
	}

	public boolean onItemUseEvent(QuestEnv env, Item item)
	{
		for(int questId : getQuestItemIds(item.getItemTemplate().getTemplateId()))
		{
			QuestHandler questHandler = getQuestHandlerByQuestId(questId);
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
			QuestHandler questHandler = getQuestHandlerByQuestId(questId);
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
			QuestHandler questHandler = getQuestHandlerByQuestId(env.getQuestId());
			if(questHandler != null)
				if(questHandler.onMovieEndEvent(env, movieId))
					return true;
		}
		return false;
	}

	public boolean deleteQuest(Player player, int questId)
	{
		if(DataManager.QUEST_DATA.getQuestById(questId).isCannotGiveup())
			return false;

		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if(qs == null)
			return false;

		qs.setStatus(QuestStatus.NONE);
		
		//remove all worker list item if abandoned
		QuestWorkItems qwi = DataManager.QUEST_DATA.getQuestById(questId).getQuestWorkItems();
		
		if(qwi != null)
		{
			int count = 0;
			for(QuestItems qi : qwi.getQuestWorkItem())
			{
				if(qi != null)
				{	
					count = player.getInventory().getItemCountByItemId(qi.getItemId());
					if(count > 0)
						player.getInventory().removeFromBagByItemId(qi.getItemId(), count);					
				}
			}
		}
		
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

	public List<QuestDrop> setQuestDrop(int npcId)
	{
		if(!_questDrop.containsKey(npcId))
		{
			_questDrop.put(npcId, new ArrayList<QuestDrop>());
		}
		return _questDrop.get(npcId);
	}

	public List<QuestDrop> getQuestDrop(int npcId)
	{
		if(_questDrop.containsKey(npcId))
		{
			return _questDrop.get(npcId);
		}
		return new ArrayList<QuestDrop>();
	}

	public void addQuestLvlUp(int questId)
	{
		if(!_questLvlUp.contains(questId))
			_questLvlUp.add(questId);
	}

	public void addOnEnterWorld(int questId)
	{
		if(!_questOnEnterWorld.contains(questId))
			_questOnEnterWorld.add(questId);
	}

	public void addOnDie(int questId)
	{
		if(!_questOnDie.contains(questId))
			_questOnDie.add(questId);
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

	public void clear()
	{
		_npcQuestData.clear();
		_questItemIds.clear();
		_questLvlUp.clear();
		_questOnEnterWorld.clear();
		_questOnDie.clear();
		_questEnterZone.clear();
		_questMovieEndIds.clear();
		_questDrop.clear();
		questHandlers.clear();
	}
	
	public void addQuestHandler (QuestHandler questHandler)
	{
		injector.injectMembers(questHandler);
		questHandler.register();
		questHandlers.put(questHandler.getQuestId(), questHandler);
	}
	
	public QuestHandler getQuestHandlerByQuestId(int questId)
	{
		return questHandlers.get(questId);
	}
	
	public int getSize()
	{
		return questHandlers.size();
	}

	/**
	 * @param injector the injector to set
	 */
	public void setInjector(Injector injector)
	{
		this.injector = injector;
	}
}