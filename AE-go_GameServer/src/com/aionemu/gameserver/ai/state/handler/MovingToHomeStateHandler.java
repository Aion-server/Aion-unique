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
import com.aionemu.gameserver.ai.desires.impl.MoveToHomeDesire;
import com.aionemu.gameserver.ai.state.AIState;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 *
 */
public class MovingToHomeStateHandler extends StateHandler
{

	@Override
	public AIState getState()
	{
		return AIState.MOVINGTOHOME;
	}
	
	/**
	 * State MOVINGTOHOME
	 * AI MonsterAi
	 * AI GuardAi
	 */
	@Override
	public void handleState(AIState state, AI<?> ai)
	{
		ai.clearDesires();
		Npc npc = (Npc) ai.getOwner();
		npc.setTarget(null);
		npc.getAggroList().clear();
		PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, 30, 0, 0));
		PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, 20, 0, 0));
		ai.addDesire(new MoveToHomeDesire(npc, AIState.MOVINGTOHOME.getPriority()));
		
		ai.schedule();
	}
}
