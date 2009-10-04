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