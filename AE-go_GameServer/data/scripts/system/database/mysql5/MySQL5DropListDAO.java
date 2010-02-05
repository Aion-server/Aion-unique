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

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.DropListDAO;
import com.aionemu.gameserver.model.drop.DropList;
import com.aionemu.gameserver.model.drop.DropTemplate;

/**
 * @author ATracer
 * 
 */

public class MySQL5DropListDAO extends DropListDAO
{
    public static final String SELECT_QUERY = "SELECT * FROM `droplist`";
    
    @Override
    public DropList load() {
  
        final DropList dropList = new DropList();
        
        DB.select(SELECT_QUERY, new ParamReadStH()
        {
            @Override
            public void setParams(PreparedStatement stmt) throws SQLException
            {
            }

            @Override
            public void handleRead(ResultSet rset) throws SQLException
            {
                while(rset.next())
                {
                    int mobId = rset.getInt("mobId");
                    int itemId = rset.getInt("itemId");
                    int min = rset.getInt("min");
                    int max = rset.getInt("max");
                    float chance = rset.getFloat("chance");
                    int quest = rset.getInt("quest");
                    
                    if(chance > 0)
                    {
                        DropTemplate dropTemplate = new DropTemplate(mobId, itemId, min, max, chance, quest);
                        dropList.addDropTemplate(mobId, dropTemplate);
                    }
                }
            }
        });
        
        return dropList;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean supports(String databaseName, int majorVersion, int minorVersion)
    {
        return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
    }
}
