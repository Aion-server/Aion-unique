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
 * This error will be thrown if any of custom appenders won't be able to initialize. We have some logs there, so we
 * shouldn't do any actions with them in case of errors.
 * 
 * @author SoulKeeper
 */
public class AppenderInitializationError extends Error
{
	/**
	 * SerialID
	 */
	private static final long	serialVersionUID	= -6090251689433934051L;

	/**
	 * Creates new Error
	 */
	public AppenderInitializationError()
	{
	}

	/**
	 * Creates new error
	 * 
	 * @param message
	 *            error description
	 */
	public AppenderInitializationError(String message)
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
	public AppenderInitializationError(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Creates new error
	 * 
	 * @param cause
	 *            reason of this error
	 */
	public AppenderInitializationError(Throwable cause)
	{
		super(cause);
	}
}
