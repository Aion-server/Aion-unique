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
package com.aionemu.gameserver.controllers.factory;

import com.aionemu.gameserver.controllers.RiftController;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.google.inject.assistedinject.Assisted;

/**
 * @author ATracer
 * 
 */
public interface RiftControllerFactory
{
	/**
	 * 
	 * @param slave
	 * @param maxEntries
	 * @param maxLevel
	 * @return riftControllerFactory
	 */
	public RiftController create(@Assisted Npc slave, @Assisted("maxEntries") Integer maxEntries,
		@Assisted("maxLevel") Integer maxLevel);
	
}
