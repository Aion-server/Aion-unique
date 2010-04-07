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

import org.apache.log4j.Logger;

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

	@SuppressWarnings("unused")
	private static final Logger	log	= Logger.getLogger(MoveController.class);
	
	private Future<?> moveTask;
	private Creature owner;
	private boolean directionChanged = true;

	private float targetX;
	private float targetY;
	private float targetZ;

	private boolean isFollowTarget;
	private boolean isStopped = false;

	private int moveCounter;
	private float speed = 0;
	private float distance = 2;
	
	private boolean walking;
	
	/**
	 * 
	 * @param owner
	 */
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

	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(float speed)
	{
		this.speed = speed;
	}

	/**
	 * @param distance the distance to set
	 */
	public void setDistance(float distance)
	{
		this.distance = distance;
	}

	/**
	 * @return the walking
	 */
	public boolean isWalking()
	{
		return walking;
	}

	/**
	 * @param walking the walking to set
	 */
	public void setWalking(boolean walking)
	{
		this.walking = walking;
	}

	public void setNewDirection(float x, float y, float z)
	{
		if(x != targetX || y != targetY || z != targetZ)
			this.directionChanged = true;
		this.targetX = x;
		this.targetY = y;
		this.targetZ = z;
	}

	public float getTargetX()
	{
		return targetX;
	}

	public float getTargetY()
	{
		return targetY;
	}

	public float getTargetZ()
	{
		return targetZ;
	}

	public boolean isScheduled()
	{
		return moveTask != null && !moveTask.isCancelled();
	}

	public void schedule()
	{
		if(speed == 0)
			speed = owner.getGameStats().getCurrentStat(StatEnum.SPEED) / 1000;
		
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
		/**
		 * Demo npc skills - prevent movement while casting
		 */
		if(!owner.canPerformMove() || owner.isCasting())
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
		if(dist > this.distance)
		{
			isStopped = false;


			float x2 = (float) (((targetX - ownerX)/dist) * speed * 0.2) ;
			float y2 = (float) (((targetY - ownerY)/dist) * speed * 0.2) ;
			float z2 = (float) (((targetZ - ownerZ)/dist) * speed * 0.2) ; 

			byte heading2 = (byte) (Math.toDegrees(Math.atan2(y2, x2))/3) ;

			if(directionChanged)
			{
				PacketSendUtility.broadcastPacket(owner, new SM_MOVE(owner,	ownerX, ownerY, ownerZ,
					(float) (x2 / 0.2) , (float) (y2 / 0.2) , 0 , heading2, MovementType.MOVEMENT_START_KEYBOARD));
				directionChanged = false;
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
		this.walking = false;
		
		if(moveTask != null)
		{
			moveTask.cancel(true);
			moveTask = null;
		}
	}
}
