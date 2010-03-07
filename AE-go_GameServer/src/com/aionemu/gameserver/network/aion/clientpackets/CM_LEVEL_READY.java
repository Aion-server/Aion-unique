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
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.WeatherService;
import com.aionemu.gameserver.services.ZoneService.ZoneUpdateMode;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * Client is saying that level[map] is ready.
 * 
 * @author -Nemesiss-
 * @author Kwazar
 */
public class CM_LEVEL_READY extends AionClientPacket
{
	@Inject
	private World			world;

	@Inject
	private WeatherService	weatherService;

	/**
	 * Constructs new instance of <tt>CM_LEVEL_READY </tt> packet
	 * 
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

		// here check flying zone may be to disallow teleporting itself
		activePlayer.unsetState(CreatureState.FLYING);
		activePlayer.getController().startProtectionActiveTask();

		sendPacket(new SM_PLAYER_INFO(activePlayer, false));

		/**
		 * Spawn player into the world.
		 */
		world.spawn(activePlayer);
		
		activePlayer.getController().addZoneUpdateMask(ZoneUpdateMode.ZONE_REFRESH);

		/**
		 * Loading weather for the player's region
		 */
		weatherService.loadWeather(activePlayer);

		QuestEngine.getInstance().onEnterWorld(new QuestEnv(null, activePlayer, 0, 0));
		
		// zone channel message
		sendPacket(new SM_SYSTEM_MESSAGE(1390122, activePlayer.getPosition().getInstanceId()));

		activePlayer.getEffectController().updatePlayerEffectIcons();
	}
}
