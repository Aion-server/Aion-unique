package com.aionemu.loginserver.service;

import com.aionemu.commons.database.ThreadLocalTransactionService;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.loginserver.dao.AccountDAO;
import com.aionemu.loginserver.dao.AccountTimeDAO;
import com.aionemu.loginserver.model.Account;
import com.aionemu.loginserver.model.AccountTime;
import com.aionemu.loginserver.network.netty.handler.ClientChannelHandler;
import com.aionemu.loginserver.utils.AccountUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author: SoulKeeper
 */
@Service
public class AccountService {

	/**
	 * Logger
	 */
	private static final Logger log = Logger.getLogger(AccountService.class);

	/**
	 * TransactionService
	 */
	private final ThreadLocalTransactionService transactionService;

	/**
	 * Account time service, used to contorl account times
	 */
	private final AccountTimeService accountTimeService;

	/**
	 * DAOManager used to get daos
	 */
	private final DAOManager daoManager;

	@Autowired
	public AccountService(DAOManager daoManager, ThreadLocalTransactionService transactionService,
						  AccountTimeService accountTimeService) {
		this.daoManager = daoManager;
		this.transactionService = transactionService;
		this.accountTimeService = accountTimeService;
	}

	/**
	 * Loads account from database using specified name.
	 *
	 * @param name Account name
	 * @return return account object or null if not found
	 * @throws RuntimeException if something went wrong
	 */
	public Account loadAccount(String name) {
		Account account = daoManager.getDAO(AccountDAO.class).load(name);
		if (account != null) {
			account.setAccountTime(daoManager.getDAO(AccountTimeDAO.class).load(account.getId()));
		}

		return account;
	}

	/**
	 * Saves account in database
	 *
	 * @param account account to save
	 * @throws RuntimeException if something went wrong
	 */
	public void saveAccount(Account account) {

		if (account == null) {
			throw new IllegalArgumentException("account == null");
		}

		try {
			transactionService.beginTransaction();
			daoManager.getDAO(AccountDAO.class).save(account);
			daoManager.getDAO(AccountTimeDAO.class).save(account.getAccountTime());
		} catch (Exception e) {
			log.error("Some crap while saving account", e);
		}
		finally {
			transactionService.commitAndClose();
		}
	}

	/**
	 * Creates new account and stores it in DB. Returns account object in case of success or null if failed
	 *
	 * @param name	 account name
	 * @param password account password
	 * @return account object or null
	 */
	public Account createAccount(String name, String password) {
		String passwordHash = AccountUtils.encodePassword(password);
		Account account = new Account();

		account.setName(name);
		account.setPasswordHash(passwordHash);
		account.setAccessLevel((byte) 0);

		account.setAccountTime(new AccountTime());
		account.getAccountTime().setId(account.getId());

		try {
			saveAccount(account);
		} catch (Exception e) {
			// TODO: check SQL exception for message about constraint violation (duplicated name) and ignore it
			log.error("Failed to save account", e);
			return null;
		}

		return account;
	}

	/**
	 * Actions that should be done on account login
	 * @param account object that logged in
	 * @param channelHandler
	 */
	public void onLogin(Account account, ClientChannelHandler channelHandler){
		account.setLastIp(channelHandler.getIP());
		accountTimeService.updateOnLogin(account.getAccountTime());
		saveAccount(account);
	}

	/**
	 * Actions that should be done on logout
	 * @param account object that logged out
	 */
	public void onLogout(Account account){
		accountTimeService.updateOnLogout(account.getAccountTime());
		saveAccount(account);
	}
}
