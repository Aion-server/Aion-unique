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
 * This exception is thrown if DAO is already registered in {@link com.aionemu.commons.database.dao.DAOManager}
 * 
 * @author SoulKeeper
 */
public class DAOAlreadyRegisteredException extends DAOException
{
	private static final long serialVersionUID = -4966845154050833016L;

	/**
	 * {@inheritDoc}
	 */
	public DAOAlreadyRegisteredException()
	{
	}

	/**
	 * {@inheritDoc}
	 */
	public DAOAlreadyRegisteredException(String message)
	{
		super(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public DAOAlreadyRegisteredException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * {@inheritDoc}
	 */
	public DAOAlreadyRegisteredException(Throwable cause)
	{
		super(cause);
	}
}
