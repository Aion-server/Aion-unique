/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.google.inject.Inject;
import com.aionemu.gameserver.services.ItemService;
/**
 *
 * @author kosyak
 */
public class CM_SPLIT_ITEM extends AionClientPacket
{
	@Inject
	private ItemService itemService;

	int itemObjId;
	int splitAmount;
	int slotNum; // destination slot?

	public CM_SPLIT_ITEM(int opcode)
	{
		super(opcode);
	}

	@Override
	protected void readImpl()
	{
		itemObjId = readD();
		splitAmount = readD();
		byte[] zeros = readB(10);
		slotNum = readH();        //Not needed right now, Items adding only to next available slot.
	}

	@Override
	protected void runImpl()
	{
		Player player = getConnection().getActivePlayer();
		itemService.splitItem(player, itemObjId, splitAmount, slotNum);
	}
}
