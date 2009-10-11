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
public class Inventory
{

	public int kinah;
	public int totalItemsCount;
	public int itemUniqueIdArray[];
	public int itemIdArray[];
	public int itemNameIdArray[];
	public int itemCountArray[];
	public int totalDbItemsCount;
	public int isEquiped;
	public int isEquipedItemId;
	public int isEquipedItemUniqueId;
	public int itemId;
	public int isEquipedItemSlot;

	public int totalEquipedItemsCount;
	public int equipedItemUniqueIdArray[];
	public int equipedItemSlotArray[];
	public int equipedItemIdArray[];

	public int newItemUniqueIdValue;

	public void getInventoryFromDb(int activePlayer) {
		PreparedStatement ps = DB.prepareStatement("SELECT `itemUniqueId`, `itemId`,`itemCount`FROM `inventory` WHERE `itemOwner`=" + activePlayer);
		try
		{
			ResultSet rs = ps.executeQuery();
			rs.last();
			int row = rs.getRow();
			totalItemsCount = row;
			if (row !=0) {
				int row2 = 0;
				
				itemUniqueIdArray = new int[row+1];
				itemIdArray = new int[row+1];
				itemCountArray = new int[row+1];
			
				while (row > 0) {
					rs.absolute(row);
					itemUniqueIdArray[row2] = rs.getInt("itemUniqueId");
					itemIdArray[row2] = rs.getInt("itemId");
					itemCountArray[row2] = rs.getInt("itemCount");
					row2 = row2 +1;
					row = row - 1;
				}
			}
		}
		catch (SQLException e)
		{
			Logger.getLogger(Inventory.class).error("Error loading items", e);
			
		}
		finally
		{
			DB.close(ps);
		}
	}

	public void getEquipedItemsFromDb(int activePlayer) {
		PreparedStatement ps10 = DB.prepareStatement("SELECT `itemUniqueId`,`itemId`, `slot` FROM `inventory` WHERE `isEquiped`='1' AND `itemOwner`=" + activePlayer + " ORDER BY `slot` DESC"); //  
		try
		{
			ResultSet rs = ps10.executeQuery();
			rs.last();
			int row = rs.getRow();
			totalEquipedItemsCount = row;
			if (row !=0) {
				int row2 = 0;
				
				equipedItemUniqueIdArray = new int[row+1];
				equipedItemSlotArray = new int[row+1];
				equipedItemIdArray = new int[row+1];

				while (row > 0) {
					rs.absolute(row);
					equipedItemUniqueIdArray[row2] = rs.getInt("itemUniqueId");
					equipedItemSlotArray[row2] = rs.getInt("slot");
					equipedItemIdArray[row2] = rs.getInt("itemId");
					row2 = row2 +1;
					row = row - 1;
				}
			}
		}
		catch (SQLException e)
		{
			Logger.getLogger(Inventory.class).error("Error loading equiped items info", e);
			
		}
		finally
		{
			DB.close(ps10);
		}
	}

	public int getKinahFromDb(int activePlayer) {
		PreparedStatement ps2 = DB.prepareStatement("SELECT `kinah` FROM `players` WHERE `id`=" + activePlayer);
		try
		{
			ResultSet rs = ps2.executeQuery();
			rs.absolute(1);
			kinah = rs.getInt("kinah");
		}
		catch(SQLException e)
		{
			Logger.getLogger(Inventory.class).error("Error loading kinah", e);
		}
		finally
		{
			DB.close(ps2);
		}
		return activePlayer;
	}

	public void getIsEquipedFromDb(int activePlayer, int slot) {
		PreparedStatement ps6 = DB.prepareStatement("SELECT `isEquiped`,`itemId`,`itemUniqueId`  FROM `inventory` WHERE `slot` =" + slot);
		try
		{
			ResultSet rs = ps6.executeQuery();
			rs.last();
			int row = rs.getRow();
			if (row !=0) {
				rs.absolute(1);
				isEquiped = rs.getInt("isEquiped");
				isEquipedItemId = rs.getInt("itemId");
				isEquipedItemUniqueId = rs.getInt("itemUniqueId");
			}
		}
		catch(SQLException e)
		{
			Logger.getLogger(Inventory.class).error("Error loading is equiped", e);
		}
		finally
		{
			DB.close(ps6);
		}
	}

	public void getDbItemsCountFromDb() {
		PreparedStatement ps5 = DB.prepareStatement("SELECT * FROM `inventory`");
		try
		{
			ResultSet rs = ps5.executeQuery();
			rs.last();
			totalDbItemsCount = rs.getRow();
			
		}
		catch(SQLException e)
		{
			Logger.getLogger(Inventory.class).error("Error loading db items count", e);
		}
		finally
		{
			DB.close(ps5);
		}
	}

	public void getItemIdByUniqueItemId(int itemUniqueId) {
		PreparedStatement ps2 = DB.prepareStatement("SELECT `itemId` FROM `inventory` WHERE `itemUniqueId`=" + itemUniqueId);
		try
		{
			ResultSet rs = ps2.executeQuery();
			rs.last();
			int row = rs.getRow();
			if (row !=0) {
				rs.absolute(1);
				itemId = rs.getInt("itemId");
			}
		}
		catch (SQLException e)
		{
			Logger.getLogger(Inventory.class).error("Error loading item by unique id", e);
			
		}
		finally
		{
			DB.close(ps2);
		}
	}

	public void getLastUniqueIdFromDb() {
		PreparedStatement ps11 = DB.prepareStatement("SELECT `itemUniqueId` FROM `inventory` ORDER BY `itemUniqueId` ASC");
		try
		{
			ResultSet rs = ps11.executeQuery();
			rs.last();
			int row = rs.getRow();
			rs.absolute(row);
			newItemUniqueIdValue = rs.getInt("itemUniqueId");

		}
		catch (SQLException e)
		{
			Logger.getLogger(Inventory.class).error("Error loading last unique id", e);
			
		}
		finally
		{
			DB.close(ps11);
		}
	}

	public void putKinahToDb(int activePlayer, int count) {
		PreparedStatement ps3 = DB.prepareStatement("UPDATE `players` SET `kinah` = kinah+? WHERE `id`= ? ");
		try
		{
			ps3.setInt(1, count);
			ps3.setInt(2, activePlayer);
			ps3.executeUpdate();
		}
		catch(SQLException e)
		{
			Logger.getLogger(Inventory.class).error("Error storing kinah", e);
		}
		finally
		{
			DB.close(ps3);
		}
	}

	public void putItemToDb(int activePlayer, int itemId, int itemCount) {
		PreparedStatement ps4 = DB.prepareStatement("INSERT INTO `inventory` (`itemId`,`itemCount`,`itemOwner`) VALUES(?,?,?)");
		try
		{
			ps4.setInt(1, itemId);
			ps4.setInt(2, itemCount);
			ps4.setInt(3, activePlayer);
			ps4.executeUpdate();
		}
		catch(SQLException e)
		{
			Logger.getLogger(Inventory.class).error("Error storing item", e);
		}
		finally
		{
			DB.close(ps4);
		}
	}
	
	public void putIsEquipedToDb(int itemUniqueId, int IsEquiped, int slot) {
		PreparedStatement ps4 = DB.prepareStatement("UPDATE `inventory` SET `isEquiped` = ? , `slot` = ? WHERE `itemUniqueId` =" + itemUniqueId);
		try
		{
			ps4.setInt(1, IsEquiped);
			ps4.setInt(2, slot);
			ps4.executeUpdate();
		}
		catch(SQLException e)
		{
			Logger.getLogger(Inventory.class).error("Error storing is equiped", e);
		}
		finally
		{
			DB.close(ps4);
		}
	}

	public void deleteItemFromDb(int itemUniqueId) {
		PreparedStatement ps12 = DB.prepareStatement("DELETE FROM `inventory` WHERE `itemUniqueId` =" + itemUniqueId);
		try
		{
			ps12.executeUpdate();
		}
		catch(SQLException e)
		{
			Logger.getLogger(Inventory.class).error("Error storing is equiped", e);
		}
		finally
		{
			DB.close(ps12);
		}
	}
	

	public int getKinahCount() {
		return kinah;
	}

	public int getDbItemsCount() {
		return totalDbItemsCount;
	}

	public int getItemsCount() {
		return totalItemsCount;
	}

	public int getEquipedItemsCount() {
		return totalEquipedItemsCount;
	}

	public int getItemUniqueIdArray(int row) {
		return itemUniqueIdArray[row];
	}
	public int getItemIdArray(int row) {
		return itemIdArray[row];
	}

	public int getItemCountArray(int row) {
		return itemCountArray[row];
	}
	public int getIsEquiped() {
		return isEquiped;
	}
	public int getIsEquipedItemId() {
		return isEquipedItemId;
	}
	public int getIsEquipedItemUniqueId() {
		return isEquipedItemUniqueId;
	}
	public int getItemId() {
		return itemId;
	}
	public int getEquipedItemSlotArray(int row) {
		return equipedItemSlotArray[row];
	}
	public int getEquipedItemUniqueIdArray(int row) {
		return equipedItemUniqueIdArray[row];
	}
	public int getEquipedItemIdArray(int row) {
		return equipedItemIdArray[row];
	}
	public int getnewItemUniqueIdValue() {
		return newItemUniqueIdValue;
	}

}