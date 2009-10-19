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
import com.aionemu.gameserver.model.templates.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_INFO;
import com.aionemu.gameserver.services.ItemService;
import com.google.inject.Inject;

/**
 * 
 * @author orz
 * 
 */
public class CM_BUY_ITEM extends AionClientPacket
{
	public int npcObjId;
	public int unk1; 
	public int amount;
	public int itemId;
	public int count;

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
		Player player = getConnection().getActivePlayer();
		Inventory bag = player.getInventory();
				
		if ((unk1 != 1)||(unk1 != 12))
			log.info(String.format("Unhandle shop action unk1: %d", unk1));
		
		for (int i = 0; i < amount; i++) 
		{
			itemId = readD();
			count  = readD();
			unk2   = readD();
			
			//Todo check the amount of kinah at server side
			
			if (unk1 == 12) //buy
			{
				Item item = itemService.newItem(itemId, count);
				Item resultItem = bag.addToBag(item);
				if(resultItem != null)
				{
					//TODO check retail the real packets that sent to player
					sendPacket(new SM_INVENTORY_INFO(Collections.singletonList(resultItem)));
				}
		
			}
			else if (unk1 == 1) //sell
			{
				log.info(String.format("Sell itemId: %d count: %d", itemId, count));
			}
			else
			{
				log.info(String.format("Unhandle shop action: %d", unk1));
				log.info(String.format("ItemId: %d count: %d", itemId, count));
			}
		}

		//TODO uncomment only after inventory slot save testing
		//DAOManager.getDAO(InventoryDAO.class).store(bag);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
	}
}
