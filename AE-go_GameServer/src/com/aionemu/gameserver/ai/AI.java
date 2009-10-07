/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.gameserver.ai;

import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.ai.desires.Desire;
import com.aionemu.gameserver.ai.desires.DesireQueue;
import com.aionemu.gameserver.ai.events.AIEvent;
import com.aionemu.gameserver.ai.task.AiTask;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.utils.ThreadPoolManager;

public abstract class AI<T extends Creature>
{

	private static Logger log = Logger.getLogger(AI.class);
	
	protected DesireQueue	desireQueue	= new DesireQueue();

	protected final T		creature;
	
	private Future<?> scheduledTask;	
	
	private AiTask simpleTask;
	
	private AIState aiState;

	public AI(T creature)
	{
		this.creature = creature;
		aiState = AIState.IDLE;
	}
	
	/**
	 * @return the scheduledTask
	 */
	public Future<?> getScheduledTask()
	{
		return scheduledTask;
	}

	/**
	 * @param scheduledTask the scheduledTask to set
	 */
	public void setScheduledTask(Future<?> scheduledTask)
	{
		this.scheduledTask = scheduledTask;
	}

	/**
	 * @return the aiState
	 */
	public AIState getAiState()
	{
		return aiState;
	}

	/**
	 * @param aiState the aiState to set
	 */
	public void setAiState(AIState aiState)
	{
		this.aiState = aiState;
	}

	public void startNewTask(AiTask task)
	{
		if(scheduledTask != null)
		{
			scheduledTask.cancel(false);
		}
		this.simpleTask = task;
		task.setTaskValid(true);
		scheduledTask = ThreadPoolManager.getInstance().schedule(task, 1000);	
	}
	
	public void stopTask()
	{
		if(scheduledTask != null)
		{
			scheduledTask.cancel(false);
			scheduledTask = null;
		}
		if(simpleTask != null)
		{
			simpleTask.setTaskValid(false);
		}
	}
	
	public boolean isBusyForTask(int taskPriority)
	{
		return taskPriority <= aiState.getPriority();
	}

	protected void handleDesire(Desire desire)
	{
		desire.handleDesire(this);
	}

	public void handleEvent(AIEvent event)
	{
		event.handleEvent(this);
	}
	
	public T getOwner()
	{
		return creature;
	}

}
