/*
 * This file is part of aion-unique <www.aion-unique.com>.
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
package com.aionemu.gameserver.itemengine.handlers;

import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABNORMAL_STATE;

/**
 * @author Avol
 *
 */

public class HpItem
{

	private static final Logger log = Logger.getLogger(HpItem.class);

	public int value;
	public int timerInterval;
	public int timerEnd;
	public int stopAt;
	public int effect;

	Future<?> task;

	private static HpItem instance = new HpItem();

	public void execute(final int value, final int timerEnd, final int timerInterval, final int effect, final Player player) 
	{
		if (timerInterval>0 && timerEnd>0)
		{
			PacketSendUtility.sendPacket(player, new SM_ABNORMAL_STATE(1, effect, timerEnd*1000));
		
			this.stopAt = timerEnd / timerInterval;
			this.value = value;

			task = ThreadPoolManager.getInstance().scheduleAtFixedRate((new Runnable()
			{
				@Override
				public void run()
				{	
					if (stopAt>0) {	
						player.getLifeStats().increaseHp(value);
						stopAt--;
					} else {
						PacketSendUtility.sendPacket(player, new SM_ABNORMAL_STATE(0, effect, timerEnd));
						task.cancel(false);
					}
				}
			
			}), 0, timerInterval*1000);
		}
		else
		{
			player.getLifeStats().increaseHp(value);
		}
	}	
}
