/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
package com.aionemu.gameserver.ai.task;

/**
 * @author ATracer
 *
 */
public abstract class AiTask implements Runnable
{

	protected int delay = 1000;
	
	protected boolean taskValid = true;
	
	
	public AiTask(int delay)
	{
		this.delay = delay;
	}
	
	/**
	 * @param taskValid the taskValid to set
	 */
	public void setTaskValid(boolean taskValid)
	{
		this.taskValid = taskValid;
	}
	
	public abstract int getPriority();

}
