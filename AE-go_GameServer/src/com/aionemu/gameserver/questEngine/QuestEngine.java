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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.quest.NpcQuestData;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.handlers.QuestHandlers;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * @author MrPoke
 * 
 */
public class QuestEngine
{
	private static QuestEngine					instance;
	private FastMap<Integer, NpcQuestData>		_npcQuestData;
	private FastMap<Integer, List<Integer>>		_questItemIds;
	private List<Integer>						_questLvlUp;
	private FastMap<ZoneName, List<Integer>>	_questEnterZone;
	private FastMap<Integer, List<Integer>>		_questMovieEndIds;
	private List<Integer>						_questOnDie;
	private List<Integer>						_questOnEnterWorld;

	private QuestEngine()
	{
		_npcQuestData = new FastMap<Integer, NpcQuestData>();
		_questItemIds = new FastMap<Integer, List<Integer>>();
		_questLvlUp = new ArrayList<Integer>();
		_questOnEnterWorld = new ArrayList<Integer>();
		_questOnDie = new ArrayList<Integer>();
		_questEnterZone = new FastMap<ZoneName, List<Integer>>();
		_questMovieEndIds = new FastMap<Integer, List<Integer>>();
	}

	public boolean onDialog(QuestEnv env)
	{
		QuestHandler questHandler = null;
		if(env.getQuestId() != 0)
		{
			questHandler = QuestHandlers.getInstance().getQuestHandlerByQuestId(env.getQuestId());
			if(questHandler != null)
				if(questHandler.onDialogEvent(env))
					return true;
		}
		else
		{
			Npc npc = (Npc) env.getVisibleObject();
			for(int questId : getNpcQuestData(npc == null ? 0 : npc.getNpcId()).getOnTalkEvent())
			{
				questHandler = QuestHandlers.getInstance().getQuestHandlerByQuestId(questId);
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
			QuestHandler questHandler = QuestHandlers.getInstance().getQuestHandlerByQuestId(questId);
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
			QuestHandler questHandler = QuestHandlers.getInstance().getQuestHandlerByQuestId(questId);
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
			QuestHandler questHandler = QuestHandlers.getInstance().getQuestHandlerByQuestId(questId);
			if(questHandler != null)
				questHandler.onLvlUpEvent(env);
		}
	}

	public void onDie(QuestEnv env)
	{
		for(int questId : _questOnDie)
		{
			QuestHandler questHandler = QuestHandlers.getInstance().getQuestHandlerByQuestId(questId);
			if(questHandler != null)
				questHandler.onDieEvent(env);
		}
	}
	
	public void onEnterWorld(QuestEnv env)
	{
		for(int questId : _questOnEnterWorld)
		{
			QuestHandler questHandler = QuestHandlers.getInstance().getQuestHandlerByQuestId(questId);
			if(questHandler != null)
				questHandler.onEnterWorldEvent(env);
		}
	}

	public boolean onItemUseEvent(QuestEnv env, Item item)
	{
		for(int questId : getQuestItemIds(item.getItemTemplate().getTemplateId()))
		{
			QuestHandler questHandler = QuestHandlers.getInstance().getQuestHandlerByQuestId(questId);
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
			QuestHandler questHandler = QuestHandlers.getInstance().getQuestHandlerByQuestId(questId);
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
			QuestHandler questHandler = QuestHandlers.getInstance().getQuestHandlerByQuestId(env.getQuestId());
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