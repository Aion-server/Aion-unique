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

import com.aionemu.gameserver.dataholders.SkillData;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerLifeStats;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NPC_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.SkillHandler;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.ClassStats;
import com.aionemu.gameserver.utils.stats.StatFunctions;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * This class is for controlling players.
 * 
 * @author -Nemesiss-, ATracer (2009-09-29)
 *
 */
public class PlayerController extends CreatureController<Player>
{
	private static Logger log = Logger.getLogger(PlayerController.class);
	
	// TEMP till player AI introduced
	private Creature lastAttacker;

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
	 *  Shoul only be triggered from one place (life stats)
	 */
	@Override
	public void onDie()
	{
		super.onDie();

		//TODO probably introduce variable - last attack creature in player AI
		PacketSendUtility.broadcastPacket(this.getOwner(), new SM_EMOTION(this.getOwner().getObjectId(), 13 , lastAttacker.getObjectId()), true);
		Player player = this.getOwner();
		PacketSendUtility.sendPacket(player, new SM_DIE());
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.DIE);
	}

	public void attackTarget(int targetObjectId)
	{
		Player player = getOwner();
		PlayerGameStats gameStats = player.getGameStats();
		long time = System.currentTimeMillis();
		int attackType = 0; //TODO investigate attack types	

		World world = player.getActiveRegion().getWorld();
		Npc npc = (Npc) world.findAionObject(targetObjectId);

		//TODO fix last attack - cause mob is already dead
		int damage = StatFunctions.calculateBaseDamageToTarget(player, npc);
		PacketSendUtility.broadcastPacket(player,
			new SM_ATTACK(player.getObjectId(), targetObjectId,
				gameStats.getAttackCounter(), (int) time, attackType, damage), true);

		boolean attackSuccess = npc.getController().onAttack(player);

		if(attackSuccess)
		{
			npc.getLifeStats().reduceHp(damage);
			gameStats.increateAttackCounter();
		}		
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.controllers.CreatureController#onAttack(com.aionemu.gameserver.model.gameobjects.Creature)
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
		SkillHandler skillHandler = SkillEngine.getInstance().getSkillHandlerFor(skillId);
		if(skillHandler != null)
		{
			skillHandler.useSkill(this.getOwner());
		}
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.controllers.CreatureController#doDrop()
	 */
	@Override
	public void doDrop()
	{
		// TODO Auto-generated method stub
		super.doDrop();
	}
}
