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
package com.aionemu.gameserver.controllers;

import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.ai.events.Event;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.NpcLifeStats;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.DropService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class is for controlling Npc's
 * 
 * @author -Nemesiss-, ATracer (2009-09-29)
 */
public class NpcController extends CreatureController<Npc>
{

	private static Logger log = Logger.getLogger(NpcController.class);

	protected Future<?> decayTask;

	protected DropService dropService;

	public void setDropService(DropService dropService)
	{
		this.dropService = dropService;
	}

	@Override
	public void onRespawn()
	{
		this.decayTask = null;
		this.getOwner().setLifeStats(new NpcLifeStats(getOwner()));
	}

	@Override
	public Npc getOwner()
	{
		return (Npc) super.getOwner();
	}

	@Override
	public void onDialogRequest(Player player)
	{
		if (QuestEngine.getInstance().onDialog(new QuestEnv(getOwner(), player, 0 , -1)))
			return;
		//TODO need check here
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 10));
	}
	
}