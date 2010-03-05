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
package com.aionemu.gameserver.ai.desires.impl;

import java.util.Collections;

import com.aionemu.gameserver.ai.AI;
import com.aionemu.gameserver.ai.desires.AbstractDesire;
import com.aionemu.gameserver.ai.events.Event;
import com.aionemu.gameserver.ai.state.AIState;
import com.aionemu.gameserver.controllers.attack.AttackResult;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author KKnD
 */
public final class AggressionDesire extends AbstractDesire
{
	protected  Npc	npc;
	
	public AggressionDesire(Npc npc, int desirePower)
	{
		super(desirePower);
		this.npc = npc;
	}
	
	@Override
	public boolean handleDesire(AI<?> ai)
	{
		if (npc == null) return false;
		
		for(VisibleObject visibleObject : npc.getKnownList())
		{
			if (visibleObject == null)
				continue;
			
			if (visibleObject instanceof Player)
			{
				final Player player = (Player) visibleObject;
				
				if(!player.getLifeStats().isAlreadyDead() && MathUtil.isInRange(npc, player, npc.getAggroRange())
					&& (Math.abs(player.getZ() - npc.getZ()) < 30))
				{

					if(!npc.isAggressiveTo(player.getCommonData().getRace()))
						continue;

					npc.getAi().setAiState(AIState.NONE);// TODO
					// ToDO proper aggro emotion on aggro range enter
					PacketSendUtility.broadcastPacket(npc, new SM_ATTACK(npc, player, 0,
						633, 0, Collections.singletonList(new AttackResult(0, AttackStatus.NORMALHIT))));

					ThreadPoolManager.getInstance().schedule(new Runnable(){
						@Override
						public void run()
						{
							npc.getAggroList().addDamageHate(player, 0, 0);
							npc.getAi().handleEvent(Event.ATTACKED);
						}
					}, 1000);
					break;
				}
			}
		}
		return true;
	}

	@Override
	public int getExecutionInterval()
	{
		return 2;
	}

	@Override
	public void onClear()
	{
		// TODO Auto-generated method stub
		
	}

}
