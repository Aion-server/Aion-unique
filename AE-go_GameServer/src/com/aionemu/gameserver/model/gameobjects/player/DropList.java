/**
 * This file is part of aion-unique <aionunique.smfnew.com>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
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
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.gameserver.model.gameobjects.player;

import java.util.Random;

import com.aionemu.gameserver.configs.Config;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;

/**
 *
 * @author Avol
 */
public class DropList
{
	public int player;
	public int totalItemsCount;
	public int DropDataItemId[];
	public int DropDataMin[];
	public int DropDataMax[];
	public int DropDataChance[];
	
	public void getDropList(int mobId) {
		PreparedStatement ps = DB.prepareStatement("SELECT `itemId`, `min`, `max`, `chance` FROM `droplist` WHERE `mobId`=" + mobId);
		try
		{
			ResultSet rs = ps.executeQuery();
			rs.last();
			int row = rs.getRow();
			int row2 = 0;
			totalItemsCount = row;
			DropDataItemId = new int[row+1];
			DropDataMin = new int[row+1];
			DropDataMax = new int[row+1];
			DropDataChance = new int[row+1];
			
			while (row > 0) {
				rs.absolute(row);
				DropDataItemId[row2] = rs.getInt("itemId");
				DropDataMin[row2] = rs.getInt("min");
				DropDataMax[row2] = rs.getInt("max");
				DropDataChance[row2] = rs.getInt("chance");
				row2 = row2 +1;
				row = row - 1;
			}
		}
		catch (SQLException e)
		{
			Logger.getLogger(DropList.class).error("Error loading droplist", e);
			
		}
		finally
		{
			DB.close(ps);
		}
	}

	public int getDropDataItemId(int row) {
		return DropDataItemId[row];
	}
	public int getDropDataMin(int row) {
		return DropDataMin[row];
	}
	public int getDropDataMax(int row) {
		return DropDataMax[row];
	}
	public int getDropDataChance(int row) {
		return DropDataChance[row];
	}
	public int getItemsCount() {
		return totalItemsCount;
	}


}