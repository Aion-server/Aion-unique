/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.gameserver.ai.events;

import com.aionemu.gameserver.ai.AI;

/**
 * This ai event should be invoked when NPC has nothing to do.<br>
 * <br>
 * 
 * Event has no parameters, so shared instance should be used.
 * 
 * @author SoulKeeper
 */
public class DoNothingEvent implements AIEvent
{

	/**
	 * Shared instance of DoNotihingEvent
	 */
	public static final DoNothingEvent	INSTANCE	= new DoNothingEvent();

	/**
	 * Private constructor so event can't be instantiated. Sharded instance should be used
	 */
	private DoNothingEvent()
	{

	}

	/**
	 * Handles default "I have nothing else to do" event for ai
	 * 
	 * @param ai
	 *            Ai intance that is executiong this event
	 */
	@Override
	public void handleEvent(AI ai)
	{
		// TODO: Default behavior for DoNothingEvent
	}
}
