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
package com.aionemu.gameserver.model.gameobjects.stats;

import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * @author ATracer
 *
 */
public class NpcLifeStats extends CreatureLifeStats<Npc>
{

	public NpcLifeStats(Npc owner)
	{
		super(owner,0,0);
		this.currentHp = owner.getGameStats().getCurrentStat(StatEnum.MAXHP);
		this.currentMp = owner.getGameStats().getCurrentStat(StatEnum.MAXMP);	
	}


	@Override
	protected void onIncreaseHp()
	{
		sendAttackStatusPacketUpdate();
	}

	@Override
	protected void onIncreaseMp()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onReduceHp()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onReduceMp()
	{
		// TODO Auto-generated method stub
		
	}

}
