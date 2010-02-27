/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package mysql5;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.PlayerSkillListDAO;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.SkillList;
import com.aionemu.gameserver.model.gameobjects.player.SkillListEntry;

/**
 * Created on: 15.07.2009 19:33:07
 * Edited On:  13.09.2009 19:48:00
 *
 * @author IceReaper, orfeo087, Avol, AEJTester
 */
public class MySQL5PlayerSkillListDAO extends PlayerSkillListDAO
{
	public static final String INSERT_QUERY = "INSERT INTO `player_skills` (`player_id`, `skillId`, `skillLevel`) VALUES (?,?,?)";
	public static final String UPDATE_QUERY = "UPDATE `player_skills` set skillLevel=? where player_id=? AND skillId=?";
	public static final String DELETE_QUERY = "DELETE FROM `player_skills` WHERE `player_id`=? AND skillId=?";
	public static final String SELECT_QUERY = "SELECT `skillId`, `skillLevel` FROM `player_skills` WHERE `player_id`=?";


	/** {@inheritDoc} */
	@Override
	public SkillList loadSkillList(final int playerId)
	{
		final Map<Integer, SkillListEntry> skills = new HashMap<Integer, SkillListEntry>();
		DB.select(SELECT_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, playerId);
			}

			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while(rset.next())
				{
					int id = rset.getInt("skillId");
					int lv = rset.getInt("skillLevel");

					skills.put(id, new SkillListEntry(id, lv, PersistentState.UPDATED));
				}
			}
		});
		return new SkillList(skills);
	}
	/**
	 *  Stores all player skills according to their persistence state
	 */
	@Override
	public boolean storeSkills(Player player)
	{
		SkillListEntry[] skillsActive = player.getSkillList().getAllSkills();
		SkillListEntry[] skillsDeleted = player.getSkillList().getDeletedSkills();
		store(player, skillsActive);
		store(player, skillsDeleted);
		
		return true;
	}
	/**
	 * 
	 * @param player
	 * @param skills
	 */
	private void store(Player player, SkillListEntry[] skills)
	{
		for(int i = 0; i < skills.length ; i++)
		{
			SkillListEntry skill = skills[i];
			switch(skill.getPersistentState())
			{
				case NEW:
					addSkill(player.getObjectId(), skill.getSkillId(), skill.getSkillLevel());
					break;
				case UPDATE_REQUIRED:
					updateSkill(player.getObjectId(), skill.getSkillId(), skill.getSkillLevel());
					break;
				case DELETED:
					deleteSkill(player.getObjectId(), skill.getSkillId());
					break;
			}
			skill.setPersistentState(PersistentState.UPDATED);
		}
	}
	
	/**
	 * Add a skill information into database
	 *
	 * @param playerId player object id
	 * @param skill    skill contents.
	 */
	private void addSkill(final int playerId, final int skillId, final int skillLevel)
	{
		DB.insertUpdate(INSERT_QUERY, new IUStH() {
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, playerId);
				stmt.setInt(2, skillId);
				stmt.setInt(3, skillLevel);
				stmt.execute();
			}
		});
	}
	
	/**
	 *  Updates skill in database (after level change)
	 *  
	 * @param playerId
	 * @param skillId
	 * @param skillLevel
	 */
	private void updateSkill(final int playerId, final int skillId, final int skillLevel)
	{
		DB.insertUpdate(UPDATE_QUERY, new IUStH() {
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, skillLevel);
				stmt.setInt(2, playerId);
				stmt.setInt(3, skillId);
				stmt.execute();
			}
		});
	}

	/**
	 *  Deletes skill from database
	 *  
	 * @param playerId
	 * @param skillId
	 */
	private void deleteSkill(final int playerId, final int skillId)
	{		
		DB.insertUpdate(DELETE_QUERY, new IUStH()
		{
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, playerId);
				stmt.setInt(2, skillId);
				stmt.execute();
			}
		});
	}

	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion)
	{
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}
}