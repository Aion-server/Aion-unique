/**
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.gameserver.network.aion.clientpackets;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.model.gameobjects.player.ItemList;
import java.util.Random;
import com.aionemu.gameserver.configs.Config;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.account.AccountTime;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.network.aion.serverpackets.unk.*;
import com.aionemu.gameserver.services.PlayerService;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;
import org.apache.log4j.Logger;

/**
 * In this packets aion client is asking if given char [by oid] may login into game [ie start playing].
 * 
 * @author -Nemesiss-, Avol, Dominion
 * 
 */
public class CM_ENTER_WORLD extends AionClientPacket
{

	private static final Logger	log	= Logger.getLogger(CM_ENTER_WORLD.class);
	/**
	 * Object Id of player that is entering world
	 */
	private int				objectId;
	@Inject
	private World			world;
	@Inject
	private PlayerService	playerService;

	/**
	 * Constructs new instance of <tt>CM_ENTER_WORLD </tt> packet
	 * 
	 * @param opcode
	 */
	public CM_ENTER_WORLD(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		objectId = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		AionConnection client = getConnection();
		PlayerAccountData playerAccData = client.getAccount().getPlayerAccountData(objectId);

		if(playerAccData == null)
		{
			//Somebody wanted to login on character that is not at his account
			return;
		}
		
		Player player = playerService.getPlayer(objectId);
		
		if(player != null && client.setActivePlayer(player))
		{
			player.setClientConnection(client);
			player.setProtectionActive(true);
			/*
			 * Store player into World.
			 */
			world.storeObject(player);

			sendPacket(new SM_SKILL_LIST(player));

			// sendPacket(new SM_UNK91());
			// sendPacket(new SM_UNKC7());
			// sendPacket(new SM_UNKC8());

			/*
			 * Needed
			 */
			client.sendPacket(new SM_ENTER_WORLD_CHECK());

			// sendPacket(new SM_UNK60());
			// sendPacket(new SM_UNK17());
			// sendPacket(new SM_UNK5E());

			
//////////////////////////////LOAD INVENTORY FROM DB//////////////////////////////////////////////////////////////
			Player player2 = getConnection().getActivePlayer();
			int activePlayer = player2.getObjectId();





			//items

			Inventory items = new Inventory();
			items.getInventoryFromDb(activePlayer);
			int totalItemsCount = items.getItemsCount();
			ItemList itemName = new ItemList();
			Inventory inventory = new Inventory();
			
			int slot;


			int row = 0;
			if (totalItemsCount==0) {
				sendPacket(new SM_INVENTORY_INFO(1234235, 169300001, 20, 1, 8)); // give item	
			}
			while (totalItemsCount > 0) {
				sendPacket(new SM_INVENTORY_INFO(items.getItemUniqueIdArray(row), items.getItemIdArray(row), items.getItemCountArray(row), 1, 0)); // give item
				totalItemsCount = totalItemsCount-1;
				row+=1;
			} 
			Inventory equipedItems = new Inventory();
			equipedItems.getEquipedItemsFromDb(activePlayer);
			int totalEquipedItemsCount = equipedItems.getEquipedItemsCount();


			row = 0;
			while (totalEquipedItemsCount > 0) {
				sendPacket(new SM_UPDATE_ITEM(equipedItems.getEquipedItemSlotArray(row), 0, equipedItems.getEquipedItemUniqueIdArray(row)));
				totalEquipedItemsCount = totalEquipedItemsCount-1;
				row+=1;
			}
				
			



			// kinah

			Inventory kinah2 = new Inventory();
			kinah2.getKinahFromDb(activePlayer);
			int kinah = kinah2.getKinahCount();
			int uniquedeId = 0;
			sendPacket(new SM_INVENTORY_UPDATE(uniquedeId, 182400001, kinah));

			

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// sendPacket(new SM_UNKD3());

			/*
			 * Needed
			 */
			client.sendPacket(new SM_STATS_INFO(player));

			// sendPacket(new SM_UNKE1());
			sendPacket(new SM_MACRO_LIST(player));
			

			sendPacket(new SM_GAME_TIME());
			sendPacket(SM_SYSTEM_MESSAGE.REMAINING_PLAYING_TIME(12043));

			/**
			 * Player's accumulated time; params are: - 0h 12m - play time (1st and 2nd string-params) - 1h 26m - rest
			 * time (3rd and 4th string-params)
			 */
			AccountTime accountTime = getConnection().getAccount().getAccountTime();

			sendPacket(SM_SYSTEM_MESSAGE.ACCUMULATED_TIME(
															accountTime .getAccumulatedOnlineHours(), 
															accountTime.getAccumulatedOnlineMinutes(),
															accountTime.getAccumulatedRestHours(),
															accountTime.getAccumulatedRestMinutes())
														);

			/*
			 * Needed
			 */
			sendPacket(new SM_UNKF5(player));

			// sendPacket(new SM_UNK32());
			// sendPacket(new SM_UNK15());
			// sendPacket(new SM_UNKC6());
			// sendPacket(new SM_UNK66());
			// sendPacket(new SM_UNKCB());
			// sendPacket(new SM_UNK64());
			// sendPacket(new SM_UNKE7());
			// sendPacket(new SM_UNK0A());
			// sendPacket(new SM_UNK97());
			// sendPacket(new SM_UNK8D());
			
			sendPacket(new SM_MESSAGE(0, null, "Welcome to " + Config.SERVER_NAME
				+ " server\nPowered by aion-unique software\ndeveloped by www.aion-unique.com team.\nCopyright 2009", null,
				ChatType.ANNOUNCEMENTS));
			
			playerService.playerLoggedIn(player);
		}
		else
		{
			// TODO this is an client error - inform client.
		}
	}
}
