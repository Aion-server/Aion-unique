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
package com.aionemu.gameserver.network.loginserver;

import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aionemu.commons.network.Dispatcher;
import com.aionemu.commons.network.NioServer;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.model.account.AccountTime;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.SM_L2AUTH_LOGIN_CHECK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RECONNECT_KEY;
import com.aionemu.gameserver.network.factories.LoginServerConnectionFactory;
import com.aionemu.gameserver.network.loginserver.LoginServerConnection.State;
import com.aionemu.gameserver.network.loginserver.serverpackets.SM_ACCOUNT_AUTH;
import com.aionemu.gameserver.network.loginserver.serverpackets.SM_ACCOUNT_DISCONNECTED;
import com.aionemu.gameserver.network.loginserver.serverpackets.SM_ACCOUNT_RECONNECT_KEY;
import com.aionemu.gameserver.services.AccountService;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.google.inject.Inject;

/**
 * Utill class for connecting GameServer to LoginServer.
 * 
 * @author -Nemesiss-
 * 
 */
public class LoginServer
{
	/**
	 * Logger for this class.
	 */
	private static final Logger				log					= Logger.getLogger(LoginServer.class);

	/**
	 * Map<accountId,Connection> for waiting request. This request is send to LoginServer and GameServer is waiting for
	 * response.
	 */
	private Map<Integer, AionConnection>	loginRequests		= new HashMap<Integer, AionConnection>();

	/**
	 * Map<accountId,Connection> for all logged in accounts.
	 */
	private Map<Integer, AionConnection>	loggedInAccounts	= new HashMap<Integer, AionConnection>();

	/**
	 * Connection to LoginServer.
	 */
	private LoginServerConnection			loginServer;

	private NioServer						nioServer;
	private LoginServerConnectionFactory	lscFactory;
	private boolean							serverShutdown = false;

	@Inject
	private AccountService accountService;
	
	@Inject
	public void setNioServer(NioServer nioServer)
	{
		this.nioServer = nioServer;
	}

	@Inject
	public void setLSConnectionFactory(LoginServerConnectionFactory lscFactory)
	{
		this.lscFactory = lscFactory;
	}

	/**
	 * Connect to LoginServer and return object representing this connection. This method is blocking and may block till
	 * connect successful.
	 * 
	 * @return LoginServerConnection
	 */
	public LoginServerConnection connect()
	{
		SocketChannel sc;
		for(;;)
		{
			loginServer = null;
			log.info("Connecting to LoginServer: " + NetworkConfig.LOGIN_ADDRESS);
			try
			{
				sc = SocketChannel.open(NetworkConfig.LOGIN_ADDRESS);
				sc.configureBlocking(false);
				Dispatcher d = nioServer.getReadWriteDispatcher();
				loginServer = lscFactory.createConnection(sc, d);
				return loginServer;
			}
			catch(Exception e)
			{
				log.info("Cant connect to LoginServer: " + e.getMessage());
			}
			try
			{
				/**
				 * 10s sleep
				 */
				Thread.sleep(10 * 1000);
			}
			catch(Exception e)
			{
			}
		}
	}

	/**
	 * This method is called when we lost connection to LoginServer. We will disconnects all aionClients waiting for
	 * LoginServer response and also try reconnect to LoginServer.
	 */
	public void loginServerDown()
	{
		log.warn("Connection with LoginServer lost...");

		loginServer = null;
		synchronized(this)
		{
			/**
			 * We lost connection for LoginServer so client pending authentication should be disconnected [cuz
			 * authentication will never ends]
			 */
			for(AionConnection client : loginRequests.values())
			{
				// TODO! somme error packet!
				client.close(/* closePacket, */true);
			}
			loginRequests.clear();
		}

		/**
		 * Reconnect after 5s if not server shutdown sequence
		 */
		if (!serverShutdown) {
			ThreadPoolManager.getInstance().schedule(new Runnable(){
				@Override
				public void run()
				{
					connect();
				}
			}, 5000);
		}
	}

	/**
	 * Notify that client is disconnected - we must clear waiting request to LoginServer if any to prevent leaks. Also
	 * notify LoginServer that this account is no longer on GameServer side.
	 * 
	 * @param client
	 */
	public void aionClientDisconnected(int accountId)
	{
		synchronized(this)
		{
			loginRequests.remove(accountId);
			loggedInAccounts.remove(accountId);
		}
		if(loginServer != null && loginServer.getState() == State.AUTHED)
			loginServer.sendPacket(new SM_ACCOUNT_DISCONNECTED(accountId));
	}

	/**
	 * Starts authentication procedure of this client - LoginServer will sends response with information about account
	 * name if authentication is ok.
	 * 
	 * @param accountId 
	 * @param client
	 * @param loginOk
	 * @param playOk1
	 * @param playOk2
	 */
	public void requestAuthenticationOfClient(int accountId, AionConnection client, int loginOk, int playOk1, int playOk2)
	{
		/**
		 * There are no connection to LoginServer. We should disconnect this client since authentication is not
		 * possible.
		 */
		if(loginServer == null || loginServer.getState() != State.AUTHED)
		{
			System.out.println("LS !!! " + (loginServer == null ? "NULL" : loginServer.getState()));
			// TODO! somme error packet!
			client.close(/* closePacket, */true);
			return;
		}

		synchronized(this)
		{
			if(loginRequests.containsKey(accountId))
				return;
			loginRequests.put(accountId, client);
		}
		loginServer.sendPacket(new SM_ACCOUNT_AUTH(accountId, loginOk, playOk1, playOk2));
	}

	/**
	 * This method is called by CM_ACCOUNT_AUTH_RESPONSE LoginServer packets to notify GameServer about results of
	 * client authentication.
	 * 
	 * @param accountId
	 * @param accountName
	 * @param result
	 * @param accountTime 
	 */
	public void accountAuthenticationResponse(int accountId, String accountName, boolean result, AccountTime accountTime, byte accessLevel, byte membership)
	{
		AionConnection client = loginRequests.remove(accountId);

		if(client == null)
			return;

		if(result)
		{
			client.setState(AionConnection.State.AUTHED);
			loggedInAccounts.put(accountId, client);
			log.info("Account authed: " + accountId + " = " + accountName);
			client.setAccount(accountService.getAccount(accountId, accountName, accountTime, accessLevel, membership));
			client.sendPacket(new SM_L2AUTH_LOGIN_CHECK(true));
		}
		else
		{
			log.info("Account not authed: " + accountId);
			client.close(new SM_L2AUTH_LOGIN_CHECK(false), true);
		}
	}

	/**
	 * Starts reconnection to LoginServer procedure. LoginServer in response will send reconnection key.
	 * 
	 * @param client
	 */
	public void requestAuthReconnection(AionConnection client)
	{
		/**
		 * There are no connection to LoginServer. We should disconnect this client since authentication is not
		 * possible.
		 */
		if(loginServer == null || loginServer.getState() != State.AUTHED)
		{
			// TODO! somme error packet!
			client.close(/* closePacket, */false);
			return;
		}

		synchronized(this)
		{
			if(loginRequests.containsKey(client.getAccount().getId()))
				return;
			loginRequests.put(client.getAccount().getId(), client);

		}
		loginServer.sendPacket(new SM_ACCOUNT_RECONNECT_KEY(client.getAccount().getId()));
	}

	/**
	 * This method is called by CM_ACCOUNT_RECONNECT_KEY LoginServer packets to give GameServer reconnection key for
	 * client that was requesting reconnection.
	 * 
	 * @param accountId
	 * @param reconnectKey
	 */
	public void authReconnectionResponse(int accountId, int reconnectKey)
	{
		AionConnection client = loginRequests.remove(accountId);

		if(client == null)
			return;

		log.info("Account reconnectimg: " + accountId + " = " + client.getAccount().getName());
		client.close(new SM_RECONNECT_KEY(reconnectKey), false);
	}

	/**
	 * This method is called by CM_REQUEST_KICK_ACCOUNT LoginServer packets to request GameServer to disconnect client
	 * with given account id.
	 * 
	 * @param accountId
	 */
	public void kickAccount(int accountId)
	{
		synchronized(this)
		{
			AionConnection client = loggedInAccounts.get(accountId);
			if(client != null)
				client.close(/* closePacket, */false);
			/**
			 * This account is not logged in on this GameServer but LS thinks different...
			 */
			else
			{
				if(loginServer == null || loginServer.getState() != State.AUTHED)
					return;
				loginServer.sendPacket(new SM_ACCOUNT_DISCONNECTED(accountId));
			}
		}
	}

	/**
	 * Returns unmodifiable map with accounts that are logged in to current GS Map Key: Account ID Map Value:
	 * AionConnectionObject
	 * 
	 * @return unmodifiable map wwith accounts
	 */
	public Map<Integer, AionConnection> getLoggedInAccounts()
	{
		return Collections.unmodifiableMap(loggedInAccounts);
	}

	/**
	 * When Game Server shutdown, have to close all pending client connection
	 */
	public void gameServerDisconnected()
	{
		synchronized(this)
		{
			serverShutdown = true;
			/**
			 * GameServer shutting down, must close all pending login requests
			 */
			for(AionConnection client : loginRequests.values())
			{
				// TODO! somme error packet!
				client.close(/* closePacket, */true);
			}
			loginRequests.clear();
			
			loginServer.close(false);
		}

		log.info("GameServer disconnected from the Login Server...");
	}
}
