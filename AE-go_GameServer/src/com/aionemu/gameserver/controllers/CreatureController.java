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

import com.aionemu.gameserver.controllers.movement.MoveObserver;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * This class is for controlling Creatures [npc's, players etc]
 * 
 * @author -Nemesiss-, ATracer(2009-09-29)
 * 
 */
public abstract class CreatureController<T extends Creature> extends VisibleObjectController<Creature>
{
	
	protected Queue<MoveObserver> moveObservers = new ConcurrentLinkedQueue<MoveObserver>();

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
	 *  Perform tasks on Creature starting to move
	 */
	public void onStartMove()
	{
		notifyMoveObservers();
	}
	
	/**
	 *  Perform tasks on Creature move in progress
	 */
	public void onMove()
	{

	}
	
	/**
	 *  Perform tasks on Creature stop move
	 */
	public void onStopMove()
	{
			
	}
	
	/**
	 *  Perform tasks on Creature death
	 */
	public void onDie()
	{

	}
	
	/**
	 *  Perform tasks on Creature respawn
	 */
	public void onRespawn()
	{
		
	}
	
	/**
	 *  Perform tasks when Creature was attacked
	 */
	public void onAttack(Creature creature, int skillId,  int damage)
	{

	}
	
	/**
	 *  Perform drop operation
	 */
	public void doDrop(Player player)
	{
		
	}
	
	/**
	 * Perform reward operation
	 * 
	 */
	//TODO probably do reward on list of objects
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
	 * @param moveObserver
	 */
	public void attach(MoveObserver moveObserver)
	{
		moveObservers.add(moveObserver);
	}
	
	protected void notifyMoveObservers()
	{
		while(!moveObservers.isEmpty())
		{
			MoveObserver observer = moveObservers.poll();
			observer.moved();
		}
	}

	/**
	 * 
	 * @param targetObjectId
	 */
	public void attackTarget(int targetObjectId)
	{
		//attack target
	}
	
	/**
	 *  Specifies whether this target is attackable
	 *  
	 * @return
	 */
	public boolean isAttackable()
	{
		return false;
	}
}
