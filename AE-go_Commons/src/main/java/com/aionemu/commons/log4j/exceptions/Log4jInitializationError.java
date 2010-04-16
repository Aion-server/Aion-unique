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
package com.aionemu.commons.log4j.exceptions;

/**
 * This class is thrown when logging system cant be initialized
 * 
 * @author SoulKeeper
 */
public class Log4jInitializationError extends Error
{
	/**
	 * SerialID
	 */
	private static final long	serialVersionUID	= -628697707807736993L;

	/**
	 * Creates new Error
	 */
	public Log4jInitializationError()
	{
	}

	/**
	 * Creates new error
	 * 
	 * @param message
	 *            error description
	 */
	public Log4jInitializationError(String message)
	{
		super(message);
	}

	/**
	 * Creates new error
	 * 
	 * @param message
	 *            error description
	 * @param cause
	 *            reason of this error
	 */
	public Log4jInitializationError(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Creates new error
	 * 
	 * @param cause
	 *            reason of this error
	 */
	public Log4jInitializationError(Throwable cause)
	{
		super(cause);
	}
}
