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
package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.ai.npcai.CitizenAi;
import com.aionemu.gameserver.ai.npcai.GuardAi;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.controllers.CitizenController;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.spawn.SpawnTemplate;

/**
 * @author ATracer
 */
public class Citizen extends Npc
{

	/**
	 * 
	 * @param objId
	 * @param controller
	 * @param spawn
	 */
	public Citizen(int objId, CitizenController controller, SpawnTemplate spawn, VisibleObjectTemplate objectTemplate)
	{
		super(objId, controller, spawn, objectTemplate);
	}

	@Override
	public CitizenController getController()
	{
		return (CitizenController) super.getController();
	}
	
	@Override
	public CitizenAi getAi()
	{
		return (CitizenAi) super.getAi();
	}

	@Override
	public void initializeAi()
	{
		if(isAggressive() && !CustomConfig.DISABLE_MOB_AGGRO)
			this.ai = new GuardAi();
		else
			this.ai = new CitizenAi();
		ai.setOwner(this);
	}
}
