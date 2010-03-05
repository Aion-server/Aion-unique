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
package com.aionemu.gameserver.controllers;

import java.util.List;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.controllers.attack.AttackResult;
import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.PlayerQuestListDAO;
import com.aionemu.gameserver.dao.PlayerSkillListDAO;
import com.aionemu.gameserver.model.DuelResult;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Monster;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticObject;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.gameobjects.player.SkillListEntry;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerLifeStats;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DUEL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GATHERABLE_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEVEL_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NEARBY_QUESTS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NPC_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PRIVATE_STORE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_CANCEL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.HopType;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneManager;

/**
 * This class is for controlling players.
 * 
 * @author -Nemesiss-, ATracer (2009-09-29), xavier
 * 
 */
public class PlayerController extends CreatureController<Player>
{
	private static Logger	log	= Logger.getLogger(PlayerController.class);

	// TEMP till player AI introduced
	private Creature		lastAttacker;

	private boolean			isInShutdownProgress;

	/**
	 * @return the lastAttacker
	 */
	public Creature getLastAttacker()
	{
		return lastAttacker;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void see(VisibleObject object)
	{
		super.see(object);
		if(object instanceof Player)
		{
			PacketSendUtility.sendPacket(getOwner(), new SM_PLAYER_INFO((Player) object, isEnemy((Player) object)));
			getOwner().getEffectController().sendEffectIconsTo((Player) object);
		}
		else if(object instanceof Npc)
		{
			boolean update = false;
			Npc npc = ((Npc) object);
			PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO(npc, getOwner()));
			for(int questId : QuestEngine.getInstance().getNpcQuestData(npc.getNpcId()).getOnQuestStart())
			{
				if(QuestEngine.getInstance().getQuest(new QuestEnv(object, getOwner(), questId, 0))
					.checkStartCondition())
				{
					if(!getOwner().getNearbyQuests().contains(questId))
					{
						update = true;
						getOwner().getNearbyQuests().add(questId);
					}
				}
			}
			if(update)
				updateNearbyQuestList();
		}
		else if(object instanceof Gatherable || object instanceof StaticObject)
		{
			PacketSendUtility.sendPacket(getOwner(), new SM_GATHERABLE_INFO(object));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notSee(VisibleObject object)
	{
		super.notSee(object);
		if(object instanceof Npc)
		{
			boolean update = false;
			for(int questId : QuestEngine.getInstance().getNpcQuestData(((Npc) object).getNpcId()).getOnQuestStart())
			{
				QuestEnv env = new QuestEnv(object, getOwner(), questId, 0);
				if(QuestEngine.getInstance().getQuest(env).checkStartCondition())
				{
					if(getOwner().getNearbyQuests().contains(questId))
					{
						update = true;
						getOwner().getNearbyQuests().remove(getOwner().getNearbyQuests().indexOf(questId));
					}
				}
			}
			if(update)
				updateNearbyQuestList();
		}

		PacketSendUtility.sendPacket(getOwner(), new SM_DELETE(object));
	}

	/**
	 * Will be called by ZoneManager when player enters specific zone
	 * 
	 * @param zoneInstance
	 */
	public void onEnterZone(ZoneInstance zoneInstance)
	{
		QuestEngine.getInstance().onEnterZone(new QuestEnv(null, this.getOwner(), 0, 0),
			zoneInstance.getTemplate().getName());
	}

	/**
	 * Will be called by ZoneManager when player leaves specific zone
	 * 
	 * @param zoneInstance
	 */
	public void onLeaveZone(ZoneInstance zoneInstance)
	{

	}

	/**
	 * {@inheritDoc}
	 * 
	 * Shoul only be triggered from one place (life stats)
	 */
	@Override
	public void onDie()
	{
		super.onDie();
		// TODO probably introduce variable - last attack creature in player AI
		Player player = this.getOwner();
		// TODO move to DuelController
		if(lastAttacker instanceof Player && !isEnemy((Player) lastAttacker))
		{
			this.lostDuelWith((Player) lastAttacker);
			((Player) lastAttacker).getController().wonDuelWith(player);
		}
		else
		{
			getOwner().setState(CreatureState.DEAD);
			if(player.getLevel() > 4)
				player.getCommonData().calculateExpLoss();

			PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 13, 0, lastAttacker.getObjectId()), true);
			PacketSendUtility.sendPacket(player, new SM_DIE());
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.DIE);
			QuestEngine.getInstance().onDie(new QuestEnv(null, player, 0, 0));
		}
		if(lastAttacker instanceof Player && isEnemy((Player) lastAttacker))
		{
			sp.getAbyssService().doReward(getOwner(), (Player) lastAttacker);
		}
	}

	@Override
	public void attackTarget(int targetObjectId)
	{
		super.attackTarget(targetObjectId);

		Player player = getOwner();

		if(!player.canAttack())
			return;

		PlayerGameStats gameStats = player.getGameStats();

		Creature target = (Creature) sp.getWorld().findAionObject(targetObjectId);

		if(!RestrictionsManager.canAttack(player, target))
			return;

		List<AttackResult> attackResult = AttackUtil.calculateAttackResult(player, target);

		int damage = 0;
		for(AttackResult result : attackResult)
		{
			damage += result.getDamage();
		}

		long time = System.currentTimeMillis();
		int attackType = 0; // TODO investigate attack types
		PacketSendUtility.broadcastPacket(player, new SM_ATTACK(player, target, gameStats
			.getAttackCounter(), (int) time, attackType, attackResult), true);

		target.getController().onAttack(player, damage);

		gameStats.increaseAttackCounter();
	}

	@Override
	public void onAttack(Creature creature, int skillId, TYPE type, int damage)
	{
		if(getOwner().getLifeStats().isAlreadyDead())
			return;

		super.onAttack(creature, skillId, type, damage);
		lastAttacker = creature;
		getOwner().getLifeStats().reduceHp(damage);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_ATTACK_STATUS(getOwner(), type, skillId, damage), true);
	}
	
	/**
	 * 
	 * @param skillId
	 */
	public void useSkill(int skillId)
	{
		Player player = getOwner();

		Skill skill = SkillEngine.getInstance().getSkillFor(player, skillId, player.getTarget());
		if(skill != null)
		{
			if(!RestrictionsManager.canUseSkill(player, skill))
				return;
			
			// later differentiate between skills
			getOwner().getObserveController().notifyAttackObservers();
			
			skill.useSkill();
		}
	}

	@Override
	public void doDrop(Player player)
	{
		super.doDrop(player);
	}

	@Override
	public void onMove()
	{
		super.onMove();
		PlayerGameStats pgs = getOwner().getGameStats();
		pgs.increaseMoveCounter();
		if(pgs.getMoveCounter() % 5 == 0)
		{
			ZoneManager.getInstance().checkZone(getOwner());
		}
	}

	@Override
	public void onStopMove()
	{
		super.onStopMove();
	}

	@Override
	public void onStartMove()
	{
		if(this.getOwner().isCasting())
		{
			this.getOwner().setCasting(null);
			PacketSendUtility.sendPacket(this.getOwner(), new SM_SKILL_CANCEL(this.getOwner()));
			PacketSendUtility.sendPacket(this.getOwner(), SM_SYSTEM_MESSAGE.STR_SKILL_CANCELED());
		}
		super.onStartMove();
	}

	/**
	 * Send the duel request to the owner
	 * 
	 * @param requester
	 *            the player who requested the duel
	 */
	public void onDuelRequest(Player requester)
	{
		log.debug("[Duel] Player " + this.getOwner().getName() + " has been requested for a duel by "
			+ requester.getName());
		RequestResponseHandler rrh = new RequestResponseHandler(requester){
			@Override
			public void denyRequest(Creature requester, Player responder)
			{
				responder.getController().rejectDuelRequest((Player) requester);
			}

			@Override
			public void acceptRequest(Creature requester, Player responder)
			{
				responder.getController().startDuelWith((Player) requester);
				((Player) requester).getController().startDuelWith(responder);
			}
		};
		this.getOwner().getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_ACCEPT_DUEL, rrh);
		PacketSendUtility.sendPacket(this.getOwner(), new SM_QUESTION_WINDOW(
			SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_ACCEPT_DUEL, requester.getObjectId(), requester.getName()));
		PacketSendUtility.sendPacket(this.getOwner(), SM_SYSTEM_MESSAGE.DUEL_ASKED_BY(requester.getName()));
	}

	/**
	 * Asks confirmation for the duel request
	 * 
	 * @param target
	 *            the player whose the duel was requested
	 */
	public void confirmDuelWith(Player target)
	{
		log.debug("[Duel] Player " + this.getOwner().getName() + " has to confirm his duel with " + target.getName());
		RequestResponseHandler rrh = new RequestResponseHandler(target){
			@Override
			public void denyRequest(Creature requester, Player responder)
			{
				log.debug("[Duel] Player " + responder.getName() + " confirmed his duel with " + requester.getName());
			}

			@Override
			public void acceptRequest(Creature requester, Player responder)
			{
				responder.getController().cancelDuelRequest((Player) requester);
			}
		};
		this.getOwner().getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_CONFIRM_DUEL, rrh);
		PacketSendUtility.sendPacket(this.getOwner(), new SM_QUESTION_WINDOW(
			SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_CONFIRM_DUEL, target.getObjectId(), target.getName()));
		PacketSendUtility.sendPacket(this.getOwner(), SM_SYSTEM_MESSAGE.DUEL_ASKED_TO(target.getName()));
	}

	/**
	 * Rejects the duel request
	 * 
	 * @param requester
	 *            the duel requester
	 */
	public void rejectDuelRequest(Player requester)
	{
		log.debug("[Duel] Player " + this.getOwner().getName() + " rejected duel request from " + requester.getName());
		requester.getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.DUEL_REJECTED_BY(this.getOwner().getName()));
		this.getOwner().getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.DUEL_REJECT_DUEL_OF(requester.getName()));
	}

	/**
	 * Cancels the duel request
	 * 
	 * @param target
	 *            the duel target
	 */
	public void cancelDuelRequest(Player target)
	{
		log
			.debug("[Duel] Player " + this.getOwner().getName() + " cancelled his duel request with "
				+ target.getName());
		target.getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.DUEL_CANCEL_DUEL_BY(this.getOwner().getName()));
		this.getOwner().getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.DUEL_CANCEL_DUEL_WITH(target.getName()));
	}

	/**
	 * Starts the duel
	 * 
	 * @param player
	 *            the player to start duel with
	 */
	public void startDuelWith(Player player)
	{
		log.debug("[Duel] Player " + this.getOwner().getName() + " start duel with " + player.getName());
		PacketSendUtility.sendPacket(getOwner(), SM_DUEL.SM_DUEL_STARTED(player.getObjectId()));
		lastAttacker = player;
	}

	/**
	 * Won the duel
	 * 
	 * @param attacker
	 *            the other player
	 */
	public void wonDuelWith(Player attacker)
	{
		log.debug("[Duel] Player " + attacker.getName() + " won duel against " + this.getOwner().getName());
		PacketSendUtility.sendPacket(getOwner(), SM_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_WON, attacker.getName()));
	}

	/**
	 * Lost the duel
	 * 
	 * @param attacker
	 *            the other player
	 */
	public void lostDuelWith(Player attacker)
	{
		log.debug("[Duel] Player " + attacker.getName() + " lost duel against " + this.getOwner().getName());
		PacketSendUtility.sendPacket(getOwner(), SM_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_LOST, attacker.getName()));
		PlayerLifeStats pls = getOwner().getLifeStats();
		getOwner().setLifeStats(new PlayerLifeStats(getOwner(), 1, pls.getCurrentMp()));
		getOwner().getLifeStats().triggerRestoreTask();
	}

	/**
	 * 
	 */
	public void updatePassiveStats()
	{
		Player player = getOwner();
		for(SkillListEntry skillEntry : player.getSkillList().getAllSkills())
		{
			Skill skill = SkillEngine.getInstance().getSkillFor(player, skillEntry.getSkillId(), player.getTarget());
			if(skill != null && skill.isPassive())
			{
				skill.useSkill();
			}
		}
	}

	@Override
	public Player getOwner()
	{
		return (Player) super.getOwner();
	}

	@Override
	public void onRestore(HopType hopType, int value)
	{
		super.onRestore(hopType, value);
		switch(hopType)
		{
			case DP:
				getOwner().getCommonData().addDp(value);
				break;
		}
	}

	public boolean isEnemy(Player player)
	{
		return player.getCommonData().getRace() != getOwner().getCommonData().getRace();
	}

	public boolean isEnemy(Npc npc)
	{
		return npc instanceof Monster || npc.isAggressiveTo(getOwner().getCommonData().getRace());
	}

	public void updateNearbyQuestList()
	{
		getOwner().addPacketBroadcastMask(BroadcastMode.UPDATE_NEARBY_QUEST_LIST);
	}

	public void updateNearbyQuestListImpl()
	{
		PacketSendUtility.sendPacket(getOwner(), new SM_NEARBY_QUESTS(getOwner().getNearbyQuests()));
	}

	public boolean isInShutdownProgress()
	{
		return isInShutdownProgress;
	}

	public void setInShutdownProgress(boolean isInShutdownProgress)
	{
		this.isInShutdownProgress = isInShutdownProgress;
	}

	/**
	 * Handle dialog
	 */
	public void onDialogSelect(int dialogId, Player player, int questId)
	{
		switch(dialogId)
		{
			case 2:
				PacketSendUtility.sendPacket(player, new SM_PRIVATE_STORE(getOwner().getStore()));
				break;
		}
	}

	/**
	 * @param level
	 */
	public void upgradePlayer(int level)
	{
		Player player = getOwner();
		PlayerStatsTemplate statsTemplate = sp.getPlayerService().getPlayerStatsData().getTemplate(player);

		player.getGameStats().doLevelUpgrade(sp.getPlayerService().getPlayerStatsData(), level);
		player.setPlayerStatsTemplate(statsTemplate);
		player.setLifeStats(new PlayerLifeStats(player, statsTemplate.getMaxHp(), statsTemplate.getMaxMp()));
		player.getLifeStats().synchronizeWithMaxStats();

		PacketSendUtility.broadcastPacket(player, new SM_LEVEL_UPDATE(player.getObjectId(), 0, level), true);

		// Temporal
		ClassChangeService.showClassChangeDialog(player);

		QuestEngine.getInstance().onLvlUp(new QuestEnv(null, player, 0, 0));

		PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));

		// add new skills
		sp.getSkillLearnService().addNewSkills(player, false);
		if(level == 10)
		{
			player.getSkillList().removeSkill(30001);
			PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player));
		}
		DAOManager.getDAO(PlayerSkillListDAO.class).storeSkills(player);

		DAOManager.getDAO(PlayerQuestListDAO.class).store(player);
		// save player at this point
		DAOManager.getDAO(PlayerDAO.class).storePlayer(player);

		/** update member list packet if player is legion member **/
		if(player.isLegionMember())
			sp.getLegionService().updateMemberInfo(player);
	}

	public void startFly()
	{
		Player player = getOwner();

		if(player.isInState(CreatureState.FLYING) && !player.isInState(CreatureState.GLIDING))
		{
			player.setFlyState(1);
		}
		else if(player.isInState(CreatureState.GLIDING))
		{
			player.setFlyState(2);
		}
		PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));

		// TODO: period task for fly time decrease
	}

	public void endFly()
	{
		Player player = getOwner();

		if(player.isInState(CreatureState.FLYING))
		{
			player.setFlyState(1);
		}
		else
		{
			player.setFlyState(0);
		}
		PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));

		// TODO: period task for fly time increase
	}

	/**
	 * TODO: REMOVE THIS AND FIX FOR RETURNEFFECT AND WORLDSCRIPTSOMETHING
	 * @param b
	 */
	public void moveToBindLocation(boolean b)
	{
		sp.getTeleportService().moveToBindLocation(getOwner(), b);
	}
}