package com.aionemu.loginserver.service;

import com.aionemu.commons.database.ThreadLocalTransactionService;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.loginserver.dao.AccountDAO;
import com.aionemu.loginserver.dao.AccountTimeDAO;
import com.aionemu.loginserver.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author: SoulKeeper
 */
@Service
public class AccountService {

	/**
	 * DAOManager used to get daos
	 */
	private final DAOManager daoManager;

	/**
	 * TransactionService
	 */
	private final ThreadLocalTransactionService transactionService;

	@Autowired
	public AccountService(DAOManager daoManager, ThreadLocalTransactionService transactionService) {
		this.daoManager = daoManager;
		this.transactionService = transactionService;
	}

	/**
	 * Loads account from database using specified name.
	 * @param name Account name
	 * @return return account object or null if not found
	 * @throws RuntimeException if something went wrong
	 */
	public Account loadAccount(String name){
		Account account = daoManager.getDAO(AccountDAO.class).load(name);
		if(account != null){
			account.setAccountTime(daoManager.getDAO(AccountTimeDAO.class).load(account.getId()));
		}

		return account;
	}

	/**
	 * Saves account in database
	 * @param account account to save
	 * @throws RuntimeException if something went wrong
	 */
	public void saveAccount(Account account){

		if(account == null){
			throw new IllegalArgumentException("account == null");
		}

		transactionService.beginTransaction();
		daoManager.getDAO(AccountDAO.class).save(account);
		daoManager.getDAO(AccountTimeDAO.class).save(account.getAccountTime());
		transactionService.commitAndClose();
	}
}
