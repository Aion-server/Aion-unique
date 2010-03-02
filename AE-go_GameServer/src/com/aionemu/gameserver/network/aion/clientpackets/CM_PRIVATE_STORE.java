/*
 * This file is part of aion-unique <aion-unique.org>.
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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.trade.TradePSItem;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.services.PrivateStoreService;
import com.google.inject.Inject;

/**
 * @author Simple
 */
public class CM_PRIVATE_STORE extends AionClientPacket
{
	/**
	 * Inject Private Store Service
	 */
	@Inject
	PrivateStoreService		privateStoreService;

	/**
	 * Private store information
	 */
	private Player			activePlayer;
	private TradePSItem[]	tradePSItems;
	private int				itemCount;

	/**
	 * Constructs new instance of <tt>CM_PRIVATE_STORE </tt> packet
	 * 
	 * @param opcode
	 */
	public CM_PRIVATE_STORE(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		/**
		 * Define who wants to create a private store
		 */
		activePlayer = getConnection().getActivePlayer();

		/**
		 * Read the amount of items that need to be put into the player's store
		 */
		itemCount = readH();
		tradePSItems = new TradePSItem[itemCount];
		for(int i = 0; i < itemCount; i++)
		{
			tradePSItems[i] = new TradePSItem(readD(), readD(), readH(), readD());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		/**
		 * Add a check for now if private store is enabled or not
		 */

		/**
		 * Let PrivateStoreService handle everything
		 */
		if(itemCount > 0)
		{
			privateStoreService.addItem(activePlayer, tradePSItems);
		}
		else
		{
			privateStoreService.closePrivateStore(activePlayer);
		}

	}
}
