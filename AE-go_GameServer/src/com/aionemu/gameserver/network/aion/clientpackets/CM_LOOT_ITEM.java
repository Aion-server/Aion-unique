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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_ITEMLIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import org.apache.log4j.Logger;

import java.util.Random;
/**
 * 
 * @author alexa026
 * 
 */
public class CM_LOOT_ITEM extends AionClientPacket
{
	private static final Logger	log	= Logger.getLogger(CM_LOOT_ITEM.class);

	private int					targetObjectId;
	private int					unk;
	private int					itemIdArray;
	public CM_LOOT_ITEM(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		targetObjectId = readD();// empty
		unk = readC();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{

		
		Player player = getConnection().getActivePlayer();
		int activePlayer = player.getObjectId();

		Inventory itemsDbOfPlayerCount = new Inventory(); // wrong
		itemsDbOfPlayerCount.getInventoryFromDb(activePlayer);
		int totalItemsCount = itemsDbOfPlayerCount.getItemsCount();

		Inventory equipedItems = new Inventory();
		equipedItems.getEquipedItemsFromDb(activePlayer);
		int totalEquipedItemsCount = equipedItems.getEquipedItemsCount();

		int itemCountArray;
		int cubes = 1;
		int cubesize = 27;
		int allowItemsCount = cubesize*cubes-1;

		totalItemsCount = totalItemsCount - totalEquipedItemsCount;	
			
		if (totalItemsCount<=allowItemsCount){
			Inventory items = new Inventory();
			int newItemUniqueId;
			int arrayLenght = player.getGameStats().getArrayLenght();

			int a=0;
			
			while (arrayLenght > 0 ) {
				itemIdArray = player.getGameStats().getItemIdArray(a);
				itemCountArray = player.getGameStats().getItemCountArray(a);
				if(itemIdArray == 182400001) {
					//items.putKinahToDb(activePlayer, itemCountArray);
					Inventory kinah2 = new Inventory();
					kinah2.putKinahToDb(activePlayer, itemCountArray);
					kinah2.getKinahFromDb(activePlayer);
					int kinah = kinah2.getKinahCount();
					int uniquedeId = 0;
					sendPacket(new SM_INVENTORY_INFO(uniquedeId, 182400001, kinah, 1, 8));
					sendPacket(new SM_LOOT_STATUS(uniquedeId,3));
				} else {
					items.putItemToDb(activePlayer, itemIdArray, itemCountArray);
					items.getLastUniqueIdFromDb();
					newItemUniqueId = items.getnewItemUniqueIdValue();

					sendPacket(new SM_INVENTORY_INFO(newItemUniqueId, itemIdArray, itemCountArray, 1, 8));
					sendPacket(new SM_LOOT_STATUS(newItemUniqueId,3));
				}
				arrayLenght--;
				a++;
			}
			
		} else {
				//todo show SM_INVENTORY_IS_FULL packet or smth.
		}
	}
}