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
package com.aionemu.gameserver.skillengine.task;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * @author ATracer
 *
 */
public abstract class AbstractCraftTask extends AbstractInteractionTask
{
	
	protected int successValue;
	protected int failureValue;
	protected int currentSuccessValue;
	protected int currentFailureValue;
	
	/**
	 * 
	 * @param requestor
	 * @param responder
	 * @param successValue
	 * @param failureValue
	 */
	public AbstractCraftTask(Player requestor, VisibleObject responder, int successValue, int failureValue)
	{
		super(requestor, responder);
		this.successValue = successValue;
		this.failureValue = failureValue;
	}

	@Override
	protected boolean onInteraction()
	{
		if(currentSuccessValue == successValue)
		{
			onSuccessFinish();
			return true;
		}
		if(currentFailureValue == failureValue)
		{
			onFailureFinish();
			return true;
		}
		
		analyzeInteraction();
		
		sendInteractionUpdate();
		return false;
	}
	
	/**
	 *  Perform interaction calculation
	 */
	private void analyzeInteraction()
	{
		//TODO better random
		if(Rnd.nextBoolean())
		{
			currentSuccessValue += Rnd.get(20,60);
		}
		else
		{
			currentFailureValue += Rnd.get(0,30);
		}
		
		if(currentSuccessValue >= successValue)
		{
			currentSuccessValue = successValue;
		}
		else if(currentFailureValue >= failureValue)
		{
			currentFailureValue = failureValue;
		}
	}
	
	protected abstract void sendInteractionUpdate();
	
	protected abstract void onSuccessFinish();
	
	protected abstract void onFailureFinish();
}
