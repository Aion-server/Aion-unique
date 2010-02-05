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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RESTORE_CHARACTER;
import com.aionemu.gameserver.services.PlayerService;
import com.google.inject.Inject;

/**
 * In this packets aion client is requesting cancellation of character deleting.
 * 
 * @author -Nemesiss-
 * 
 */
public class CM_RESTORE_CHARACTER extends AionClientPacket
{
	/**
	 * PlayOk2 - we dont care...
	 */
	@SuppressWarnings("unused")
	private int	playOk2;
	/**
	 * ObjectId of character that deletion should be canceled
	 */
	private int	chaOid;

	@Inject
	private PlayerService playerService;
	
	/**
	 * Constructs new instance of <tt>CM_RESTORE_CHARACTER </tt> packet
	 * @param opcode
	 */
	public CM_RESTORE_CHARACTER(int opcode)
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
		chaOid = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		Account account = getConnection().getAccount();
		PlayerAccountData pad = account.getPlayerAccountData(chaOid);

		boolean success = pad != null && playerService.cancelPlayerDeletion(pad);
		sendPacket(new SM_RESTORE_CHARACTER(chaOid, success));
	}
}
