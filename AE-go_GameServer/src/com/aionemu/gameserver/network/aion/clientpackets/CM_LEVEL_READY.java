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
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_WEATHER;
import com.aionemu.gameserver.services.WeatherService;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.zone.ZoneManager;
import com.google.inject.Inject;

/**
 * Client is saying that level[map] is ready.
 * 
 * @author -Nemesiss-
 * 
 */
public class CM_LEVEL_READY extends AionClientPacket
{
	@Inject
	private World	world;

	/**
	 * Constructs new instance of <tt>CM_LEVEL_READY </tt> packet
	 * @param opcode
	 */
	public CM_LEVEL_READY(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		// empty
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		
		Player activePlayer = getConnection().getActivePlayer();
		sendPacket(new SM_PLAYER_INFO(activePlayer, true));

		/**
		 * Spawn player into the world.
		 */
		world.spawn(activePlayer);
		/**
		 * Find zone in current map
		 */
		ZoneManager.getInstance().findZoneInCurrentMap(activePlayer);
		
		//random weather
		int weatherMaskId = WeatherService.getRandomWeather();
		sendPacket(new SM_WEATHER(weatherMaskId));
		
		activePlayer.getEffectController().updatePlayerEffectIcons();
	}
}
