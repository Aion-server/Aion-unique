/**
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
package com.aionemu.gameserver.network.aion.clientpackets;

import java.util.Collections;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.inject.Inject;

/**
 * 
 * @author orz
 * 
 */
public class CM_BUY_ITEM extends AionClientPacket
{
	private int npcObjId;
	private int unk1; 
	private int amount;
	private int itemId;
	private int count;

	public int unk2;
	
	public CM_BUY_ITEM(int opcode)
	{
		super(opcode);
	}

	/**
	 * Logger
	 */
	private static final Logger	log	= Logger.getLogger(CM_BUY_ITEM.class);
	
	@Inject
	private ItemService itemService;

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		npcObjId = readD();
		unk1	 = readH();
		amount = readH(); //total no of items
		int totalprice = 0;
		
		Player player = getConnection().getActivePlayer();
		Inventory bag = player.getInventory();
		
		for (int i = 0; i < amount; i++) 
		{
			itemId = readD();
			count  = readD();
			unk2   = readD();
							
			if (unk1 == 12) //buy
			{
				Item item = itemService.newItem(itemId, count);
				if((item != null)&&(!bag.isFull()))
				{
					Item resultItem = bag.addToBag(item);
					totalprice = totalprice - item.getItemTemplate().getPrice() * count;
					//TODO check retail the real packets that sent to player
					sendPacket(new SM_INVENTORY_INFO(Collections.singletonList(resultItem)));
				}
			}
			else if (unk1 == 1) //sell
			{
				Item item = bag.getItemByObjId(itemId);
				if (item != null)
				{
					bag.removeFromBag(item);
					totalprice = totalprice + item.getItemTemplate().getPrice()* count / 4;
				//TODO check retail the real packets that sent to player
				sendPacket(new SM_DELETE_ITEM(item.getObjectId()));
				}
			}
			else
			{
				log.info(String.format("Unhandle shop action unk1: %d", unk1));
			}
		}

		bag.increaseKinah(2 * totalprice);
		if (totalprice != 0)
			PacketSendUtility.sendPacket(player, new SM_UPDATE_ITEM(bag.getKinahItem()));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
	}

}
