/**
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
package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.model.gameobjects.stats.MonsterLifeStats;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerLifeStats;
import com.aionemu.gameserver.model.templates.SpawnTemplate;

public class Monster extends Npc
{

	private MonsterLifeStats lifeStats;
	
	/**
	 * @param template
	 */
	public Monster(SpawnTemplate spawn, int objId, NpcController controller)
	{
		super(spawn, objId, controller);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the lifeStats
	 */
	@Override
	public MonsterLifeStats getLifeStats()
	{
		return lifeStats;
	}

	/**
	 * @param lifeStats the lifeStats to set
	 */
	public void setLifeStats(MonsterLifeStats lifeStats)
	{
		this.lifeStats = lifeStats;
	}

}
