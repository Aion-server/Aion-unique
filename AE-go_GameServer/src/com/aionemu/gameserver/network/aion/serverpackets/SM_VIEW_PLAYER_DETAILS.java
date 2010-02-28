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
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Avol
 */

public class SM_VIEW_PLAYER_DETAILS extends AionServerPacket
{
	private List<Item> items;
	private int size;
	
	public SM_VIEW_PLAYER_DETAILS(List<Item> items)
	{
		this.items = items;
		this.size = items.size();
	}


	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{

		writeD(buf, 0); // unk
		writeC(buf, 10); //unk
		writeC(buf, size); // itemCount
		writeC(buf, 0);
		writeD(buf, 0);
		for(Item item : items)
		{	
			//////general info/////////////
			writeD(buf, item.getItemTemplate().getTemplateId());//itemId
			writeH(buf, 36); // 
			writeD(buf, item.getItemTemplate().getNameId());// itemNameId
			writeH(buf, 0);
			/////who knows/////////////
			writeH(buf, 36);
			writeC(buf, 4);
			writeC(buf, 1);
			writeH(buf, 0);
			writeH(buf, 0);
			writeC(buf, 0);
			////////////////////////
			writeH(buf, 0);
			writeC(buf, 6);
			writeH(buf, item.getEquipmentSlot()); // slot
			writeH(buf, 0);
			writeC(buf, 0);
			writeH(buf, 62);
			writeH(buf, item.getItemCount()); // count
			////////////////////////
			//Here comes the lol part.
			////////////////////////
			writeD(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);
			writeC(buf, 0);
		}
		
	}
}