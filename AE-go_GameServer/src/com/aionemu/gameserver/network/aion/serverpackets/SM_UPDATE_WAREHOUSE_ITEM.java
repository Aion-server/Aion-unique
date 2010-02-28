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
public class SM_UPDATE_WAREHOUSE_ITEM extends InventoryPacket
{
	Item item;
	int warehouseType;

	public SM_UPDATE_WAREHOUSE_ITEM(Item item, int warehouseType)
	{
		this.item = item;
		this.warehouseType = warehouseType;
	}

	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
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
		writeC(buf, warehouseType);
		ItemTemplate itemTemplate = item.getItemTemplate();
		writeH(buf, 0x24);
		writeD(buf, itemTemplate.getNameId());
		writeH(buf, 0);
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
		writeC(buf, 0xFF); // FF FF equipment
		writeC(buf, 0xFF);
	}
}
