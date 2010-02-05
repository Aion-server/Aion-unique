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
package com.aionemu.gameserver.services;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.templates.teleport.TelelocationTemplate;
import com.aionemu.gameserver.model.templates.teleport.TeleportLocation;
import com.aionemu.gameserver.model.templates.teleport.TeleporterTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TELEPORT_LOC;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer , orz
 *
 */
public class TeleportService
{
	private static final Logger log = Logger.getLogger(TeleportService.class);

	private static final int TELEPORT_DEFAULT_DELAY = 2200;

	private static TeleportService instance = new TeleportService();
	
	/**
	 *  Schedules teleport animation
	 *  
	 * @param activePlayer
	 * @param mapid
	 * @param x
	 * @param y
	 * @param z
	 */
	public void scheduleTeleportTask(final Player activePlayer, final int mapid, final float x, final float y, final float z)
	{
		activePlayer.getController().teleportTo(mapid, x, y, z, TELEPORT_DEFAULT_DELAY);
	}
	
	// TODO injectable bean
	public static TeleportService getInstance()
	{
		return instance;
	}
	
	/**
	 *  Performs flight teleportation
	 *  
	 * @param template
	 * @param locId
	 * @param player
	 */
	public void flightTeleport(TeleporterTemplate template, int locId, Player player)
	{
		if(template.getTeleLocIdData() == null)
		{
			log.info(String.format("Missing locId for this teleporter at teleporter_templates.xml with locId: %d", locId));
			PacketSendUtility.sendMessage(player, "Missing locId for this teleporter at teleporter_templates.xml with locId: "+locId);
			return;
		}
		
		TeleportLocation location = template.getTeleLocIdData().getTeleportLocation(locId);
		if(location == null)
		{
			log.info(String.format("Missing locId for this teleporter at teleporter_templates.xml with locId: %d", locId));
			PacketSendUtility.sendMessage(player, "Missing locId for this teleporter at teleporter_templates.xml with locId: "+locId);
			return;
		}			
		
		TelelocationTemplate locationTemplate = DataManager.TELELOCATION_DATA.getTelelocationTemplate(locId);
		if(locationTemplate == null)
		{
			log.info(String.format("Missing info at teleport_location.xml with locId: %d", locId));
			PacketSendUtility.sendMessage(player, "Missing info at teleport_location.xml with locId: "+locId);
			return;
		}
		
		if(!checkKinahForTransportation(location, player))
			return;
		player.setState(CreatureState.FLYING);
		player.unsetState(CreatureState.ACTIVE);
		PacketSendUtility.sendPacket(player, new SM_EMOTION(player, 6, location.getTeleportId(), 0));
	}
	
	/**
	 *  Performs regular teleportation
	 *  
	 * @param template
	 * @param locId
	 * @param player
	 */
	public void regularTeleport(TeleporterTemplate template, int locId, Player player)
	{

		if(template.getTeleLocIdData() == null)
		{
			log.info(String.format("Missing locId for this teleporter at teleporter_templates.xml with locId: %d", locId));
			PacketSendUtility.sendMessage(player, "Missing locId for this teleporter at teleporter_templates.xml with locId: "+locId);
			return;
		}
		
		TeleportLocation location = template.getTeleLocIdData().getTeleportLocation(locId);
		if(location == null)
		{
			log.info(String.format("Missing locId for this teleporter at teleporter_templates.xml with locId: %d", locId));
			PacketSendUtility.sendMessage(player, "Missing locId for this teleporter at teleporter_templates.xml with locId: "+locId);
			return;
		}
		
		TelelocationTemplate locationTemplate = DataManager.TELELOCATION_DATA.getTelelocationTemplate(locId);
		if(locationTemplate == null)
		{
			log.info(String.format("Missing info at teleport_location.xml with locId: %d", locId));
			PacketSendUtility.sendMessage(player, "Missing info at teleport_location.xml with locId: "+locId);
			return;
		}
		
		if(!checkKinahForTransportation(location, player))
			return;
		
		PacketSendUtility.sendPacket(player, new SM_TELEPORT_LOC(locationTemplate.getMapId(),
			locationTemplate.getX(), locationTemplate.getY(), locationTemplate.getZ()));
		scheduleTeleportTask(player, locationTemplate.getMapId(),
			locationTemplate.getX(), locationTemplate.getY(), locationTemplate.getZ());
	}

	/**
	 *  Check kinah in inventory for teleportation
	 *  
	 * @param location
	 * @param player
	 * @return
	 */
	private boolean checkKinahForTransportation(TeleportLocation location, Player player)
	{
		Inventory inventory = player.getInventory();
		
		if (!inventory.decreaseKinah(location.getPrice()))
		{
			// TODO using the correct system message
			PacketSendUtility.sendPacket(player, new SM_MESSAGE(0, null, "You don't have enough Kinah", null, ChatType.ANNOUNCEMENTS));
			return false;
		}
		return true;
	}

}

