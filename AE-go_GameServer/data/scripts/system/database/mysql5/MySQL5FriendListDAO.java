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
import java.util.ArrayList;
import java.util.List;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.FriendListDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dataholders.PlayerInitialData;
import com.aionemu.gameserver.model.gameobjects.player.Friend;
import com.aionemu.gameserver.model.gameobjects.player.FriendList;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.world.World;

/**
 * @author Ben
 *
 */
public class MySQL5FriendListDAO extends FriendListDAO
{
	
	public static final String LOAD_QUERY = "SELECT * FROM `friends` WHERE `player`=?";
	
	public static final String ADD_QUERY = "INSERT INTO `friends` (`player`,`friend`) VALUES (?, ?)";
	
	public static final String DEL_QUERY = "DELETE FROM friends WHERE player = ? AND friend = ?";

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.dao.FriendListDAO#load(com.aionemu.gameserver.model.gameobjects.player.Player)
	 */
	@Override
	public FriendList load(final Player player, final World world, final PlayerInitialData playerInitialData)
	{
		final List<Friend> friends = new ArrayList<Friend>();
		
		DB.select(LOAD_QUERY, new ParamReadStH()
		{
			
			@Override
			public void handleRead(ResultSet rs) throws SQLException
			{
				PlayerDAO dao = DAOManager.getDAO(PlayerDAO.class);
				while (rs.next()) 
				{
					int objId = rs.getInt("friend");
					
					PlayerCommonData pcd = dao.loadPlayerCommonData(objId, world, playerInitialData);
					Friend friend = new Friend(pcd);
					friends.add(friend);
				}
				
			}
			
			@Override
			public void setParams(PreparedStatement ps) throws SQLException
			{
				ps.setInt(1, player.getObjectId());
				
			}
		});
		
		return new FriendList(player,friends);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addFriends(final Player player, final Player friend)
	{
		
		return DB.insertUpdate(ADD_QUERY, new IUStH()
		{
			
			@Override
			public void handleInsertUpdate(PreparedStatement ps) throws SQLException
			{
				ps.setInt(1, player.getObjectId());
				ps.setInt(2, friend.getObjectId());
				ps.addBatch();
				
				ps.setInt(1, friend.getObjectId());
				ps.setInt(2, player.getObjectId());
				ps.addBatch();
				
				ps.executeBatch();
			}
		});
		
	}
	
	@Override
	public boolean delFriends(final int playerOid, final int friendOid) 
	{
		
		return DB.insertUpdate(DEL_QUERY, new IUStH() 
		{
				
			@Override
			public void handleInsertUpdate(PreparedStatement ps) throws SQLException
			{
				ps.setInt(1, playerOid);
				ps.setInt(2, friendOid);
				ps.addBatch();
				
				ps.setInt(1, friendOid);
				ps.setInt(2, playerOid);
				ps.addBatch();
				
				ps.executeBatch();
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
