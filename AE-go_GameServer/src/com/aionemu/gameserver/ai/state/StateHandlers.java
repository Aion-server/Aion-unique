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
package com.aionemu.gameserver.ai.state;

import com.aionemu.gameserver.ai.state.handler.ActiveAggroStateHandler;
import com.aionemu.gameserver.ai.state.handler.ActiveNpcStateHandler;
import com.aionemu.gameserver.ai.state.handler.AttackingStateHandler;
import com.aionemu.gameserver.ai.state.handler.MovingToHomeStateHandler;
import com.aionemu.gameserver.ai.state.handler.NoneCitizenStateHandler;
import com.aionemu.gameserver.ai.state.handler.NoneMonsterStateHandler;
import com.aionemu.gameserver.ai.state.handler.RestingStateHandler;
import com.aionemu.gameserver.ai.state.handler.StateHandler;
import com.aionemu.gameserver.ai.state.handler.TalkingStateHandler;
import com.aionemu.gameserver.ai.state.handler.ThinkingAggroStateHandler;
import com.aionemu.gameserver.ai.state.handler.ThinkingMonsterStateHandler;

/**
 * @author ATracer
 *
 */
public enum StateHandlers
{
	/**
	 * AIState.MOVINGTOHOME
	 */
	MOVINGTOHOME_SH(new MovingToHomeStateHandler()),
	/**
	 * AIState.NONE
	 */
	NONE_MONSTER_SH(new NoneMonsterStateHandler()),
	NONE_CITIZEN_SH(new NoneCitizenStateHandler()),
	/**
	 * AIState.ATTACKING
	 */
	ATTACKING_SH(new AttackingStateHandler()),
	/**
	 * AIState.THINKING
	 */
	THINKING_MONSTER_SH(new ThinkingMonsterStateHandler()),
	THINKING_AGGRO_SH(new ThinkingAggroStateHandler()),	
	/**
	 * AIState.ACTIVE
	 */
	ACTIVE_NPC_SH(new ActiveNpcStateHandler()),
	ACTIVE_AGGRO_SH(new ActiveAggroStateHandler()),	
	/**
	 * AIState.RESTING
	 */
	RESTING_SH(new RestingStateHandler()),
	/**
	 * AIState.TALKING
	 */
	TALKING_SH(new TalkingStateHandler());
	
	private StateHandler stateHandler;
	
	private StateHandlers(StateHandler stateHandler)
	{
		this.stateHandler = stateHandler;
	}
	
	public StateHandler getHandler()
	{
		return stateHandler;
	}
}
