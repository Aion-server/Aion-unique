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

package mysql5;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javolution.util.FastMap;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.LegionMemberDAO;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.legion.LegionMember;
import com.aionemu.gameserver.model.legion.OfflineLegionMember;
import com.aionemu.gameserver.services.LegionService;

/**
 * Class that that is responsible for loading/storing {@link com.aionemu.gameserver.model.legions.LegionMember} object
 * from MySQL 5.
 * 
 * @author Simple
 */
public class MySQL5LegionMemberDAO extends LegionMemberDAO
{

	/** Logger */
	private static final Logger	log	= Logger.getLogger(MySQL5LegionMemberDAO.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isIdUsed(final int playerObjId)
	{
		PreparedStatement s = DB
			.prepareStatement("SELECT count(id) as cnt FROM legion_members WHERE ? = legion_members.player_id");
		try
		{
			s.setInt(1, playerObjId);
			ResultSet rs = s.executeQuery();
			rs.next();
			return rs.getInt("cnt") > 0;
		}
		catch(SQLException e)
		{
			log.error("Can't check if name " + playerObjId + ", is used, returning possitive result", e);
			return true;
		}
		finally
		{
			DB.close(s);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean saveNewLegionMember(final int playerObjId, final LegionMember legionMember)
	{
		boolean success = DB.insertUpdate("INSERT INTO legion_members(legion_id, `player_id`, `rank`) " + "VALUES (?, ?, ?)",
			new IUStH(){
				@Override
				public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException
				{
					log.debug("[DAO: MySQL5LegionDAO] saving new legion member: " + playerObjId + " "
						+ legionMember.getLegion().getLegionId());

					preparedStatement.setInt(1, legionMember.getLegion().getLegionId());
					preparedStatement.setInt(2, playerObjId);
					preparedStatement.setInt(3, legionMember.getRank());
					preparedStatement.execute();
				}
			});
		return success;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeLegionMember(final int playerId, final LegionMember legionMember)
	{
		DB.insertUpdate("UPDATE legion_members SET nickname=?, rank=?, selfintro=? WHERE player_id=?", new IUStH(){
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				log.debug("[DAO: MySQL5LegionDAO] storing legion member " + playerId);

				stmt.setString(1, legionMember.getNickname());
				stmt.setInt(2, legionMember.getRank());
				stmt.setString(3, legionMember.getSelfIntro());
				stmt.setInt(4, playerId);
				stmt.execute();
			}
		});
	}

	private Object	pcdLock	= new Object();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LegionMember loadLegionMember(final Player player, final LegionService legionService)
	{
		if(player == null)
			return null;

		final LegionMember legionMember = new LegionMember(player.getObjectId());

		log.debug(player.getObjectId() + " so this means player is not empty");

		boolean success = DB.select("SELECT * FROM legion_members WHERE player_id = ?", new ParamReadStH(){
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, player.getObjectId());
			}

			@Override
			public void handleRead(ResultSet resultSet)
			{
				try
				{
					resultSet.next();
					int legionId = resultSet.getInt("legion_id");
					legionMember.setRank(resultSet.getInt("rank"));
					legionMember.setNickname(resultSet.getString("nickname"));
					legionMember.setSelfIntro(resultSet.getString("selfintro"));

					legionMember.setLegion(legionService.getLegion(legionId));
				}
				catch(SQLException sqlE)
				{
					log.debug("[DAO: MySQL5LegionMemberDAO] Player is not in a Legion");
				}
			}
		});

		if(success && legionMember.getLegion() != null)
		{
			synchronized(pcdLock)
			{
				return legionMember;
			}
		}
		else
			return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OfflineLegionMember loadOfflineLegionMember(final int playerObjId, final LegionService legionService)
	{
		final OfflineLegionMember legionMember = new OfflineLegionMember(playerObjId);

		log.debug(playerObjId + " so this means player is not empty");

		boolean success = DB.select("SELECT players.name, players.exp, players.player_class, players.last_online, players.world_id, legion_members.* FROM players, legion_members WHERE id = ? AND players.id=legion_members.player_id", new ParamReadStH(){
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, playerObjId);
			}

			@Override
			public void handleRead(ResultSet resultSet)
			{
				try
				{
					resultSet.next();
					legionMember.setName(resultSet.getString("name"));
					legionMember.setExp(resultSet.getLong("exp"));
					legionMember.setPlayerClass(PlayerClass.valueOf(resultSet.getString("player_class")));
					legionMember.setLastOnline(resultSet.getTimestamp("last_online"));
					legionMember.setWorldId(resultSet.getInt("world_id"));
					
					int legionId = resultSet.getInt("legion_id");
					legionMember.setRank(resultSet.getInt("rank"));
					legionMember.setNickname(resultSet.getString("nickname"));
					legionMember.setSelfIntro(resultSet.getString("selfintro"));

					legionMember.setLegion(legionService.getLegion(legionId));
				}
				catch(SQLException sqlE)
				{
					log.debug("[DAO: MySQL5LegionMemberDAO] Player is not in a Legion");
				}
			}
		});

		if(success && legionMember.getLegion() != null)
		{
			synchronized(pcdLock)
			{
				return legionMember;
			}
		}
		else
			return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<Integer, LegionMember> loadLegionMembers(final int legionId)
	{
		final Map<Integer, LegionMember> legionMembers = new FastMap<Integer, LegionMember>();

		boolean success = DB.select("SELECT * FROM legion_members WHERE legion_id = ?", new ParamReadStH(){
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, legionId);
			}

			@Override
			public void handleRead(ResultSet resultSet)
			{
				try
				{
					while(resultSet.next())
					{
						int playerObjId = resultSet.getInt("player_id");
						LegionMember legionMember = new LegionMember(playerObjId);
						legionMembers.put(playerObjId, legionMember);
					}
				}
				catch(SQLException sqlE)
				{
					log.error("[DAO: MySQL5LegionMemberDAO] No players in Legion. DELETE Legion Id: " + legionId);
				}
			}
		});

		if(success && legionMembers.size() > 0)
		{
			synchronized(pcdLock)
			{
				return legionMembers;
			}
		}
		else
			return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(String s, int i, int i1)
	{
		return MySQL5DAOUtils.supports(s, i, i1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteLegionMember(int playerObjId)
	{
		PreparedStatement statement = DB.prepareStatement("DELETE FROM legion_members WHERE player_id = ?");
		try
		{
			statement.setInt(1, playerObjId);
		}
		catch(SQLException e)
		{
			log.error("Some crap, can't set int parameter to PreparedStatement", e);
		}
		DB.executeUpdateAndClose(statement);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int[] getUsedIDs()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
