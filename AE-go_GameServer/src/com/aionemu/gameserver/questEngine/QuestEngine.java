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

import javolution.util.FastMap;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.quest.NpcQuestData;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.events.QuestEvent;
import com.aionemu.gameserver.questEngine.types.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author MrPoke
 * 
 */
public class QuestEngine
{
	private static final Logger	log			= Logger.getLogger(QuestEngine.class);

	private static QuestEngine	instance;
	private FastMap<Integer, Quest> _quests;
	private FastMap<Integer, NpcQuestData> _npcQuestData;

	private QuestEngine()
	{
		_quests = new FastMap<Integer, Quest>();
		_npcQuestData = new FastMap<Integer, NpcQuestData>();
	}

	public boolean onDialog(int targetObjectId, int questId, int dialogId, Player player)
	{
		Quest quest = null;

		try
		{
			if (questId > 0)
			{
				quest = getQuest(questId);
			}
		}
		catch(QuestEngineException e)
		{
			log.error("Quest event error: ", e);
		}

		try
		{
			Npc npc = (Npc) player.getActiveRegion().getWorld().findAionObject(targetObjectId);
			if(npc != null)
			{
				for(QuestEvent questEvent : getNpcQuestData(npc.getNpcId()).getOnTalkEvent())
					if(questEvent.getQuestId() == questId)
						if(questEvent.operate(player, dialogId))
							return true;
			}
			else
			{
				if(quest != null)
				{
					if(quest.getEventsByType("on_talk", player, dialogId))
						return true;
				}
			}
		}
		catch(QuestEngineException e)
		{
			log.error("Quest event error: ", e);
		}

		switch(dialogId)
		{
			case 10:
				Npc npc = (Npc) player.getActiveRegion().getWorld().findAionObject(targetObjectId);
				for (Quest endQuest : getNpcQuestData(npc.getNpcId()).getOnQuestEnd())
				{
					QuestState qs = player.getQuestStateList().getQuestState(endQuest.getId());
					if (qs == null)
						continue;
					if (qs.getStatus() != QuestStatus.REWARD)
						continue;
	
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(player.getTarget().getObjectId(), 5, qs.getQuestId()));
					return true;
					
				}
				break;
			case 17:
				if (quest == null)
					break;
				quest.questFinish(player);
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(player.getTarget().getObjectId(), 10));
				return true;
			case 25:
				QuestState qs = player.getQuestStateList().getQuestState(questId);
				if(qs == null || qs.getStatus() == QuestStatus.NONE)
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(player.getTarget().getObjectId(), 1011,
						questId));
				else
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(player.getTarget().getObjectId(), 2375,
						questId));
				return true;
			case 33:
				if (quest == null)
					break;
				if(quest.collectItemCheck(player))
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(player.getTarget().getObjectId(), 5,
						questId));
				else
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(player.getTarget().getObjectId(), 2716,
						questId));
				return true;
			case 1002:
				if (quest == null)
					break;
				try
				{
					if(quest.startQuest(player, ((Npc) player.getActiveRegion().getWorld().findAionObject(
						targetObjectId)).getNpcId()))
					{
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(player.getTarget().getObjectId(), 1003, questId));
						return true;
					}
					else
					{
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, dialogId));
						return true;
					}
				}
				catch(QuestEngineException e)
				{
					log.error("Quest error: ", e);
					break;
				}
			case 0:	
			case 1003:
			case 1004:
			case 1005:
			case 1006:
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(player.getTarget().getObjectId(), dialogId+1, questId));
				return true;
			case 1007:
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(player.getTarget().getObjectId(), 4, questId));
				return true;
			case 1009:
				if (quest == null)
					break;
				if(quest.questComplite(player))
				{
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(player.getTarget().getObjectId(), 5, questId));
					return true;
				}
				break;
			default:
				return false;
		}
		return false;
	}

	public void onEnterWorld(final Player player)
	{
		ThreadPoolManager.getInstance().schedule(new Runnable() 
		{
			public void run() 
			{
				try
				{
					playerInitQuest(player);
				}
				catch(QuestEngineException e)
				{
					log.error("Quest error: ", e);
				}
			}   
		}, 10000);
	}

	public void playerInitQuest(final Player player) throws QuestEngineException
	{
		final int id;
		if (player.getCommonData().getRace() == Race.ELYOS)
		{
			id = 1000;
		}
		else
		{
			id = 2000;
		}

		if (player.getQuestStateList().getQuestState(id) == null)
		{
			getQuest(id).startQuest(player, 0);
		}
		QuestState qs = player.getQuestStateList().getQuestState(id);
		if (qs != null)
		{
			if (qs.getStatus() == QuestStatus.COMPLITE)
				return;
			qs.setStatus(QuestStatus.REWARD);
			ThreadPoolManager.getInstance().schedule(new Runnable() 
			{
				public void run() 
				{
					try
					{
						getQuest(id).questFinish(player);
					}
					catch(QuestEngineException e)
					{
						log.error("Quest error: ", e);
					}
				}   
			}, 5000);
		}
	}

	public Quest getQuest(int id) throws QuestEngineException
	{
		if (!_quests.containsKey(id))
		{
			throw new QuestEngineException("Quest (id = " + id + ") not found.");
		}

		return _quests.get(id);
	}

	public Quest newQuest(int id, int startNpcId, int endNpcId) throws QuestEngineException
	{
		if (_quests.containsKey(id))
		{
			throw new QuestEngineException("Duplicate Quest (id = " + id + ").");
		}
		Quest quest = new Quest(id, startNpcId, endNpcId);
		_quests.put(id, quest);
		return quest;
	}

	public boolean deleteQuest(Player player, int questId)
	{
		if (DataManager.QUEST_DATA.getQuestById(questId).isCannotGiveup())
			return false;

		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null)
			return false;
		qs.setStatus(QuestStatus.NONE);
		return true;
	}

	public NpcQuestData getNpcQuestData(int npcTemplateId)
	{
		if (_npcQuestData.containsKey(npcTemplateId))
		{
			return _npcQuestData.get(npcTemplateId);
		}
		return new NpcQuestData();
	}

	public NpcQuestData setNpcQuestData(int npcTemplateId)
	{
		if (!_npcQuestData.containsKey(npcTemplateId))
		{
			_npcQuestData.put(npcTemplateId, new NpcQuestData());
		}
		return _npcQuestData.get(npcTemplateId);
	}

	public void log()
	{
		log.info("Loaded "+size()+" quests.");
	}

	public int size()
	{
		return _quests.size();
	}

	public void clear()
	{
		_quests.clear();
		_npcQuestData.clear();
	}

	public static QuestEngine getInstance()
	{
		if (instance == null)
		{
			instance = new QuestEngine();
		}
		return instance;
	}
}