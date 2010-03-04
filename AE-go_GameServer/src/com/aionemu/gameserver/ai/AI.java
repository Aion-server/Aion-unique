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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import com.aionemu.gameserver.ai.desires.Desire;
import com.aionemu.gameserver.ai.desires.DesireQueue;
import com.aionemu.gameserver.ai.desires.impl.CounterBasedDesireFilter;
import com.aionemu.gameserver.ai.desires.impl.GeneralDesireIteratorHandler;
import com.aionemu.gameserver.ai.events.Event;
import com.aionemu.gameserver.ai.events.handler.EventHandler;
import com.aionemu.gameserver.ai.state.AIState;
import com.aionemu.gameserver.ai.state.handler.StateHandler;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.utils.ThreadPoolManager;

public abstract class AI<T extends Creature> implements Runnable
{	
	protected Map<Event, EventHandler> eventHandlers = new HashMap<Event, EventHandler>();
	protected Map<AIState, StateHandler> stateHandlers = new HashMap<AIState, StateHandler>();
	
	protected DesireQueue desireQueue = new DesireQueue();

	protected Creature owner;

	protected AIState aiState = AIState.NONE;
	
	protected boolean isStateChanged = false;
	
	private Future<?> aiTask;	
	
	/**
	 * @param desire
	 */
	public void handleDesire(Desire desire)
	{
		desire.handleDesire(this);
	}
	
	/**
	 * 
	 * @param event The event that needs to be handled
	 */
	public void handleEvent(Event event)
	{
		//allow only handling event Event.DIED in dead stats
		//probably i need to define rules for which events could be handled in which state
		if(event != Event.DIED && owner.getLifeStats().isAlreadyDead())
			return;
		
		EventHandler eventHandler = eventHandlers.get(event);
		if(eventHandler != null)
			eventHandler.handleEvent(event, this);
	}
	
	/**
	 * @return owner of this AI
	 */
	public Creature getOwner()
	{
		return owner;
	}
	
	/**
	 * of the AI
	 * @param owner the owner of the AI
	 */
	public void setOwner(Creature owner)
	{
		this.owner = owner;
	}

	/**
	 * @return the aiState
	 */
	public AIState getAiState()
	{
		return aiState;
	}
	
	/**
	 * 
	 * @param eventHandler
	 */
	protected void addEventHandler(EventHandler eventHandler)
	{
		this.eventHandlers.put(eventHandler.getEvent(), eventHandler);
	}
	
	/**
	 * 
	 * @param stateHandler
	 */
	protected void addStateHandler(StateHandler stateHandler)
	{
		this.stateHandlers.put(stateHandler.getState(), stateHandler);
	}

	/**
	 * @param aiState the aiState to set
	 */
	public void setAiState(AIState aiState)
	{
		if(this.aiState != aiState)
		{
			this.aiState = aiState;
			isStateChanged = true;
		}
	}

	public void analyzeState()
	{
		isStateChanged = false;
		StateHandler stateHandler = stateHandlers.get(aiState);
		if(stateHandler != null)
			stateHandler.handleState(aiState, this);
	}
	
	@Override
	public void run()
	{
		desireQueue.iterateDesires(new GeneralDesireIteratorHandler(this), new CounterBasedDesireFilter());
		// todo move to home
		if(desireQueue.isEmpty() || isStateChanged)
		{
			analyzeState();
		}
	}

	public void schedule()
	{
		if(!isScheduled())
		{
			aiTask = ThreadPoolManager.getInstance().scheduleAiAtFixedRate(this, 1000, 1000);
		}	
	}

	public void stop()
	{
		if(aiTask != null && !aiTask.isCancelled())
		{
			aiTask.cancel(true);
			aiTask = null;
		}
	}

	public boolean isScheduled()
	{
		return aiTask != null && !aiTask.isCancelled();
	}
	
	public void clearDesires()
	{
		this.desireQueue.clear();
	}
	
	public void addDesire(Desire desire)
	{
		this.desireQueue.addDesire(desire);
	}
	
	public int desireQueueSize()
	{
		return desireQueue.isEmpty() ? 0 : desireQueue.size();
	}
}
