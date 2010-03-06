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

import com.aionemu.gameserver.dataholders.BindPointData;
import com.aionemu.gameserver.dataholders.PlayerInitialData;
import com.aionemu.gameserver.dataholders.TeleLocationData;
import com.aionemu.gameserver.dataholders.TeleporterData;
import com.aionemu.gameserver.dataholders.PlayerInitialData.LocationData;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.Storage;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.templates.BindPointTemplate;
import com.aionemu.gameserver.model.templates.teleport.TelelocationTemplate;
import com.aionemu.gameserver.model.templates.teleport.TeleportLocation;
import com.aionemu.gameserver.model.templates.teleport.TeleporterTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CHANNEL_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_SPAWN;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SET_BIND_POINT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TELEPORT_LOC;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TELEPORT_MAP;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author ATracer , orz, Simple
 * 
 */
public class TeleportService
{
	private static final Logger	log						= Logger.getLogger(TeleportService.class);

	private static final int	TELEPORT_DEFAULT_DELAY	= 2200;

	@Inject
	private World				world;
	@Inject
	private PlayerService		playerService;
	@Inject
	private TeleLocationData	teleLocationData;
	@Inject
	private TeleporterData		teleporterData;
	@Inject
	private BindPointData		bindPointData;
	@Inject
	private PlayerInitialData	playerInitialData;

	/**
	 * Schedules teleport animation
	 * 
	 * @param activePlayer
	 * @param mapid
	 * @param x
	 * @param y
	 * @param z
	 */
	public void scheduleTeleportTask(final Player activePlayer, final int mapid, final float x, final float y,
		final float z)
	{
		teleportTo(activePlayer, mapid, x, y, z, TELEPORT_DEFAULT_DELAY);
	}

	/**
	 * Performs flight teleportation
	 * 
	 * @param template
	 * @param locId
	 * @param player
	 */
	public void flightTeleport(TeleporterTemplate template, int locId, Player player)
	{
		if(template.getTeleLocIdData() == null)
		{
			log.info(String.format("Missing locId for this teleporter at teleporter_templates.xml with locId: %d",
				locId));
			PacketSendUtility.sendMessage(player,
				"Missing locId for this teleporter at teleporter_templates.xml with locId: " + locId);
			return;
		}

		TeleportLocation location = template.getTeleLocIdData().getTeleportLocation(locId);
		if(location == null)
		{
			log.info(String.format("Missing locId for this teleporter at teleporter_templates.xml with locId: %d",
				locId));
			PacketSendUtility.sendMessage(player,
				"Missing locId for this teleporter at teleporter_templates.xml with locId: " + locId);
			return;
		}

		TelelocationTemplate locationTemplate = teleLocationData.getTelelocationTemplate(locId);
		if(locationTemplate == null)
		{
			log.info(String.format("Missing info at teleport_location.xml with locId: %d", locId));
			PacketSendUtility.sendMessage(player, "Missing info at teleport_location.xml with locId: " + locId);
			return;
		}

		if(!checkKinahForTransportation(location, player))
			return;
		player.setState(CreatureState.FLYING);
		player.unsetState(CreatureState.ACTIVE);
		PacketSendUtility.sendPacket(player, new SM_EMOTION(player, 6, location.getTeleportId(), 0));
	}

	/**
	 * Performs regular teleportation
	 * 
	 * @param template
	 * @param locId
	 * @param player
	 */
	public void regularTeleport(TeleporterTemplate template, int locId, Player player)
	{

		if(template.getTeleLocIdData() == null)
		{
			log.info(String.format("Missing locId for this teleporter at teleporter_templates.xml with locId: %d",
				locId));
			PacketSendUtility.sendMessage(player,
				"Missing locId for this teleporter at teleporter_templates.xml with locId: " + locId);
			return;
		}

		TeleportLocation location = template.getTeleLocIdData().getTeleportLocation(locId);
		if(location == null)
		{
			log.info(String.format("Missing locId for this teleporter at teleporter_templates.xml with locId: %d",
				locId));
			PacketSendUtility.sendMessage(player,
				"Missing locId for this teleporter at teleporter_templates.xml with locId: " + locId);
			return;
		}

		TelelocationTemplate locationTemplate = teleLocationData.getTelelocationTemplate(locId);
		if(locationTemplate == null)
		{
			log.info(String.format("Missing info at teleport_location.xml with locId: %d", locId));
			PacketSendUtility.sendMessage(player, "Missing info at teleport_location.xml with locId: " + locId);
			return;
		}

		if(!checkKinahForTransportation(location, player))
			return;

		PacketSendUtility.sendPacket(player, new SM_TELEPORT_LOC(locationTemplate.getMapId(), locationTemplate.getX(),
			locationTemplate.getY(), locationTemplate.getZ()));
		scheduleTeleportTask(player, locationTemplate.getMapId(), locationTemplate.getX(), locationTemplate.getY(),
			locationTemplate.getZ());
	}

	/**
	 * Check kinah in inventory for teleportation
	 * 
	 * @param location
	 * @param player
	 * @return
	 */
	private boolean checkKinahForTransportation(TeleportLocation location, Player player)
	{
		Storage inventory = player.getInventory();

		if(!inventory.decreaseKinah(location.getPrice()))
		{
			// TODO using the correct system message
			PacketSendUtility.sendPacket(player, new SM_MESSAGE(0, null, "You don't have enough Kinah",
				ChatType.ANNOUNCEMENTS));
			return false;
		}
		return true;
	}

	/**
	 * @param player
	 * @param targetObjectId
	 */
	public void showMap(Player player, int targetObjectId, int npcId)
	{
		if(player.isInState(CreatureState.FLYING))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_AIRPORT_WHEN_FLYING);
			return;
		}
		PacketSendUtility.sendPacket(player, new SM_TELEPORT_MAP(player, targetObjectId, getTeleporterTemplate(npcId)));
	}

	/**
	 * Teleport Creature to the location using current heading and instanceId
	 * 
	 * @param worldId
	 * @param x
	 * @param y
	 * @param z
	 * @param delay
	 * @return true or false
	 */
	public boolean teleportTo(Player player, int worldId, float x, float y, float z, int delay)
	{
		int instanceId = 1;
		if(player.getWorldId() == worldId)
		{
			instanceId = player.getInstanceId();
		}
		return teleportTo(player, worldId, instanceId, x, y, z, delay);
	}

	/**
	 * 
	 * @param worldId
	 * @param instanceId
	 * @param x
	 * @param y
	 * @param z
	 * @param delay
	 * @return true or false
	 */
	public boolean teleportTo(Player player, int worldId, int instanceId, float x, float y, float z, int delay)
	{
		return teleportTo(player, worldId, instanceId, x, y, z, player.getHeading(), delay);
	}

	public boolean teleportTo(final Player player, final int worldId, final int instanceId, final float x,
		final float y, final float z, final byte heading, final int delay)
	{
		if(delay == 0)
		{
			changePosition(player, worldId, instanceId, x, y, z, heading);
			return true;
		}

		PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, 0, delay, 0, 0));
		ThreadPoolManager.getInstance().schedule(new Runnable(){
			@Override
			public void run()
			{
				if(player.getLifeStats().isAlreadyDead())
					return;

				if(delay != 0)
				{
					PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(0, 0, 0, 0, 1, 0));
				}
				changePosition(player, worldId, instanceId, x, y, z, heading);
			}
		}, delay);

		return true;
	}

	/**
	 * 
	 * @param worldId
	 * @param instanceId
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 */
	private void changePosition(Player player, int worldId, int instanceId, float x, float y, float z, byte heading)
	{
		if(player.getInstanceId() != instanceId || player.getWorldId() != worldId)
		{
			world.getWorldMap(player.getWorldId()).getWorldMapInstanceById(player.getInstanceId()).removePlayer(
				player.getObjectId());
			world.getWorldMap(worldId).getWorldMapInstanceById(instanceId).addPlayer(player.getObjectId());
		}
		world.despawn(player);
		world.setPosition(player, worldId, instanceId, x, y, z, heading);
		player.getController().startProtectionActiveTask();
		PacketSendUtility.sendPacket(player, new SM_CHANNEL_INFO(player.getPosition()));
		PacketSendUtility.sendPacket(player, new SM_PLAYER_SPAWN(player));
	}

	/**
	 * @return the teleporterData
	 */
	public TeleporterTemplate getTeleporterTemplate(int npcId)
	{
		return teleporterData.getTeleporterTemplate(npcId);
	}

	/**
	 * @return the bindPointData
	 */
	public BindPointTemplate getBindPointTemplate2(int bindPointId)
	{
		return bindPointData.getBindPointTemplate2(bindPointId);
	}

	/**
	 * @param channel
	 */
	public void changeChannel(Player player, int channel)
	{
		world.despawn(player);
		world.setPosition(player, player.getWorldId(), channel + 1, player.getX(), player.getY(), player.getZ(), player
			.getHeading());
		player.getController().startProtectionActiveTask();
		PacketSendUtility.sendPacket(player, new SM_CHANNEL_INFO(player.getPosition()));
		PacketSendUtility.sendPacket(player, new SM_PLAYER_SPAWN(player));
	}

	/**
	 * This method will move a player to their bind location with 0 delay
	 * @param player
	 * @param useTeleport
	 */
	public void moveToBindLocation(Player player, boolean useTeleport)
	{
		this.moveToBindLocation(player, useTeleport, 0);
	}

	/**
	 * This method will move a player to their bind location
	 * @param player
	 * @param useTeleport
	 * @param delay
	 */
	public void moveToBindLocation(Player player, boolean useTeleport, int delay)
	{	
		float x, y, z;
		int worldId;
		
		int bindPointId = player.getCommonData().getBindPoint();

		if(bindPointId != 0)
		{
			BindPointTemplate bplist = getBindPointTemplate2(bindPointId);
			worldId = bplist.getZoneId();
			x = bplist.getX();
			y = bplist.getY();
			z = bplist.getZ();
		}
		else
		{
			LocationData locationData = playerService.getPlayerInitialData().getSpawnLocation(
				player.getCommonData().getRace());
			worldId = locationData.getMapId();
			x = locationData.getX();
			y = locationData.getY();
			z = locationData.getZ();
		}

		if(useTeleport)
		{
			teleportTo(player, worldId, x, y, z, delay);
		}
		else
		{
			if(player.getInstanceId() != 1 || player.getWorldId() != worldId)
			{
				world.getWorldMap(player.getWorldId()).getWorldMapInstanceById(player.getInstanceId()).removePlayer(
					player.getObjectId());
				world.getWorldMap(worldId).getWorldMapInstanceById(1).addPlayer(player.getObjectId());
			}
			world.setPosition(player, worldId, x, y, z, player.getHeading());
		}
	}
	
	/**
	 * This method will send the set bind point packet
	 * @param player
	 */
	public void sendSetBindPoint(Player player)
	{
		int worldId;
		float x, y, z;
		if(player.getCommonData().getBindPoint() != 0)
		{
			BindPointTemplate bplist = bindPointData.getBindPointTemplate2(player.getCommonData().getBindPoint());
			worldId = bplist.getZoneId();
			x = bplist.getX();
			y = bplist.getY();
			z = bplist.getZ();
		}
		else
		{
			LocationData locationData = playerInitialData.getSpawnLocation(player.getCommonData().getRace());
			worldId = locationData.getMapId();
			x = locationData.getX();
			y = locationData.getY();
			z = locationData.getZ();
		}
		PacketSendUtility.sendPacket(player, new SM_SET_BIND_POINT(worldId, x, y, z));
	}
}
