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

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.model.templates.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.InventoryPacket;

/**
 * 
 * @author ATracer
 *
 */

public class SM_UPDATE_ITEM extends InventoryPacket
{
	private static final Logger	log	= Logger.getLogger(SM_UPDATE_ITEM.class);
	
	private Item item;

	public SM_UPDATE_ITEM(Item item, int action)
	{
		this.item = item;	
	}
	
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{

		writeGeneralInfo(buf, item);
		
		ItemTemplate itemTemplate = item.getItemTemplate();
		
		if(itemTemplate.getItemId() == ItemId.KINAH.value())
		{
			writeKinah(buf, item);
		}
		else if (itemTemplate.isWeapon())
		{
			writeWeaponInfo(buf, item);
		}
		else if (itemTemplate.isArmor())
		{
			writeArmorInfo(buf,item);
		}
		else
		{
			writeGeneralItemInfo(buf, item);
		}
	}

}