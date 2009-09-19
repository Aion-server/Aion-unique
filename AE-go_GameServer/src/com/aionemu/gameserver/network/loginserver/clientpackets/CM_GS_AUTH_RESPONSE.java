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
package com.aionemu.gameserver.network.loginserver.clientpackets;

import org.apache.log4j.Logger;

import com.aionemu.commons.utils.ExitCode;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.network.loginserver.LsClientPacket;
import com.aionemu.gameserver.network.loginserver.LoginServerConnection.State;
import com.aionemu.gameserver.network.loginserver.serverpackets.SM_ACCOUNT_LIST;
import com.aionemu.gameserver.network.loginserver.serverpackets.SM_GS_AUTH;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.google.inject.Inject;

/**
 * This packet is response for SM_GS_AUTH its notify Gameserver if registration was ok or what was wrong.
 * 
 * @author -Nemesiss-
 * 
 */
public class CM_GS_AUTH_RESPONSE extends LsClientPacket
{
	/**
	 * Logger for this class.
	 */
	protected static final Logger	log	= Logger.getLogger(CM_GS_AUTH_RESPONSE.class);

	/**
	 * Response: 0=Authed,1=NotAuthed,2=AlreadyRegistered
	 */
	private int						response;
	@Inject
	private LoginServer				loginServer;

	/**
	 * Constructs new instance of <tt>CM_GS_AUTH_RESPONSE </tt> packet.
	 * @param opcode
	 */
	public CM_GS_AUTH_RESPONSE(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		response = readC();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		/**
		 * Authed
		 */
		if(response == 0)
		{
			getConnection().setState(State.AUTHED);
			sendPacket(new SM_ACCOUNT_LIST(loginServer.getLoggedInAccounts()));
		}

		/**
		 * NotAuthed
		 */
		else if(response == 1)
		{
			log.fatal("GameServer is not authenticated at LoginServer side, shutting down!");
			System.exit(ExitCode.CODE_ERROR);
		}
		/**
		 * AlreadyRegistered
		 */
		else if(response == 2)
		{
			log.info("GameServer is already registered at LoginServer side! trying again...");
			/**
			 * try again after 10s
			 */
			ThreadPoolManager.getInstance().schedule(new Runnable(){
				@Override
				public void run()
				{
					CM_GS_AUTH_RESPONSE.this.getConnection().sendPacket(new SM_GS_AUTH());
				}

			}, 10000);
		}
	}
}
