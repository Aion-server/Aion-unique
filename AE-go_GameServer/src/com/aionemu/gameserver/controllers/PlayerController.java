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

import javolution.util.FastMap;

import org.apache.log4j.Logger;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.AttackList;
import com.aionemu.gameserver.model.AttackType;
import com.aionemu.gameserver.model.DuelResult;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.Monster;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerLifeStats;
import com.aionemu.gameserver.model.gameobjects.stats.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DUEL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GATHERABLE_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NEARBY_QUESTS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NPC_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.Skill.SkillType;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.StatFunctions;
import com.aionemu.gameserver.world.World;
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
	
	private FastMap<Integer,AttackList> _attacklist = new FastMap<Integer,AttackList>();

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
			PacketSendUtility.sendPacket(getOwner(), new SM_PLAYER_INFO((Player) object, false));
		}
		else if(object instanceof Npc)
		{
			boolean update = false;
			Npc npc = ((Npc)object);
			PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO(npc));
			for (int questId : QuestEngine.getInstance().getNpcQuestData(npc.getNpcId()).getOnQuestStart())
			{
				if (QuestEngine.getInstance().getQuest(new QuestEnv(object, getOwner(), questId, 0)).checkStartCondition())
				{
					if (!getOwner().getNearbyQuests().contains(questId))
					{
					    update = true;
					    getOwner().getNearbyQuests().add(questId);
					}
				}
			}
			if (update)
				PacketSendUtility.sendPacket( getOwner(), new SM_NEARBY_QUESTS(getOwner().getNearbyQuests()));
		}
		else if(object instanceof Gatherable)
		{
			PacketSendUtility.sendPacket(getOwner(), new SM_GATHERABLE_INFO((Gatherable) object));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notSee(VisibleObject object)
	{
		super.notSee(object);
		if (object instanceof Npc)
		{
			boolean update = false;
			for (int questId : QuestEngine.getInstance().getNpcQuestData(((Npc)object).getNpcId()).getOnQuestStart())
			{
				QuestEnv env = new QuestEnv(object, getOwner(), questId, 0);
				if (QuestEngine.getInstance().getQuest(env).checkStartCondition())
				{
					if (getOwner().getNearbyQuests().contains(questId))
					{
					    update = true;
					    getOwner().getNearbyQuests().remove(getOwner().getNearbyQuests().indexOf(questId));
					}
				}
			}
			if (update)
				PacketSendUtility.sendPacket( getOwner(), new SM_NEARBY_QUESTS(getOwner().getNearbyQuests()));
		}

		PacketSendUtility.sendPacket(getOwner(), new SM_DELETE(object));
	}
	
	/**
	 *  Will be called by ZoneManager when player enters specific zone
	 *  
	 * @param zoneInstance
	 */
	public void onEnterZone(ZoneInstance zoneInstance)
	{
		QuestEngine.getInstance().onEnterZone(new QuestEnv(null, this.getOwner(), 0, 0), zoneInstance.getTemplate().getName());
	}
	
	/**
	 *  Will be called by ZoneManager when player leaves specific zone
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
		//TODO move to DuelController
		if (lastAttacker instanceof Player) { // PvP
			this.lostDuelWith((Player)lastAttacker);
			((Player)lastAttacker).getController().wonDuelWith(player);
		} else { // PvE
			PacketSendUtility.broadcastPacket(this.getOwner(), new SM_EMOTION(this.getOwner().getObjectId(), 13,
				0, lastAttacker.getObjectId()), true);
			PacketSendUtility.sendPacket(player, new SM_DIE());
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.DIE);
		}
	}

	private boolean doAttack (Player player, Creature target, PlayerGameStats pgs, long time, int attackType, int damage, FastMap<Integer,AttackList> attacklist) 
	{
		PacketSendUtility.broadcastPacket(player, new SM_ATTACK(player.getObjectId(), target.getObjectId(), pgs
			.getAttackCounter(), (int) time, attackType, attacklist), true);
		boolean attackSuccess = target.getController().onAttack(player);

		if(attackSuccess)
		{
			target.getLifeStats().reduceHp(damage);
			pgs.increaseAttackCounter();
			if (target instanceof Monster)
			{
				((Monster)target).getController().addDamageHate(player, damage, 0);
			}
		}
		return attackSuccess;
	}
	
	public void attackTarget(int targetObjectId)
	{
		Player player = getOwner();
		PlayerGameStats gameStats = player.getGameStats();
		long time = System.currentTimeMillis();
		int attackType = 0; // TODO investigate attack types
		_attacklist.clear();

		World world = player.getActiveRegion().getWorld();
		Creature target = (Creature) world.findAionObject(targetObjectId);
		if(target == null || !target.getController().isAttackable())
			return;
		
		// TODO fix last attack - cause mob is already dead
		int damage = StatFunctions.calculateBaseDamageToTarget(player, target);
		int hitCount = Rnd.get(1,gameStats.getCurrentStat(StatEnum.MAIN_HAND_HITS) + gameStats.getCurrentStat(StatEnum.OFF_HAND_HITS));
		AttackList atk;
		int finaldamage=0;
		for (int i=0; (i<hitCount); i++) {
			int damages;
			if (i==0)
			{
				damages = Math.round(damage*0.75f);
				atk = new AttackList(damages,AttackType.NORMALHIT);
			}
			else
			{
				damages = Math.round(damage/((float)hitCount-1f));
				atk = new AttackList(damages,AttackType.NORMALHIT);
			}
			//attackSuccess = doAttack(player,target,gameStats,time,attackType,damages);
			_attacklist.put(_attacklist.size(), atk);
			damage -= damages;
			finaldamage += damages;
		}
		doAttack(player,target,gameStats,time,attackType,finaldamage, _attacklist);
	}

	@Override
	public boolean onAttack(Creature creature)
	{
		super.onAttack(creature);
		lastAttacker = creature;

		Player player = getOwner();
		PlayerLifeStats lifeStats = player.getLifeStats();

		return !lifeStats.isAlreadyDead();

	}

	public void useSkill(int skillId)
	{
		Skill skill = SkillEngine.getInstance().getSkillFor(getOwner(), skillId);
		if(skill != null)
		{
			skill.useSkill(SkillType.CAST);
		}
	}

	@Override
	public void doDrop(Player player)
	{
		// TODO Auto-generated method stub
		super.doDrop(player);
	}

	@Override
	public void onMove()
	{
		PlayerGameStats pgs = getOwner().getGameStats();
		pgs.increaseMoveCounter();
		if(pgs.getMoveCounter() % 5 == 0)
		{
			ZoneManager.getInstance().checkZone(getOwner());
		}
	}
	
	@Override
	public void onStartMove()
	{
		super.onStartMove();
	}

	/**
	 * Send the duel request to the owner
	 * 
	 * @param requester the player who requested the duel
	 */
	public void onDuelRequest(Player requester)
	{
		log.debug("[PvP] Player " + this.getOwner().getName() + " has been requested for a duel by "
			+ requester.getName());
		RequestResponseHandler rrh = new RequestResponseHandler(requester){
			public void denyRequest(Player requester, Player responder)
			{
				responder.getController().rejectDuelRequest(requester);
			}

			@Override
			public void acceptRequest(Player requester, Player responder)
			{
				responder.getController().startDuelWith(requester);
				requester.getController().startDuelWith(responder);
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
	 * @param target the player whose the duel was requested
	 */
	public void confirmDuelWith(Player target)
	{
		log.debug("[PvP] Player " + this.getOwner().getName() + " has to confirm his duel with " + target.getName());
		RequestResponseHandler rrh = new RequestResponseHandler(target){
			@Override
			public void denyRequest(Player requester, Player responder)
			{
				log.debug("PvP] Player "+ responder.getName() + " confirmed his duel with "+requester.getName());
			}

			@Override
			public void acceptRequest(Player requester, Player responder)
			{
				responder.getController().cancelDuelRequest(requester);
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
	 * @param requester the duel requester
	 */
	public void rejectDuelRequest(Player requester)
	{
		log.debug("[PvP] Player " + this.getOwner().getName() + " rejected duel request from " + requester.getName());
		requester.getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.DUEL_REJECTED_BY(this.getOwner().getName()));
		this.getOwner().getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.DUEL_REJECT_DUEL_OF(requester.getName()));
	}

	/**
	 * Cancels the duel request
	 * 
	 * @param target the duel target
	 */
	public void cancelDuelRequest(Player target)
	{
		log.debug("[PvP] Player " + this.getOwner().getName() + " cancelled his duel request with " + target.getName());
		target.getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.DUEL_CANCEL_DUEL_BY(this.getOwner().getName()));
		this.getOwner().getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.DUEL_CANCEL_DUEL_WITH(target.getName()));
	}

	/**
	 * Starts the duel
	 * 
	 * @param player the player to start duel with
	 */
	public void startDuelWith(Player player)
	{
		log.debug("[PvP] Player " + this.getOwner().getName() + " start duel with " + player.getName());
		PacketSendUtility.sendPacket(getOwner(), SM_DUEL.SM_DUEL_STARTED(player.getObjectId()));
		lastAttacker = player;
	}

	/**
	 * Won the duel
	 * 
	 * @param attacker the other player
	 */
	public void wonDuelWith(Player attacker)
	{
		log.debug("[PvP] Player " + attacker.getName() + " won duel against " + this.getOwner().getName());
		PacketSendUtility.sendPacket(getOwner(), SM_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_WON,attacker.getName()));
	}
	
	/**
	 * Lost the duel
	 * 
	 * @param attacker the other player
	 */
	public void lostDuelWith(Player attacker)
	{
		log.debug("[PvP] Player " + attacker.getName() + " lost duel against " + this.getOwner().getName());
		PacketSendUtility.sendPacket(getOwner(), SM_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_LOST,attacker.getName()));
		PlayerLifeStats pls = getOwner().getLifeStats();
		getOwner().setLifeStats(new PlayerLifeStats(getOwner(), 1, pls.getCurrentMp()));
		getOwner().getLifeStats().triggerRestoreTask();
	}

	@Override
	public Player getOwner()
	{
		return (Player) super.getOwner();
	}


	@Override
	public boolean isAttackable()
	{
		return true;
	}
}