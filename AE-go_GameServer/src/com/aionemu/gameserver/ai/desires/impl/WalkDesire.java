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

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai.AI;
import com.aionemu.gameserver.ai.desires.AbstractDesire;
import com.aionemu.gameserver.ai.desires.MoveDesire;
import com.aionemu.gameserver.controllers.movement.MovementType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.walker.RouteData;
import com.aionemu.gameserver.model.templates.walker.WalkerTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOVE;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author KKnD
 *
 */
public class WalkDesire extends AbstractDesire implements MoveDesire
{
	private Npc owner;
	private RouteData _route;
	private boolean _walkingToNextPoint = false;
	private int _currentPos;
	private long _nextMoveTime;
	private boolean isRandomWalk = false;
	
	public WalkDesire(Npc npc, int power)
	{
		super(power);
		owner = npc;
		if (owner != null)
		{
			WalkerTemplate template = DataManager.WALKER_DATA.getWalkerTemplate(owner.getSpawn().getWalkerId());
			if (template != null)
			{
				isRandomWalk = owner.getSpawn().hasRandomWalk();
				_route = template.getRouteData();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean handleDesire(AI ai)
	{
		
		if (owner == null)
			return false;
		
		if (_route == null)
			return false;
		
		if (isWalkingToNextPoint())
			checkArrived();
		
		walkToLocation();
		return true;
	}
	
	private void checkArrived()
	{
		float destinationX = _route.getRouteSteps().get(_currentPos).getX();
		float destinationY = _route.getRouteSteps().get(_currentPos).getY();
		float destinationZ = _route.getRouteSteps().get(_currentPos).getZ();
		double dist = MathUtil.getDistance(owner.getX(), owner.getY(), owner.getZ(), destinationX, destinationY, destinationZ);
		if (dist <=2 )
		{
			setWalkingToNextPoint(false);
			getNextTime();
		}
	}
	
	private void walkToLocation()
	{
		if (!isWalkingToNextPoint() && _nextMoveTime <= System.currentTimeMillis())
		{
			getNextRoute();
			
			setWalkingToNextPoint(true);
		}

		float destinationX = _route.getRouteSteps().get(_currentPos).getX();
		float destinationY = _route.getRouteSteps().get(_currentPos).getY();
		float destinationZ = _route.getRouteSteps().get(_currentPos).getZ();
		
		float walkSpeed = owner.getObjectTemplate().getStatsTemplate().getWalkSpeed();
		
		double dist = MathUtil.getDistance(owner.getX(), owner.getY(), owner.getZ(), destinationX, destinationY, destinationZ);
		//TODO refactor to new MoveController
		if (dist > 2)
		{
			float x2 = (float) (((destinationX - owner.getX())/dist) * walkSpeed) ;
			float y2 = (float) (((destinationY - owner.getY())/dist) * walkSpeed) ;
			float z2 = (float) (((destinationZ - owner.getZ())/dist) * walkSpeed) ; 

			byte heading2 = (byte) (Math.toDegrees(Math.atan2(y2, x2))/3) ;
			
			//TODO [ATracer] probably we don't need to send SM_EMOTION each 0.5 sec - just when
			// new player sees it (onSee in controller) - this needs implementation of current stats
			// like attacking - send corresponding emotion etc
			PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner,0x15,0,0));
			PacketSendUtility.broadcastPacket(owner, new SM_MOVE(owner, owner.getX(), owner.getY(), owner.getZ(),(x2) , (y2) , 0, heading2, MovementType.MOVEMENT_START_KEYBOARD));
			owner.getActiveRegion().getWorld().updatePosition(owner, owner.getX() + x2, owner.getY() + y2, owner.getZ() + z2, heading2);
		}
		else
		{
			owner.getActiveRegion().getWorld().updatePosition(owner, owner.getX(), owner.getY(), owner.getZ(), owner.getHeading());			
			PacketSendUtility.broadcastPacket(owner, new SM_MOVE(owner, owner.getX(), owner.getY(), owner.getZ(), 0, 0, 0, (byte) 0, MovementType.MOVEMENT_STOP));
		}
		
	}
	
	public boolean isWalkingToNextPoint()
	{
		return _walkingToNextPoint;
	}

	public void setWalkingToNextPoint(boolean value)
	{
		_walkingToNextPoint = value;
	}
	
	public void restartRoutes()
	{
		_currentPos = 0;
		_nextMoveTime = 0;
	}
	
	private void getNextRoute()
	{
		if (isRandomWalk)
		{
			_currentPos = Rnd.get(0, _route.getRouteSteps().size() - 1);
		}
		else
		{
			if(_currentPos < (_route.getRouteSteps().size() - 1))
				_currentPos++;
			else
				_currentPos = 0;
		}
	}
	
	private void getNextTime()
	{
		if (isRandomWalk)
		{
			_nextMoveTime = System.currentTimeMillis() + Rnd.get(5, 60)*1000;
		}
		else
		{
			_nextMoveTime = System.currentTimeMillis() + _route.getRouteSteps().get(_currentPos).getRestTime()*1000;
		}
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
