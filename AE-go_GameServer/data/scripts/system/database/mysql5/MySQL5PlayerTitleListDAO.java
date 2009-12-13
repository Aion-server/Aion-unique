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

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.PlayerTitleListDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.Title;
import com.aionemu.gameserver.model.gameobjects.player.TitleList;

/**
 * @author xavier
 *
 */
public class MySQL5PlayerTitleListDAO extends PlayerTitleListDAO
{
	private static final String LOAD_QUERY = "SELECT `title_id` FROM `player_titles` WHERE `player_id`=?";
	private static final String INSERT_QUERY = "INSERT INTO `player_titles`(`player_id`,`title_id`) VALUES (?,?)";
	private static final String CHECK_QUERY = "SELECT `title_id` FROM `player_titles` WHERE `player_id`=? AND `title_id`=?";

	@Override
	public TitleList loadTitleList(final int playerId)
	{
		final TitleList tl = new TitleList ();
		
		DB.select(LOAD_QUERY, new ParamReadStH()
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
					int id = rset.getInt("title_id");
					tl.addTitle(id);
				}
			}
		});
		
		return tl;
	}

	@Override
	public boolean storeTitles(Player player)
	{
		final int playerId = player.getObjectId();
		
		for (final Title t : player.getTitleList().getTitles())
		{
			DB.select(CHECK_QUERY, new ParamReadStH()
			{
				@Override
				public void setParams(PreparedStatement stmt) throws SQLException
				{
					stmt.setInt(1, playerId);
					stmt.setInt(2, t.getTemplate().getTitleId());
				}

				@Override
				public void handleRead(ResultSet rset) throws SQLException
				{
					if (!rset.next())
					{
						DB.insertUpdate(INSERT_QUERY, new IUStH() {
							@Override
							public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
							{
								stmt.setInt(1, playerId);
								stmt.setInt(2, t.getTemplate().getTitleId());
								stmt.execute();
							}
						});
					}
				}
			});

		}
		return true;
	}

	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion)
	{
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}

}
