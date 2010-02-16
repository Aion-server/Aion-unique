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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.FriendList.Status;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_SEARCH;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * Received when a player searches using the social search panel
 * 
 * @author Ben
 * 
 */
public class CM_PLAYER_SEARCH extends AionClientPacket
{
	/**
	 * The max number of players to return as results
	 */
	public static final int	MAX_RESULTS	= 125;

	@Inject
	private World			world;

	private String			name;
	private int				region;
	private int				classMask;
	private int				minLevel;
	private int				maxLevel;
	private int				lfgOnly;

	public CM_PLAYER_SEARCH(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		if(!(name = readS()).isEmpty())
		{
			name = Util.convertName(name);
			readB(44 - (name.length() * 2 + 2));
		}
		else
		{
			readB(42);
		}
		region = readD();
		classMask = readD();
		minLevel = readC();
		maxLevel = readC();
		lfgOnly = readC();
		readC(); // 0x00 in search pane 0x30 in /who?
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		Player activePlayer = getConnection().getActivePlayer();

		Iterator<Player> it = world.getPlayersIterator();

		List<Player> matches = new ArrayList<Player>(MAX_RESULTS);

		if(activePlayer != null && activePlayer.getLevel() < 10)
		{
			sendPacket(SM_SYSTEM_MESSAGE.LEVEL_NOT_ENOUGH_FOR_SEARCH("10"));
			return;
		}
		while(it.hasNext() && matches.size() < MAX_RESULTS)
		{
			Player player = it.next();
			if(!player.isSpawned())
				continue;
			else if(player.getFriendList().getStatus() == Status.OFFLINE)
				continue;
			else if(lfgOnly == 1 && !player.isLookingForGroup())
				continue;
			else if(!name.isEmpty() && !player.getName().toLowerCase().contains(name.toLowerCase()))
				continue;
			else if(minLevel != 0xFF && player.getLevel() < minLevel)
				continue;
			else if(maxLevel != 0xFF && player.getLevel() > maxLevel)
				continue;
			else if(classMask > 0 && (player.getPlayerClass().getMask() & classMask) == 0)
				continue;
			else if(region > 0 && player.getActiveRegion().getMapId() != region)
				continue;
			else
			// This player matches criteria
			{
				matches.add(player);
			}
		}

		sendPacket(new SM_PLAYER_SEARCH(matches, region));
	}

}
