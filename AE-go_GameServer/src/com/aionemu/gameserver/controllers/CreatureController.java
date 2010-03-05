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

import com.aionemu.gameserver.controllers.movement.MovementType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOVE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.skillengine.model.HopType;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class is for controlling Creatures [npc's, players etc]
 * 
 * @author -Nemesiss-, ATracer(2009-09-29)
 * 
 */
public abstract class CreatureController<T extends Creature> extends VisibleObjectController<Creature>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notSee(VisibleObject object)
	{
		super.notSee(object);
		if(object == getOwner().getTarget())
			getOwner().setTarget(null);
	}

	/**
	 * Perform tasks on Creature starting to move
	 */
	public void onStartMove()
	{
		getOwner().getObserveController().notifyMoveObservers();
	}

	/**
	 * Perform tasks on Creature move in progress
	 */
	public void onMove()
	{

	}

	/**
	 * Perform tasks on Creature stop move
	 */
	public void onStopMove()
	{

	}

	/**
	 * Perform tasks on Creature death
	 */
	public void onDie()
	{
		this.getOwner().getEffectController().removeAllEffects();
		this.getOwner().getMoveController().stop();
	}

	/**
	 * Perform tasks on Creature respawn
	 */
	@Override
	public void onRespawn()
	{

	}

	/**
	 * Perform tasks when Creature was attacked //TODO may be pass only Skill object - but need to add properties in it
	 */
	public void onAttack(Creature creature, int skillId, TYPE type, int damage)
	{
		getOwner().getObserveController().notifyAttackedObservers();
	}

	/**
	 * Perform tasks when Creature was attacked
	 */
	public void onAttack(Creature creature, int damage)
	{
		this.onAttack(creature, 0, TYPE.REGULAR, damage);
	}

	public void onRestore(HopType hopType, int value)
	{
		switch(hopType)
		{
			case HP:
				getOwner().getLifeStats().increaseHp(value);
				break;
			case MP:
				getOwner().getLifeStats().increaseMp(value);
				break;
		}
	}

	/**
	 * Perform drop operation
	 */
	public void doDrop(Player player)
	{

	}

	/**
	 * Perform reward operation
	 * 
	 */
	// TODO probably do reward on list of objects
	public void doReward(Creature creature)
	{

	}

	/**
	 * This method should be overriden in more specific controllers
	 */
	public void onDialogRequest(Player player)
	{

	}

	/**
	 * 
	 * @param targetObjectId
	 */
	public void attackTarget(int targetObjectId)
	{
		getOwner().getObserveController().notifyAttackObservers();
	}

	/**
	 * Stops movements
	 */
	public void stopMoving()
	{
		Creature owner = getOwner();
		sp.getWorld().updatePosition(owner, owner.getX(), owner.getY(), owner.getZ(), owner.getHeading());
		PacketSendUtility.broadcastPacket(owner, new SM_MOVE(owner, owner.getX(), owner.getY(), owner.getZ(), 0, 0, 0,
			owner.getHeading(), MovementType.MOVEMENT_STOP));
	}

	/**
	 * Handle Dialog_Select
	 */
	public void onDialogSelect(int dialogId, Player player, int questId)
	{
		// TODO Auto-generated method stub

	}
}
