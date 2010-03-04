/*
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

import com.aionemu.gameserver.dataholders.TeleporterData;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.teleport.TelelocationTemplate;
import com.aionemu.gameserver.model.templates.teleport.TeleporterTemplate;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.services.TeleportService;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author ATracer, orz
 *
 */
public class CM_TELEPORT_SELECT extends AionClientPacket
{
	/** NPC ID */
	public  int					targetObjectId;

	/** Destination of teleport */
	public  int					locId;

	public  TelelocationTemplate _tele;

	private TeleporterTemplate teleport;
	
	@Inject
	private TeleportService teleportService;
	@Inject
	private World world;
	@Inject
	private TeleporterData teleporterData;

	public CM_TELEPORT_SELECT(int opcode)
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
		targetObjectId = readD();
		locId = readD(); //locationId
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		Player activePlayer = getConnection().getActivePlayer();

		Npc npc = (Npc)world.findAionObject(targetObjectId);

		if(activePlayer == null || activePlayer.getLifeStats().isAlreadyDead())
			return;

		teleport = teleporterData.getTeleporterTemplate(npc.getNpcId());

		switch(teleport.getType())
		{
			case FLIGHT:
				teleportService.flightTeleport(teleport, locId, activePlayer);
				break;
			case REGULAR:
				teleportService.regularTeleport(teleport, locId, activePlayer);
				break;
			default:
				//TODO
		}
	}
}
