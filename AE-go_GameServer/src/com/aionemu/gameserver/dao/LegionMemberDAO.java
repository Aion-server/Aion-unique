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

import java.util.Map;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.legion.LegionMember;
import com.aionemu.gameserver.model.legion.OfflineLegionMember;
import com.aionemu.gameserver.services.LegionService;

/**
 * Class that is responsible for storing/loading legion data
 * 
 * @author Simple
 */

public abstract class LegionMemberDAO implements IDFactoryAwareDAO
{

	/**
	 * Returns true if name is used, false in other case
	 * 
	 * @param name
	 *            name to check
	 * @return true if name is used, false in other case
	 */
	public abstract boolean isIdUsed(int playerObjId);

	/**
	 * Creates legion member in DB
	 * 
	 * @param playerObjId
	 * @param legionMember
	 */
	public abstract boolean saveNewLegionMember(int playerObjId, LegionMember legionMember);

	/**
	 * Stores legion member to DB
	 * 
	 * @param player
	 */
	public abstract void storeLegionMember(int playerObjId, LegionMember legionMember);

	/**
	 * This method is used to store only newly created characters
	 * 
	 * @param world
	 * 
	 * @param pcd
	 *            player to save in database
	 * @return true if every things went ok.
	 */
	public abstract LegionMember loadLegionMember(Player player, LegionService legionService);

	/**
	 * @param playerObjId
	 * @param legionService
	 * @return
	 */
	public abstract OfflineLegionMember loadOfflineLegionMember(int playerObjId, LegionService legionService);

	/**
	 * This method is used to store only newly created characters
	 * 
	 * @param world
	 * 
	 * @param pcd
	 *            player to save in database
	 * @return a list of all legion members
	 */
	public abstract Map<Integer, LegionMember> loadLegionMembers(int legionId);

	/**
	 * Removes legion member and all related data (Done by CASCADE DELETION)
	 * 
	 * @param playerId
	 *            legion member to delete
	 */
	public abstract void deleteLegionMember(int playerObjId);

	/**
	 * Identifier name for all LegionDAO classes
	 * 
	 * @return LegionDAO.class.getName()
	 */
	@Override
	public final String getClassName()
	{
		return LegionMemberDAO.class.getName();
	}

}
