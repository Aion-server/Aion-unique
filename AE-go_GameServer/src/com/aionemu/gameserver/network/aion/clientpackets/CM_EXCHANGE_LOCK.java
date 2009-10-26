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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EXCHANGE_CONFIRMATION;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author -Avol-
 * 
 */
public class CM_EXCHANGE_LOCK extends AionClientPacket
{

	private int action;
	@Inject	
	private World			world;

	public CM_EXCHANGE_LOCK(int opcode)
	{
		super(opcode);
	}

	@Override
	protected void readImpl()
	{
		//nothing
	}

	@Override
	protected void runImpl()
	{	
		final Player activePlayer = getConnection().getActivePlayer();
		int targetPlayerId = activePlayer.getExchangeList().getExchangePartner();

		final Player targetPlayer = world.findPlayer(targetPlayerId);
		PacketSendUtility.sendPacket(targetPlayer, new SM_EXCHANGE_CONFIRMATION(3));
	}
}