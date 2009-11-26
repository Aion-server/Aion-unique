/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package mysql5;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.PlayerSettingsDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * @author ATracer
 *
 */
public class MySQL5PlayerSettingsDAO extends PlayerSettingsDAO
{

	private static final Logger log = Logger.getLogger(MySQL5PlayerSettingsDAO.class);


	@Override
	public void loadSettings(final Player player)
	{
		final int playerId = player.getObjectId();

		boolean success = DB.select("SELECT * FROM player_settings WHERE player_id = ?", new ParamReadStH() {

			@Override
			public void handleRead(ResultSet resultSet) throws SQLException
			{
				while(resultSet.next())
				{
					int type = resultSet.getInt("settings_type");
					if(type == 0)
					{
						player.setUiSettings(resultSet.getBytes("settings"));
					}
					else if(type == 1)
					{
						player.setShortcuts(resultSet.getBytes("settings"));
					}
				}
				if(resultSet.next())
				{
					
				}
			}

			@Override
			public void setParams(PreparedStatement preparedStatement) throws SQLException
			{
				preparedStatement.setInt(1, playerId);
			}
		});
		log.info("Loaded settings with success: " + success);
	}

	@Override
	public void saveSettings(final Player player)
	{
		final int playerId = player.getObjectId();
		final byte[] uiSettings = player.getUiSettings();
		final byte[] shortcuts = player.getShortcuts();
		
		log.info("Saving settings");
		
		if(uiSettings != null)
		{
			DB.insertUpdate("REPLACE INTO player_settings values (?, ?,  ?)", new IUStH() {
				@Override
				public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
				{
					stmt.setInt(1, playerId);
					stmt.setInt(2, 0);
					stmt.setBytes(3, uiSettings);
					stmt.execute();
				}
			});
		}
		
		if(shortcuts != null)
		{
			DB.insertUpdate("REPLACE INTO player_settings values (?, ?,  ?)", new IUStH() {
				@Override
				public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
				{
					stmt.setInt(1, playerId);
					stmt.setInt(2, 1);
					stmt.setBytes(3, shortcuts);
					stmt.execute();
				}
			});
		}	
	}

	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion)
	{
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}
}
