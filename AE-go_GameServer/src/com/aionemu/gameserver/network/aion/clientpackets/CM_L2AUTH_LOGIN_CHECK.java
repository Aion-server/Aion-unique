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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.services.AccountService;
import com.google.inject.Inject;

/**
 * In this packets aion client is authenticating himself by providing accountId and rest of sessionKey - we will check
 * if its valid at login server side.
 * 
 * @author -Nemesiss-
 * 
 */
// TODO: L2AUTH? Really? :O
public class CM_L2AUTH_LOGIN_CHECK extends AionClientPacket
{
	@Inject
	private LoginServer	loginServer;
	@SuppressWarnings("unused")
	@Inject
	private AccountService accountService;
	
	/**
	 * playOk2 is part of session key - its used for security purposes we will check if this is the key what login
	 * server sends.
	 */
	private int			playOk2;
	/**
	 * playOk1 is part of session key - its used for security purposes we will check if this is the key what login
	 * server sends.
	 */
	private int			playOk1;
	/**
	 * accountId is part of session key - its used for authentication we will check if this accountId is matching any
	 * waiting account login server side and check if rest of session key is ok.
	 */
	private int			accountId;
	/**
	 * loginOk is part of session key - its used for security purposes we will check if this is the key what login
	 * server sends.
	 */
	private int			loginOk;

	/**
	 * Constructs new instance of <tt>CM_L2AUTH_LOGIN_CHECK </tt> packet
	 * @param opcode
	 */
	public CM_L2AUTH_LOGIN_CHECK(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		playOk2 = readD();
		playOk1 = readD();
		accountId = readD();
		loginOk = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		loginServer.requestAuthenticationOfClient(accountId, getConnection(), loginOk, playOk1, playOk2);
	}
}
