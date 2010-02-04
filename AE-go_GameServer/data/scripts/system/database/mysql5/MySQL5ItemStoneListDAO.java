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
import com.aionemu.gameserver.dao.ItemStoneListDAO;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.items.ItemStone;

/**
 * @author ATracer
 *
 */
public class MySQL5ItemStoneListDAO extends ItemStoneListDAO
{
	public static final String INSERT_QUERY = "INSERT INTO `item_stones` (`itemUniqueId`, `itemId`, `slot`) VALUES (?,?,?)";
	public static final String DELETE_QUERY = "DELETE FROM `item_stones` WHERE `itemUniqueId`=? AND slot=?";
	public static final String SELECT_QUERY = "SELECT `itemId`, `slot` FROM `item_stones` WHERE `itemUniqueId`=?";


	@Override
	public List<ItemStone> load(final int itemObjId)
	{
		final List<ItemStone> itemStones = new ArrayList<ItemStone>();
		DB.select(SELECT_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, itemObjId);
			}

			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while(rset.next())
				{
					int itemId = rset.getInt("itemId");
					int slot = rset.getInt("slot");			

					itemStones.add(new ItemStone(itemObjId, itemId,
						slot, PersistentState.UPDATED));

				}
			}
		});
		return itemStones;
	}

	@Override
	public void save(final List<ItemStone> itemStoneList)
	{
		for(int i = 0; i < itemStoneList.size() ; i++)
		{
			ItemStone itemStone = itemStoneList.get(i);
			switch(itemStone.getPersistentState())
			{
				case NEW:
					addItemStone(itemStone.getItemObjId(), itemStone.getItemId(),
						itemStone.getSlot());
					break;
				case DELETED:
					deleteItmeStone(itemStone.getItemObjId(), itemStone.getSlot());
					break;
			}
			itemStone.setPersistentState(PersistentState.UPDATED);
		}
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
	private void deleteItmeStone(final int itemObjId, final int slot)
	{		
		DB.insertUpdate(DELETE_QUERY, new IUStH()
		{
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, itemObjId);
				stmt.setInt(2, slot);
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
