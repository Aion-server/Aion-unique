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
	private int	itemNameId;
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
		/*
		writeD(buf, targetObjectId);
		writeC(buf, 0x01); 
		writeC(buf, 0x00);
		writeC(buf, 0xc4);
		writeC(buf, 0x68);
		writeC(buf, 0xf7);
		
		writeC(buf, 0x05);
		writeC(buf, 0x01);
		writeD(buf, 0x00);
		writeC(buf, 0x00);
		*/

		if (itemId != 0)
		{
		
		writeH(buf, 25); 
		writeH(buf, 1); // unk
		
		writeD(buf, itemUniqueId); // unique item id

		writeD(buf, itemId); //item id
		writeH(buf, 36); //always 0x24

		writeD(buf, itemNameId); // itemNameId
		writeH(buf, 0);
		writeH(buf, 0x16); // length of item details

		//details block//

		writeC(buf, 0x02); // equiped data follows

		writeC(buf, 0x00);
		writeC(buf, 8); // where this can be equiped. or whatever
		writeH(buf, 0);

		//---------------

		writeC(buf, 0x06);
		writeC(buf, 0x00);
		writeC(buf, 0x08); 
		writeD(buf, 0);
		writeD(buf, 0);
		writeH(buf, 0);

		//---------------
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
		//------------

		writeC(buf, 0); // general info fallows
		writeH(buf, 11264);  // sets the varios bits of attribute test on the tooltip
		writeD(buf, 1); // quanty
		
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);

		writeC(buf, 0);
		writeC(buf, 0);
		writeC(buf, 0);
		//details block end//


		writeC(buf, 24); // location in inventory -?
		writeC(buf, 0); //
		writeC(buf, 0); // sometimes 0x01

		/*writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeH(buf, 0);
		writeC(buf, 0);
		
		writeC(buf, 0xff);
		writeC(buf, 0xff);
		writeC(buf, 0);*/
		}
		else
		{
			//if item's id is wrong Do nothing
		}
	}	
}