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
package com.aionemu.gameserver.world.container;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.world.exceptions.DuplicateAionObjectException;

/**
 * Container for storing Players by objectId and name.
 * 
 * @author -Nemesiss-
 * 
 */
public class PlayerContainer implements Iterable<Player>
{
	/**
	 * Map<ObjectId,Player>
	 */
	private final Map<Integer, Player>	playersById		= new ConcurrentHashMap<Integer, Player>();
	/**
	 * Map<Name,Player>
	 */
	private final Map<String, Player>	playersByName	= new ConcurrentHashMap<String, Player>();

	/**
	 * Add Player to this Container.
	 * 
	 * @param player
	 */
	public void add(Player player)
	{
		if(playersById.put(player.getObjectId(), player) != null)
			throw new DuplicateAionObjectException();
		if(playersByName.put(player.getName(), player) != null)
			throw new DuplicateAionObjectException();
	}

	/**
	 * Remove Player from this Container.
	 * 
	 * @param player
	 */
	public void remove(Player player)
	{
		playersById.remove(player.getObjectId());
		playersByName.remove(player.getName());
	}

	/**
	 * Get Player object by objectId.
	 * 
	 * @param objectId
	 *            - ObjectId of player.
	 * @return Player with given ojectId or null if Player with given objectId is not logged.
	 */
	public Player get(int objectId)
	{
		return playersById.get(objectId);
	}

	/**
	 * Get Player object by name.
	 * 
	 * @param name
	 *            - name of player
	 * @return Player with given name or null if Player with given name is not logged.
	 */
	public Player get(String name)
	{
		return playersByName.get(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Player> iterator()
	{
		return playersById.values().iterator();
	}
}
