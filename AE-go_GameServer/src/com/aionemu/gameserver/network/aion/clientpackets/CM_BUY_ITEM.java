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

import org.apache.log4j.Logger;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_INFO;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
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
		int activePlayer = player.getObjectId();
		
		for (int i = 0; i < amount; i++) 
		{
			itemId = readD();
			count  = readD();
			unk2   = readD();
			log.info(String.format("Buying itemId: %d count: %d", itemId, count));
			
			//Todo check the amount of kinah at server side
			
			Inventory itemsDbOfPlayerCount = new Inventory(); // wrong
			itemsDbOfPlayerCount.getInventoryFromDb(activePlayer);
			int totalItemsCount = itemsDbOfPlayerCount.getItemsCount();
			int cubes = 1;
			int cubesize = 27;
			int allowItemsCount = cubesize*cubes-1;
			if (totalItemsCount<=allowItemsCount){
				Inventory items = new Inventory();
				items.putItemToDb(activePlayer, itemId, count);
				items.getDbItemsCountFromDb();
				int totalDbItemsCount = items.getDbItemsCount();
				int newItemUniqueId = totalDbItemsCount;

				sendPacket(new SM_INVENTORY_INFO(newItemUniqueId, itemId, count, 1, 8));

				} else {
				//todo show SM_INVENTORY_IS_FULL packet or smth.
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
	}
}
