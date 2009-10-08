/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.model.gameobjects.player.ItemList;

/**
 * In this packet Server is sending Inventory Info?
 * 
 * @author -Nemesiss-
 * @updater alexa026
 * @finisher Avol ;d
 */
public class SM_INVENTORY_INFO extends AionServerPacket
{
	private int uniqueItemId;
	private int itemId;
	private int itemNameId;
	private int itemQuanty;
	private int entries;
	private int slot;
	/**
	 * Constructs new <tt>SM_INVENTORY_INFO </tt> packet
	 */
	public SM_INVENTORY_INFO(int uniqueItemId, int itemId, int itemQuanty, int entries, int slot)
	{
		this.uniqueItemId = uniqueItemId;
		this.itemId = itemId;
		this.itemQuanty = itemQuanty;
		this.entries = entries;
		this.slot = slot;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{

		//get item name id.
		ItemList itemName = new ItemList();
		itemName.getItemList(itemId);
		itemNameId = itemName.getItemNameId();

		ItemList itemSlot = new ItemList();

		itemSlot.getItemList(itemId);
		String slotName = itemSlot.getEquipmentSlots();
	
		char test = slotName.charAt(0);
		boolean isAnInt= test>='0' && test<='9';
	
		if (isAnInt){
			slot = Integer.parseInt(slotName);
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
		} else {
			slot = 0;
		}

		// something wrong with cube part.

		writeC(buf, 1); // TRUE/FALSE (1/0) update cube size
		writeC(buf, 0); // cube size
		writeH(buf, 0); // padding?
		writeH(buf, 1); // number of entries

		//while (count < entries) {
		
		writeD(buf, uniqueItemId); //Unique Id
		writeD(buf, itemId); //item Id 162000007
		writeH(buf, 0x24); // always 36
		writeD(buf, itemNameId); // item name id
		writeH(buf, 0); //always 0



		writeH(buf, 22); //lenght of item details
if (slot == 0) {
		writeC(buf, 0x00);
		writeH(buf, 0x23E3);
		writeD(buf, itemQuanty);
		writeD(buf, 0);	
		writeD(buf, 0);	
		writeD(buf, 0);	
		writeD(buf, 0);	
		writeD(buf, 0);	
		writeD(buf, 0);	
		writeD(buf, 0);	
		writeH(buf, 0);	
}
if (slot > 0) {
		//item details block//

		writeC(buf, 0x02); // equiped data follows

		writeC(buf, 0x00);
		writeC(buf, 8); // where this can be equiped. or whatever
		writeH(buf, 0);

		//---------------

		writeC(buf, 0x02);
		writeC(buf, 0x00);
		writeC(buf, 0x08); 
		writeD(buf, 0);
		writeD(buf, 0);
		writeH(buf, 0);

		/*//---------------
		writeC(buf, 0x0B); // appearance info follows?
		writeH(buf, 0);
		writeD(buf, 0x7C85AE06); // changing this value tags item as skinned
		writeD(buf, 0); // 4608 manastone type
		writeD(buf, 0); // 14 mana stone atribute bonus
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeC(buf, 0);
		writeC(buf, 0);
		writeC(buf, 0);
		//------------
		writeC(buf, 0x0A);
		writeD(buf, 196628);
		writeC(buf, 0);
		writeC(buf, 0);
		writeC(buf, 0);
		//------------
		writeC(buf, 0x0A);
		writeD(buf, 20971923);
		writeC(buf, 0);
		writeC(buf, 0);
		writeC(buf, 0);
		//------------
		writeC(buf, 0x0A);
		writeD(buf, 327784);
		writeC(buf, 0);
		writeC(buf, 0);
		writeC(buf, 0);
		//------------*/

		writeC(buf, 0); // general info fallows
		writeH(buf, 11264);  // sets the varios bits of attribute test on the tooltip
		writeD(buf, itemQuanty); // quanty
		
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);

		writeC(buf, 0);
		writeC(buf, 0);
		writeC(buf, 0);

		///details block end/// 
}
		writeC(buf, 24); // location in inventory -?
		writeC(buf, 0); //
		writeC(buf, 0); // sometimes 0x01

	}
}
