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
package com.aionemu.commons.log4j.filters;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

/**
 * Log4J filter that looks if there is exception present in the logging event and accepts event if present. Otherwise it
 * blocks filtring.
 * 
 * @author SoulKeeper
 */
public class ThrowablePresentFilter extends Filter
{
	/**
	 * Decides what to do with logging event.<br>
	 * This method accepts only log events that contain exceptions.
	 * 
	 * @param loggingEvent
	 *            log event that is going to be filtred.
	 * @return {@link org.apache.log4j.spi.Filter#ACCEPT} if throwable present, {@link org.apache.log4j.spi.Filter#DENY}
	 *         otherwise
	 */
	@Override
	public int decide(LoggingEvent loggingEvent)
	{
		Object message = loggingEvent.getMessage();

		if(message instanceof Throwable)
		{
			return ACCEPT;
		}

		ThrowableInformation information = loggingEvent.getThrowableInformation();

		// noinspection ThrowableResultOfMethodCallIgnored
		if(information != null && information.getThrowable() != null)
		{
			return ACCEPT;
		}

		return DENY;
	}
}
