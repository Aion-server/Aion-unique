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
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.LegionDAO;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.StorageType;
import com.aionemu.gameserver.model.legion.Legion;
import com.aionemu.gameserver.model.legion.LegionEmblem;
import com.aionemu.gameserver.model.legion.LegionWarehouse;

/**
 * Class that that is responsible for loading/storing {@link com.aionemu.gameserver.model.legions.legion} object from
 * MySQL 5.
 * 
 * @author Simple
 */
public class MySQL5LegionDAO extends LegionDAO
{
	/** Logger */
	private static final Logger	log								= Logger.getLogger(MySQL5LegionDAO.class);

	/** Legion Queries */
	private static final String	INSERT_LEGION_QUERY				= "INSERT INTO legions(id, `name`) VALUES (?, ?)";
	private static final String	SELECT_LEGION_QUERY				= "SELECT * FROM legions WHERE id=?";
	private static final String	DELETE_LEGION_QUERY				= "DELETE FROM legions WHERE id = ?";
	private static final String	UPDATE_LEGION_QUERY				= "UPDATE legions SET name=?, level=?, legionar_permission2=?, centurion_permission1=?, centurion_permission2=?, disband_time=? WHERE id=?";

	/** Announcement Queries **/
	private static final String	INSERT_ANNOUNCEMENT_QUERY		= "INSERT INTO legion_announcement_list(`legion_id`, `announcement`) VALUES (?, ?)";
	private static final String	SELECT_ANNOUNCEMENTLIST_QUERY	= "SELECT * FROM legion_announcement_list WHERE legion_id=? ORDER BY date DESC";

	/** Emblem Queries **/
	private static final String INSERT_EMBLEM_QUERY				= "INSERT INTO legion_emblems(legion_id) VALUES (?)";
	private static final String	UPDATE_EMBLEM_QUERY				= "UPDATE legion_emblems SET emblem_id=?, color_r=?, color_g=?, color_b=? WHERE legion_id=?";
	private static final String	SELECT_EMBLEM_QUERY				= "SELECT * FROM legion_emblems WHERE legion_id=?";

	/** Storage Queries **/
	private static final String	SELECT_STORAGE_QUERY			= "SELECT `itemUniqueId`, `itemId`, `itemCount`, `itemColor`, `isEquiped`, `slot` FROM `inventory` WHERE `itemOwner`=? AND `itemLocation`=? AND `isEquiped`=?";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNameUsed(final String name)
	{
		PreparedStatement s = DB.prepareStatement("SELECT count(id) as cnt FROM legions WHERE ? = legions.name");
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
	public boolean saveNewLegion(final Legion legion)
	{
		boolean success = DB.insertUpdate(INSERT_LEGION_QUERY, new IUStH(){
			@Override
			public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException
			{
				log.debug("[DAO: MySQL5LegionDAO] saving new legion: " + legion.getLegionId() + " "
					+ legion.getLegionName());

				preparedStatement.setInt(1, legion.getLegionId());
				preparedStatement.setString(2, legion.getLegionName());
				preparedStatement.execute();
			}
		});
		return success;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeLegion(final Legion legion)
	{
		DB.insertUpdate(UPDATE_LEGION_QUERY, new IUStH(){
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				log.debug("[DAO: MySQL5LegionDAO] storing player " + legion.getLegionId() + " "
					+ legion.getLegionName());

				stmt.setString(1, legion.getLegionName());
				stmt.setInt(2, legion.getLegionLevel());
				stmt.setInt(3, legion.getLegionarPermission2());
				stmt.setInt(4, legion.getCenturionPermission1());
				stmt.setInt(5, legion.getCenturionPermission2());
				stmt.setInt(6, legion.getDisbandTime());
				stmt.setInt(7, legion.getLegionId());
				stmt.execute();
			}
		});
	}

	// private Object pcdLock = new Object();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Legion loadLegion(final int legionId)
	{
		final Legion legion = new Legion(legionId);

		boolean success = DB.select(SELECT_LEGION_QUERY, new ParamReadStH(){
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, legionId);
			}

			@Override
			public void handleRead(ResultSet resultSet) throws SQLException
			{
				while(resultSet.next())
				{
					legion.setLegionName(resultSet.getString("name"));
					legion.setLegionLevel(resultSet.getInt("level"));

					legion.setLegionarPermissions(resultSet.getInt("legionar_permission2"), resultSet
						.getInt("centurion_permission1"), resultSet.getInt("centurion_permission2"));

					legion.setDisbandTime(resultSet.getInt("disband_time"));
				}
			}
		});

		log.debug("[MySQL5LegionDAO] Loaded " + legion.getLegionId() + " legion.");

		return success ? legion : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteLegion(int legionId)
	{
		PreparedStatement statement = DB.prepareStatement(DELETE_LEGION_QUERY);
		try
		{
			statement.setInt(1, legionId);
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
		PreparedStatement statement = DB.prepareStatement("SELECT id FROM legions", ResultSet.TYPE_SCROLL_INSENSITIVE,
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
			log.error("Can't get list of id's from legions table", e);
		}
		finally
		{
			DB.close(statement);
		}

		return new int[0];
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
	public LinkedHashMap<Timestamp, String> loadAnnouncementList(final int legionId)
	{
		final LinkedHashMap<Timestamp, String> announcementList = new LinkedHashMap<Timestamp, String>();

		boolean success = DB.select(SELECT_ANNOUNCEMENTLIST_QUERY, new ParamReadStH(){
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, legionId);
			}

			@Override
			public void handleRead(ResultSet resultSet) throws SQLException
			{
				while(resultSet.next())
				{
					String message = resultSet.getString("announcement");
					Timestamp date = resultSet.getTimestamp("date");

					announcementList.put(date, message);
				}
			}
		});

		log.debug("[MySQL5LegionDAO] Loaded announcementList " + legionId + " legion.");

		return success ? announcementList : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean saveNewAnnouncement(final int legionId, final String message)
	{
		boolean success = DB.insertUpdate(INSERT_ANNOUNCEMENT_QUERY, new IUStH(){
			@Override
			public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException
			{
				log.debug("[DAO: MySQL5LegionDAO] saving new announcement.");

				preparedStatement.setInt(1, legionId);
				preparedStatement.setString(2, message);
				preparedStatement.execute();
			}
		});
		return success;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeLegionEmblem(final int legionId, final int emblemId, final int red, final int green, final int blue)
	{
		DB.insertUpdate(UPDATE_EMBLEM_QUERY, new IUStH(){
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				log.debug("[DAO: MySQL5LegionDAO] storing emblem for legion id: " + legionId);

				stmt.setInt(1, emblemId);
				stmt.setInt(2, red);
				stmt.setInt(3, green);
				stmt.setInt(4, blue);
				stmt.setInt(5, legionId);
				stmt.execute();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean saveNewLegionEmblem(final int legionId)
	{
		boolean success = DB.insertUpdate(INSERT_EMBLEM_QUERY, new IUStH(){
			@Override
			public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException
			{
				log.debug("[DAO: MySQL5LegionDAO] saving new legion emblem: " + legionId);

				preparedStatement.setInt(1, legionId);
				preparedStatement.execute();
			}
		});
		return success;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LegionEmblem loadLegionEmblem(final int legionId)
	{
		final LegionEmblem legionEmblem = new LegionEmblem();

		boolean success = DB.select(SELECT_EMBLEM_QUERY, new ParamReadStH(){
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, legionId);
			}

			@Override
			public void handleRead(ResultSet resultSet) throws SQLException
			{
				while(resultSet.next())
				{
					legionEmblem.setEmblem(resultSet.getInt("emblem_id"), resultSet.getInt("color_r"), resultSet
						.getInt("color_g"), resultSet.getInt("color_b"));
				}
			}
		});

		log.debug("[MySQL5LegionDAO] Loaded " + legionId + " legion emblem.");

		return (success && !legionEmblem.isDefaultEmblem()) ? legionEmblem : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LegionWarehouse loadLegionStorage(Legion legion)
	{
		final LegionWarehouse inventory = new LegionWarehouse(legion);
		final int legionId = legion.getLegionId();
		final int storage = StorageType.LEGION_WAREHOUSE.getId();
		final int equipped = 0;

		DB.select(SELECT_STORAGE_QUERY, new ParamReadStH(){
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, legionId);
				stmt.setInt(2, storage);
				stmt.setInt(3, equipped);
			}

			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while(rset.next())
				{
					int itemUniqueId = rset.getInt("itemUniqueId");
					int itemId = rset.getInt("itemId");
					int itemCount = rset.getInt("itemCount");
					int itemColor = rset.getInt("itemColor");
					int isEquiped = rset.getInt("isEquiped");
					int slot = rset.getInt("slot");
					Item item = new Item(itemUniqueId, itemId, itemCount, itemColor, isEquiped == 1, slot, storage);
					item.setPersistentState(PersistentState.UPDATED);
					inventory.onLoadHandler(item);
				}
			}
		});
		return inventory;
	}
}
