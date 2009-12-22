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
	private Npc _npc;
	private RouteData _route;
	private boolean _walkingToNextPoint = false;
	private int _currentPos;
	private long _nextMoveTime;
	private boolean isRandomWalk = false;
	
	public WalkDesire(Npc npc, int power)
	{
		super(power);
		_npc = npc;
		if (_npc != null)
		{
			WalkerTemplate template = DataManager.WALKER_DATA.getWalkerTemplate(_npc.getSpawn().getWalkerId());
			if (template != null)
			{
				isRandomWalk = _npc.getSpawn().hasRandomWalk();
				_route = template.getRouteData();
			}
		}
	}
	
	@Override
	public void handleDesire(AI ai)
	{
		
		if (_npc == null)
			return;
		
		if (_route == null)
			return;
		
		if (isWalkingToNextPoint())
			checkArrived();
		
		walkToLocation();
	}
	
	private void checkArrived()
	{
		float destinationX = _route.getRouteSteps().get(_currentPos).getX();
		float destinationY = _route.getRouteSteps().get(_currentPos).getY();
		float destinationZ = _route.getRouteSteps().get(_currentPos).getZ();
		double dist = MathUtil.getDistance(_npc.getX(), _npc.getY(), _npc.getZ(), destinationX, destinationY, destinationZ);
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
		
		float walkSpeed = _npc.getTemplate().getStatsTemplate().getWalkSpeed();
		double dist = MathUtil.getDistance(_npc.getX(), _npc.getY(), _npc.getZ(), destinationX, destinationY, destinationZ);
		
		if (dist > 2)
		{
			float x2 = (float) (((destinationX - _npc.getX())/dist) * walkSpeed * 0.5) ;
			float y2 = (float) (((destinationY - _npc.getY())/dist) * walkSpeed * 0.5) ;
			float z2 = (float) (((destinationZ - _npc.getZ())/dist) * walkSpeed * 0.5) ; 

			
			byte heading2 = (byte) (Math.toDegrees(Math.atan2(y2, x2))/3) ;
			
			//TODO [ATracer] probably we don't need to send SM_EMOTION each 0.5 sec - just when
			// new player sees it (onSee in controller) - this needs implementation of current stats
			// like attacking - send corresponding emotion etc
			PacketSendUtility.broadcastPacket(_npc, new SM_EMOTION(_npc.getObjectId(),0x15,0,0));
			PacketSendUtility.broadcastPacket(_npc, new SM_MOVE(_npc, _npc.getX(), _npc.getY(), _npc.getZ(),(float) (x2 / 0.5) , (float) (y2 / 0.5) , 0, heading2, MovementType.MOVEMENT_START_KEYBOARD));
			_npc.getActiveRegion().getWorld().updatePosition(_npc, _npc.getX() + x2, _npc.getY() + y2, _npc.getZ() + z2, heading2);
		}
		else
		{
			_npc.getActiveRegion().getWorld().updatePosition(_npc, _npc.getX(), _npc.getY(), _npc.getZ(), _npc.getHeading());			
			PacketSendUtility.broadcastPacket(_npc, new SM_MOVE(_npc, _npc.getX(), _npc.getY(), _npc.getZ(), 0, 0, 0, (byte) 0, MovementType.MOVEMENT_STOP));
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
	
	public void stop()
	{
		PacketSendUtility.broadcastPacket(_npc, new SM_MOVE(_npc, _npc.getX(), _npc.getY(), _npc.getZ(), 0, 0, 0, (byte) 0, MovementType.MOVEMENT_STOP));
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
}
