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
		int itemId = player.getGameStats().getItemId();
		int count = player.getGameStats().getItemCount();

		log.info(String.format("CM_LOOT_ITEM itemId: %s", itemId));
			
		if (itemId==182400001){
		 //Figure out packet to update kinah
			Random generator = new Random();
			int randomKinah = generator.nextInt(50)+1;
			int randomUniqueId = generator.nextInt(99999999)+generator.nextInt(99999999)+99999999+99999999; // To prevent replacement of other item.
		
			//calculate how much kinah to send

			Inventory kina = new Inventory();
			kina.getKinahFromDb(activePlayer);
			int kinah = kina.getKinahCount();
			int totalKinah = kinah + randomKinah;
			//kina.putKinahToDb(activePlayer, totalKinah);
			//sendPacket(new SM_UPDATE_ITEM(0, 2, 0));
		
		} else { 
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
}
