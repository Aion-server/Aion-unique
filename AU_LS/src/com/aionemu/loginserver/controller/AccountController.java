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

package com.aionemu.loginserver.controller;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.utils.NetworkUtils;
import com.aionemu.loginserver.GameServer;
import com.aionemu.loginserver.GameServerService;
import com.aionemu.loginserver.dao.AccountDAO;
import com.aionemu.loginserver.dao.AccountTimeDAO;
import com.aionemu.loginserver.model.Account;
import com.aionemu.loginserver.model.AccountTime;
import com.aionemu.loginserver.model.ReconnectingAccount;
import com.aionemu.loginserver.network.aion.AionAuthResponse;
import com.aionemu.loginserver.network.aion.SessionKey;
import com.aionemu.loginserver.network.aion.serverpackets.SM_UPDATE_SESSION;
import com.aionemu.loginserver.network.gameserver.serverpackets.SM_ACCOUNT_AUTH_RESPONSE;
import com.aionemu.loginserver.network.netty.handler.ClientChannelHandler;
import com.aionemu.loginserver.network.netty.handler.GameChannelHandler;
import com.aionemu.loginserver.network.netty.handler.ClientChannelHandler.State;
import com.aionemu.loginserver.service.AccountTimeService;
import com.aionemu.loginserver.utils.AccountUtils;

import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.channel.ChannelHandler;

/**
 * This class is resposible for controlling all account actions
 *
 * @author KID
 * @author SoulKeeper
 */
public class AccountController
{
	/**
	 * Map with accounts that are active on LoginServer or joined GameServer and are not authenticated yet.
	 */
	private static final Map<Integer, ClientChannelHandler>		accountsOnLS         = new HashMap<Integer,
	ClientChannelHandler>();

	/**
	 * Map with accounts that are reconnecting to LoginServer ie was joined GameServer.
	 */
	private static final Map<Integer, ReconnectingAccount>	reconnectingAccounts = new HashMap<Integer,
																					   ReconnectingAccount>();

	/**
	 * Removes account from list of connections
	 *
	 * @param account
	 *            account
	 */
	public static synchronized void removeAccountOnLS(Account account)
	{
		accountsOnLS.remove(account.getId());
	}

	/**
	 * This method is for answering GameServer question about account authentication on GameServer side.
	 *
	 * @param key
	 * @param gameChannelHandler
	 */
	public static synchronized void checkAuth(SessionKey key, GameChannelHandler gameChannelHandler)
	{
		ClientChannelHandler clientChannelHandler = accountsOnLS.get(key.accountId);

		if(clientChannelHandler != null)
		{
			if(clientChannelHandler.getSessionKey().checkSessionKey(key))
			{
				/**
				 * account is successful logged in on gs remove it from here
				 */
				accountsOnLS.remove(key.accountId);

				GameServer gsi = gameChannelHandler.getGameServerInfo();
				Account			acc = clientChannelHandler.getAccount();

				/**
				 * Add account to accounts on GameServer list and update accounts last server
				 */
				gsi.addAccountToGameServer(acc);

				acc.setLastServer(gsi.getId());
				getAccountDAO().updateLastServer(acc.getId(), acc.getLastServer());

				/**
				 * Send response to GameServer
				 */
				gameChannelHandler.sendPacket(new SM_ACCOUNT_AUTH_RESPONSE(key.accountId, true, acc.getName()));
			}
		}
		else
		{
			gameChannelHandler.sendPacket(new SM_ACCOUNT_AUTH_RESPONSE(key.accountId, false, null));
		}
	}

	/**
	 * Add account to reconnectionAccount list
	 *
	 * @param acc
	 */
	public static synchronized void addReconnectingAccount(ReconnectingAccount acc)
	{
		reconnectingAccounts.put(acc.getAccount().getId(), acc);
	}

	/**
	 * Check if reconnecting account may auth.
	 *
	 * @param accountId
	 *            id of account
	 * @param loginOk
	 *            loginOk
	 * @param reconnectKey
	 *            reconnect key
	 * @param channelHandler
	 *            aion client
	 */
	public static synchronized void authReconnectingAccount(int accountId, int loginOk, int reconnectKey,
			ClientChannelHandler channelHandler)
	{
		ReconnectingAccount	reconnectingAccount = reconnectingAccounts.remove(accountId);

		if(reconnectingAccount != null && reconnectingAccount.getReconnectionKey() == reconnectKey)
		{
			Account	acc = reconnectingAccount.getAccount();

			channelHandler.setAccount(acc);
			accountsOnLS.put(acc.getId(), channelHandler);
			channelHandler.setState(State.AUTHED_LOGIN);
			channelHandler.setSessionKey(new SessionKey(channelHandler.getAccount()));
			channelHandler.sendPacket(new SM_UPDATE_SESSION(channelHandler.getSessionKey()));
		}
		else
		{
			channelHandler.close( /* new SM_UPDATE_SESSION, */true);
		}
	}

	/**
	 * Tries to authentificate account.<br>
	 * If success returns {@link AionAuthResponse#AUTHED} and sets account object to connection.<br>
	 *
	 * If {@link com.aionemu.loginserver.configs.Config#ACCOUNT_AUTO_CREATION} is enabled - creates new account.<br>
	 *
	 * @param name
	 *            name of account
	 * @param password
	 *            password of account
	 * @param connection
	 *            connection for account
	 * @return Response with error code
	 */
	public static AionAuthResponse login(String name, String password, ClientChannelHandler channelHandler)
	{
		Account account = loadAccount(name);

		// Try to create new account
		if(account == null && Config.ACCOUNT_AUTO_CREATION)
		{
			account = createAccount(name, password);
		}

		// If account not found and not created
		if(account == null)
		{
			return AionAuthResponse.INVALID_PASSWORD;
		}

		// check for paswords beeing equals
		if(!account.getPasswordHash().equals(AccountUtils.encodePassword(password)))
		{
			return AionAuthResponse.INVALID_PASSWORD;
		}
		
		// check for paswords beeing equals
		if(account.getActivated() != 1)
		{
			return AionAuthResponse.INVALID_PASSWORD;
		}

		// If account expired
		if(AccountTimeService.isAccountExpired(account))
		{
			return AionAuthResponse.TIME_EXPIRED;
		}

		// if account is banned
		if(AccountTimeService.isAccountPenaltyActive(account))
		{
			return AionAuthResponse.BAN_IP;
		}

		// if account is restricted to some ip or mask
		if(account.getIpForce() != null)
		{
			if(!NetworkUtils.checkIPMatching(account.getIpForce(), channelHandler.getIP()))
			{
				return AionAuthResponse.BAN_IP;
			}
		}

		// if ip is banned
		if(BannedIpService.isBanned(channelHandler.getIP()))
		{
			return AionAuthResponse.BAN_IP;
		}

		// Do not allow to login two times with same account
		synchronized (AccountController.class)
		{
			if(GameServerService.isAccountOnAnyGameServer(account))
			{
				GameServerService.kickAccountFromGameServer(account);
				return AionAuthResponse.ALREADY_LOGGED_IN;
			}

			// If someone is at loginserver, he should be disconnected
			if(accountsOnLS.containsKey(account.getId()))
			{
				ClientChannelHandler clientChannelHandler = accountsOnLS.remove(account.getId());

				clientChannelHandler.close(true);

				return AionAuthResponse.ALREADY_LOGGED_IN;
			}
			else
			{
				channelHandler.setAccount(account);
				accountsOnLS.put(account.getId(), channelHandler);
			}
		}

		AccountTimeService.updateOnLogin(account);

		// if everything was OK
		getAccountDAO().updateLastIp(account.getId(), channelHandler.getIP());

		return AionAuthResponse.AUTHED;
	}

	/**
	 * Loads account from DB and returns it, or returns null if account was not loaded
	 * @param name acccount name
	 * @return loaded account or null
	 */
	public static Account loadAccount(String name) 
	{
		Account	account = getAccountDAO().getAccount(name);
		if (account != null)
		{
			account.setAccountTime(getAccountTimeDAO().getAccountTime(account.getId()));
		}
		return account;
	}



	/**
	 * Returns {@link com.aionemu.loginserver.dao.AccountDAO}, just a shortcut
	 *
	 * @return {@link com.aionemu.loginserver.dao.AccountDAO}
	 */
	private static AccountDAO getAccountDAO()
	{
		return DAOManager.getDAO(AccountDAO.class);
	}

	/**
	 * Returns {@link com.aionemu.loginserver.dao.AccountTimeDAO}, just a shortcut
	 *
	 * @return {@link com.aionemu.loginserver.dao.AccountTimeDAO}
	 */
	private static AccountTimeDAO getAccountTimeDAO()
	{
		return DAOManager.getDAO(AccountTimeDAO.class);
	}
}
