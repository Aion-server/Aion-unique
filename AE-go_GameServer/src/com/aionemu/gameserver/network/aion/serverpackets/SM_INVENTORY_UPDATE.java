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
import java.util.Random;

/**
 * 
 * @author alexa026
 * 
 */
public class SM_INVENTORY_UPDATE extends AionServerPacket
{
	private int	itemId;
	private int	itemCount = 0;
	private int	itemNameId = 0;
	private int	itemUniqueId;
	private int	slot;
	
	public SM_INVENTORY_UPDATE(int itemUniqueId,int itemId, int itemCount)
	{
		this.itemId = itemId;
		this.itemCount = itemCount;
		this.itemUniqueId = itemUniqueId;
	}

	/**
	 * {@inheritDoc} dc
	 */
	
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{	
		ItemList itemName = new ItemList();
		itemName.getItemList(itemId);
		itemNameId = itemName.getItemNameId();
		slot = 8;
		if (itemId !=0) {
		writeH(buf, 25); 
		writeH(buf, 1); // unk
		
		writeD(buf, itemUniqueId); // unique item id

		writeD(buf, itemId); //item id
		writeH(buf, 36); //always 0x24

		writeD(buf, itemNameId); // itemNameId
		writeH(buf, 0);
		writeH(buf, 0x16); // length of item details
		writeC(buf, 0);
		writeH(buf, 0xa3e);
		writeD(buf, itemCount); //count
		
		//dummy
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeH(buf, 0);
		writeC(buf, 0);
		
		writeC(buf, 0xff);
		writeC(buf, 0xff);
		writeC(buf, 0);

		}
		else
		{
			//if item's id is wrong Do nothing
		}
	}	
}