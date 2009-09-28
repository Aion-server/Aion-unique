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
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE;

import java.util.Random;
/**
 * 
 * @author alexa026
 * 
 */
public class CM_LOOT_ITEM extends AionClientPacket
{
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
		//sendPacket(new SM_EMOTION(targetObjectId,36,0));
		Player player = getConnection().getActivePlayer();
		int activePlayer = player.getObjectId();
		int itemId = player.getGameStats().getItemId();
		int itemNameId = player.getGameStats().getItemNameId();
		int itemCount = 1;
		Inventory itemsDbOfPlayerCount = new Inventory(); // wrong
		//
		itemsDbOfPlayerCount.getInventoryFromDb(activePlayer);
		int totalItemsCount = itemsDbOfPlayerCount.getItemsCount();
		int cubes = 1;
		int cubesize = 27;
		int allowItemsCount = cubesize*cubes-1;
		if (totalItemsCount<=allowItemsCount){
			Inventory items = new Inventory();
			items.putItemToDb(activePlayer, itemId, itemNameId, itemCount);
			items.getDbItemsCountFromDb();
			int totalDbItemsCount = items.getDbItemsCount();
			int newItemUniqueId = totalDbItemsCount;
			sendPacket(new SM_INVENTORY_UPDATE(newItemUniqueId, itemId, itemNameId, itemCount)); // give item
		} else {
			//todo show SM_INVENTORY_FULL packet or smth.
		}
		
	}
}
