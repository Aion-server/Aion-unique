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
package com.aionemu.gameserver.ai.state.handler;

import com.aionemu.gameserver.ai.AI;
import com.aionemu.gameserver.ai.desires.impl.AggressionDesire;
import com.aionemu.gameserver.ai.desires.impl.WalkDesire;
import com.aionemu.gameserver.ai.events.Event;
import com.aionemu.gameserver.ai.state.AIState;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Monster;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * @author ATracer
 *
 */
public class ActiveAggroStateHandler extends StateHandler
{

	@Override
	public AIState getState()
	{
		return AIState.ACTIVE;
	}
	
	/**
	 * State ACTIVE
	 * AI AggressiveMonsterAi
	 */
	@Override
	public void handleState(AIState state, AI<?> ai)
	{
		ai.clearDesires();
		Npc owner = (Npc) ai.getOwner();
		if (owner.hasWalkRoutes())
		{
			ai.addDesire(new WalkDesire(owner, AIState.ACTIVE.getPriority()));
		}
		
		//if there are players visible - add AggressionDesire filter
		int playerCount = 0;
		for(VisibleObject visibleObject : ai.getOwner().getKnownList())
		{
			if (visibleObject instanceof Player)
			{
				Race playerRace = ((Player) visibleObject).getCommonData().getRace();
				switch(playerRace)
				{
					case ASMODIANS:
						if(owner.isAggressiveTo("PC_DARK"))
							playerCount++;
						break;
					case ELYOS:
						if(owner.isAggressiveTo("PC"))
							playerCount++;
						break;
				}
			}
		}
		if(playerCount > 0)
		{
			ai.addDesire(new AggressionDesire((Monster) ai.getOwner(), AIState.ACTIVE.getPriority()));
		}
		
		if(ai.desireQueueSize() == 0)
			ai.handleEvent(Event.NOTHING_TODO);
		else
			ai.schedule();
	}

}
