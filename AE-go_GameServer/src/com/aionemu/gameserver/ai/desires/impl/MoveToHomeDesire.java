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
import com.aionemu.gameserver.controllers.movement.MovementType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.spawn.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOVE;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;

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

	@Override
	public boolean handleDesire(AI ai)
	{
		if (owner == null || owner.getLifeStats().isAlreadyDead())
			return false;
		
		float walkSpeed = owner.getTemplate().getStatsTemplate().getRunSpeedFight();
		double dist = MathUtil.getDistance(owner.getX(), owner.getY(), owner.getZ(), x, y, z)  ;
		if(dist > 2)
		{
			
			float x2 = (float) (((x - owner.getX())/dist) * walkSpeed * 0.3) ;
			float y2 = (float) (((y - owner.getY())/dist) * walkSpeed * 0.3) ;
			float z2 = (float) (((z - owner.getZ())/dist) * walkSpeed * 0.3) ; 

			
			byte heading2 = (byte) (Math.toDegrees(Math.atan2(y2, x2))/3) ;

			PacketSendUtility.broadcastPacket(owner, new SM_MOVE(owner, owner.getX(), owner.getY(), owner.getZ(),(float) (x2 / 0.3) , (float) (y2 / 0.3) , 0 , heading2, MovementType.MOVEMENT_START_KEYBOARD));
			owner.getActiveRegion().getWorld().updatePosition(owner, owner.getX() + x2, owner.getY() + y2, owner.getZ() + z2, heading2);
		}
		else
		{
			owner.getActiveRegion().getWorld().updatePosition(owner, owner.getX(), owner.getY(), owner.getZ(), owner.getHeading());
			PacketSendUtility.broadcastPacket(owner, new SM_MOVE(owner, owner.getX(), owner.getY(), owner.getZ(), 0, 0, 0, (byte) 0, MovementType.MOVEMENT_STOP));
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
		PacketSendUtility.broadcastPacket(owner, new SM_MOVE(owner, owner.getX(), owner.getY(), owner.getZ(), 0, 0, 0, (byte) 0, MovementType.MOVEMENT_STOP));
	}	
	
}
