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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.configs.main.CacheConfig;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dataholders.PlayerInitialData;
import com.aionemu.gameserver.dataholders.PlayerInitialData.LocationData;
import com.aionemu.gameserver.model.Gender;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.utils.collections.cachemap.CacheMap;
import com.aionemu.gameserver.utils.collections.cachemap.CacheMapFactory;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * Class that that is responsible for loading/storing {@link com.aionemu.gameserver.model.gameobjects.player.Player}
 * object from MySQL 5.
 * 
 * @author SoulKeeper, Saelya
 */
public class MySQL5PlayerDAO extends PlayerDAO
{

	/** Logger */
	private static final Logger					log					= Logger.getLogger(MySQL5PlayerDAO.class);

	/** Cache for {@link PlayerCommonData} objects */
	private CacheMap<Integer, PlayerCommonData>	playerCommonData	= CacheMapFactory.createCacheMap("PlayerCommon","player common");

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNameUsed(final String name)
	{
		PreparedStatement s = DB.prepareStatement("SELECT count(id) as cnt FROM players WHERE ? = players.name");
		try
		{
			s.setString(1, name);
			ResultSet rs = s.executeQuery();
			rs.next();
			return rs.getInt("cnt") > 0;
		}
		catch(SQLException e)
		{
			log.error("Can't check if name " + name + ", is used, returning possitive result", e);
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
	public void storePlayer(final Player player)
	{
		DB.insertUpdate("UPDATE players SET name=?, exp=?, recoverexp=?, x=?, y=?, z=?, heading=?, world_id=?, player_class=?, last_online=?, cube_size=?, warehouse_size=?, note=?, bind_point=?, title_id=? WHERE id=?", new IUStH(){
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				log.debug("[DAO: MySQL5PlayerDAO] storing player "+player.getObjectId()+" "+player.getName());

				stmt.setString(1, player.getName());
				stmt.setLong(2, player.getCommonData().getExp());
				stmt.setLong(3, player.getCommonData().getExpRecoverable());
				stmt.setFloat(4, player.getX());
				stmt.setFloat(5, player.getY());
				stmt.setFloat(6, player.getZ());
				stmt.setInt(7, player.getHeading());
				stmt.setInt(8, player.getWorldId());
				stmt.setString(9, player.getCommonData().getPlayerClass().toString());
				stmt.setTimestamp(10, player.getCommonData().getLastOnline());
				stmt.setInt(11, player.getCubeSize());
				stmt.setInt(12, player.getWarehouseSize());
				stmt.setString(13,player.getCommonData().getNote());
				stmt.setInt(14, player.getCommonData().getBindPoint());
				stmt.setInt(15, player.getCommonData().getTitleId());
				stmt.setInt(16, player.getObjectId());
				stmt.execute();
			}
		});
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean saveNewPlayer(final PlayerCommonData pcd, final int accountId, final String accountName)
	{
		boolean success = DB.insertUpdate(
			"INSERT INTO players(id, `name`, account_id, account_name, x, y, z, heading, world_id, gender, race, player_class , cube_size, warehouse_size, online) " +
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)",
			new IUStH(){
				@Override
				public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException
				{
					log.debug("[DAO: MySQL5PlayerDAO] saving new player: "+pcd.getPlayerObjId()+" "+pcd.getName());

					preparedStatement.setInt(1, pcd.getPlayerObjId());
					preparedStatement.setString(2, pcd.getName());
					preparedStatement.setInt(3, accountId);
					preparedStatement.setString(4, accountName);
					preparedStatement.setFloat(5, pcd.getPosition().getX());
					preparedStatement.setFloat(6, pcd.getPosition().getY());
					preparedStatement.setFloat(7, pcd.getPosition().getZ());
					preparedStatement.setInt(8, pcd.getPosition().getHeading());
					preparedStatement.setInt(9, pcd.getPosition().getMapId());
					preparedStatement.setString(10, pcd.getGender().toString());
					preparedStatement.setString(11, pcd.getRace().toString());
					preparedStatement.setString(12, pcd.getPlayerClass().toString());
					preparedStatement.setInt(13, pcd.getCubeSize());
					preparedStatement.setInt(14, pcd.getWarehouseSize());
					preparedStatement.execute();
				}
			});
		if(CacheConfig.CACHE_COMMONDATA && success)
		{
			playerCommonData.put(pcd.getPlayerObjId(), pcd);
		}
		return success;
	}

	private Object	pcdLock	= new Object();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlayerCommonData loadPlayerCommonData(final int playerObjId, final World world, final PlayerInitialData playerInitialData)
	{

		PlayerCommonData cached = playerCommonData.get(playerObjId);
		if(cached != null)
		{
			log.debug("[DAO: MySQL5PlayerDAO] PlayerCommonData for id: "+playerObjId+" obtained from cache");
			return cached;
		}
		final PlayerCommonData cd = new PlayerCommonData(playerObjId);

		boolean success = DB.select("SELECT * FROM players WHERE id = ?", new ParamReadStH(){
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, playerObjId);
			}

			@Override
			public void handleRead(ResultSet resultSet) throws SQLException
			{
				log.debug("[DAO: MySQL5PlayerDAO] loading from db "+playerObjId);

				resultSet.next();

				cd.setName(resultSet.getString("name"));
				//set player class before exp
				cd.setPlayerClass(PlayerClass.valueOf(resultSet.getString("player_class")));
				cd.setExp(resultSet.getLong("exp"));
				cd.setRecoverableExp(resultSet.getLong("recoverexp"));
				cd.setRace(Race.valueOf(resultSet.getString("race")));
				cd.setGender(Gender.valueOf(resultSet.getString("gender")));			;
				cd.setLastOnline(resultSet.getTimestamp("last_online"));
				cd.setNote(resultSet.getString("note"));
				cd.setCubesize(resultSet.getInt("cube_size"));
				cd.setBindPoint(resultSet.getInt("bind_point"));
				cd.setTitleId(resultSet.getInt("title_id"));
				cd.setWarehouseSize(resultSet.getInt("warehouse_size"));

				float x = resultSet.getFloat("x");
				float y = resultSet.getFloat("y");
				float z = resultSet.getFloat("z");
				byte heading = resultSet.getByte("heading");
				int worldId = resultSet.getInt("world_id");

				if(z < -1000)
				{
					//unstuck unlucky characters :)
					LocationData ld = playerInitialData.getSpawnLocation(cd.getRace());
					x = ld.getX();
					y = ld.getY();
					z = ld.getZ();
					heading = ld.getHeading();
					worldId = ld.getMapId();
				}		

				WorldPosition position = world.createPosition(worldId, x, y, z, heading);
				cd.setPosition(position);
			}
		});

		if(success)
		{
			synchronized(pcdLock)
			{
				if (CacheConfig.CACHE_COMMONDATA)
                {
					playerCommonData.put(playerObjId, cd);
                }
					return cd;

			}
		}
		else
			return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePlayer(int playerId)
	{
		PreparedStatement statement = DB.prepareStatement("DELETE FROM players WHERE id = ?");
		try
		{
			statement.setInt(1, playerId);
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
	public List<Integer> getPlayerOidsOnAccount(final int accountId)
	{
		final List<Integer> result = new ArrayList<Integer>();
		boolean success = DB.select("SELECT id FROM players WHERE account_id = ?", new ParamReadStH(){
			@Override
			public void handleRead(ResultSet resultSet) throws SQLException
			{
				while(resultSet.next())
				{
					result.add(resultSet.getInt("id"));
				}
			}

			@Override
			public void setParams(PreparedStatement preparedStatement) throws SQLException
			{
				preparedStatement.setInt(1, accountId);
			}
		});

		return success ? result : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCreationDeletionTime(final PlayerAccountData acData)
	{
		DB.select("SELECT creation_date, deletion_date FROM players WHERE id = ?", new ParamReadStH(){
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, acData.getPlayerCommonData().getPlayerObjId());
			}

			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				rset.next();

				acData.setDeletionDate(rset.getTimestamp("deletion_date"));
				acData.setCreationDate(rset.getTimestamp("creation_date"));
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateDeletionTime(final int objectId, final Timestamp deletionDate)
	{
		DB.insertUpdate("UPDATE players set deletion_date = ? where id = ?", new IUStH(){
			@Override
			public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException
			{
				preparedStatement.setTimestamp(1, deletionDate);
				preparedStatement.setInt(2, objectId);
				preparedStatement.execute();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeCreationTime(final int objectId, final Timestamp creationDate)
	{
		DB.insertUpdate("UPDATE players set creation_date = ? where id = ?", new IUStH(){
			@Override
			public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException
			{
				preparedStatement.setTimestamp(1, creationDate);
				preparedStatement.setInt(2, objectId);
				preparedStatement.execute();
			}
		});
	}

	@Override
	public void storeLastOnlineTime(final int objectId, final Timestamp lastOnline)
	{
		DB.insertUpdate("UPDATE players set last_online = ? where id = ?", new IUStH(){
			@Override
			public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException
			{
				preparedStatement.setTimestamp(1, lastOnline);
				preparedStatement.setInt(2, objectId);
				preparedStatement.execute();
			}
		});
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int[] getUsedIDs()
	{
		PreparedStatement statement = DB.prepareStatement("SELECT id FROM players", ResultSet.TYPE_SCROLL_INSENSITIVE,
			ResultSet.CONCUR_READ_ONLY);

		try
		{
			ResultSet rs = statement.executeQuery();
			rs.last();
			int count = rs.getRow();
			rs.beforeFirst();
			int[] ids = new int[count];
			for(int i = 0; i < count; i++)
			{
				rs.next();
				ids[i] = rs.getInt("id");
			}
			return ids;
		}
		catch(SQLException e)
		{
			log.error("Can't get list of id's from players table", e);
		}
		finally
		{
			DB.close(statement);
		}

		return new int[0];
	}

	/**
	 * {@inheritDoc} - Saelya
	 */
	@Override
	public void onlinePlayer(final Player player, final boolean online)
	{
		DB.insertUpdate("UPDATE players SET online=? WHERE id=?", new IUStH(){
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				log.debug("[DAO: MySQL5PlayerDAO] online status "+player.getObjectId()+" "+player.getName());

				stmt.setBoolean(1, online);
				stmt.setInt(2, player.getObjectId());
				stmt.execute();
			}
		});
	}

	/**
	 * {@inheritDoc} - Nemiroff
	 */
	@Override
	public void setPlayersOffline(final boolean online)
	{
		DB.insertUpdate("UPDATE players SET online=?", new IUStH(){
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setBoolean(1, online);
				stmt.execute();
			}
		});
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(String s, int i, int i1)
	{
		return MySQL5DAOUtils.supports(s, i, i1);
	}
}
