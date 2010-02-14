/*
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
package com.aionemu.gameserver.services;

import java.sql.Timestamp;
import java.util.List;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.CacheConfig;
import com.aionemu.gameserver.configs.Config;
import com.aionemu.gameserver.controllers.EffectController;
import com.aionemu.gameserver.controllers.PlayerController;
import com.aionemu.gameserver.dao.AbyssRankDAO;
import com.aionemu.gameserver.dao.BlockListDAO;
import com.aionemu.gameserver.dao.FriendListDAO;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dao.PlayerAppearanceDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.PlayerMacrossesDAO;
import com.aionemu.gameserver.dao.PlayerSettingsDAO;
import com.aionemu.gameserver.dao.PlayerSkillListDAO;
import com.aionemu.gameserver.dao.PlayerTitleListDAO;
import com.aionemu.gameserver.dao.QuestListDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.PlayerInitialData.LocationData;
import com.aionemu.gameserver.dataholders.PlayerInitialData.PlayerCreationData;
import com.aionemu.gameserver.dataholders.PlayerInitialData.PlayerCreationData.ItemType;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.MacroList;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerAppearance;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Storage;
import com.aionemu.gameserver.model.gameobjects.player.StorageType;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerLifeStats;
import com.aionemu.gameserver.model.gameobjects.stats.listeners.TitleChangeListener;
import com.aionemu.gameserver.model.group.PlayerGroup;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.model.legion.LegionMember;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.clientpackets.CM_ENTER_WORLD;
import com.aionemu.gameserver.network.aion.clientpackets.CM_QUIT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGIONMEMBER_INFO;
import com.aionemu.gameserver.skillengine.SkillLearnService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.collections.cachemap.CacheMap;
import com.aionemu.gameserver.utils.collections.cachemap.CacheMapFactory;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.utils.idfactory.IDFactoryAionObject;
import com.aionemu.gameserver.world.KnownList;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;
import com.google.inject.Inject;

/**
 * 
 * This class is designed to do all the work related with loading/storing players.<br>
 * Same with storing, {@link #storePlayer(com.aionemu.gameserver.model.gameobjects.player.Player)} stores all player
 * data like appearance, items, etc...
 * 
 * @author SoulKeeper, Saelya
 */
public class PlayerService
{
	private CacheMap<Integer, Player>	playerCache	= CacheMapFactory.createSoftCacheMap("Player", "player");

	@SuppressWarnings("unused")
	private IDFactory					aionObjectsIDFactory;
	private World						world;
	private ItemService					itemService;
	private LegionService				legionService;

	@Inject
	public PlayerService(@IDFactoryAionObject IDFactory aionObjectsIDFactory, World world, ItemService itemService,
		LegionService legionService)
	{
		this.aionObjectsIDFactory = aionObjectsIDFactory;
		this.world = world;
		this.itemService = itemService;
		this.legionService = legionService;
	}

	/**
	 * Checks if name is already taken or not
	 * 
	 * @param name
	 *            character name
	 * @return true if is free, false in other case
	 */
	public boolean isFreeName(String name)
	{
		return !DAOManager.getDAO(PlayerDAO.class).isNameUsed(name);
	}

	/**
	 * Checks if a name is valid. It should contain only english letters
	 * 
	 * @param name
	 *            character name
	 * @return true if name is valid, false overwise
	 */
	public boolean isValidName(String name)
	{
		return Config.CHAR_NAME_PATTERN.matcher(name).matches();
	}

	/**
	 * Stores newly created player
	 * 
	 * @param player
	 *            player to store
	 * @return true if character was successful saved.
	 */
	public boolean storeNewPlayer(Player player, String accountName, int accountId)
	{
		return DAOManager.getDAO(PlayerDAO.class).saveNewPlayer(player.getCommonData(), accountId, accountName)
			&& DAOManager.getDAO(PlayerAppearanceDAO.class).store(player)
			&& DAOManager.getDAO(PlayerSkillListDAO.class).storeSkills(player)
			&& DAOManager.getDAO(InventoryDAO.class).store(player)
			&& DAOManager.getDAO(PlayerTitleListDAO.class).storeTitles(player);
	}

	/**
	 * Stores player data into db
	 * 
	 * @param player
	 */
	public void storePlayer(Player player)
	{
		DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
		DAOManager.getDAO(InventoryDAO.class).store(player);
		DAOManager.getDAO(PlayerSkillListDAO.class).storeSkills(player);
		DAOManager.getDAO(PlayerSettingsDAO.class).saveSettings(player);
		DAOManager.getDAO(QuestListDAO.class).store(player.getObjectId(), player.getQuestStateList());
		DAOManager.getDAO(PlayerTitleListDAO.class).storeTitles(player);
		DAOManager.getDAO(AbyssRankDAO.class).storeAbyssRank(player);
	}

	/**
	 * Returns the player with given objId (if such player exists)
	 * 
	 * @param playerObjId
	 * @return
	 */
	public Player getPlayer(int playerObjId)
	{
		Player player = playerCache.get(playerObjId);
		if(player != null)
			return player;

		PlayerCommonData pcd = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(playerObjId, world);
		PlayerAppearance appereance = DAOManager.getDAO(PlayerAppearanceDAO.class).load(playerObjId);
		MacroList macroses = DAOManager.getDAO(PlayerMacrossesDAO.class).restoreMacrosses(playerObjId);

		player = new Player(new PlayerController(), pcd, appereance);

		LegionMember legionMember = legionService.getLegionMember(player);
		// LegionMember legionMember = DAOManager.getDAO(LegionMemberDAO.class).loadLegionMember(player, legionService,
		// this);
		if(legionMember != null)
		{
			player.setLegionMember(legionMember);
		}

		player.setMacroList(macroses);

		player.setSkillList(DAOManager.getDAO(PlayerSkillListDAO.class).loadSkillList(playerObjId));
		player.setKnownlist(new KnownList(player));
		player.setFriendList(DAOManager.getDAO(FriendListDAO.class).load(player, world));
		player.setBlockList(DAOManager.getDAO(BlockListDAO.class).load(player, world));
		player.setTitleList(DAOManager.getDAO(PlayerTitleListDAO.class).loadTitleList(playerObjId));

		DAOManager.getDAO(PlayerSettingsDAO.class).loadSettings(player);
		DAOManager.getDAO(AbyssRankDAO.class).loadAbyssRank(player);

		player.setPlayerStatsTemplate(DataManager.PLAYER_STATS_DATA.getTemplate(player));

		player.setGameStats(new PlayerGameStats(DataManager.PLAYER_STATS_DATA, player));
		player.setLifeStats(new PlayerLifeStats(player, player.getPlayerStatsTemplate().getMaxHp(), player
			.getPlayerStatsTemplate().getMaxMp()));
		player.setEffectController(new EffectController(player));

		// update passive stats after effect controller and stats are initialized
		player.getController().updatePassiveStats();

		player.setQuestStateList(DAOManager.getDAO(QuestListDAO.class).load(player));
		player.setStorage(DAOManager.getDAO(InventoryDAO.class).load(player, StorageType.CUBE), StorageType.CUBE);
		player.setStorage(DAOManager.getDAO(InventoryDAO.class).load(player, StorageType.REGULAR_WAREHOUSE),
			StorageType.REGULAR_WAREHOUSE);
		player.setStorage(DAOManager.getDAO(InventoryDAO.class).load(player, StorageType.ACCOUNT_WAREHOUSE),
			StorageType.ACCOUNT_WAREHOUSE);

		itemService.loadItemStones(player);

		// if kinah was deleted by some reason it should be restored with 0 count
		if(player.getStorage(StorageType.CUBE.getId()).getKinahItem() == null)
		{
			Item kinahItem = itemService.newItem(182400001, 0);
			player.getStorage(StorageType.CUBE.getId()).onLoadHandler(kinahItem);
		}

		if(player.getStorage(StorageType.ACCOUNT_WAREHOUSE.getId()).getKinahItem() == null)
		{
			Item kinahItem = itemService.newItem(182400001, 0);
			kinahItem.setItemLocation(StorageType.ACCOUNT_WAREHOUSE.getId());
			player.getStorage(StorageType.ACCOUNT_WAREHOUSE.getId()).onLoadHandler(kinahItem);
		}

		if(player.getCommonData().getTitleId() > 0)
		{
			TitleChangeListener.onTitleChange(player.getGameStats(), player.getCommonData().getTitleId(), true);
		}

		if(CacheConfig.CACHE_PLAYERS)
			playerCache.put(playerObjId, player);

		return player;
	}

	/**
	 * This method is used for creating new players
	 * 
	 * @param playerCommonData
	 * @param playerAppearance
	 * @return
	 */
	public Player newPlayer(PlayerCommonData playerCommonData, PlayerAppearance playerAppearance)
	{
		// TODO values should go from template
		LocationData ld = DataManager.PLAYER_INITIAL_DATA.getSpawnLocation(playerCommonData.getRace());

		WorldPosition position = world.createPosition(ld.getMapId(), ld.getX(), ld.getY(), ld.getZ(), ld.getHeading());

		playerCommonData.setPosition(position);

		Player newPlayer = new Player(new PlayerController(), playerCommonData, playerAppearance);

		// Starting skills
		SkillLearnService.addNewSkills(newPlayer, true);

		// Starting items
		PlayerCreationData playerCreationData = DataManager.PLAYER_INITIAL_DATA.getPlayerCreationData(playerCommonData
			.getPlayerClass());

		List<ItemType> items = playerCreationData.getItems();

		Storage playerInventory = new Storage(newPlayer, StorageType.CUBE);
		Storage regularWarehouse = new Storage(newPlayer, StorageType.REGULAR_WAREHOUSE);
		Storage accountWarehouse = new Storage(newPlayer, StorageType.ACCOUNT_WAREHOUSE);
		newPlayer.setStorage(playerInventory, StorageType.CUBE);
		newPlayer.setStorage(regularWarehouse, StorageType.REGULAR_WAREHOUSE);
		newPlayer.setStorage(accountWarehouse, StorageType.ACCOUNT_WAREHOUSE);

		for(ItemType itemType : items)
		{
			int itemId = itemType.getTemplate().getItemId();
			Item item = itemService.newItem(itemId, itemType.getCount());
			if(item == null)
				continue;

			// When creating new player - all equipment that has slot values will be equipped
			// Make sure you will not put into xml file more items than possible to equip.
			ItemTemplate itemTemplate = item.getItemTemplate();

			if(itemTemplate.isArmor() || itemTemplate.isWeapon())
			{
				item.setEquipped(true);
				List<ItemSlot> itemSlots = ItemSlot.getSlotsFor(itemTemplate.getItemSlot());
				item.setEquipmentSlot(itemSlots.get(0).getSlotIdMask());
			}

			playerInventory.onLoadHandler(item);
		}

		return newPlayer;
	}

	/**
	 * This method is called just after player logged in to the game.<br>
	 * <br>
	 * <b><font color='red'>NOTICE: </font> This method called only from {@link CM_ENTER_WORLD} and must not be called
	 * from anywhere else.</b>
	 * 
	 * @param player
	 */
	public void playerLoggedIn(Player player)
	{
		player.getCommonData().setOnline(true);
		DAOManager.getDAO(PlayerDAO.class).onlinePlayer(player, true);
		player.onLoggedIn();
	}

	/**
	 * This method is called when player leaves the game, which includes just two cases: either player goes back to char
	 * selection screen or it's leaving the game [closing client].<br>
	 * <br>
	 * 
	 * <b><font color='red'>NOTICE: </font> This method is called only from {@link AionConnection} and {@link CM_QUIT}
	 * and must not be called from anywhere else</b>
	 * 
	 * @param player
	 */
	public void playerLoggedOut(Player player)
	{
		player.onLoggedOut();

		if(player.getLifeStats().isAlreadyDead())
			player.getController().moveToBindLocation(false);

		player.getCommonData().setOnline(false);
		player.getCommonData().setLastOnline(new Timestamp(System.currentTimeMillis()));

		player.getController().delete();
		player.setClientConnection(null);
		DAOManager.getDAO(PlayerDAO.class).onlinePlayer(player, false);

		// TODO this is a temprorary solution till GroupService will be introduced
		PlayerGroup playerGroup = player.getPlayerGroup();
		if(playerGroup != null)
			playerGroup.removePlayerFromGroup(player);

		if(player.isLegionMember())
		{
			for(Player onlineLegionMember : player.getLegionMember().getLegion().getOnlineLegionMembers(world))
			{
				if(onlineLegionMember.getObjectId() != player.getObjectId())
				{
					// TODO: Get proper packet
					PacketSendUtility.broadcastPacket(onlineLegionMember, new SM_LEGIONMEMBER_INFO(player), true);
				}
			}
		}
		storePlayer(player);
	}

	/**
	 * Cancel Player deletion process if its possible.
	 * 
	 * @param accData
	 *            PlayerAccountData
	 * 
	 * @return True if deletion was successful canceled.
	 */
	public boolean cancelPlayerDeletion(PlayerAccountData accData)
	{
		if(accData.getDeletionDate() == null)
			return true;

		if(accData.getDeletionDate().getTime() > System.currentTimeMillis())
		{
			accData.setDeletionDate(null);
			storeDeletionTime(accData);
			return true;
		}
		return false;
	}

	/**
	 * Starts player deletion process if its possible. If deletion is possible character should be deleted after 5
	 * minutes.
	 * 
	 * @param accData
	 *            PlayerAccountData
	 */
	public void deletePlayer(PlayerAccountData accData)
	{
		if(accData.getDeletionDate() != null)
			return;

		accData.setDeletionDate(new Timestamp(System.currentTimeMillis() + 5 * 60 * 1000));
		storeDeletionTime(accData);
	}

	/**
	 * Completely removes player from database
	 * 
	 * @param playerId
	 *            id of player to delete from db
	 */
	void deletePlayerFromDB(int playerId)
	{
		DAOManager.getDAO(PlayerDAO.class).deletePlayer(playerId);
		DAOManager.getDAO(InventoryDAO.class).deletePlayerItems(playerId);
	}

	/**
	 * Updates deletion time in database
	 * 
	 * @param accData
	 *            PlayerAccountData
	 */
	private void storeDeletionTime(PlayerAccountData accData)
	{
		DAOManager.getDAO(PlayerDAO.class).updateDeletionTime(accData.getPlayerCommonData().getPlayerObjId(),
			accData.getDeletionDate());
	}

	/**
	 * 
	 * @param objectId
	 * @param creationDate
	 */
	public void storeCreationTime(int objectId, Timestamp creationDate)
	{
		DAOManager.getDAO(PlayerDAO.class).storeCreationTime(objectId, creationDate);
	}

	/**
	 * Add macro for player
	 * 
	 * @param player
	 *            Player
	 * @param macroOrder
	 *            Macro order
	 * @param macroXML
	 *            Macro XML
	 */
	public void addMacro(Player player, int macroOrder, String macroXML)
	{
		if(player.getMacroList().addMacro(macroOrder, macroXML))
		{
			DAOManager.getDAO(PlayerMacrossesDAO.class).addMacro(player.getObjectId(), macroOrder, macroXML);
		}
	}

	/**
	 * Remove macro with specified index from specified player
	 * 
	 * @param player
	 *            Player
	 * @param macroOrder
	 *            Macro order index
	 */
	public void removeMacro(Player player, int macroOrder)
	{
		if(player.getMacroList().removeMacro(macroOrder))
		{
			DAOManager.getDAO(PlayerMacrossesDAO.class).deleteMacro(player.getObjectId(), macroOrder);
		}
	}

	/**
	 * Gets a player ONLY if he is in the cache
	 * 
	 * @return Player or null if not cached
	 */
	public Player getCachedPlayer(int playerObjectId)
	{
		return playerCache.get(playerObjectId);
	}
}
