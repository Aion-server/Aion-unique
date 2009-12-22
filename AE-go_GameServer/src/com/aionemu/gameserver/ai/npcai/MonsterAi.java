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
import com.aionemu.gameserver.ai.desires.Desire;
import com.aionemu.gameserver.ai.desires.impl.AttackDesire;
import com.aionemu.gameserver.ai.desires.impl.AttackDesireFilter;
import com.aionemu.gameserver.ai.desires.impl.MoveDesireFilter;
import com.aionemu.gameserver.ai.desires.impl.MoveToHomeDesire;
import com.aionemu.gameserver.ai.desires.impl.MoveToTargetDesire;
import com.aionemu.gameserver.ai.desires.impl.SimpleDesireIteratorHandler;
import com.aionemu.gameserver.ai.desires.impl.WalkDesire;
import com.aionemu.gameserver.ai.events.AttackEvent;
import com.aionemu.gameserver.model.gameobjects.Monster;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 *
 */
public class MonsterAi extends NpcAi
{
	private static Logger log = Logger.getLogger(MonsterAi.class);

	private Future<?> moveTask;
	private Future<?> attackTask;

	/**
	 * @param event
	 */
	public void handleEvent(AttackEvent event)
	{
		aiLock.lock();
		try{
			if(aiState != AIState.ATTACKING)
			{
				setAiState(AIState.ATTACKING);
				PacketSendUtility.broadcastPacket(getOwner(),
					new SM_EMOTION(getOwner().getObjectId(), 30, 0, event.getOriginator() == null ? 0:event.getOriginator().getObjectId()));
				PacketSendUtility.broadcastPacket(getOwner(),
					new SM_EMOTION(getOwner().getObjectId(), 19, 0, event.getOriginator() == null ? 0:event.getOriginator().getObjectId()));
				desireQueue.addDesire(new AttackDesire(event.getOriginator(), AIState.ATTACKING.getPriority()));
				desireQueue.addDesire(new MoveToTargetDesire(event.getOriginator(), getOwner(), AIState.ATTACKING.getPriority()));
			}
		}finally
		{
			aiLock.unlock();
		}	
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.ai.AI#handleDesire(com.aionemu.gameserver.ai.desires.Desire)
	 */
	@Override
	public void handleDesire(Desire desire)
	{
		this.handleDesire(desire);
	}

	/**
	 * @param desire
	 */
	protected void handleDesire(MoveToTargetDesire desire)
	{

	}
	
	@Override
	public Monster getOwner()
	{
		return (Monster)owner;
	}
	

	@Override
	public void run()
	{
		desireQueue.iterateDesires(new SimpleDesireIteratorHandler(this));
	}

	@Override
	public void schedule()
	{
		//stop all previous tasks
		stop();
		
		//schedule new tasks
		final SimpleDesireIteratorHandler handler = new SimpleDesireIteratorHandler(this);
		final MoveDesireFilter moveDesireFilter = new MoveDesireFilter();
		final AttackDesireFilter attackDesireFilter = new AttackDesireFilter();

		moveTask = ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new Runnable(){

			@Override
			public void run()
			{
				desireQueue.iterateDesires(handler, moveDesireFilter);
			}
		}, 1000, 500);	//TODO profile/optimize and probably increase interval

		attackTask = ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new Runnable(){

			@Override
			public void run()
			{
				desireQueue.iterateDesires(handler, attackDesireFilter);
			}
		}, 1000, 2000);	//TODO attack speed
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.ai.AI#stop()
	 */
	@Override
	public void stop()
	{
		stopAttackTask();
		stopMoveTask();
	}

	private void stopMoveTask()
	{
		if(moveTask != null && !moveTask.isCancelled())
		{
			moveTask.cancel(true);
			moveTask = null;
		}
	}

	private void stopAttackTask()
	{
		if(attackTask != null && !attackTask.isCancelled())
		{
			attackTask.cancel(true);
			attackTask = null;
		}	
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.ai.AI#isScheduled()
	 */
	@Override
	public boolean isScheduled()
	{
		return moveTask != null && attackTask != null;
	}

	@Override
	protected void analyzeState(AIState aiState)
	{
		aiLock.lock();
		try
		{
			switch(aiState)
			{
				case IDLE:
					desireQueue.clear();//TODO remove based on filter
					stopAttackTask();
					PacketSendUtility.broadcastPacket(getOwner(),
						new SM_EMOTION(getOwner().getObjectId(), 30, 0,  getOwner().getTarget() == null ? 0:getOwner().getTarget().getObjectId()));
					PacketSendUtility.broadcastPacket(getOwner(),
						new SM_EMOTION(getOwner().getObjectId(), 20, 0, getOwner().getTarget() == null ? 0:getOwner().getTarget().getObjectId()));
					PacketSendUtility.broadcastPacket(getOwner(),
						new SM_DIALOG_WINDOW(getOwner().getObjectId(), 0));
					desireQueue.addDesire(new MoveToHomeDesire(getOwner().getSpawn(),
						AIState.IDLE.getPriority()));
					break;
				case NONE:
					desireQueue.clear();
					stop();
					break;
				case ATTACKING:
					desireQueue.clear();
					schedule();
					break;
				case ACTIVE:
					desireQueue.clear();
					if (getOwner().hasWalkRoutes())
						desireQueue.addDesire(new WalkDesire(getOwner(), AIState.ACTIVE.getPriority()));
					schedule();
					break;
			}
		}finally
		{
			aiLock.unlock();
		}

	}
}