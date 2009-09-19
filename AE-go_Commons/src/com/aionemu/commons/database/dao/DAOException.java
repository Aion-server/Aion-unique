/*
 * This file is part of aion-emu.
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

package com.aionemu.commons.database.dao;

/**
 * Generic DAO exception class
 * 
 * @author SoulKeeper
 */
public class DAOException extends RuntimeException
{
	private static final long serialVersionUID = 7637014806313099318L;

	/**
	 * {@inheritDoc}
	 */
	public DAOException()
	{
	}

	/**
	 * {@inheritDoc}
	 */
	public DAOException(String message)
	{
		super(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public DAOException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * {@inheritDoc}
	 */
	public DAOException(Throwable cause)
	{
		super(cause);
	}
}
