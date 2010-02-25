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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.aionemu.gameserver.controllers.movement.ActionObserver;
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
	// TODO revisit here later
	protected Queue<ActionObserver>	moveObservers		= new ConcurrentLinkedQueue<ActionObserver>();
	protected Queue<ActionObserver>	attackObservers		= new ConcurrentLinkedQueue<ActionObserver>();
	protected Queue<ActionObserver>	attackedObservers	= new ConcurrentLinkedQueue<ActionObserver>();
	
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
		notifyMoveObservers();
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
		notifyAttackedObservers();
	}

	/**
	 * Perform tasks when Creature was attacked
	 */
	public void onAttack(Creature creature, int damage)
	{
		this.onAttack(creature, 0, TYPE.REGULAR, damage);
	}

	/**
	 * Teleport Creature to the location using current heading and instanceId
	 * 
	 * @param worldId
	 * @param x
	 * @param y
	 * @param z
	 * @param delay
	 * @return
	 */
	public boolean teleportTo(int worldId, float x, float y, float z, int delay)
	{
		int instanceId = 1;
		if(getOwner().getWorldId() == worldId)
		{
			instanceId = getOwner().getInstanceId();
		}
		return teleportTo(worldId, instanceId, x, y, z, delay);
	}

	/**
	 * 
	 * @param worldId
	 * @param instanceId
	 * @param x
	 * @param y
	 * @param z
	 * @param delay
	 * @return
	 */
	public boolean teleportTo(int worldId, int instanceId, float x, float y, float z, int delay)
	{
		return teleportTo(worldId, instanceId, x, y, z, getOwner().getHeading(), delay);
	}

	/**
	 * Teleport Creature to the location using specific heading
	 * 
	 * @param worldId
	 * @param instanceId
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 * @param delay
	 * @return
	 */
	public boolean teleportTo(int worldId, int instanceId, float x, float y, float z, byte heading, int delay)
	{
		return true;
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
	 * @param observer
	 */
	public void attach(ActionObserver observer)
	{
		switch(observer.getObserverType())
		{
			case ATTACK:
				attackObservers.add(observer);
				break;
			case ATTACKED:
				attackedObservers.add(observer);
				break;
			case MOVE:
				moveObservers.add(observer);
				break;
		}
	}

	/**
	 * notify that creature moved
	 */
	protected void notifyMoveObservers()
	{
		while(!moveObservers.isEmpty())
		{
			ActionObserver observer = moveObservers.poll();
			observer.moved();
		}
	}

	/**
	 * notify that creature attacking
	 */
	protected void notifyAttackObservers()
	{
		while(!attackObservers.isEmpty())
		{
			ActionObserver observer = attackObservers.poll();
			observer.attack();
		}
	}

	/**
	 * notify that creature attacked
	 */
	protected void notifyAttackedObservers()
	{
		while(!attackedObservers.isEmpty())
		{
			ActionObserver observer = attackedObservers.poll();
			observer.attacked();
		}
	}

	/**
	 * 
	 * @param targetObjectId
	 */
	public void attackTarget(int targetObjectId)
	{
		notifyAttackObservers();
	}

	/**
	 * Stops movements
	 */
	public void stopMoving()
	{
		Creature owner = getOwner();
		sp.getWorld().updatePosition(owner, owner.getX(), owner.getY(), owner.getZ(),
			owner.getHeading());
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
