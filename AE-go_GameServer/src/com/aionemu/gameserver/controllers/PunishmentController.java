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
package com.aionemu.gameserver.controllers;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerPunishmentsDAO;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapType;

/**
 * @author lord_rex
 * 
 */
public class PunishmentController
{
	// ***********************************************************
	// Prison Section
	private Player				player;
	private ScheduledFuture<?>	prisonTask;
	private boolean				isInPrison	= false;
	private long				prisonTimer	= 0;

	public PunishmentController(Player player)
	{
		this.player = player;
	}

	private class PrisonTask implements Runnable
	{
		@Override
		public void run()
		{
			setIsInPrison(false, 0);
		}
	}

	public Player getOwner()
	{
		return player;
	}

	public boolean isInPrison()
	{
		return isInPrison;
	}

	public void setIsInPrison(boolean state)
	{
		isInPrison = state;
	}

	public void setIsInPrison(boolean state, long delayInMinutes)
	{
		isInPrison = state;
		stopPrisonTask(false);

		if(isInPrison)
		{
			if(delayInMinutes > 0)
			{
				prisonTimer = delayInMinutes * 60000L;

				prisonTask = ThreadPoolManager.getInstance().schedule(new PrisonTask(), prisonTimer);
				PacketSendUtility.sendMessage(getOwner(), "You are in prison for " + delayInMinutes + " minutes.");
			}
			getOwner().getController().teleportTo(WorldMapType.PRISON.getId(), 256, 256, 49, 0);
			DAOManager.getDAO(PlayerPunishmentsDAO.class).punishPlayer(getOwner(), 1);
		}
		else
		{
			PacketSendUtility.sendMessage(getOwner(), "You removed from prison!");

			if(getOwner().getCommonData().getRace() == Race.ELYOS)
				getOwner().getController().teleportTo(WorldMapType.POETA.getId(), 806, 1242, 119, 0);
			else
				getOwner().getController().teleportTo(WorldMapType.ISHALGEN.getId(), 529, 2449, 281, 0);

			DAOManager.getDAO(PlayerPunishmentsDAO.class).unpunishPlayer(getOwner());
		}
	}

	public void stopPrisonTask(boolean save)
	{
		if(prisonTask != null)
		{
			if(save)
			{
				long delay = prisonTask.getDelay(TimeUnit.MILLISECONDS);
				if(delay < 0)
					delay = 0;
				setPrisonTimer(delay);
			}
			prisonTask.cancel(false);
			prisonTask = null;
		}
	}

	public long getPrisonTimer()
	{
		if(prisonTask != null)
			return prisonTask.getDelay(TimeUnit.MILLISECONDS);

		return prisonTimer;
	}

	public void setPrisonTimer(long time)
	{
		prisonTimer = time;
	}

	public void updatePrisonStatus()
	{
		if(isInPrison())
		{
			if(prisonTimer > 0)
			{
				prisonTask = ThreadPoolManager.getInstance().schedule(new PrisonTask(), prisonTimer);
				PacketSendUtility.sendMessage(getOwner(), "You are still in prison for "
					+ Math.round(prisonTimer / 60000) + " minutes.");
			}
			if(!(getOwner().getWorldId() == WorldMapType.PRISON.getId()))
				getOwner().getController().teleportTo(WorldMapType.PRISON.getId(), 256, 256, 49, 0);
		}
	}
}
