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
import com.aionemu.gameserver.dao.GameTimeDAO;

/**
 * @author Ben
 * 
 */
public class MySQL5GameTimeDAO extends GameTimeDAO
{
	private static Logger log = Logger.getLogger(MySQL5GameTimeDAO.class);
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int load()
	{
		PreparedStatement ps = DB.prepareStatement("SELECT `value` FROM `server_variables` WHERE `key`='time'");
		try
		{
			ResultSet rs = ps.executeQuery();
			if(rs.next())
				return Integer.parseInt(rs.getString("value"));
		}
		catch(SQLException e)
		{
			log.error("Error loading last saved server time", e);
		}
		finally
		{
			DB.close(ps);
		}

		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean store(int time)
	{
		boolean success = false;
		PreparedStatement ps = DB.prepareStatement("REPLACE INTO `server_variables` (`key`,`value`) VALUES (?,?)");
		try
		{
			ps.setString(1, "time");
			ps.setString(2, String.valueOf(time));
			success = ps.executeUpdate() > 0;
		}
		catch(SQLException e)
		{
			log.error("Error storing server time", e);
		}
		finally
		{
			DB.close(ps);
		}

		return success;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion)
	{
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}
}
