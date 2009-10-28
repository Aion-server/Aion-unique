/**
 * This file is part of aion-unique <aion-unique.com>.
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
import java.util.Random;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.model.templates.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Avol
 */

public class SM_EXCHANGE_ADD_ITEM extends AionServerPacket
{
	private static final Logger	log	= Logger.getLogger(SM_EXCHANGE_ADD_ITEM.class);

	private int itemObjId;
	private int itemCount;
	private int action;
	private int itemId;
	private int itemNameId;
	private Player player;

	public SM_EXCHANGE_ADD_ITEM(int itemObjId, int itemCount, int action, int itemId, int itemNameId, Player player)
	{
		this.itemObjId = itemObjId;
		this.itemCount = itemCount;	
		this.action = action;
		this.itemId = itemId;
		this.itemNameId = itemNameId;
		this.player = player;
	}

	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{

		writeC(buf, action); // 0 -self 1-other
		writeD(buf, itemId); // itemId
		writeD(buf, itemObjId); // itemObjId

		writeH(buf, 36);

		writeD(buf, itemNameId); // itemNameId

		writeH(buf, 0); 

		Inventory inventory = player.getInventory();
		Item item = inventory.getItemByObjId(itemObjId);
		ItemTemplate itemTemplate = item.getItemTemplate();

		if (itemTemplate.isWeapon()) 
		{

		int itemSlotId = item.getEquipmentSlot();
		writeH(buf, 0x46);
		writeC(buf, 0x06);	
		writeD(buf, 0);
		writeC(buf, 0x01);
		writeD(buf, 0);
		writeD(buf, 0x02);
		writeC(buf, 0x0B); //? some details separator
		writeH(buf, 0);
		writeD(buf, itemId);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeC(buf, 0x3E);
		writeC(buf, 0x0A);
		writeD(buf, itemCount);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeH(buf, 0);
		writeC(buf, 0);
		writeH(buf, 0); // FF FF equipment
		writeC(buf,  0);//item.isEquipped() ? 1 : 0

		} 
		else if (itemTemplate.isArmor()) 
		{

		writeH(buf, 0x4A);
		writeC(buf, 0x06);		
		writeD(buf, 0);
		writeC(buf, 0x02);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeC(buf, 0x0B); //? some details separator
		writeH(buf, 0);
		writeD(buf, itemId);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeC(buf, 0x3E);
		writeC(buf, 0x02);
		writeD(buf, itemCount);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeH(buf, 0);
		writeC(buf, 0);
		writeH(buf, 0); // FF FF equipment
		writeC(buf, 1);//item.isEquipped() ? 1 : 0

		}
		else 
		{

		writeH(buf, 0x16); //length of details
		writeC(buf, 0);
		writeC(buf, 0x3E); //or can be 0x1E
		writeC(buf, 0x63); // ?
		writeD(buf, itemCount);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeH(buf, 0);
		writeC(buf, 0);
		writeH(buf, 0); // not equipable items
		writeC(buf, 0);

		}

	}
}