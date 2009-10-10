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

import java.util.Random;
import org.apache.log4j.Logger;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_PLAYER_APPEARANCE;
import com.aionemu.gameserver.model.gameobjects.player.ItemList;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
/**
 * 
 * @author Avol
 * 
 */
public class CM_EQUIP_ITEM extends AionClientPacket
{
	private static final Logger	log	= Logger.getLogger(CM_EQUIP_ITEM.class);

	public int slotRead;
	public int slot;
	public int itemUniqueId;
	public int action;
	public CM_EQUIP_ITEM(int opcode)
	{
		super(opcode);
	}


	@Override
	protected void readImpl()
	{
		action = readC(); // 0/1 equip/unequip
		slotRead = readD();
		itemUniqueId = readD();
	}

	@Override
	protected void runImpl()
	{	


	/*	
	slots
	0 - inventory
	1 - main hand
	2 - off hand
	3 - if 2 handed sword. covers 2 slots.
	4 - head
	8 - jacket
	16 - glove
	32 - shoes 
	64 - Right Earring 
	128 - Left Earring
	256 - Right Ring
	512 - Left Right
	1024 - Necklace
	2048 - Pauldrons
	4096 - Legs
	8192 - Left Power Shard
	16384 - Right Power Shard
	32758 - Wings
	65536 - Waist
	131072 - Secondary Main Hand
	262144 - Secondary Off Hand
 	
	there are alot of other combinations like head+jacket+main hand etc.
	*/
	final Player activePlayer = getConnection().getActivePlayer();
	int activeplayer = activePlayer.getObjectId();

	Inventory inventory = new Inventory();

	inventory.getItemIdByUniqueItemId(itemUniqueId);
	int itemId = inventory.getItemId();

	ItemList itemName = new ItemList();

	itemName.getItemList(itemId);
	String slotName = itemName.getEquipmentSlots();
	
	char test = slotName.charAt(0);
	boolean isAnInt= test>='0' && test<='9';
	
	if (isAnInt){
		slot = Integer.parseInt(slotName);
		if (slot==5) {
			inventory.getIsEquipedFromDb(activeplayer, 1);
			int isEquiped = inventory.getIsEquiped();
			if (isEquiped == 1){
				slot = 2;
			} else {
				slot = 1;
			}
			// 1, 2 weapon
		}
		if (slot==6) {
			inventory.getIsEquipedFromDb(activeplayer, 8192);
			int isEquiped = inventory.getIsEquiped();
			if (isEquiped == 1){
				slot = 16384;
			} else {
				slot = 8192;
			}
			//8192, 16384 power shards
		}
		if (slot==7) {
			inventory.getIsEquipedFromDb(activeplayer, 256);
			int isEquiped = inventory.getIsEquiped();
			if (isEquiped == 1){
				slot = 512;
			} else {
				slot = 256;
			}
			// 256, 512 rings
		}
		if (slot==9) {
			inventory.getIsEquipedFromDb(activeplayer, 54);
			int isEquiped = inventory.getIsEquiped();
			if (isEquiped == 1){
				slot = 128;
			} else {
				slot = 64;
			}
			// 128, 64 earrings
		}
	} else {
		slot = 0;
	}
	



		inventory.getIsEquipedFromDb(activeplayer, slot);
		int isEquiped = inventory.getIsEquiped();
		int unequipItemUniqueId = inventory.getIsEquipedItemUniqueId();

		ItemList itemLevel = new ItemList();
		itemLevel.getItemList(inventory.getItemId());

		if (action==0) {
			if (isEquiped==1) {
				if (itemLevel.getLevel() <= activePlayer.getLevel()) {
					inventory.putIsEquipedToDb(unequipItemUniqueId, 0, 0);
					sendPacket(new SM_UPDATE_ITEM(0, 1, unequipItemUniqueId));
				
					inventory.putIsEquipedToDb(itemUniqueId, 1, slot);
					sendPacket(new SM_UPDATE_ITEM(slot, action, itemUniqueId));

					sendPacket(new SM_UPDATE_PLAYER_APPEARANCE(activeplayer));
				} 
				// show cannot equip packet or smth
			}


			if (isEquiped==0) {
				if (itemLevel.getLevel() <= activePlayer.getLevel()) {
					inventory.getItemIdByUniqueItemId(itemUniqueId);
					inventory.putIsEquipedToDb(itemUniqueId, 1, slot);
					sendPacket(new SM_UPDATE_ITEM(slot, action, itemUniqueId));
					sendPacket(new SM_UPDATE_PLAYER_APPEARANCE(activeplayer));
				}
				// show cannot equip packet or smth
			}
		} else if (action==1) {
			inventory.getInventoryFromDb(activeplayer);
			int totalItemsCount = inventory.getItemsCount();

			inventory.getEquipedItemsFromDb(activeplayer);
			int totalEquipedItemsCount = inventory.getEquipedItemsCount();

			//get item id by unique
			

			
			totalItemsCount = totalItemsCount - totalEquipedItemsCount;
			
			int cubes = 1;
			int cubesize = 27;
			int allowItemsCount = cubesize*cubes-1;

			if (totalItemsCount<=allowItemsCount) {
					inventory.putIsEquipedToDb(itemUniqueId, 0, 0);
					sendPacket(new SM_UPDATE_ITEM(0, 1, itemUniqueId));
					sendPacket(new SM_UPDATE_PLAYER_APPEARANCE(activeplayer));
				
			}
		}
		
	}
}
