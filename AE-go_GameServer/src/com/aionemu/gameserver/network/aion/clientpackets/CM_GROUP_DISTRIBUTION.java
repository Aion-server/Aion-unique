/*
 * This file is part of aion-unique <aion-unique.com>.
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
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.GroupService;
import com.google.inject.Inject;

/**
 * @author Lyahim
 * @author Simple
 */
public class CM_GROUP_DISTRIBUTION extends AionClientPacket
{
	@Inject
	GroupService	groupService;

	private int		amount;

	public CM_GROUP_DISTRIBUTION(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		amount = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		if(amount < 1)
			return;

		Player player = getConnection().getActivePlayer();
		
		if(!RestrictionsManager.canTrade(player))
			return;

		groupService.groupDistribution(player, amount);
	}
}
