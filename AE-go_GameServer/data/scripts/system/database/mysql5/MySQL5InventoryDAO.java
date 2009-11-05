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

import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.SkillList;
import com.aionemu.gameserver.model.gameobjects.Item;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;

import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ATracer
 * 
 */
public class MySQL5InventoryDAO extends InventoryDAO
{
    private static final Logger log = Logger.getLogger(MySQL5InventoryDAO.class);

    public static final String SELECT_QUERY = "SELECT `itemUniqueId`, `itemId`, `itemCount`, `isEquiped`, `slot` FROM `inventory` WHERE `itemOwner`=?";

    /* (non-Javadoc)
     * @see com.aionemu.gameserver.dao.InventoryDAO#load(int)
     */
    @Override
    public Inventory load(final int playerId)
    {
        //TODO load parts - cube, equipment etc.
        final Inventory inventory = new Inventory();

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
                    int isEquiped = rset.getInt("isEquiped");
                    int slot = rset.getInt("slot");
                    Item item = new Item(itemUniqueId, itemId, itemCount, isEquiped == 1, slot);
                    inventory.onLoadHandler(item);
                }
            }
        });
        return inventory;
    }

    /* (non-Javadoc)
     * @see com.aionemu.gameserver.dao.InventoryDAO#store(com.aionemu.gameserver.model.gameobjects.player.Inventory)
     */
    @Override
    public void store(Inventory inventory)
    {
        int playerId = inventory.getOwner().getObjectId();
        List<Item> inventoryItems = inventory.getAllItems();

        for(Item item : inventoryItems)
        {
            store(item, playerId);      
        } 
    }

    /**
     * @param item
     * @return
     */
    public boolean store(final Item item, final int playerId)
    {   
        //TODO decide on performance
        return DB.insertUpdate("REPLACE INTO inventory ("
                + "itemUniqueId, itemId, itemCount, itemOwner, isEquiped, slot)" + " VALUES "
                + "(?, ?, ?, ?, ?, ?" + ")", new IUStH() {
                    @Override
                    public void handleInsertUpdate(PreparedStatement ps) throws SQLException
                    {
                        ps.setInt(1, item.getObjectId());
                        ps.setInt(2, item.getItemTemplate().getItemId());
                        ps.setInt(3, item.getItemCount());
                        ps.setInt(4, playerId);
                        ps.setInt(5, item.isEquipped() ? 1 : 0);
                        ps.setInt(6, item.getEquipmentSlot());
                        ps.execute();
                    }
                });
    }

    /* (non-Javadoc)
     * @see com.aionemu.gameserver.dao.InventoryDAO#delete(com.aionemu.gameserver.model.gameobjects.Item)
     */
    @Override
    public void delete(Item item) 
    {
        PreparedStatement statement = DB.prepareStatement("DELETE FROM inventory WHERE itemUniqueId = ?");
        try
        {
            statement.setInt(1, item.getObjectId());
        }
        catch(SQLException e)
        {
            log.error("Can't set int parameter to PreparedStatement", e);
        }
        DB.executeUpdateAndClose(statement);
    }

    /* (non-Javadoc)
     * @see com.aionemu.gameserver.dao.IDFactoryAwareDAO#getUsedIDs()
     */
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
