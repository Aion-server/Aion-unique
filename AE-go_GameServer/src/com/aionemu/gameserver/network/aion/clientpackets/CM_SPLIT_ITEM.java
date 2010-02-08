/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.services.ItemService;
import com.google.inject.Inject;

/**
 *
 * @author kosyak
 */
public class CM_SPLIT_ITEM extends AionClientPacket
{
	@Inject
	private ItemService itemService;

	int sourceItemObjId;
	int sourceStorageType;
	int itemAmount;
	int destinationItemObjId;
	int destinationStorageType;
	int slotNum; // destination slot.

	public CM_SPLIT_ITEM(int opcode)
	{
		super(opcode);
	}

	@Override
	protected void readImpl()
	{
		sourceItemObjId = readD();       // drag item unique ID. If merging and itemCount becoming null, this item must be deleted.
		itemAmount = readD();            // Items count to be moved.
		@SuppressWarnings("unused")
		byte[] zeros = readB(4);         // Nothing
		sourceStorageType = readC();     // Source storage
		destinationItemObjId = readD();  // Destination item unique ID if merging. Null if spliting.
		destinationStorageType = readC();// Destination storage
		slotNum = readH();               // Destination slot. Not needed right now, Items adding only to next available slot. Not needed at all when merge.
	}


	@Override
	protected void runImpl()
	{
		Player player = getConnection().getActivePlayer();

		if(destinationItemObjId == 0)
			itemService.splitItem(player, sourceItemObjId, itemAmount, slotNum, sourceStorageType, destinationStorageType);
		else
			itemService.mergeItems(player, sourceItemObjId, itemAmount, destinationItemObjId, sourceStorageType, destinationStorageType);
	}
}
