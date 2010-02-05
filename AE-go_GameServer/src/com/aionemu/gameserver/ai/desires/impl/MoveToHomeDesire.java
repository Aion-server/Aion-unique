/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.ai.desires.impl;

import com.aionemu.gameserver.ai.AI;
import com.aionemu.gameserver.ai.desires.AbstractDesire;
import com.aionemu.gameserver.ai.desires.MoveDesire;
import com.aionemu.gameserver.ai.events.Event;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.spawn.SpawnTemplate;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * @author ATracer
 *
 */
public class MoveToHomeDesire extends AbstractDesire implements MoveDesire
{
	private Npc owner;
	private float x;
	private float y;
	private float z;
	
	public MoveToHomeDesire(Npc owner, int desirePower)
	{
		super(desirePower);
		this.owner = owner;
		SpawnTemplate template = owner.getSpawn();
		x = template.getX();
		y = template.getY();
		z = template.getZ();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean handleDesire(AI ai)
	{
		if (owner == null || owner.getLifeStats().isAlreadyDead())
			return false;
		
		owner.getMoveController().setNewDirection(x, y, z);
		owner.getMoveController().setFollowTarget(false);
		
		if(!owner.getMoveController().isScheduled())
			owner.getMoveController().schedule();
		
		double dist = MathUtil.getDistance(owner.getX(), owner.getY(), owner.getZ(), x, y, z)  ;
		if(dist < 2)
		{			
			ai.handleEvent(Event.BACK_HOME);
			return false;
		}
		return true;
	}

	@Override
	public int getExecutionInterval()
	{
		return 1;
	}

	@Override
	public void onClear()
	{
		owner.getMoveController().stop();
	}	
}
