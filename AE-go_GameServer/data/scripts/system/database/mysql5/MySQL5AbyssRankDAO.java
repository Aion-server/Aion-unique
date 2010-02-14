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

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.AbyssRankDAO;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.AbyssRank;
import com.aionemu.gameserver.model.gameobjects.player.Player;


/**
 * @author ATracer
 *
 */
public class MySQL5AbyssRankDAO extends AbyssRankDAO
{

	public static final String SELECT_QUERY = "SELECT `ap`, `rank`, `all_kill`, `max_rank` FROM `abyss_rank` WHERE `player_id`=?";
	public static final String INSERT_QUERY = "INSERT INTO `abyss_rank` (`player_id`, `ap`, `rank`, `all_kill`, `max_rank`) VALUES(?,?,?,?,?)";
	public static final String UPDATE_QUERY = "UPDATE abyss_rank SET  ap=?, rank=?, all_kill=?, max_rank=? WHERE player_id=?";
	
	@Override
	public void loadAbyssRank(final Player player)
	{
		DB.select(SELECT_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, player.getObjectId());
			}

			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				if(rset.next())
				{
					int ap = rset.getInt("ap");
					int rank = rset.getInt("rank");
                    int all_kill = rset.getInt("all_kill");
                    int max_rank = rset.getInt("max_rank");
					AbyssRank abyssRank = new AbyssRank(ap, rank, all_kill, max_rank);
					abyssRank.setPersistentState(PersistentState.UPDATED);
					player.setAbyssRank(abyssRank);
				}
				else
				{
					AbyssRank abyssRank = new AbyssRank(0, 1, 0, 1);
					abyssRank.setPersistentState(PersistentState.NEW);
					player.setAbyssRank(abyssRank);
				}
			}
		});
	}

	@Override
	public boolean storeAbyssRank(Player player)
	{
		AbyssRank rank = player.getAbyssRank();
		switch(rank.getPersistentState())
		{
			case NEW:
				 return addRank(player.getObjectId(), rank);
			case UPDATE_REQUIRED:
				return updateRank(player.getObjectId(), rank);
		}
		return false;
	}

	/**
	 * @param objectId
	 * @param rank
	 * @return
	 */
	private boolean addRank(final int objectId, final AbyssRank rank)
	{
		return DB.insertUpdate(INSERT_QUERY, new IUStH() {
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, objectId);
				stmt.setInt(2, rank.getAp());
				stmt.setInt(3, rank.getRank().getId());
				stmt.setInt(4, rank.getAllKill());
                stmt.setInt(5, rank.getMaxRank());
                stmt.execute();
			}
		});
	}

	/**
	 * @param objectId
	 * @param rank
	 * @return
	 */
	private boolean updateRank(final int objectId, final AbyssRank rank)
	{
		return DB.insertUpdate(UPDATE_QUERY, new IUStH() {
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, rank.getAp());
				stmt.setInt(2, rank.getRank().getId());
                stmt.setInt(3, rank.getAllKill());
                stmt.setInt(4, rank.getMaxRank());
				stmt.setInt(5, objectId);
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
