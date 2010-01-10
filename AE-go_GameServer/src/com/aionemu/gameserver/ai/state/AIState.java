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
package com.aionemu.gameserver.ai.state;

/**
 * @author ATracer
 *
 */
public enum AIState
{
	THINKING(5),
	TALKING(4),
	AGGRO(3),
	ACTIVE(3),
	ATTACKING(2),
	RESTING(1),
	MOVINGTOHOME(1),
	NONE(0);
	
	private int priority;
	
	private AIState(int priority)
	{
		this.priority = priority;
	}
	
	public int getPriority()
	{
		return priority;
	}

}
