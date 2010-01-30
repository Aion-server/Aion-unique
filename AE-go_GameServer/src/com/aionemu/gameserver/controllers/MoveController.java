/*
 * This file is part of aion-emu <aion-unique.org>.
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
package com.aionemu.gameserver.controllers;

import java.util.concurrent.Future;

import com.aionemu.gameserver.controllers.movement.MovementType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.stats.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOVE;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 *
 */
public class MoveController
{
	
	private Future<?> moveTask;
	private Creature owner;
	private boolean directionChanged = true;
	
	private float targetX;
	private float targetY;
	private float targetZ;

	private boolean isFollowTarget;
	private boolean isStopped = false;
	
	private int moveCounter;
	
	public MoveController(Creature owner)
	{
		this.owner = owner;
	}

	/**
	 * @param isFollowTarget the isFollowTarget to set
	 */
	public void setFollowTarget(boolean isFollowTarget)
	{
		this.isFollowTarget = isFollowTarget;
	}


	public void setNewDirection(float x, float y, float z)
	{
		if(x != targetX || y != targetY || z != targetZ)
			this.directionChanged = true;
		this.targetX = x;
		this.targetY = y;
		this.targetZ = z;
	}
	
	public boolean isScheduled()
	{
		return moveTask != null && !moveTask.isCancelled();
	}

	public void schedule()
	{
		moveTask = ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new Runnable(){
			
			@Override
			public void run()
			{
				move();
			}
		}, 0, 200);
	}
	
	private void move()
	{		
		if(!owner.canPerformMove())
		{
			if(!isStopped)
			{
				isStopped = true;
				owner.getController().stopMoving();
			}		
			return;
		}
		
		VisibleObject target = owner.getTarget();
		
		if(isFollowTarget && target != null)
		{
			setNewDirection(target.getX(), target.getY(), target.getZ());
		}
		
		float ownerX = owner.getX();
		float ownerY = owner.getY();
		float ownerZ = owner.getZ();
		
		double dist = MathUtil.getDistance(ownerX, ownerY, ownerZ, targetX, targetY, targetZ);
		if(dist > 2)
		{
			isStopped = false;
			
			float speed = owner.getGameStats().getCurrentStat(StatEnum.SPEED) / 1000;
			
			float x2 = (float) (((targetX - ownerX)/dist) * speed * 0.2) ;
			float y2 = (float) (((targetY - ownerY)/dist) * speed * 0.2) ;
			float z2 = (float) (((targetZ - ownerZ)/dist) * speed * 0.2) ; 
		
			byte heading2 = (byte) (Math.toDegrees(Math.atan2(y2, x2))/3) ;
				
			if(directionChanged)
			{
				PacketSendUtility.broadcastPacket(owner, new SM_MOVE(owner,	ownerX, ownerY, ownerZ,
					(float) (x2 / 0.2) , (float) (y2 / 0.2) , 0 , heading2, MovementType.MOVEMENT_START_KEYBOARD));			
			}
			
			moveCounter++;		
			owner.getActiveRegion().getWorld().updatePosition(owner, 
				ownerX + x2, ownerY + y2, ownerZ + z2, heading2, moveCounter % 5 == 0);
		}
		else
		{
			if(!isStopped)
			{
				isStopped = true;
				owner.getController().stopMoving();
			}
		}
	}
	
	public double getDistanceToTarget()
	{
		if(isFollowTarget)
		{
			VisibleObject target = owner.getTarget();
			if(target != null)
				return MathUtil.getDistance(owner.getX(), owner.getY(), owner.getZ(),
					target.getX(), target.getY(), target.getZ());
			
		}		
		return MathUtil.getDistance(owner.getX(), owner.getY(), owner.getZ(), targetX, targetY, targetZ);
	}
	
	public void stop()
	{
		if(moveTask != null)
		{
			moveTask.cancel(true);
			moveTask = null;
		}
	}
}
