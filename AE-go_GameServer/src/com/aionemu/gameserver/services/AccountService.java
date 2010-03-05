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

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.CacheConfig;
import com.aionemu.gameserver.controllers.factory.ControllerFactory;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dao.LegionMemberDAO;
import com.aionemu.gameserver.dao.PlayerAppearanceDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dataholders.PlayerInitialData;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.AccountTime;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerAppearance;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Storage;
import com.aionemu.gameserver.model.gameobjects.player.StorageType;
import com.aionemu.gameserver.model.legion.LegionMember;
import com.aionemu.gameserver.utils.collections.cachemap.CacheMap;
import com.aionemu.gameserver.utils.collections.cachemap.CacheMapFactory;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * This class is a front-end for daos and it's responsibility is to retrieve the Account objects
 * 
 * @author Luno
 * 
 */
public class AccountService
{
	private static final Logger			log			= Logger.getLogger(AccountService.class);

	private CacheMap<Integer, Account>	accountsMap	= CacheMapFactory.createSoftCacheMap("Account", "account");

	@Inject
	private World						world;
	@Inject
	private PlayerService				playerService;
	@Inject
	private ControllerFactory			controllerFactory;
	@Inject
	private LegionService				legionService;
	@Inject
	private PlayerInitialData			playerInitialData;

	/**
	 * Returns {@link Account} object that has given id.
	 * 
	 * @param accountId
	 * @param accountTime
	 * @param accountName
	 * @param accessLevel
	 * @param membership
	 * @return Account
	 */
	public Account getAccount(int accountId, String accountName, AccountTime accountTime, byte accessLevel,
		byte membership)
	{
		log.debug("[AS] request for account: " + accountId);

		Account account = accountsMap.get(accountId);
		if(account == null)
		{
			account = loadAccount(accountId);

			if(CacheConfig.CACHE_ACCOUNTS)
				accountsMap.put(accountId, account);
		}

		account.setName(accountName);
		account.setAccountTime(accountTime);
		account.setAccessLevel(accessLevel);
		account.setMembership(membership);

		removeDeletedCharacters(account);

		return account;
	}

	/**
	 * Removes from db characters that should be deleted (their deletion time has passed).
	 * 
	 * @param account
	 */
	private void removeDeletedCharacters(Account account)
	{
		/* Removes chars that should be removed */
		Iterator<PlayerAccountData> it = account.iterator();
		while(it.hasNext())
		{
			PlayerAccountData pad = it.next();
			int deletionTime = pad.getDeletionTimeInSeconds() * 1000;
			if(deletionTime != 0 && deletionTime <= System.currentTimeMillis())
			{
				it.remove();
				playerService.deletePlayerFromDB(pad.getPlayerCommonData().getPlayerObjId());
			}
		}
	}

	/**
	 * Loads account data and returns.
	 * 
	 * @param accountId
	 * @param accountName
	 * @return
	 */
	private Account loadAccount(int accountId)
	{
		Account account = new Account(accountId);

		PlayerDAO playerDAO = DAOManager.getDAO(PlayerDAO.class);
		PlayerAppearanceDAO appereanceDAO = DAOManager.getDAO(PlayerAppearanceDAO.class);

		List<Integer> playerOids = playerDAO.getPlayerOidsOnAccount(accountId);

		for(int playerOid : playerOids)
		{
			PlayerCommonData playerCommonData = playerDAO.loadPlayerCommonData(playerOid, world, playerInitialData);
			PlayerAppearance appereance = appereanceDAO.load(playerOid);
			Player player = new Player(controllerFactory.createPlayerController(), playerCommonData, appereance);

			Storage inventory = DAOManager.getDAO(InventoryDAO.class).loadStorage(player, StorageType.CUBE);
			Equipment equipment = DAOManager.getDAO(InventoryDAO.class).loadEquipment(player);
			LegionMember legionMember = DAOManager.getDAO(LegionMemberDAO.class).loadLegionMember(player.getObjectId(),
				legionService);
			PlayerAccountData acData = new PlayerAccountData(playerCommonData, appereance, inventory, equipment,
				legionMember);
			playerDAO.setCreationDeletionTime(acData);

			account.addPlayerAccountData(acData);
		}

		return account;
	}
}
