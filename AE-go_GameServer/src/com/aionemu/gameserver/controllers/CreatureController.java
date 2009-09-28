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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.services.DecayService;
import com.aionemu.gameserver.services.RespawnService;

/**
 * This class is for controlling Creatures [npc's, players etc]
 * 
 * @author -Nemesiss-, ATracer(2009-09-29)
 * 
 */
public abstract class CreatureController<T extends Creature> extends VisibleObjectController<T>
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
	public boolean onAttack(Creature creature)
	{
		return true;
	}
	
	/**
	 *  Perform drop operation
	 */
	public void doDrop()
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
}
