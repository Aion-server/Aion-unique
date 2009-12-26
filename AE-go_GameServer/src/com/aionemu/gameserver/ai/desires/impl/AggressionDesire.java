/* This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.ai.desires.impl;

import com.aionemu.gameserver.ai.AI;
import com.aionemu.gameserver.ai.AIState;
import com.aionemu.gameserver.ai.desires.AbstractDesire;
import com.aionemu.gameserver.model.gameobjects.Monster;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * @author KKnD
 */
public final class AggressionDesire extends AbstractDesire
{
	protected  Monster	_npc;
	protected  Creature	_pc;
	
	public AggressionDesire(Monster npc, int desirePower)
	{
		super(desirePower);
		this._npc = npc;
	}
	
	@Override
	public void handleDesire(AI<?> ai)
	{
		if (_npc == null) return;
		
		for(VisibleObject pcs : _npc.getKnownList())
		{
			if (pcs == null)
				continue;
			
			if (pcs instanceof Player)
			{
				Player pc = (Player)pcs;
				if (!pc.getLifeStats().isAlreadyDead() && MathUtil.isInRange(_npc, pc, _npc.getAggroRange()) && (Math.abs(pc.getZ() - _npc.getZ()) < 30))
				{
					_npc.getAi().setAiState(AIState.NONE);
					_npc.getAi().hanndleAggroTask(pc);
					break;
				}
			}
		}
	}
}
