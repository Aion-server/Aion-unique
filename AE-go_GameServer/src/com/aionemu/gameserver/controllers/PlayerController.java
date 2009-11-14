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

import org.apache.log4j.Logger;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.DuelResult;
import com.aionemu.gameserver.model.SkillElement;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerLifeStats;
import com.aionemu.gameserver.model.gameobjects.stats.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DUEL_RESULT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DUEL_STARTED;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NPC_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.StatFunctions;
import com.aionemu.gameserver.world.World;

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
			PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO((Npc) object));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notSee(VisibleObject object)
	{
		super.notSee(object);
		PacketSendUtility.sendPacket(getOwner(), new SM_DELETE(object));
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
		if (lastAttacker instanceof Player) { // PvP
			this.lostDuelWith((Player)lastAttacker);
			((Player)lastAttacker).getController().wonDuelWith(player);
		} else { // PvE
			PacketSendUtility.broadcastPacket(this.getOwner(), new SM_EMOTION(this.getOwner().getObjectId(), 13,
				lastAttacker.getObjectId()), true);
			PacketSendUtility.sendPacket(player, new SM_DIE());
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.DIE);
		}
	}

	private boolean doAttack (Player player, Creature target, PlayerGameStats pgs, long time, int attackType, int damage) {
		PacketSendUtility.broadcastPacket(player, new SM_ATTACK(player.getObjectId(), target.getObjectId(), pgs
			.getAttackCounter(), (int) time, attackType, damage), true);
		boolean attackSuccess = target.getController().onAttack(player);

		if(attackSuccess)
		{
			target.getLifeStats().reduceHp(damage);
			pgs.increaseAttackCounter();
		}
		return attackSuccess;
	}
	
	public void attackTarget(int targetObjectId)
	{
		Player player = getOwner();
		PlayerGameStats gameStats = player.getGameStats();
		long time = System.currentTimeMillis();
		int attackType = 0; // TODO investigate attack types

		World world = player.getActiveRegion().getWorld();
		Creature target = (Creature) world.findAionObject(targetObjectId);

		// TODO fix last attack - cause mob is already dead
		int damage;
		if (gameStats.getBaseStat(StatEnum.IS_MAGICAL_ATTACK)==1) {
			int baseDamage = gameStats.getBaseStat(StatEnum.MIN_DAMAGES);
			baseDamage += Rnd.get(gameStats.getBaseStat(StatEnum.MIN_DAMAGES), gameStats.getBaseStat(StatEnum.MAX_DAMAGES));
			damage = StatFunctions.calculateMagicDamageToTarget(player, target, baseDamage, SkillElement.NONE);
		} else {
			damage = StatFunctions.calculateBaseDamageToTarget(player, target);
		}
		boolean attackSuccess = doAttack(player,target,gameStats,time,attackType,damage);
		for (int i=1; (i<gameStats.getBaseStat(StatEnum.HIT_COUNT))&&attackSuccess; i++) {
			damage = Rnd.get(damage/10);
			attackSuccess = doAttack(player,target,gameStats,time,attackType,damage);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aionemu.gameserver.controllers.CreatureController#onAttack(com.aionemu.gameserver.model.gameobjects.Creature)
	 */
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
			skill.useSkill();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aionemu.gameserver.controllers.CreatureController#doDrop()
	 */
	@Override
	public void doDrop()
	{
		// TODO Auto-generated method stub
		super.doDrop();
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.controllers.CreatureController#onMove()
	 */
	@Override
	public void onMove()
	{
		super.onMove();
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
			SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_ACCEPT_DUEL, requester.getName()));
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
			SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_CONFIRM_DUEL, target.getName()));
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
		PacketSendUtility.sendPacket(getOwner(), new SM_DUEL_STARTED(player.getObjectId()));
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
		PacketSendUtility.sendPacket(getOwner(), new SM_DUEL_RESULT(DuelResult.DUEL_WON,attacker.getName()));
	}
	
	/**
	 * Lost the duel
	 * 
	 * @param attacker the other player
	 */
	public void lostDuelWith(Player attacker)
	{
		log.debug("[PvP] Player " + attacker.getName() + " lost duel against " + this.getOwner().getName());
		PacketSendUtility.sendPacket(getOwner(), new SM_DUEL_RESULT(DuelResult.DUEL_LOST,attacker.getName()));
		PlayerLifeStats pls = getOwner().getLifeStats();
		getOwner().setLifeStats(new PlayerLifeStats(getOwner(), 1, pls.getCurrentMp()));
		getOwner().getLifeStats().triggerRestoreTask();
	}

}
