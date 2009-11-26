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


import java.util.List;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.configs.Config;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.PlayerInitialData.LocationData;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.account.AccountTime;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ENTER_WORLD_CHECK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GAME_TIME;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MACRO_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTLIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UI_SETTINGS;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNK5E;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNK7B;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKC9;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKDC;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKF5;
import com.aionemu.gameserver.services.PlayerService;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * In this packets aion client is asking if given char [by oid] may login into game [ie start playing].
 * 
 * @author -Nemesiss-, Avol
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
			client.sendPacket(new SM_QUESTLIST());
			
			byte[] uiSettings = player.getUiSettings();
			byte[] shortcuts = player.getShortcuts();
			
			if(uiSettings != null)
				client.sendPacket(new SM_UI_SETTINGS(uiSettings, 0));
			
			if(shortcuts != null)
				client.sendPacket(new SM_UI_SETTINGS(shortcuts, 1));;
			
			LocationData locationData = DataManager.PLAYER_INITIAL_DATA.getSpawnLocation(player.getCommonData().getRace());
			if((player.getPosition().getX() == locationData.getX())&&(player.getPosition().getY() == locationData.getY()))
			{
				client.sendPacket(new SM_PLAY_MOVIE(player));
				
			}
			// sendPacket(new SM_UNK60());
			// sendPacket(new SM_UNK17());
			sendPacket(new SM_UNK5E());
			
			//Cubesize limit set in inventory.
			int cubeSize = player.getCubeSize();
			player.getInventory().setLimit(27 + cubeSize * 9);
			
			//TODO no need to load items here - inventory will be populated at startup
			// will be removed next time
			
			//items
			Inventory inventory = player.getInventory();	
			
			//TODO send <= 10 items at once only
			List<Item> equipedItems = inventory.getEquippedItems();
			if(equipedItems.size() != 0)
			{
				client.sendPacket(new SM_INVENTORY_INFO(inventory.getEquippedItems(), player.getCubeSize()));
			}
			
			List<Item> unequipedItems = inventory.getUnquippedItems();
			if(unequipedItems.size() != 0)
			{
				client.sendPacket(new SM_INVENTORY_INFO(inventory.getUnquippedItems(), player.getCubeSize()));
			}
			
			client.sendPacket(new SM_INVENTORY_INFO()); 
			client.sendPacket(new SM_UNKDC()); //?? unknwon
			//sendPacket(new SM_UNKD3());
			
			/*
			 * Needed
			 */
			client.sendPacket(new SM_STATS_INFO(player));
			sendPacket(new SM_UNK7B());
			// sendPacket(new SM_UNKE1());
			sendPacket(new SM_MACRO_LIST(player));
			

			sendPacket(new SM_GAME_TIME());
			sendPacket(new SM_UNKC9());
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
			sendPacket(new SM_EMOTION_LIST());
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
