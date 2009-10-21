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
package com.aionemu.gameserver.itemengine.itemeffects.consumables.potions;

import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author Avol
 *
 */

public class HpPotion
{

	private static final Logger log = Logger.getLogger(HpPotion.class);

	public int value;
	public int timerInterval;
	public int timerEnd;
	public int stopAt;
	Future<?> task;

	private static HpPotion instance = new HpPotion();

	public void execute(final int value, final int timerEnd, final int timerInterval,final Player player) 
	{
		this.value = value;
		this.timerEnd = timerEnd;
		this.timerInterval = timerInterval;
		this.stopAt = timerEnd / timerInterval;

		if (timerInterval>0 && timerEnd>0)
		{
			task = ThreadPoolManager.getInstance().scheduleAtFixedRate((new Runnable()
			{
				@Override
				public void run()
				{	
					if (stopAt>0) {	
						log.info("heal: "+ value);
						player.getLifeStats().increaseHp(value);
						stopAt--;
					} else {
						task.cancel(false);
					}
				}
			
			}), 0, timerInterval*1000);
		}
		else
		{
			player.getLifeStats().increaseHp(value);
			log.info("instant");
		}
	}	
}
