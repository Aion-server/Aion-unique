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
public class ItemList
{
	public String equipment_slots;
	public int min_damage;
	public int max_damage;
	public int price;
	public int level;
	public String quality;
	public String attack_range;
	public int hit_count;
	public int attack_delay;
	public int magical_hit_accuracy;
	public int magical_skill_boost;
	public int parry;
	public int critical;
	public int kno;
	public int agi;
	public int str;
	public String itemNameId;
	
	public void getItemList(int itemId) {
		PreparedStatement ps = DB.prepareStatement("SELECT `id`, `equipment_slots`,`min_damage`, `max_damage`, `price`, `level`, `quality`, `attack_range`, `hit_count`, `attack_delay`, `magical_hit_accuracy`, `magical_skill_boost`, `parry`, `critical`, `kno`, `agi`, `str`, `desc` FROM `item_list` WHERE `Id`=" + itemId);
		try
		{
			ResultSet rs = ps.executeQuery();
			rs.next();
			int row = rs.getRow();
			if (row!=0) {
				rs.absolute(1);
				equipment_slots = rs.getString("equipment_slots");
				min_damage = rs.getInt("min_damage");
				max_damage = rs.getInt("max_damage");
				price = rs.getInt("price");
				level = rs.getInt("level");
				quality = rs.getString("quality");
				attack_range = rs.getString("attack_range");
				hit_count = rs.getInt("hit_count");
				attack_delay = rs.getInt("attack_delay");
				magical_hit_accuracy = rs.getInt("magical_hit_accuracy");
				magical_skill_boost = rs.getInt("magical_skill_boost");
				parry = rs.getInt("parry");
				critical = rs.getInt("critical");
				kno = rs.getInt("kno");
				agi = rs.getInt("agi");
				str = rs.getInt("str");
				itemNameId = rs.getString("desc");
			}
		}
		catch (SQLException e)
		{
			Logger.getLogger(Inventory.class).error("Error loading item list", e);
			
		}
		finally
		{
			DB.close(ps);
		}
	}

	public String getEquipmentSlots() {
		return equipment_slots;
	}
	public int getItemNameId() {
		int itemNameIdClear = Integer.parseInt(itemNameId);
		return itemNameIdClear;
	}
	public int getParry() {
		return parry;
	}
	public int getLevel() {
		return level;
	}
	
}