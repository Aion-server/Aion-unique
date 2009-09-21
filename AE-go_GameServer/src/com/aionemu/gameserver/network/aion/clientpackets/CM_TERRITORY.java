/**
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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

import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.commons.services.LoggingService;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_WEATHER;
import com.google.inject.Inject;
import java.io.*;


public class CM_TERRITORY extends AionClientPacket
{
	private static final Logger	log	= Logger.getLogger(CM_TERRITORY.class);
	@Inject	
	private World			world;
	private int			territoryId;

	public CM_TERRITORY(int opcode)
	{
		super(opcode);
	}

	@Override
	protected void readImpl()
	{
		territoryId = readD();

	}


	@Override
	protected void runImpl()
	{	
		final Player activePlayer = getConnection().getActivePlayer();
		activePlayer.getClientConnection().sendPacket(new SM_WEATHER());
		//sendPacket(SM_UNKF54unk(1, territoryId)); 179372032
	}
}