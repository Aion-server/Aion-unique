/*
 * This file is part of aion-unique <aionu-unique.com>.
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
import java.util.List;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * @author ATracer
 * 
 */
public class MySQL5InventoryDAO extends InventoryDAO
{
    private static final Logger log = Logger.getLogger(MySQL5InventoryDAO.class);

    public static final String SELECT_QUERY = "SELECT `itemUniqueId`, `itemId`, `itemCount`, `itemColor`, `isEquiped`, `slot` FROM `inventory` WHERE `itemOwner`=?";
    public static final String INSERT_QUERY = "INSERT INTO `inventory` (`itemUniqueId`, `itemId`, `itemCount`, `itemColor`, `itemOwner`, `isEquiped`, `slot`) VALUES(?,?,?,?,?,?,?)";
    public static final String UPDATE_QUERY = "UPDATE inventory SET  itemCount=?, itemColor=?, isEquiped=?, slot=? WHERE itemUniqueId=?";
    public static final String DELETE_QUERY = "DELETE FROM inventory WHERE itemUniqueId=?";

    @Override
    public Inventory load(Player player)
    {
        //TODO load parts - cube, equipment etc.
        final Inventory inventory = new Inventory(player);
        final int playerId = player.getObjectId();

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
                    int itemUniqueId = rset.getInt("itemUniqueId");
                    int itemId = rset.getInt("itemId");
                    int itemCount = rset.getInt("itemCount");
                    int itemColor = rset.getInt("itemColor");
                    int isEquiped = rset.getInt("isEquiped");
                    int slot = rset.getInt("slot");
                    Item item = new Item(itemUniqueId, itemId, itemCount, itemColor, isEquiped == 1, slot);
                    item.setPersistentState(PersistentState.UPDATED);
                    inventory.onLoadHandler(item);
                }
            }
        });
        return inventory;
    }

    @Override
    public boolean store(Inventory inventory)
    {
        int playerId = inventory.getOwner().getObjectId();
        List<Item> inventoryItems = inventory.getAllItems();
        
        boolean resultSuccess = true;
        for(Item item : inventoryItems)
        {   	
        	resultSuccess = store(item, playerId);      
        } 
        return resultSuccess;
    }
    
    /**
     * @param item
     * @param playerId
     * @return
     */
    public boolean store(final Item item, final int playerId)
    {   
    	boolean result = false;
    	
    	switch(item.getPersistentState())
    	{
    		case NEW:
    			result = insertItem(item, playerId);
    			break;
    		case UPDATE_REQUIRED:
    			result = updateItem(item);
    			break;
    		case DELETED:
    			result = deleteItem(item);
    			break;
    	}
    	item.setPersistentState(PersistentState.UPDATED);
    	return result;
    }
    
    /**
     * @param item
     * @param playerId
     * @return
     */
    private boolean insertItem(final Item item, final int playerId)
    {
    	return DB.insertUpdate(INSERT_QUERY, new IUStH() {
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, item.getObjectId());
				stmt.setInt(2, item.getItemTemplate().getItemId());
				stmt.setInt(3, item.getItemCount());
				stmt.setInt(4, item.getItemColor());
				stmt.setInt(5, playerId);
				stmt.setBoolean(6, item.isEquipped());
				stmt.setInt(7, item.getEquipmentSlot());
				stmt.execute();
			}
		});
    }
    
    /**
     * @param item
     * @return
     */
    private boolean updateItem(final Item item)
    {
    	return DB.insertUpdate(UPDATE_QUERY, new IUStH() {
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, item.getItemCount());
				stmt.setInt(2, item.getItemColor());
				stmt.setBoolean(3, item.isEquipped());
				stmt.setInt(4, item.getEquipmentSlot());
				stmt.setInt(5, item.getObjectId());
				stmt.execute();
			}
		});
    }

    /**
     * 
     * @param item
     */
    private boolean deleteItem(final Item item) 
    {
    	return DB.insertUpdate(DELETE_QUERY, new IUStH() {
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, item.getObjectId());
				stmt.execute();
			}
		});
    }

    @Override
    public int[] getUsedIDs() 
    {
        PreparedStatement statement = DB.prepareStatement("SELECT itemUniqueId FROM inventory", ResultSet.TYPE_SCROLL_INSENSITIVE,
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
                ids[i] = rs.getInt("itemUniqueId");
            }
            return ids;
        }
        catch(SQLException e)
        {
            log.error("Can't get list of id's from inventory table", e);
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

}
