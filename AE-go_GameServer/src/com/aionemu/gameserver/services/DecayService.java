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

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 *
 */
public class DecayService
{
	private static final int DECAY_DEFAULT_DELAY = 20000;

	private static DecayService instance = new DecayService();
	
	public Future<?> scheduleDecayTask(final Npc npc)
	{
		return ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				npc.getController().onDespawn(false);
			}
		}, DECAY_DEFAULT_DELAY);
	}
	
	public static DecayService getInstance()
	{
		return instance;
	}

}

