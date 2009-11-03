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
package com.aionemu.gameserver.model.gameobjects.stats;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.stats.NpcStatsTemplate;

/**
 * @author xavier
 *
 */
public class NpcGameStats extends CreatureGameStats<Npc>
{
	public NpcGameStats(Npc owner, NpcStatsTemplate nst) {
		super(owner,0,0,0,0,0,0,0,0,0,0,Math.round(nst.getAttackSpeed()*1000),0);
		// TODO set other stats
		this.physicalDefense = Math.round(nst.getPdef());
		this.magicResistance = Math.round(nst.getMdef());
		this.mainHandAttack = nst.getPower();
	}
}
