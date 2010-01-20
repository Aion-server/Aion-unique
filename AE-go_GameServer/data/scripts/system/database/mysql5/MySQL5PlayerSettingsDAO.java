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
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerSettings;

/**
 * @author ATracer
 *
 */
public class MySQL5PlayerSettingsDAO extends PlayerSettingsDAO
{

	private static final Logger log = Logger.getLogger(MySQL5PlayerSettingsDAO.class);

	/**
	 * TODO
	 * 1) analyze possibility to zip settings
	 * 2) insert/update instead of replace
	 * 
	 *  0 - uisettings
	 *  1 - shortcuts
	 *  2 - display
	 *  3 - deny
	 */

	@Override
	public void loadSettings(final Player player)
	{
		final int playerId = player.getObjectId();
		final PlayerSettings playerSettings = new PlayerSettings();

		boolean success = DB.select("SELECT * FROM player_settings WHERE player_id = ?", new ParamReadStH() {

			@Override
			public void handleRead(ResultSet resultSet) throws SQLException
			{
				while(resultSet.next())
				{
					int type = resultSet.getInt("settings_type");
					switch(type)
					{
						case 0:
							playerSettings.setUiSettings(resultSet.getBytes("settings"));
							break;
						case 1:
							playerSettings.setShortcuts(resultSet.getBytes("settings"));
							break;
						case 2:
							playerSettings.setDisplay(resultSet.getInt("settings"));
							break;
						case 3:
							playerSettings.setDeny(resultSet.getInt("settings"));
							break;
					}			
				}
			}

			@Override
			public void setParams(PreparedStatement preparedStatement) throws SQLException
			{
				preparedStatement.setInt(1, playerId);
			}
		});
		playerSettings.setPersistentState(PersistentState.UPDATED);
		player.setPlayerSettings(playerSettings);
		log.info("Loaded settings with success: " + success);
	}

	@Override
	public void saveSettings(final Player player)
	{
		final int playerId = player.getObjectId();
		
		PlayerSettings playerSettings = player.getPlayerSettings();
		if(playerSettings.getPersistentState() == PersistentState.UPDATED)
			return;
		
		final byte[] uiSettings = playerSettings.getUiSettings();
		final byte[] shortcuts = playerSettings.getShortcuts();
		final int display = playerSettings.getDisplay();
		final int deny = playerSettings.getDeny();

		log.info("Saving settings");

		if(uiSettings != null)
		{
			DB.insertUpdate("REPLACE INTO player_settings values (?, ?, ?)", new IUStH() {
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
			DB.insertUpdate("REPLACE INTO player_settings values (?, ?, ?)", new IUStH() {
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

		DB.insertUpdate("REPLACE INTO player_settings values (?, ?, ?)", new IUStH() {
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, playerId);
				stmt.setInt(2, 2);
				stmt.setInt(3, display);
				stmt.execute();
			}
		});

		DB.insertUpdate("REPLACE INTO player_settings values (?, ?, ?)", new IUStH() {
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, playerId);
				stmt.setInt(2, 3);
				stmt.setInt(3, deny);
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
