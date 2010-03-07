/*
 * This file is part of aion-unique <aion-unique.org>.
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

import java.util.concurrent.Future;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerPunishmentsDAO;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapType;
import com.google.inject.Inject;

/**
 * @author lord_rex
 */
public class PunishmentService
{
	@Inject
	TeleportService teleportService;
	
	/**
	 * This method will handle moving or removing a player from prison
	 * @param player
	 * @param state
	 * @param delayInMinutes
	 */
	public void setIsInPrison(Player player, boolean state, long delayInMinutes)
	{
		stopPrisonTask(player, false);
		if(state)
		{
			long prisonTimer = player.getPrisonTimer();
			if(delayInMinutes > 0)
			{
				prisonTimer = delayInMinutes * 60000L;
				schedulePrisonTask(player, prisonTimer);
				PacketSendUtility.sendMessage(player, "You are in prison for " + delayInMinutes + " minutes.");
			}
			teleportService.teleportTo(player, WorldMapType.PRISON.getId(), 256, 256, 49, 0);
			DAOManager.getDAO(PlayerPunishmentsDAO.class).punishPlayer(player, 1);
		}
		else
		{
			PacketSendUtility.sendMessage(player, "You removed from prison!");
			player.setPrisonTimer(0);

			if(player.getCommonData().getRace() == Race.ELYOS)
				teleportService.teleportTo(player, WorldMapType.POETA.getId(), 806, 1242, 119, 0);
			else
				teleportService.teleportTo(player, WorldMapType.ISHALGEN.getId(), 529, 2449, 281, 0);

			DAOManager.getDAO(PlayerPunishmentsDAO.class).unpunishPlayer(player);
		}
	}

	/**
	 * This method will stop the prison task
	 * @param playerObjId
	 */
	public void stopPrisonTask(Player player, boolean save)
	{
		Future<?> prisonTask = player.getController().getTask(TaskId.PRISON.ordinal());
		if(prisonTask != null)
		{
			if(save)
			{
				long delay = player.getPrisonTimer();
				if(delay < 0)
					delay = 0;
				player.setPrisonTimer(delay);
			}
			player.getController().cancelTask(TaskId.PRISON.ordinal());
		}
	}

	/**
	 * This method will update the prison status
	 * @param player
	 */
	public void updatePrisonStatus(Player player)
	{
		if(player.isInPrison())
		{
			long prisonTimer = player.getPrisonTimer();
			if(prisonTimer > 0)
			{
				schedulePrisonTask(player, prisonTimer);
				PacketSendUtility.sendMessage(player, "You are still in prison for "
					+ Math.round(prisonTimer / 60000) + " minutes.");
			}
			if(!(player.getWorldId() == WorldMapType.PRISON.getId()))
				teleportService.teleportTo(player, WorldMapType.PRISON.getId(), 256, 256, 49, 0);
		}
	}

	/**
	 * This method will schedule a prison task
	 * @param player
	 * @param prisonTimer
	 */
	private void schedulePrisonTask(final Player player, long prisonTimer)
	{
		player.setPrisonTimer(prisonTimer);
		player.getController().addTask(TaskId.PRISON.ordinal(), ThreadPoolManager.getInstance().schedule(new Runnable(){
			@Override
			public void run()
			{
				setIsInPrison(player, false, 0);
			}
		}, prisonTimer));
	}
}
