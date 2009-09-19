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
 * This interface is basic event notifier for AI.<br>
 * Each event should implement {@link #handleEvent(com.aionemu.gameserver.ai.AI)} with default behaviour.<br>
 * AI can override {@link com.aionemu.gameserver.ai.AI#handleEvent(AIEvent)} with custom behaviour.<br>
 * <br>
 * To save memory and CPU resources events that doesn't depend on any parameters should be implemented as singletons.
 * 
 * @author SoulKeeper
 * @see com.aionemu.gameserver.ai.AI
 * @see com.aionemu.gameserver.ai.AI#handleEvent(AIEvent)
 */
public interface AIEvent
{

	/**
	 * This method should do basic event handling for ai by this event.<br>
	 * When {@link com.aionemu.gameserver.ai.AI#handleEvent(AIEvent)} is called - it delegates execution to this mehtod
	 * and passes active AI as argument.<br>
	 * However AI can override default event handling, so this method can be not called at all.
	 * 
	 * @param ai
	 *            active ai instance that has to handle this event.
	 */
	public void handleEvent(AI ai);
}
