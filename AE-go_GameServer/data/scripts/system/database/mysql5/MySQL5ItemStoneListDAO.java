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

import org.apache.log4j.Logger;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.ItemStoneListDAO;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.GodStone;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.items.ItemStone.ItemStoneType;

/**
 * @author ATracer
 *
 */
public class MySQL5ItemStoneListDAO extends ItemStoneListDAO
{
	private static final Logger	log	= Logger.getLogger(MySQL5ItemStoneListDAO.class);

	public static final String INSERT_QUERY = "INSERT INTO `item_stones` (`itemUniqueId`, `itemId`, `slot`, `category`) VALUES (?,?,?, ?)";
	public static final String DELETE_QUERY = "DELETE FROM `item_stones` WHERE `itemUniqueId`=? AND slot=? AND category=?";
	public static final String SELECT_QUERY = "SELECT `itemId`, `slot` FROM `item_stones` WHERE `itemUniqueId`=? AND `category`=?";
	

	@Override
	public List<ManaStone> load(final int itemObjId)
	{
		final List<ManaStone> itemStones = new ArrayList<ManaStone>();
		DB.select(SELECT_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, itemObjId);
				stmt.setInt(2, ItemStoneType.MANASTONE.ordinal());
			}

			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while(rset.next())
				{
					int itemId = rset.getInt("itemId");
					int slot = rset.getInt("slot");			

					itemStones.add(new ManaStone(itemObjId, itemId,
						slot, PersistentState.UPDATED));

				}
			}
		});
		return itemStones;
	}
	
	@Override
	public void save(Player player)
	{
		List<Item> allPlayerItems = player.getAllItems();
		
		for(Item item : allPlayerItems)
		{
			List<ManaStone> manaStones = item.getItemStones();
			store(manaStones);
			
			GodStone godStone = item.getGodStone();
			store(godStone);
		}	
	}

	@Override
	public void store(final List<ManaStone> manaStones)
	{
		if(manaStones == null)
			return;
		
		for(int i = 0; i < manaStones.size() ; i++)
		{
			ManaStone manaStone = manaStones.get(i);
			switch(manaStone.getPersistentState())
			{
				case NEW:
					addItemStone(manaStone.getItemObjId(), manaStone.getItemId(),
						manaStone.getSlot());
					break;
				case DELETED:
					deleteItemStone(manaStone.getItemObjId(), manaStone.getSlot());
					break;
				
			}
			manaStone.setPersistentState(PersistentState.UPDATED);
		}	
	}
	
	@Override
	public GodStone loadGodstone(final int itemObjId)
	{
		GodStone godStone = null;

		PreparedStatement stmt = DB.prepareStatement(SELECT_QUERY);
		try
		{
			stmt.setInt(1, itemObjId);
			stmt.setInt(2, ItemStoneType.GODSTONE.ordinal());
			ResultSet rs = stmt.executeQuery();
			if(rs.next())
			{
				int itemId = rs.getInt("itemId");
				godStone = new GodStone(itemObjId, itemId, PersistentState.UPDATED);
			}
		}
		catch(SQLException e)
		{
			log.error("Error while loading godstone" + itemObjId, e);
		}
		finally
		{
			DB.close(stmt);
		}

		return godStone;
	}
	
	/**
	 * 
	 * @param godstone
	 */
	@Override
	public void store(GodStone godstone)
	{
		if(godstone == null)
			return;
		
		switch(godstone.getPersistentState())
		{
			case NEW:
				addGodStone(godstone.getItemObjId(), godstone.getItemId(),
					godstone.getSlot());
				break;
			case DELETED:
				deleteGodStone(godstone.getItemObjId(), godstone.getSlot());
				break;
		}
		godstone.setPersistentState(PersistentState.UPDATED);
	}

	/**
	 *  Adds new item stone to item
	 *  
	 * @param itemObjId
	 * @param itemId
	 * @param statEnum
	 * @param statValue
	 * @param slot
	 */
	private void addItemStone(final int itemObjId, final int itemId, final int slot)
	{
		DB.insertUpdate(INSERT_QUERY, new IUStH() {
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, itemObjId);
				stmt.setInt(2, itemId);
				stmt.setInt(3, slot);
				stmt.setInt(4, ItemStoneType.MANASTONE.ordinal());
				stmt.execute();
			}
		});
	}
	
	/**
	 * 
	 * @param itemObjId
	 * @param itemId
	 * @param slot
	 */
	private void addGodStone(final int itemObjId, final int itemId, final int slot)
	{
		DB.insertUpdate(INSERT_QUERY, new IUStH() {
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, itemObjId);
				stmt.setInt(2, itemId);
				stmt.setInt(3, slot);
				stmt.setInt(4, ItemStoneType.GODSTONE.ordinal());
				stmt.execute();
			}
		});
	}

	/**
	 *  Deleted item stone from selected item
	 *  
	 * @param itemObjId
	 * @param slot
	 */
	private void deleteItemStone(final int itemObjId, final int slot)
	{		
		DB.insertUpdate(DELETE_QUERY, new IUStH()
		{
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, itemObjId);
				stmt.setInt(2, slot);
				stmt.setInt(3, ItemStoneType.MANASTONE.ordinal());
				stmt.execute();
			}
		});
	}
	
	/**
	 * 
	 * @param itemObjId
	 * @param slot
	 */
	private void deleteGodStone(final int itemObjId, final int slot)
	{		
		DB.insertUpdate(DELETE_QUERY, new IUStH()
		{
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, itemObjId);
				stmt.setInt(2, slot);
				stmt.setInt(3, ItemStoneType.GODSTONE.ordinal());
				stmt.execute();
			}
		});
	}

    @Override
    public boolean supports(String s, int i, int i1)
    {
        return MySQL5DAOUtils.supports(s, i, i1);
    }

}
