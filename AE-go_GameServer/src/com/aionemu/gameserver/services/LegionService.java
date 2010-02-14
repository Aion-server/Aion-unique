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
package com.aionemu.gameserver.services;

import java.util.Map;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.LegionConfig;
import com.aionemu.gameserver.dao.LegionDAO;
import com.aionemu.gameserver.dao.LegionMemberDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.legion.Legion;
import com.aionemu.gameserver.model.legion.LegionMember;
import com.aionemu.gameserver.utils.collections.cachemap.CacheMap;
import com.aionemu.gameserver.utils.collections.cachemap.CacheMapFactory;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.utils.idfactory.IDFactoryAionObject;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * 
 * This class is designed to do all the work related with loading/storing legions and their members.<br>
 * 
 * @author Simple
 */
@SuppressWarnings("unused")
public class LegionService
{
	private CacheMap<Integer, Legion>		legionCache			= CacheMapFactory
																	.createSoftCacheMap("Legion", "legion");
	private CacheMap<Integer, LegionMember>	legionMembersCache	= CacheMapFactory.createSoftCacheMap("PlayerObjId",
																	"legionMember");

	private IDFactory						aionObjectsIDFactory;
	private World							world;

	@Inject
	public LegionService(@IDFactoryAionObject IDFactory aionObjectsIDFactory, World world)
	{
		this.aionObjectsIDFactory = aionObjectsIDFactory;
		this.world = world;
	}

	/**
	 * Checks if name is already taken or not
	 * 
	 * @param name
	 *            character name
	 * @return true if is free, false in other case
	 */
	public boolean isFreeName(String name)
	{
		return !DAOManager.getDAO(LegionDAO.class).isNameUsed(name);
	}

	/**
	 * Checks if a name is valid. It should contain only english letters
	 * 
	 * @param name
	 *            legion name
	 * @return true if name is valid, false overwise
	 */
	public boolean isValidName(String name)
	{
		return LegionConfig.LEGION_NAME_PATTERN.matcher(name).matches();
	}

	/**
	 * Checks if a self intro is valid. It should contain only english letters
	 * 
	 * @param name
	 *            character name
	 * @return true if name is valid, false overwise
	 */
	public boolean isValidSelfIntro(String name)
	{
		return LegionConfig.SELF_INTRO_PATTERN.matcher(name).matches();
	}

	/**
	 * Checks if a announcement is valid. It should contain only english letters
	 * 
	 * @param name
	 *            announcement
	 * @return true if name is valid, false overwise
	 */
	public boolean isValidAnnouncement(String name)
	{
		return LegionConfig.ANNOUNCEMENT_PATTERN.matcher(name).matches();
	}

	/**
	 * Stores newly created legion
	 * 
	 * @param legion
	 *            legion to store
	 * @return true if legion was successful saved.
	 */
	public boolean storeNewLegion(Legion legion)
	{
		legionCache.put(legion.getLegionId(), legion);
		return DAOManager.getDAO(LegionDAO.class).saveNewLegion(legion);
	}

	/**
	 * Stores newly created legion member
	 * 
	 * @param playerObjId
	 *            player object Id
	 * @param legionMember
	 *            legion member to store
	 * @return true if legion member was successful saved.
	 */
	public boolean storeNewLegionMember(int playerObjId, LegionMember legionMember)
	{
		legionMembersCache.put(playerObjId, legionMember);
		return DAOManager.getDAO(LegionMemberDAO.class).saveNewLegionMember(playerObjId, legionMember);
	}

	/**
	 * Stores newly created announcement
	 * 
	 * @param playerObjId
	 *            player object Id
	 * @param legionMember
	 *            legion member to store
	 * @return true if announcement was successful saved.
	 */
	public boolean storeNewAnnouncement(int legionId, int unixTime, String message)
	{
		return DAOManager.getDAO(LegionDAO.class).saveNewAnnouncement(legionId, unixTime, message);
	}

	/**
	 * Stores legion data into db
	 * 
	 * @param legion
	 */
	public void storeLegion(Legion legion)
	{
		DAOManager.getDAO(LegionDAO.class).storeLegion(legion);
	}

	/**
	 * Stores legion member data into db
	 * 
	 * @param legionMember
	 */
	public void storeLegionMember(int playerObjId, LegionMember legionMember)
	{
		DAOManager.getDAO(LegionMemberDAO.class).storeLegionMember(playerObjId, legionMember);
	}

	/**
	 * Returns the legion with given legionId (if such legion exists)
	 * 
	 * @param legionId
	 * @return
	 */
	public Legion getLegion(int legionId)
	{
		Legion legion = legionCache.get(legionId);
		if(legion != null)
			return legion;

		legion = DAOManager.getDAO(LegionDAO.class).loadLegion(legionId);

		Map<Integer, LegionMember> legionMembers = DAOManager.getDAO(LegionMemberDAO.class).loadLegionMembers(legionId);
		for(Integer key : legionMembers.keySet())
		{
			legion.addLegionMember(legionMembers.get(key).getPlayerObjId());
		}

		legion.setAnnouncementList(DAOManager.getDAO(LegionDAO.class).loadAnnouncementList(legion));

		legionCache.put(legionId, legion);

		return legion;
	}

	/**
	 * Returns the legion with given legionId (if such legion exists)
	 * 
	 * @param playerObjId
	 * @return
	 */
	public LegionMember getLegionMember(Player player)
	{
		LegionMember legionMember = legionMembersCache.get(player.getObjectId());
		if(legionMember != null)
			return legionMember;

		legionMember = DAOManager.getDAO(LegionMemberDAO.class).loadLegionMember(player, this);

		if(legionMember != null)
			legionMembersCache.put(player.getObjectId(), legionMember);

		return legionMember;
	}

	/**
	 * Completely removes legion member from database
	 * 
	 * @param playerObjId
	 *            Player Object Id
	 */
	public void deleteLegionMember(int playerObjId)
	{
		legionMembersCache.remove(playerObjId);
		DAOManager.getDAO(LegionMemberDAO.class).deleteLegionMember(playerObjId);
	}

	/**
	 * Completely removes legion from database
	 * 
	 * @param legionId
	 *            id of legion to delete from db
	 */
	void deleteLegionFromDB(int legionId)
	{
		DAOManager.getDAO(LegionDAO.class).deleteLegion(legionId);
	}

	/**
	 * Gets a player ONLY if he is in the cache
	 * 
	 * @return Player or null if not cached
	 */
	public Legion getCachedLegion(int legionId)
	{
		return legionCache.get(legionId);
	}

	/**
	 * @param legion
	 */
	public void renewAnnouncementList(Legion legion)
	{
		legion.setAnnouncementList(DAOManager.getDAO(LegionDAO.class).loadAnnouncementList(legion));
	}
}
