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

import org.apache.log4j.Logger;

import com.aionemu.gameserver.ai.AI;
import com.aionemu.gameserver.ai.AIState;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * @author ATracer
 *
 */
public class AttackTask extends AiTask
{

	private static Logger log = Logger.getLogger(AttackTask.class);
	
	private Creature attacker;
	
	private Creature target;
	
	public static final int PRIORITY = 2;
	
	public AttackTask(Creature attacker, Creature target, int delay)
	{
		super(delay);
		this.attacker = attacker;
		this.target = target;
		
	}
	/**
	 * @param target the target to set
	 */
	public void setTarget(Creature target)
	{
		this.target = target;
	}

	
	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.ai.task.GeneralTask#run()
	 */
	@Override
	public void run()
	{
		while(taskValid)
		{
			Npc npc = (Npc) attacker;
			npc.getController().attackTarget(target.getObjectId());
			try
			{
				Thread.sleep(delay);
			}
			catch(InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//TODO generalize getNpcAi method to getAi
		((Npc)attacker).getNpcAi().setAiState(AIState.IDLE);
	}
	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.ai.task.AiTask#getPriority()
	 */
	@Override
	public int getPriority()
	{
		return PRIORITY;
	}
	
}
