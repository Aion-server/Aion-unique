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
package com.aionemu.gameserver.network.aion;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.log4j.Logger;

import com.aionemu.commons.network.AConnection;
import com.aionemu.commons.network.Dispatcher;
import com.aionemu.commons.network.PacketProcessor;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.Crypt;
import com.aionemu.gameserver.network.aion.serverpackets.SM_KEY;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.services.PlayerService;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Object representing connection between GameServer and Aion Client.
 * 
 * @author -Nemesiss-
 */
public class AionConnection extends AConnection
{
	/**
	 * Logger for this class.
	 */
	private static final Logger								log			= Logger.getLogger(AionConnection.class);
	/**
	 * PacketProcessor for executing packets.
	 */
	private final static PacketProcessor<AionConnection>	processor	= new PacketProcessor<AionConnection>(1, 8);

	/**
	 * Possible states of AionConnection
	 */
	public static enum State
	{
		/**
		 * client just connect
		 */
		CONNECTED,
		/**
		 * client is authenticated
		 */
		AUTHED,
		/**
		 * client entered world.
		 */
		IN_GAME
	}

	/**
	 * Server Packet "to send" Queue
	 */
	private final Deque<AionServerPacket>	sendMsgQueue	= new ArrayDeque<AionServerPacket>();
	/**
	 * Current state of this connection
	 */
	private State							state;

	/**
	 * AionClient is authenticating by passing to GameServer id of account.
	 */
	private Account							account;

	/**
	 * Crypt that will encrypt/decrypt packets.
	 */
	private final Crypt						crypt			= new Crypt();

	/**
	 * active Player that owner of this connection is playing [entered game]
	 */
	private Player							activePlayer;

	private LoginServer						loginServer;
	private AionPacketHandler				aionPacketHandler;
	private PlayerService					playerService;

	/**
	 * Constructor
	 * 
	 * @param sc
	 * @param d
	 * @throws IOException
	 */
	@Inject
	public AionConnection(@Assisted SocketChannel sc, @Assisted Dispatcher d, LoginServer loginServer,
		AionPacketHandler aionPacketHandler, PlayerService playerService) throws IOException
	{
		super(sc, d);

		this.loginServer = loginServer;
		this.aionPacketHandler = aionPacketHandler;
		this.playerService = playerService;

		state = State.CONNECTED;

		String ip = getIP();
		log.info("connection from: " + ip);

		/** Send SM_KEY packet */
		sendPacket(new SM_KEY());
	}

	/**
	 * Enable crypt key - generate random key that will be used to encrypt second server packet [first one is
	 * unencrypted] and decrypt client packets. This method is called from SM_KEY server packet, that packet sends key
	 * to aion client.
	 * 
	 * @return "false key" that should by used by aion client to encrypt/decrypt packets.
	 */
	public final int enableCryptKey()
	{
		return crypt.enableKey();
	}

	/**
	 * Called by Dispatcher. ByteBuffer data contains one packet that should be processed.
	 * 
	 * @param data
	 * @return True if data was processed correctly, False if some error occurred and connection should be closed NOW.
	 */
	@Override
	protected final boolean processData(ByteBuffer data)
	{
		try
		{
			if(!crypt.decrypt(data))
			{
				log.warn("Decrypt fail!");
				return false;
			}
		}
		catch(Exception ex)
		{
			log.error("Exception caught during decrypt!" + ex.getMessage());
			return false;
		}
		
		AionClientPacket pck = aionPacketHandler.handle(data, this);
		log.debug("recived packet: " + pck);

		/**
		 * Execute packet only if packet exist (!= null) and read was ok.
		 */
		if(pck != null && pck.read())
			processor.executePacket(pck);

		return true;
	}

	/**
	 * This method will be called by Dispatcher, and will be repeated till return false.
	 * 
	 * @param data
	 * @return True if data was written to buffer, False indicating that there are not any more data to write.
	 */
	@Override
	protected final boolean writeData(ByteBuffer data)
	{
		synchronized(guard)
		{
			AionServerPacket packet = sendMsgQueue.pollFirst();
			if(packet == null)
				return false;

			packet.write(this, data);
			return true;
		}
	}

	/**
	 * This method is called by Dispatcher when connection is ready to be closed.
	 * 
	 * @return time in ms after witch onDisconnect() method will be called. Always return 0.
	 */
	@Override
	protected final long getDisconnectionDelay()
	{
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void onDisconnect()
	{
		/**
		 * Client starts authentication procedure
		 */
		if(getAccount() != null)
			loginServer.aionClientDisconnected(getAccount().getId());
		if(getActivePlayer() != null)
		{
			Player player = getActivePlayer();
			
			if(player.getController().isInShutdownProgress())
				playerService.playerLoggedOut(player);
			
			// prevent ctrl+alt+del / close window exploit
			else
			{
				int delay = 15;
				playerService.playerLoggedOutDelay(player, (delay * 1000));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void onServerClose()
	{
		// TODO mb some packet should be send to client before closing?
		close(/* packet, */true);
	}

	/**
	 * Encrypt packet.
	 * 
	 * @param buf
	 */
	public final void encrypt(ByteBuffer buf)
	{
		crypt.encrypt(buf);
	}

	/**
	 * Sends AionServerPacket to this client.
	 * 
	 * @param bp
	 *            AionServerPacket to be sent.
	 */
	public final void sendPacket(AionServerPacket bp)
	{
		synchronized(guard)
		{
			/**
			 * Connection is already closed or waiting for last (close packet) to be sent
			 */
			if(isWriteDisabled())
				return;

			log.debug("sending packet: " + bp);

			sendMsgQueue.addLast(bp);
			enableWriteInterest();
		}
	}

	/**
	 * Its guaranted that closePacket will be sent before closing connection, but all past and future packets wont.
	 * Connection will be closed [by Dispatcher Thread], and onDisconnect() method will be called to clear all other
	 * things. forced means that server shouldn't wait with removing this connection.
	 * 
	 * @param closePacket
	 *            Packet that will be send before closing.
	 * @param forced
	 *            have no effect in this implementation.
	 */
	public final void close(AionServerPacket closePacket, boolean forced)
	{
		synchronized(guard)
		{
			if(isWriteDisabled())
				return;

			log.debug("sending packet: " + closePacket + " and closing connection after that.");

			pendingClose = true;
			isForcedClosing = forced;
			sendMsgQueue.clear();
			sendMsgQueue.addLast(closePacket);
			enableWriteInterest();
		}
	}

	/**
	 * Current state of this connection
	 * 
	 * @return state
	 */
	public final State getState()
	{
		return state;
	}

	/**
	 * Sets the state of this connection
	 * 
	 * @param state
	 *            state of this connection
	 */
	public void setState(State state)
	{
		this.state = state;
	}

	/**
	 * Returns account object associated with this connection
	 * 
	 * @return account object associated with this connection
	 */
	public Account getAccount()
	{
		return account;
	}

	/**
	 * Sets account object associated with this connection
	 * 
	 * @param account
	 *            account object associated with this connection
	 */
	public void setAccount(Account account)
	{
		this.account = account;
	}

	/**
	 * Sets Active player to new value. Update connection state to correct value.
	 * 
	 * @param player
	 * @return True if active player was set to new value.
	 */
	public boolean setActivePlayer(Player player)
	{
		if(activePlayer != null && player != null)
			return false;
		activePlayer = player;

		if(activePlayer == null)
			state = State.AUTHED;
		else
			state = State.IN_GAME;

		return true;
	}

	/**
	 * Return active player or null.
	 * 
	 * @return active player or null.
	 */
	public Player getActivePlayer()
	{
		return activePlayer;
	}
}
