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

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import com.aionemu.gameserver.model.legion.Legion;
import com.aionemu.gameserver.model.legion.LegionEmblem;

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
	 * Loads a legion
	 * 
	 * @param legionId
	 * @return
	 */
	public abstract Legion loadLegion(int legionId);

	/**
	 * Removes legion and all related data (Done by CASCADE DELETION)
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
	public abstract LinkedHashMap<Timestamp, String> loadAnnouncementList(int legionId);

	/**
	 * Creates announcement in DB
	 * 
	 * @param legionId
	 * @param message
	 * @return
	 */
	public abstract boolean saveNewAnnouncement(int legionId, String message);

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

	/**
	 * Stores a legion emblem in the database
	 * 
	 * @param legionId
	 * @param emblemId
	 * @param red
	 * @param green
	 * @param blue
	 */
	public abstract void storeLegionEmblem(int legionId, int emblemId, int red, int green, int blue);

	/**
	 * Saves a new legion emblem
	 * 
	 * @param legionId
	 */
	public abstract boolean saveNewLegionEmblem(int legionId);

	/**
	 * Loads a legion emblem
	 * 
	 * @param legion
	 * @return
	 */
	public abstract LegionEmblem loadLegionEmblem(int legionId);
}
