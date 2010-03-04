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

import java.util.concurrent.Future;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 *
 */
public abstract class AbstractInteractionTask
{
	private Future<?> task;
	private int interval = 2500;
	
	protected Player requestor;
	protected VisibleObject responder;
	
	/**
	 * 
	 * @param requestor
	 * @param responder
	 */
	public AbstractInteractionTask(Player requestor, VisibleObject responder)
	{
		super();
		this.requestor = requestor;
		this.responder = responder;
	}

	/**
	 *  Called on each interaction 
	 *  
	 * @return
	 */
	protected abstract boolean onInteraction();
	
	/**
	 * Called when interaction is complete
	 */
	protected abstract void onInteractionFinish();
	
	/**
	 * Called before interaction is started
	 */
	protected abstract void onInteractionStart();
	
	/**
	 * Called when interaction is not complete and need to be aborted
	 */
	protected abstract void onInteractionAbort();
	
	/**
	 * Interaction scheduling method
	 */
	public void start()
	{
		onInteractionStart();
		
		task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable(){
			
			@Override
			public void run()
			{
				if(!validateParticipants())
					stop();
					
				boolean stopTask = onInteraction();
				if(stopTask)				
					stop();
			}
	
		}, 1000, interval);
	}
	
	/**
	 * Stop current interaction
	 */
	public void stop()
	{
		onInteractionFinish();
		
		if(task != null && !task.isCancelled())
		{
			task.cancel(true);
			task = null;
		}
	}
	
	/**
	 * Abort current interaction
	 */
	public void abort()
	{
		onInteractionAbort();
		stop();
	}
	
	/**
	 * 
	 * @return true or false
	 */
	public boolean isInProgress()
	{
		return task != null && !task.isCancelled();
	}
	/**
	 * 
	 * @return true or false
	 */
	public boolean validateParticipants()
	{
		return requestor != null;
	}
}
