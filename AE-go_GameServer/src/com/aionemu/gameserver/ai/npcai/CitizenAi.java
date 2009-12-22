/*
 * This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.ai.npcai;

import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.ai.AIState;
import com.aionemu.gameserver.ai.desires.impl.MoveDesireFilter;
import com.aionemu.gameserver.ai.desires.impl.SimpleDesireIteratorHandler;
import com.aionemu.gameserver.ai.desires.impl.WalkDesire;
import com.aionemu.gameserver.model.gameobjects.Citizen;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author KKnD
 *
 */
public class CitizenAi extends NpcAi
{
	private static Logger log = Logger.getLogger(CitizenAi.class);
	
	private Future<?> moveTask;
	private Future<?> walkerTask;
	private WalkDesire walk = null;
	
	@Override
	public void run()
	{
		desireQueue.iterateDesires(new SimpleDesireIteratorHandler(this));
	}
	
	public void schedule()
	{
		stop();
		
		final SimpleDesireIteratorHandler handler = new SimpleDesireIteratorHandler(this);
		final MoveDesireFilter moveDesireFilter = new MoveDesireFilter();

		moveTask = ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new Runnable(){

			@Override
			public void run()
			{
				desireQueue.iterateDesires(handler, moveDesireFilter);
			}
		}, 1000, 500);
	}

	@Override
	public void stop()
	{
		stopMoveTask();
	}
	
	@Override
	public Citizen getOwner()
	{
		return (Citizen)owner;
	}

	private void stopMoveTask()
	{
		if(moveTask != null && !moveTask.isCancelled())
		{
			moveTask.cancel(true);
			moveTask = null;
		}
	}

	@Override
	public boolean isScheduled()
	{
		return moveTask != null;
	}

	@Override
	protected void analyzeState(AIState aiState)
	{
		aiLock.lock();
		try
		{
			switch(aiState)
			{
				case NONE:
					desireQueue.clear();
					stop();
					break;
				case ACTIVE:
					desireQueue.clear();
					if (walk == null)
						walk = new WalkDesire(getOwner(), AIState.ACTIVE.getPriority());
					desireQueue.addDesire(walk);
					schedule();
					break;
				case TALK:
					desireQueue.clear();
					stop();
					if (walk != null)
						walk.stop();
					if (walkerTask != null)
						walkerTask.cancel(true);
					walkerTask = ThreadPoolManager.getInstance().schedule(new Runnable(){

						@Override
						public void run()
						{
							getOwner().getAi().setAiState(AIState.ACTIVE);
						}
					}, 60000);
					break;
			}
		}finally
		{
			aiLock.unlock();
		}

	}
}
