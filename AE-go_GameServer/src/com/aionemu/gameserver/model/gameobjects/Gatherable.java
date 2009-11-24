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

import com.aionemu.gameserver.controllers.GatherableController;
import com.aionemu.gameserver.controllers.VisibleObjectController;
import com.aionemu.gameserver.model.templates.GatherableTemplate;
import com.aionemu.gameserver.model.templates.SpawnTemplate;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * @author ATracer
 *
 */
public class Gatherable extends VisibleObject
{
	private GatherableTemplate gatherableTemplate;
	
	public Gatherable(SpawnTemplate spawnTemplate, int objId, GatherableController controller)
	{
		super(objId, controller, spawnTemplate, new WorldPosition());
		controller.setOwner(this);
		this.gatherableTemplate = (GatherableTemplate) spawnTemplate.getObjectTemplate();
	}

	@Override
	public String getName()
	{
		return gatherableTemplate.getName();
	}

	/**
	 * @return the gatherableTemplate
	 */
	public GatherableTemplate getTemplate()
	{
		return gatherableTemplate;
	}

	@Override
	public GatherableController getController()
	{
		return (GatherableController) super.getController();
	}
}