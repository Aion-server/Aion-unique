/*  
 *  This file is part of aion-unique <aion-unique.com>.
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
import com.aionemu.gameserver.ai.AIState;
import com.aionemu.gameserver.ai.desires.AbstractDesire;
import com.aionemu.gameserver.ai.desires.MoveDesire;
import com.aionemu.gameserver.controllers.movement.MovementType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOVE;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Pinguin, ATracer
 *
 */
public class MoveToTargetDesire extends AbstractDesire implements MoveDesire
{
	/**
	 * Target to which creature should move
	 */
	private Creature target;
	
	private float spawnX;
	private float spawnY;
	private float spawnZ;

	/**
	 * @param target 
	 * @param desirePower
	 */
	public MoveToTargetDesire(Creature target, Npc npc, int desirePower)
	{
		super(desirePower);
		this.target = target;
		spawnX = npc.getSpawn().getX();
		spawnY = npc.getSpawn().getY();
		spawnZ = npc.getSpawn().getZ();
	}

	@Override
	public void handleDesire(AI ai)
	{
		Npc owner = (Npc) ai.getOwner();
		float fightRunSpeed = owner.getTemplate().getStatsTemplate().getRunSpeedFight();
		double dist = MathUtil.getDistance(owner.getX(), owner.getY(), owner.getZ(), target.getX(), target.getY(), target.getZ())  ;
		if(dist > 3)
		{
			
			float x2 = (float) (((target.getX() - owner.getX())/dist) * fightRunSpeed * 0.3) ;
			float y2 = (float) (((target.getY() - owner.getY())/dist) * fightRunSpeed * 0.3) ;
			float z2 = (float) (((target.getZ() - owner.getZ())/dist) * fightRunSpeed * 0.3) ; 

			
			byte heading2 = (byte) (Math.toDegrees(Math.atan2(y2, x2))/3) ;

			PacketSendUtility.broadcastPacket(owner, new SM_MOVE(owner, owner.getX(), owner.getY(), owner.getZ(),(float) (x2 / 0.3) , (float) (y2 / 0.3) , 0 , heading2, MovementType.MOVEMENT_START_KEYBOARD));
			owner.getActiveRegion().getWorld().updatePosition(owner, owner.getX() + x2, owner.getY() + y2, owner.getZ() + z2, heading2);
		}
		else
		{
			owner.getActiveRegion().getWorld().updatePosition(owner, owner.getX(), owner.getY(), owner.getZ(), owner.getHeading());			
			PacketSendUtility.broadcastPacket(owner, new SM_MOVE(owner, owner.getX(), owner.getY(), owner.getZ(), 0, 0, 0, (byte) 0, MovementType.MOVEMENT_STOP));
		}
		
		
		// return back to home
		double distanceToHome = MathUtil.getDistance(owner.getX(), owner.getY(), owner.getZ(), spawnX, spawnY, spawnZ) ;
		if(distanceToHome > 70)
		{
			ai.setAiState(AIState.IDLE);
		}
	}

	public boolean equals(Object o)
	{
		if(this == o)
			return true;
		if(!(o instanceof MoveToTargetDesire))
			return false;

		MoveToTargetDesire that = (MoveToTargetDesire) o;

		return target.equals(that.target);
	}

	/**
	 * @return the target
	 */
	public Creature getTarget()
	{	
		return target;
	}

}