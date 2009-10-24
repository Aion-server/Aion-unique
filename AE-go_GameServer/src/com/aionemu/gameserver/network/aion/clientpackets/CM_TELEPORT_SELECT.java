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

import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.TelelocationTemplate;
import com.aionemu.gameserver.model.templates.TeleporterTemplate;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTLIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKF5;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TELEPORT_LOC;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.services.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

import org.apache.log4j.Logger;
/**
 * @author ATracer, orz
 *
 */
public class CM_TELEPORT_SELECT extends AionClientPacket
{


    public  Player 				activePlayer;
	/**
	 * Logger
	 */
	private static final Logger	log	= Logger.getLogger(CM_TELEPORT_SELECT.class);

	/**
	 * Target object id that client wants to select or 0 if wants to unselect
	 */
	public  int					targetObjectId;

	public  int					locId;

	public  TelelocationTemplate _tele;

	public  World				world;
	
	private TeleporterTemplate teleport;

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
		Inventory bag = activePlayer.getInventory();
		World world = activePlayer.getActiveRegion().getWorld();
		Npc npc = (Npc)world.findAionObject(targetObjectId);
		
		if(activePlayer == null)
			return;

		teleport = DataManager.TELEPORTER_DATA.getTeleporterTemplate(npc.getNpcId());
		
		if ((teleport != null )&&(teleport.getLocations()!= null))
			_tele = teleport.getLocations().getLoc(locId);
		
		if (_tele == null)
			_tele = DataManager.TELELOCATION_DATA.getTelelocationTemplate(locId);
		
		if (_tele == null)
		{
			log.info(String.format("Missing info at teleport_location.xml with locId: %d", locId));
			PacketSendUtility.sendMessage(activePlayer, "Missing info at teleport_location.xml with locId: "+locId);
			return;
		}

		//normal teleport
		if (_tele.getLocId() != 0 && _tele.getMapId() != 0 && _tele.getTeleportId() == 0 && _tele.getX() != 0)
		{
			if (bag.getKinahItem().getItemCount()<_tele.getPrice())
			{
				//Todo using the correct system message
				sendPacket(new SM_MESSAGE(0, null, "You don't have enough Kinah", null, ChatType.ANNOUNCEMENTS));
				return;
			}
			
			bag.getKinahItem().increaseItemCount(-1 * _tele.getPrice());
			PacketSendUtility.sendPacket(activePlayer, new SM_UPDATE_ITEM(bag.getKinahItem()));
			sendPacket(new SM_TELEPORT_LOC(_tele.getMapId(), _tele.getX(), _tele.getY(), _tele.getZ()));
			TeleportService.getInstance().scheduleTeleportTask(activePlayer, _tele.getMapId(), _tele.getX(), _tele.getY(), _tele.getZ());
			return;
		}
		//flying teleport
		else if (_tele.getLocId() != 0 && _tele.getTeleportId() != 0)
		{
			if (bag.getKinahItem().getItemCount()<_tele.getPrice())
			{
				//Todo using the correct system message
				sendPacket(new SM_MESSAGE(0, null, "You don't have enough Kinah", null, ChatType.ANNOUNCEMENTS));
				return;
			}
			bag.getKinahItem().increaseItemCount(-1 * _tele.getPrice());
			PacketSendUtility.sendPacket(activePlayer, new SM_UPDATE_ITEM(bag.getKinahItem()));
			sendPacket(new SM_EMOTION(activePlayer.getObjectId(), 6, _tele.getTeleportId()));
			return;
		}
		else
		{
			log.info(String.format("Missing info at teleport_location.xml with locId: %d", locId));
			PacketSendUtility.sendMessage(activePlayer, "Missing info at teleport_location.xml with locId: "+locId);
		}
		
	}

}
