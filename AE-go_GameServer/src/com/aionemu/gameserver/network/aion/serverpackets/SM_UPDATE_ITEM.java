/*
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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.InventoryPacket;

/**
 * 
 * @author ATracer
 *
 */
public class SM_UPDATE_ITEM extends InventoryPacket
{	
	private Item item;
	private boolean isWeaponSwitch = false;

	public SM_UPDATE_ITEM(Item item)
	{
		this.item = item;	
	}

	public SM_UPDATE_ITEM(Item item, boolean isWeaponSwitch)
	{
		this.item = item;
		this.isWeaponSwitch = isWeaponSwitch;
	}

	@Override
	protected void writeGeneralInfo(ByteBuffer buf, Item item)
	{
		writeD(buf, item.getObjectId());
		ItemTemplate itemTemplate = item.getItemTemplate();
		writeH(buf, 0x24);
		writeD(buf, itemTemplate.getNameId());
		writeH(buf, 0);
	}

	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{

		writeGeneralInfo(buf, item);

		ItemTemplate itemTemplate = item.getItemTemplate();

		if(itemTemplate.getTemplateId() == ItemId.KINAH.value())
		{
			writeKinah(buf, item, true);
		}
		else if (itemTemplate.isWeapon())
		{
			writeWeaponInfo(buf, item, true, isWeaponSwitch, false);
		}
		else if (itemTemplate.isArmor())
		{
			writeArmorInfo(buf,item, true, false);
		}
		else
		{
			writeGeneralItemInfo(buf, item, item.isQuest(), false);
		}
	}

	@Override
	protected void writeKinah(ByteBuffer buf, Item item, boolean isInventory)
	{
		writeH(buf, 0x16); //length of details
		writeC(buf, 0);
		writeC(buf, 0x1E); //or can be 0x1E
		writeC(buf, 0x63); // ?
		writeD(buf, item.getItemCount());
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeH(buf, 0);
		writeC(buf, 0);
		writeC(buf, 0x1A); // FF FF equipment
		writeC(buf, 0);
	}

}