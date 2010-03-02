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
import java.util.Set;

import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * 
 * @author alexa026, Avol, Corrected by Metos
 * 
 * modified by ATracer
 * 
 */
public class SM_LOOT_ITEMLIST extends AionServerPacket
{
	private int	targetObjectId;
	private DropItem[] dropItems;
	private int size;

	public SM_LOOT_ITEMLIST(int targetObjectId, Set<DropItem> dropItems)
	{
		this.targetObjectId = targetObjectId;
		this.dropItems = dropItems.toArray(new DropItem[dropItems.size()]);
		size = this.dropItems.length;
	}

	/**
	 * {@inheritDoc} dc
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf) 
	{
		writeD(buf, targetObjectId);
		writeC(buf, size);

		for(DropItem dropItem : dropItems)
		{
			writeC(buf, dropItem.getIndex()); // index in droplist
			writeD(buf, dropItem.getDropTemplate().getItemId());
			writeH(buf, dropItem.getCount());
			writeD(buf, 0);
		}
	}
}
