/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * @author kosyachok
 */
public class SM_WAREHOUSE_UPDATE extends InventoryPacket
{
	private int warehouseType;
	private Item item;


	public SM_WAREHOUSE_UPDATE(Item item, int warehouseType)
	{
		this.warehouseType = warehouseType;
		this.item = item;
	}

	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeC(buf, warehouseType);
		writeH(buf, 13);
		writeH(buf, 1);

		writeGeneralInfo(buf, item);

		ItemTemplate itemTemplate = item.getItemTemplate();

		if(itemTemplate.getTemplateId() == ItemId.KINAH.value())
		{
			writeKinah(buf, item, false);
		}
		else if (itemTemplate.isWeapon())
		{
			writeWeaponInfo(buf, item, false);
		}
		else if (itemTemplate.isArmor())
		{
			writeArmorInfo(buf,item, false, false);
		}
		else
		{
			writeGeneralItemInfo(buf, item, item.isQuest(), false);
		}

	}

	@Override
	protected void writeGeneralInfo(ByteBuffer buf, Item item)
	{
		writeD(buf, item.getObjectId());
		ItemTemplate itemTemplate = item.getItemTemplate();
		writeD(buf, itemTemplate.getTemplateId());
		writeC(buf, 0); //some item info (4 - weapon, 7 - armor, 8 - rings, 17 - bottles)
		writeH(buf, 0x24);
		writeD(buf, itemTemplate.getNameId());
		writeH(buf, 0);
	}
}
