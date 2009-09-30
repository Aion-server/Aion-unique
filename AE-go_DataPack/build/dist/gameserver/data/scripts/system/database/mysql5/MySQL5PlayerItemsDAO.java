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
import org.apache.log4j.Logger;
import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.PlayerItemsDAO;
import com.aionemu.gameserver.model.gameobjects.player.PlayerItems;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;

/**
 * 
 *  @author AEJTester
 */
public class MySQL5PlayerItemsDAO extends PlayerItemsDAO
{
	private static final Logger	log= Logger.getLogger(MySQL5PlayerDAO.class);

	public static final String INSERT_QUERY = "REPLACE INTO `player_equipment` ("
		+ "`player_id`, `warmer`, `shield`, `helmet`, `armor`, `gloves`, `boots`, `learrings`, `rearrings`,"
		+ "`lring`, `rring`, `necklace`, `pauldron`, `pants`, `lshard`, `rshard`, `wing` )" + " VALUES "
		+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?" + ")";
	
	public static final String SELECT_QUERY = "SELECT * FROM `player_equipment` WHERE `player_id` = ?";

	/**
	 * Add a item information into database
	 *
	 * @param playerId player object id
	 * @param item    item contents.
	 */
	public void addStartingItems(final PlayerCommonData player,final PlayerItems pi)
	{		
		DB.insertUpdate(INSERT_QUERY, new IUStH() {
		@Override
		public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
		{
			stmt.setInt(1, player.getPlayerObjId());
			stmt.setInt(2, pi.getWarmer());
			stmt.setInt(3, pi.getShield());
			stmt.setInt(4, pi.getHelmet());
			stmt.setInt(5, pi.getArmor());
			stmt.setInt(6, pi.getGloves());
			stmt.setInt(7, pi.getBoots());
			stmt.setInt(8, pi.getLearrings());
			stmt.setInt(9, pi.getRearrings());
			stmt.setInt(10, pi.getLring());
			stmt.setInt(11, pi.getRring());
			stmt.setInt(12, pi.getNecklace());
			stmt.setInt(13, pi.getPauldron());
			stmt.setInt(14, pi.getPants());
			stmt.setInt(15, pi.getLshard());
			stmt.setInt(16, pi.getRshard());
			stmt.setInt(17, pi.getWing());
			stmt.execute();
					}
				});
	}
	/** {@inheritDoc} */
	@Override
	public PlayerItems loadItems(final int playerId)
	{
		final PlayerItems pi = new PlayerItems();
		
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
					pi.setWarmer(rset.getInt("warmer"));
					pi.setShield(rset.getInt("shield"));
					pi.setHelmet(rset.getInt("helmet"));
					pi.setArmor(rset.getInt("armor"));
					pi.setGloves(rset.getInt("gloves"));
					pi.setBoots(rset.getInt("boots"));
					pi.setLearrings(rset.getInt("learrings"));
					pi.setRearrings(rset.getInt("rearrings"));
					pi.setLring(rset.getInt("lring"));
					pi.setRring(rset.getInt("rring"));
					pi.setNecklace(rset.getInt("necklace"));
					pi.setPauldron(rset.getInt("pauldron"));
					pi.setPants(rset.getInt("pants"));
					pi.setLshard(rset.getInt("lshard"));
					pi.setRshard(rset.getInt("rshard"));
					pi.setWing(rset.getInt("wing"));
				}
			}
		});
		return pi;
	}
	/** {@inheritDoc} */
	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion)
	{
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}
}
