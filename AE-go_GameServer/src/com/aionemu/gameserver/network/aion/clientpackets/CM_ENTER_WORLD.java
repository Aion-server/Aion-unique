/*
 * This file is part of aion-unique <aion-unique.org>.
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

import java.util.List;

import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.account.AccountTime;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.Storage;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABYSS_RANK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CHANNEL_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ENTER_WORLD_CHECK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GAME_TIME;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INFLUENCE_RATIO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MACRO_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_ID;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_SPAWN;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PRICES;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RECIPE_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TITLE_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UI_SETTINGS;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNK5E;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNK7B;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.GroupService;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.services.PlayerService;
import com.aionemu.gameserver.services.PunishmentService;
import com.aionemu.gameserver.services.TeleportService;
import com.aionemu.gameserver.utils.rates.Rates;
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
	/**
	 * Object Id of player that is entering world
	 */
	private int					objectId;
	@Inject
	private World				world;
	@Inject
	private PlayerService		playerService;
	@Inject
	private LegionService		legionService;
	@Inject
	private GroupService		groupService;
	@Inject
	private TeleportService		teleportService;
	@Inject
	private PunishmentService	punishmentService;

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
			// Somebody wanted to login on character that is not at his account
			return;
		}

		Player player = playerService.getPlayer(objectId);

		if(player != null && client.setActivePlayer(player))
		{
			player.setClientConnection(client);
			player.getController().startProtectionActiveTask();
			/*
			 * Store player into World.
			 */
			world.storeObject(player);

			sendPacket(new SM_SKILL_LIST(player));

			// sendPacket(new SM_UNK91());
			// sendPacket(new SM_UNKC7());
			// sendPacket(new SM_UNKC8());

			client.sendPacket(new SM_QUEST_LIST(player));

			client.sendPacket(new SM_RECIPE_LIST(player.getRecipeList().getRecipeList()));

			/*
			 * Needed
			 */
			client.sendPacket(new SM_ENTER_WORLD_CHECK());

			byte[] uiSettings = player.getPlayerSettings().getUiSettings();
			byte[] shortcuts = player.getPlayerSettings().getShortcuts();

			if(uiSettings != null)
				client.sendPacket(new SM_UI_SETTINGS(uiSettings, 0));

			if(shortcuts != null)
				client.sendPacket(new SM_UI_SETTINGS(shortcuts, 1));

			// sendPacket(new SM_UNK60());
			// sendPacket(new SM_UNK17());
			sendPacket(new SM_UNK5E());

			// Cubesize limit set in inventory.
			int cubeSize = player.getCubeSize();
			player.getInventory().setLimit(27 + cubeSize * 9);

			// TODO no need to load items here - inventory will be populated at startup
			// will be removed next time

			// items
			Storage inventory = player.getInventory();
			List<Item> equipedItems = player.getEquipment().getEquippedItems();
			if(equipedItems.size() != 0)
			{
				client.sendPacket(new SM_INVENTORY_INFO(player.getEquipment().getEquippedItems(), cubeSize));
			}

			List<Item> unequipedItems = inventory.getAllItems();
			int itemsSize = unequipedItems.size();

			if(itemsSize != 0)
			{
				int index = 0;
				while(index + 10 < itemsSize)
				{
					client.sendPacket(new SM_INVENTORY_INFO(unequipedItems.subList(index, index + 10), cubeSize));
					index += 10;
				}
				client.sendPacket(new SM_INVENTORY_INFO(unequipedItems.subList(index, itemsSize), cubeSize));
			}

			client.sendPacket(new SM_INVENTORY_INFO());
			client.sendPacket(new SM_CHANNEL_INFO(player.getPosition())); // ?? unknwon
			// sendPacket(new SM_UNKD3());

			/*
			 * Needed
			 */
			client.sendPacket(new SM_STATS_INFO(player));
			sendPacket(new SM_UNK7B());

			teleportService.sendSetBindPoint(player);
			
			// sendPacket(new SM_UNKE1());
			sendPacket(new SM_MACRO_LIST(player));

			sendPacket(new SM_GAME_TIME());
			player.updateNearbyQuests();

			sendPacket(new SM_TITLE_LIST(player));

			/**
			 * Player's accumulated time; params are: - 0h 12m - play time (1st and 2nd string-params) - 1h 26m - rest
			 * time (3rd and 4th string-params)
			 */
			AccountTime accountTime = getConnection().getAccount().getAccountTime();

			sendPacket(SM_SYSTEM_MESSAGE.ACCUMULATED_TIME(accountTime.getAccumulatedOnlineHours(), accountTime
				.getAccumulatedOnlineMinutes(), accountTime.getAccumulatedRestHours(), accountTime
				.getAccumulatedRestMinutes()));

			/*
			 * Needed
			 */
			sendPacket(new SM_PLAYER_SPAWN(player));
			sendPacket(new SM_EMOTION_LIST());
			sendPacket(new SM_INFLUENCE_RATIO());
			sendPacket(new SM_PRICES());
			sendPacket(new SM_PLAYER_ID(player));
			sendPacket(new SM_ABYSS_RANK(player.getAbyssRank()));

			sendPacket(new SM_MESSAGE(0, null, "Welcome to " + GSConfig.SERVER_NAME
				+ " server\nPowered by aion-unique software\ndeveloped by www.aion-unique.org team.\nCopyright 2010",
				ChatType.ANNOUNCEMENTS));

			if(player.isInPrison())
				punishmentService.updatePrisonStatus(player);

			if(player.isLegionMember())
				legionService.legionMemberOnLogin(player);

			playerService.playerLoggedIn(player);

			if(player.isInGroup())
				groupService.groupMemberOnLogin(player);

			player.setRates(Rates.getRatesFor(client.getAccount().getMembership()));

			ClassChangeService.showClassChangeDialog(player);
		}
		else
		{
			// TODO this is an client error - inform client.
		}
	}

}