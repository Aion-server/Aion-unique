/**
 * This file is part of aion-unique <www.aion-unique.com>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EXCHANGE_ADD_KINAH;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * 
 * @author Avol
 * 
 */

public class CM_EXCHANGE_ADD_KINAH extends AionClientPacket
{
	public int itemObjId;
	public int itemCount;

	@Inject	
	private World			world;

	private static final Logger log = Logger.getLogger(CM_EXCHANGE_ADD_KINAH.class);

	public CM_EXCHANGE_ADD_KINAH(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		itemCount = readD();
		itemObjId = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		final Player activePlayer = getConnection().getActivePlayer();
		int targetPlayerId = activePlayer.getExchangeList().getExchangePartner();
		final Player targetPlayer = world.findPlayer(targetPlayerId);

		Inventory currentKinah = activePlayer.getInventory();
		//send update in trade kinah.

		if (activePlayer.getExchangeList().getExchangeKinah() != 0) {
			itemCount = itemCount - activePlayer.getExchangeList().getExchangeKinah();
		}

		PacketSendUtility.sendPacket(activePlayer, new SM_EXCHANGE_ADD_KINAH(itemCount,0));
		PacketSendUtility.sendPacket(targetPlayer, new SM_EXCHANGE_ADD_KINAH(itemCount,1));

		activePlayer.getExchangeList().setExchangeKinah(itemCount);

	}
}
