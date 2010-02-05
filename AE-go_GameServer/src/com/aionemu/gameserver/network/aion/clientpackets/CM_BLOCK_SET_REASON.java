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

import com.aionemu.gameserver.model.gameobjects.player.BlockedPlayer;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.SocialService;
import com.google.inject.Inject;

/**
 * @author Ben
 *
 */
public class CM_BLOCK_SET_REASON extends AionClientPacket
{
	
	String					targetName;
	String 					reason;
	@Inject
	private SocialService 	socialService;
	
	public CM_BLOCK_SET_REASON(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		targetName = readS();
		reason = readS();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		Player activePlayer = getConnection().getActivePlayer();
		BlockedPlayer target = activePlayer.getBlockList().getBlockedPlayer(targetName);
		
		if (target == null)
			sendPacket(SM_SYSTEM_MESSAGE.BLOCKLIST_NOT_BLOCKED);
		
		else 
		{
			socialService.setBlockedReason(activePlayer, target, reason);

		}

	}

}
