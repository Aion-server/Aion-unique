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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.DB;

/**
 *
 * @author Metos
 * Thank to not delete comment
 */
public class DropList {
	private static final DropList instance = new DropList();
	
	//private int [][] fullDL;
	
	private lootingList [] fullDL;
	/*
	private int [] mobId;
	private int [] itemId;
	private int [] min;
	private int [] max;
	private float [] chance;
	private int [] quest;
	*/
	private int maxDL; //or lootingList.length
	
	public DropList () { //full load of droplist
		try {
			PreparedStatement ps = DB.prepareStatement("SELECT COUNT(*) AS nb FROM droplist");
			ResultSet prs = ps.executeQuery();
			prs.first();
			maxDL = prs.getInt("nb");
			
			if (maxDL == 0) { //nothing in database
				Logger.getLogger(DropList.class).error("Error : Can't load droplist, database is empty");
				return;
			}
			
			//fullDL = new int[maxDL][5]; //6 to quest
			fullDL = new lootingList[maxDL];
			
			/*
			mobId = new int[maxDL];
			itemId = new int[maxDL];
			min = new int[maxDL];
			max = new int[maxDL];
			chance = new float[maxDL];
			quest = new int[maxDL];
			*/
			int nb = 0;
			
			ResultSet rs = ps.executeQuery("SELECT * FROM droplist");
			while (rs.next()) {
				fullDL[nb] = new lootingList(rs.getInt("mobId"), rs.getInt("itemId"), rs.getInt("min"), rs.getInt("max"), rs.getFloat("chance"), rs.getInt("quest"));
				/*
				fullDL[nb][0] = rs.getInt("mobId");
				fullDL[nb][1] = rs.getInt("itemId");
				fullDL[nb][2] = rs.getInt("min");
				fullDL[nb][3] = rs.getInt("max");
				fullDL[nb][4] = rs.getInt("chance");
				//fullDL[nb][5] = rs.getInt("quest");
				
				fullDL[nb].mobId = rs.getInt("mobId");
				fullDL[nb].itemId = rs.getInt("itemId");
				fullDL[nb].min = rs.getInt("min");
				fullDL[nb].max = rs.getInt("max");
				fullDL[nb].chance = rs.getFloat("chance");
				fullDL[nb].quest = rs.getInt("quest");
				*/
				
				nb++;
			}
			DB.close(ps);
			Logger.getLogger(DropList.class).info("ListDrop Loaded ("+maxDL+")");
		}
		catch (SQLException e) {
			Logger.getLogger(DropList.class).error("Error loading droplist", e);
		}
	}
	
	//public int [][] getLootTable (int mobId) {
	public lootingList [] getLootTable (int mobId) {
		if (maxDL == 0) { //nothing in database
			//return new int[0][4];
			return new lootingList[0];
		}
		int nb = 0;
		for (int i = 0; i < maxDL; i++) {
			if (fullDL[i].mobId == mobId) {
				nb++;
			}
		}
		if (nb == 0) { //nothing to loot with this creature
			//return new int[0][4];
			return new lootingList[0];
		}
		//int [][] temp = new int[nb][4]; //5 to quest
		lootingList [] temp = new lootingList[nb];
		nb = 0;
		for (int i = 0; i < maxDL; i++) {
			if (fullDL[i].mobId == mobId) {
				temp[nb] = new lootingList(fullDL[i].mobId, fullDL[i].itemId, fullDL[i].min, fullDL[i].max, fullDL[i].chance, fullDL[i].quest);
				
				
				/*
				temp[nb].itemId = fullDL[i].itemId;
				temp[nb].min = fullDL[i].min;
				temp[nb].max = fullDL[i].max;
				temp[nb].chance = fullDL[i].chance;
				temp[nb].quest = fullDL[i].quest;
				
				temp[nb][0] = fullDL[i][1];
				temp[nb][1] = fullDL[i][2];
				temp[nb][2] = fullDL[i][3];
				temp[nb][3] = fullDL[i][4];
				*/
				//temp[nb][4] = fullDL[i][5];
				nb++;
			}
		}
		return temp;
	}
	
	public static DropList getInstance() {
		return instance;
	}
	
	public class lootingList {
		public int mobId;
		public int itemId;
		public int min;
		public int max;
		public float chance;
		public int quest;
		
		public lootingList (int mobId, int itemId, int min, int max, float chance, int quest) {
			this.mobId = mobId;
			this.itemId = itemId;
			this.min = min;
			this.max = max;
			this.chance = chance;
			this.quest = quest;
		}
	}
}