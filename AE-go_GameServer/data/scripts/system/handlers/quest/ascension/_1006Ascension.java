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
package quest.ascension;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.ai.events.Event;
import com.aionemu.gameserver.dataholders.SpawnsData;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Monster;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_SPAWN;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.zone.ZoneManager;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.google.inject.Inject;

/**
 * @author MrPoke
 * 
 */
public class _1006Ascension extends QuestHandler
{
	private final static int	questId			= 1006;
	private int					activePlayerId	= 0;
	private List<Npc>			mobs			= new ArrayList<Npc>();
	private final SpawnsData	spawnsData;

	@Inject
	public _1006Ascension(SpawnsData spawnsData)
	{
		super(questId);
		this.spawnsData = spawnsData;
		QuestEngine.getInstance().addQuestLvlUp(questId);
		QuestEngine.getInstance().setNpcQuestData(790001).addOnTalkEvent(questId);
		QuestEngine.getInstance().setQuestItemIds(182200007).add(questId);
		QuestEngine.getInstance().setNpcQuestData(730008).addOnTalkEvent(questId);
		QuestEngine.getInstance().setNpcQuestData(205000).addOnTalkEvent(questId);
		QuestEngine.getInstance().setNpcQuestData(211042).addOnKillEvent(questId);
		QuestEngine.getInstance().setNpcQuestData(211043).addOnAttackEvent(questId);
		QuestEngine.getInstance().setQuestMovieEndIds(151).add(questId);
	}

	@Override
	public boolean onKillEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		int var = qs.getQuestVars().getQuestVarById(0);
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if(targetId == 211042)
		{
			if(var >= 51 && var <= 53)
			{
				qs.getQuestVars().setQuestVar(qs.getQuestVars().getQuestVars() + 1);
				updateQuestStatus(player, qs);
				if(mobs.contains((Monster)env.getVisibleObject()))
					mobs.remove((Monster)env.getVisibleObject());
				return true;
			}
			else if(var == 54)
			{
				qs.getQuestVars().setQuestVar(4);
				updateQuestStatus(player, qs);
				mobs.add((Monster) QuestEngine.getInstance().addNewSpawn(310010000, 211043, (float) 226.7,
					(float) 251.5, (float) 205.5, (byte) 0, false));
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		int var = qs.getQuestVars().getQuestVars();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if(qs.getStatus() == QuestStatus.START)
		{
			if(targetId == 790001)
			{
				switch(env.getDialogId())
				{
					case 25:
						if(var == 0)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
						else if(var == 3)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1693);
						else if(var == 5)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2034);
					case 10000:
						if(var == 0)
						{
							if(player.getInventory().getItemCountByItemId(182200007) == 0)
								QuestEngine.getInstance().addItem(player, 182200007, 1);
							qs.getQuestVars().setQuestVarById(0, var + 1);
							updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
								.getObjectId(), 10));
							return true;
						}
					case 10002:
						if(var == 3)
						{
							player.getInventory().removeFromBagByItemId(182200009, 1);
							qs.getQuestVars().setQuestVar(99);
							updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
								.getObjectId(), 0));
							World world = player.getActiveRegion().getWorld();
							world.despawn(player);
							// TODO! this should go to PlayerController.teleportTo(...)
							// more todo: when teleporting to the same map then SM_UNKF5 should not be send, but
							// something
							// else
							world.setPosition(player, 310010000, 52, 174, 229, player.getHeading());
							player.setProtectionActive(true);
							PacketSendUtility.sendPacket(player, new SM_PLAYER_SPAWN(player));
							return true;
						}
					case 10003:
						if(var == 5)
						{
							PlayerClass playerClass = player.getCommonData().getPlayerClass();
							if(playerClass == PlayerClass.WARRIOR)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2375);
							else if(playerClass == PlayerClass.SCOUT)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2716);
							else if(playerClass == PlayerClass.MAGE)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 3057);
							else if(playerClass == PlayerClass.PRIEST)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 3398);
						}
					case 10004:
						if(var == 5)
						{
							player.getCommonData().setPlayerClass(PlayerClass.GLADIATOR);
							player.getCommonData().upgradePlayer();
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(player, qs);
						}
					case 10005:
						if(var == 5)
							return setPlayerClass(env, qs, PlayerClass.TEMPLAR);
					case 10006:
						if(var == 5)
							return setPlayerClass(env, qs, PlayerClass.ASSASSIN);
					case 10007:
						if(var == 5)
							return setPlayerClass(env, qs, PlayerClass.RANGER);
					case 10008:
						if(var == 5)
							return setPlayerClass(env, qs, PlayerClass.SORCERER);
					case 10009:
						if(var == 5)
							return setPlayerClass(env, qs, PlayerClass.SPIRIT_MASTER);
					case 10010:
						if(var == 5)
							return setPlayerClass(env, qs, PlayerClass.CLERIC);
					case 10011:
						if(var == 5)
							return setPlayerClass(env, qs, PlayerClass.CHANTER);
				}
			}
			else if(targetId == 730008)
			{
				switch(env.getDialogId())
				{
					case 25:
						if(var == 2)
						{
							if(player.getInventory().getItemCountByItemId(182200008) != 0)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
							else
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1354);
						}
					case 1353:
						if(var == 2)
						{
							PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 14));
							player.getInventory().removeFromBagByItemId(182200008, 1);
							QuestEngine.getInstance().addItem(player, 182200009, 1);
						}
						return false;
					case 10001:
						if(var == 2)
						{
							qs.getQuestVars().setQuestVarById(0, var + 1);
							updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
								.getObjectId(), 10));
							return true;
						}
				}
			}
			else if(targetId == 205000)
			{
				switch(env.getDialogId())
				{
					case 25:
						if(var == 99)
						{
							if(activePlayerId != 0 && activePlayerId != player.getObjectId())
							{
								World world = player.getActiveRegion().getWorld();
								Player activePlayer = world.findPlayer(activePlayerId);
								if(!(activePlayer == null || !activePlayer.isOnline() || activePlayer.getWorldId() != 310010000))
									return false;
							}
							activePlayerId = player.getObjectId();
							for(Npc mob : mobs)
							{
								mob.getAi().stop();
								spawnsData.removeSpawn(mob.getSpawn());
								mob.getController().delete();
							}
							mobs.clear();
							PacketSendUtility.sendPacket(player, new SM_EMOTION(player, 6, 1001, 0));
							qs.getQuestVars().setQuestVar(50);
							updateQuestStatus(player, qs);
							ThreadPoolManager.getInstance().schedule(new Runnable(){
								@Override
								public void run()
								{
									qs.getQuestVars().setQuestVar(51);
									updateQuestStatus(player, qs);
									mobs.add((Monster) QuestEngine.getInstance().addNewSpawn(310010000, 211042,
										(float) 224.073, (float) 239.1, (float) 206.7, (byte) 0, false));
									mobs.add((Monster) QuestEngine.getInstance().addNewSpawn(310010000, 211042,
										(float) 233.5, (float) 241.04, (float) 206.365, (byte) 0, false));
									mobs.add((Monster) QuestEngine.getInstance().addNewSpawn(310010000, 211042,
										(float) 229.6, (float) 265.7, (float) 205.7, (byte) 0, false));
									mobs.add((Monster) QuestEngine.getInstance().addNewSpawn(310010000, 211042,
										(float) 222.8, (float) 262.5, (float) 205.7, (byte) 0, false));
									for(Npc mob : mobs)
									{
										((Monster) mob).getAggroList().addDamageHate(player, 1000, 0);
										mob.getAi().handleEvent(Event.ATTACKED);
									}
								}
							}, 43000);
							return true;
						}
						return false;
					default:
						return false;
				}
			}
		}
		else if(qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 790001)
			{
				return defaultQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs != null)
		{
			if(qs.getStatus() != QuestStatus.START)
				return false;
			int var = qs.getQuestVars().getQuestVars();
			if(var == 4 || (var >= 50 && var <= 55))
			{
				if(activePlayerId == player.getObjectId() && player.getWorldId() == 310010000)
					return false;
				else
				{
					if(player.getWorldId() == 310010000)
					{
						World world = player.getActiveRegion().getWorld();
						world.despawn(player);
						// TODO! this should go to PlayerController.teleportTo(...)
						// more todo: when teleporting to the same map then SM_UNKF5 should not be send, but something
						// else
						world.setPosition(player, 210010000, (float) 243.34, (float) 1639.5, (float) 100.4, player
							.getHeading());
						player.setProtectionActive(true);
						PacketSendUtility.sendPacket(player, new SM_PLAYER_SPAWN(player));
					}
					qs.getQuestVars().setQuestVar(3);
					updateQuestStatus(player, qs);
				}
			}
		}
		if(player.getCommonData().getLevel() < 9)
			return false;
		env.setQuestId(questId);
		QuestEngine.getInstance().getQuest(env).startQuest(QuestStatus.START);
		return true;
	}

	@Override
	public boolean onAttackEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.START || qs.getQuestVars().getQuestVars() != 4)
			return false;
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		if(targetId != 211043)
			return false;
		Monster monster = (Monster) env.getVisibleObject();
		if(monster.getLifeStats().getCurrentHp() < monster.getLifeStats().getMaxHp() / 2)
		{
			PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 151));
			monster.getAi().stop();
			spawnsData.removeSpawn(monster.getSpawn());
			monster.getController().delete();
		}
		return false;
	}

	@Override
	public boolean onItemUseEvent(QuestEnv env, Item item)
	{
		final Player player = env.getPlayer();
		final int id = item.getItemTemplate().getItemId();
		final int itemObjId = item.getObjectId();

		if(id != 182200007)
			return false;
		if(!ZoneManager.getInstance().isInsideZone(player, ZoneName.ITEMUSE_Q1006))
			return false;
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id,
			3000, 0, 0), true);
		ThreadPoolManager.getInstance().schedule(new Runnable(){
			@Override
			public void run()
			{
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId,
					id, 0, 1, 0), true);
				player.getInventory().removeFromBagByObjectId(itemObjId, 1);
				QuestEngine.getInstance().addItem(player, 182200008, 1);
				qs.getQuestVars().setQuestVarById(0, 2);
				updateQuestStatus(player, qs);
			}
		}, 3000);
		return true;
	}

	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId)
	{
		if(movieId != 151)
			return false;
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.START || qs.getQuestVars().getQuestVars() != 4)
			return false;
		mobs.add((Npc) QuestEngine.getInstance().addNewSpawn(310010000, 790001, (float) 220.6, (float) 247.8,
			(float) 206.0, (byte) 0, false));
		qs.getQuestVars().setQuestVar(5);
		updateQuestStatus(player, qs);
		return true;
	}

	private boolean setPlayerClass(QuestEnv env, QuestState qs, PlayerClass playerClass)
	{
		Player player = env.getPlayer();
		player.getCommonData().setPlayerClass(playerClass);
		player.getCommonData().upgradePlayer();
		qs.setStatus(QuestStatus.REWARD);
		updateQuestStatus(player, qs);
		sendQuestDialog(player, env.getVisibleObject().getObjectId(), 5);
		return true;
	}
}
