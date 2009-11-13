/*
 * This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.model.gameobjects.stats;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATUPDATE_MP;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 *
 */
public class PlayerLifeStats extends CreatureLifeStats<Player>
{

	public PlayerLifeStats(Player owner, int currentHp, int currentMp)
	{
		super(owner,currentHp,currentMp);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.gameobjects.stats.CreatureLifeStats#onReduceHp()
	 */
	@Override
	protected void onReduceHp()
	{
		triggerRestoreTask();
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.gameobjects.stats.CreatureLifeStats#onReduceMp()
	 */
	@Override
	protected void onReduceMp()
	{
		sendMpPacketUpdate();		
		triggerRestoreTask();
	}
	
	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.gameobjects.stats.CreatureLifeStats#onIncreaseMp()
	 */
	@Override
	protected void onIncreaseMp()
	{
		sendMpPacketUpdate();
	}

	/**
	 * Informs player about MP change
	 */
	private void sendMpPacketUpdate()
	{
		if(owner == null)
			return;

		PacketSendUtility.sendPacket((Player) owner, new SM_STATUPDATE_MP(currentMp, getMaxMp()));
	}
}
