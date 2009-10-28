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
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.player.SkillList;

/**
 * Created on: 15.07.2009 19:33:07
 * Edited On:  13.09.2009 19:48:00
 *
 * @author IceReaper, orfeo087, Avol, AEJTester
 */
public class MySQL5PlayerSkillListDAO extends PlayerSkillListDAO
{
	public static final String INSERT_QUERY = "INSERT INTO `player_skills` (`player_id`, `skillId`, `skillLevel`) VALUES (?,?,?)";
	public static final String DELETE_QUERY = "DELETE FROM `player_skills` WHERE `player_id`=?";
	public static final String SELECT_QUERY = "SELECT `skillId`, `skillLevel` FROM `player_skills` WHERE `player_id`=?";
	public static final String SELECT_SKILL_TREE = "SELECT `skillId`, `skillLevel` FROM `skill_trees` WHERE `class_id`=? AND `min_level`<=?";

	/**
	 * Add a skill information into database
	 *
	 * @param playerId player object id
	 * @param skill    skill contents.
	 */
	@Override
	public void addSkills(final int playerId, final int skillId, final int skillLevel)
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
	public void addSkillsTree(Player player)
	{		
		final PlayerCommonData pcd = player.getCommonData();
		
		DB.select(SELECT_SKILL_TREE, new ParamReadStH()
		{
		@Override
		public void setParams(PreparedStatement stmt) throws SQLException
		{
			
			stmt.setInt(1, pcd.getPlayerClass().getClassId());
			stmt.setInt(2, pcd.getLevel());
		}

		@Override
		public void handleRead(ResultSet rset) throws SQLException
		{
			while(rset.next())
			{
				final int id = rset.getInt("skillId");
				final int lv = rset.getInt("skillLevel");
				
				DB.insertUpdate(INSERT_QUERY, new IUStH() {
					@Override
					public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
				{
						stmt.setInt(1, pcd.getPlayerObjId());
						stmt.setInt(2, id);
						stmt.setInt(3, lv);
						stmt.execute();
					}
				});
			}
		}
		});
	}
	/** {@inheritDoc} */
	@Override
	public void deleteSkills(final int playerId)
	{		
		DB.insertUpdate(DELETE_QUERY, new IUStH()
		{
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, playerId);
				stmt.execute();
			}
		});
	}

	/** {@inheritDoc} */
	@Override
	public SkillList restoreSkillList(final int playerId)
	{
		final Map<Integer, Integer> skills = new HashMap<Integer, Integer>();
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

					skills.put(id, lv);
				}
			}
		});
		return new SkillList(skills);
	}

	/** {@inheritDoc} */
	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion)
	{
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}
}