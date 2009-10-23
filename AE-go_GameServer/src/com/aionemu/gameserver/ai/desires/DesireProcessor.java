/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.ai.desires;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.ai.AIState;
import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * @author Pinguin
 *
 */
public class DesireProcessor implements Runnable
{
	private static Logger log = Logger.getLogger(AttackDesire.class);
	
	Npc npc ;
	DesireQueue desirequeue ;
	public Desire desire;
	
	public DesireProcessor (Npc npc, DesireQueue desirequeue){
		this.npc = npc;
		this.desirequeue = desirequeue;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		while(!desirequeue.isEmpty()){
			desire = desirequeue.peek();
			if (desire != null) desire.handleDesire(npc.getNpcAi());
			desirequeue.removeDesire(desire);
		}
		
		npc.getNpcAi().setAiState(AIState.IDLE);
		
	}

}
