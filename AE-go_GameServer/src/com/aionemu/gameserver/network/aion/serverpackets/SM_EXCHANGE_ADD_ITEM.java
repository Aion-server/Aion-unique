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
 * @author Avol
 * @author ATracer
 * 
 */
public class SM_EXCHANGE_ADD_ITEM extends InventoryPacket
{
	private int action;
	private Item item;

	public SM_EXCHANGE_ADD_ITEM(int action, Item item)
	{
		this.action = action;
		this.item = item;
	}

	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{

		writeC(buf, action); // 0 -self 1-other

		writeGeneralInfo(buf, item);

		ItemTemplate itemTemplate = item.getItemTemplate();

		if(itemTemplate.getTemplateId() == ItemId.KINAH.value())
		{
			writeKinah(buf, item, true);
		}
		else if (itemTemplate.isWeapon())
		{
			writeWeaponInfo(buf, item, true);
		}
		else if (itemTemplate.isArmor())
		{
			writeArmorInfo(buf,item, true, false);
		}
		else
		{				
			writeGeneralItemInfo(buf, item, false, false);
			writeC(buf, 0);
		}
	}

	@Override
	protected void writeGeneralInfo(ByteBuffer buf, Item item)
	{	
		ItemTemplate itemTemplate = item.getItemTemplate();
		writeD(buf, itemTemplate.getTemplateId());
		writeD(buf, item.getObjectId());
		writeH(buf, 0x24);
		writeD(buf, itemTemplate.getNameId());
		writeH(buf, 0);
	}
}