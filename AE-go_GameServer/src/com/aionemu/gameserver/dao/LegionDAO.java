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

package com.aionemu.gameserver.dao;

import java.util.LinkedHashMap;

import com.aionemu.gameserver.model.legion.Legion;

/**
 * Class that is responsible for storing/loading legion data
 * 
 * @author Simple
 */

public abstract class LegionDAO implements IDFactoryAwareDAO
{

	/**
	 * Returns true if name is used, false in other case
	 * 
	 * @param name
	 *            name to check
	 * @return true if name is used, false in other case
	 */
	public abstract boolean isNameUsed(String name);

	/**
	 * Creates legion in DB
	 * 
	 * @param legion
	 */
	public abstract boolean saveNewLegion(Legion legion);

	/**
	 * Stores legion to DB
	 * 
	 * @param legion
	 */
	public abstract void storeLegion(Legion legion);

	/**
	 * This method is used to store only newly created characters
	 * 
	 * @param legionId
	 *            legionId from legion that has to be loaded
	 * @param world
	 * 
	 * @return legion
	 */
	public abstract Legion loadLegion(int legionId);

	/**
	 * Removes player and all related data (Done by CASCADE DELETION)
	 * 
	 * @param legionId
	 *            legion to delete
	 */
	public abstract void deleteLegion(int legionId);

	/**
	 * Returns the announcement list of a legion
	 * 
	 * @param legion
	 * @return announcementList
	 */
	public abstract LinkedHashMap<Integer, String> loadAnnouncementList(Legion legion);

	/**
	 * Creates legion in DB
	 * 
	 * @param legion
	 */
	public abstract boolean saveNewAnnouncement(int legionId, int unixTime, String message);

	/**
	 * Identifier name for all LegionDAO classes
	 * 
	 * @return LegionDAO.class.getName()
	 */
	@Override
	public final String getClassName()
	{
		return LegionDAO.class.getName();
	}
}
