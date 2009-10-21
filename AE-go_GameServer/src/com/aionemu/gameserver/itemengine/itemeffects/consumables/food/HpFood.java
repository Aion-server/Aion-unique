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
package com.aionemu.gameserver.itemengine.itemeffects.consumables.food;

import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.itemengine.itemeffects.consumables.potions.HpPotion;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author Avol
 *
 */

public class HpFood
{

	private static final Logger log = Logger.getLogger(HpPotion.class);

	private int stopAt;
	private int value;

	Future<?> task;

	private static HpFood instance = new HpFood();

	public void execute(final int value, int timerEnd, int timerInterval,final Player player) {

		if (timerInterval>0 && timerEnd>0)
		{
			this.stopAt = timerEnd / timerInterval;
			this.value = value;
			this.task = ThreadPoolManager.getInstance().scheduleAtFixedRate((new Runnable()
			{
				@Override
				public void run()
				{			
					if (stopAt>0) {
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
		}
	}	
}
