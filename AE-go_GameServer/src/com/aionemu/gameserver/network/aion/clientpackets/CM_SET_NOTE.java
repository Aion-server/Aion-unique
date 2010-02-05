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

import com.aionemu.gameserver.model.gameobjects.player.Friend;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_LIST;

/**
 * Received when a player sets his note
 * @author Ben
 *
 */
public class CM_SET_NOTE extends AionClientPacket
{
	private String note;
	
	public CM_SET_NOTE(int opcode)
	{
		super(opcode);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		note = readS();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		Player activePlayer = getConnection().getActivePlayer();
		
		if (!note.equals(activePlayer.getCommonData().getNote()))
		{
		
			activePlayer.getCommonData().setNote(note);
			
			for (Friend friend : activePlayer.getFriendList()) // For all my friends
			{
				
				if (friend.isOnline()) // If the player is online
				{
					friend.getPlayer().getClientConnection()
						.sendPacket(new SM_FRIEND_LIST()); // Send him a new friend list packet
				}
			}
			
		}
	}
}
