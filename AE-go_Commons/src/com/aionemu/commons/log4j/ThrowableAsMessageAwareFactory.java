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
package com.aionemu.commons.log4j;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

/**
 * Just a factory to create {@link com.aionemu.commons.log4j.ThrowableAsMessageLogger} loggers as default loggers.
 * 
 * @author SoulKeeper
 */
public class ThrowableAsMessageAwareFactory implements LoggerFactory
{
	/**
	 * Creates new logger with given name
	 * 
	 * @param name
	 *            new logger's name
	 * @return new logger instance
	 */
	@Override
	public Logger makeNewLoggerInstance(String name)
	{
		return new ThrowableAsMessageLogger(name);
	}
}
