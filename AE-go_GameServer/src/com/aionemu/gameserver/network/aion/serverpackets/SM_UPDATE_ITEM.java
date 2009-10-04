/**
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.model.gameobjects.player.ItemList;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import java.util.Random;



public class SM_UPDATE_ITEM extends AionServerPacket
{
	private static final Logger	log	= Logger.getLogger(SM_UPDATE_ITEM.class);
	private int action;
	private int uniqueId;
	private int slot;
	public SM_UPDATE_ITEM(int slot, int action, int uniqueId)
	{
		this.slot = slot;
		this.action = action;
		this.uniqueId = uniqueId;
	}


	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{

	Inventory inventory = new Inventory();

	inventory.getItemIdByUniqueItemId(uniqueId);
	int itemId = inventory.getItemId();

	ItemList itemName = new ItemList();
	itemName.getItemList(itemId);
	int itemNameId = itemName.getItemNameId();

		//equip
		if (action ==0) {
			writeD(buf, uniqueId); // unique item id
			writeH(buf, 36); // unk always 0x24
			writeD(buf, itemNameId); // item name id
			writeH(buf, 0); // always 0
			writeH(buf, 5); // length of item details section
			//item details block//
			writeC(buf, 6);
			writeD(buf, slot); // slot
			//end of item details block//
		} 
		//unequip
		if (action==1) {
			writeD(buf, uniqueId); //  unique item id
			writeH(buf, 36); // unk always 0x24
			writeD(buf, itemNameId); // item name id
			writeH(buf, 0); // always 0
			writeH(buf, 5); // length of item details section
			//item details block//
			writeC(buf, 6);
			writeD(buf, 0); // slot
			//end of item details block//
		}
		if (action==2) {
			//store kinah structure
		}
	}
}