/**
 * This file is part of aion-unique <www.aion-unique.com>.
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

// Author Avol

package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;
import java.util.Random;

import org.apache.log4j.Logger;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;


public class SM_UPDATE_PLAYER_APPEARANCE extends AionServerPacket
{
	private static final Logger	log	= Logger.getLogger(SM_UPDATE_PLAYER_APPEARANCE.class);

	public int activePlayer;

	public SM_UPDATE_PLAYER_APPEARANCE(int activePlayer)
	{
		this.activePlayer = activePlayer;
	}


	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, activePlayer); // player
		
		Inventory equipedItems = new Inventory();
		equipedItems.getEquipedItemsFromDb(activePlayer);
		int totalEquipedItemsCount = equipedItems.getEquipedItemsCount();
		
		int itemsCount = 0;
		int row = 0;
		while (totalEquipedItemsCount > 0) {
			int slot = equipedItems.getEquipedItemSlotArray(row);
			if (slot==5) {
				slot = 1; // or 2 weapon
			}
			if (slot==6) {
				slot = 8192;//or 16384 power shard
			}
			if (slot==7) {
				slot = 256;// 512 rings
			}
			if (slot==9) {
				slot = 64;// 128 earrings
			}
			itemsCount = itemsCount + slot;
			totalEquipedItemsCount = totalEquipedItemsCount-1;
			row+=1;
		}
	
		writeH(buf, itemsCount);// // items count

		totalEquipedItemsCount = equipedItems.getEquipedItemsCount();

		row = 0;
		while (totalEquipedItemsCount > 0) {
			writeD(buf, equipedItems.getEquipedItemIdArray(row));// item id
			writeD(buf, 0x00);// unk
			writeD(buf, 0x00);// color code
			totalEquipedItemsCount = totalEquipedItemsCount-1;
			row+=1;
		}
	}
}