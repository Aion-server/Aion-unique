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
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.walker.RouteData;
import com.aionemu.gameserver.model.templates.walker.WalkerTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author KKnD
 * 
 */
public class WalkDesire extends AbstractDesire implements MoveDesire
{
	private Npc			owner;
	private RouteData	route;
	private boolean		isWalkingToNextPoint	= false;
	private int			targetPosition;
	private long		nextMoveTime;
	private boolean		isRandomWalk			= false;

	public WalkDesire(Npc npc, int power)
	{
		super(power);
		owner = npc;

		WalkerTemplate template = DataManager.WALKER_DATA.getWalkerTemplate(owner.getSpawn().getWalkerId());
		if(template != null)
		{
			isRandomWalk = owner.getSpawn().hasRandomWalk();
			route = template.getRouteData();
			
			owner.getMoveController().setSpeed(owner.getObjectTemplate().getStatsTemplate().getWalkSpeed());
			owner.getMoveController().setWalking(true);
			PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, 0x15));			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean handleDesire(AI ai)
	{

		if(owner == null)
			return false;

		if(route == null)
			return false;

		if(isWalkingToNextPoint())
			checkArrivedToPoint();

		walkToLocation();
		return true;
	}

	/**
	 * Check owner is in a route point
	 */
	private void checkArrivedToPoint()
	{
		float x = route.getRouteSteps().get(targetPosition).getX();
		float y = route.getRouteSteps().get(targetPosition).getY();
		float z = route.getRouteSteps().get(targetPosition).getZ();

		double dist = MathUtil.getDistance(owner, x, y, z);
		if(dist <= 2)
		{
			setWalkingToNextPoint(false);
			getNextTime();
		}
	}

	/**
	 * set next route point if not set and time is ready
	 */
	private void walkToLocation()
	{
		if(!isWalkingToNextPoint() && nextMoveTime <= System.currentTimeMillis())
		{
			setNextPosition();
			setWalkingToNextPoint(true);
			
			float x = route.getRouteSteps().get(targetPosition).getX();
			float y = route.getRouteSteps().get(targetPosition).getY();
			float z = route.getRouteSteps().get(targetPosition).getZ();
			owner.getMoveController().setNewDirection(x, y, z);			
			if(!owner.getMoveController().isScheduled())
				owner.getMoveController().schedule();
		}
	}

	private boolean isWalkingToNextPoint()
	{
		return isWalkingToNextPoint;
	}

	private void setWalkingToNextPoint(boolean value)
	{
		isWalkingToNextPoint = value;
	}

	private void setNextPosition()
	{
		if(isRandomWalk)
		{
			targetPosition = Rnd.get(0, route.getRouteSteps().size() - 1);
		}
		else
		{
			if(targetPosition < (route.getRouteSteps().size() - 1))
				targetPosition++;
			else
				targetPosition = 0;
		}
	}

	private void getNextTime()
	{
		int nextDelay = isRandomWalk ? Rnd.get(5, 60) : route.getRouteSteps().get(targetPosition).getRestTime();
		nextMoveTime = System.currentTimeMillis() + nextDelay * 1000;
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
