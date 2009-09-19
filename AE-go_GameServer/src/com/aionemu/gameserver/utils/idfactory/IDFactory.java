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
package com.aionemu.gameserver.utils.idfactory;

import java.util.BitSet;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class is responsible for id generation for all Aion-Emu objects.<br>
 * This class is Thread-Safe.<br>
 * This class is designed to be very strict with id usage. Any illegal operation will throw {@link IDFactoryError}
 * 
 * @author SoulKeeper
 */
public class IDFactory
{

	/**
	 * Bitset that is used for all id's.<br>
	 * We are allowing BitSet to grow over time, so in the end it can be as big as {@link Integer#MAX_VALUE}
	 */
	private final BitSet		idList		= new BitSet();

	/**
	 * Synchronization of bitset
	 */
	private final ReentrantLock	lock		= new ReentrantLock();

	/**
	 * Id that will be used as minimal on next id request
	 */
	private int					nextMinId	= 0;

	/**
	 * Returns next free id.
	 * 
	 * @return next free id
	 * @throws IDFactoryError
	 *             if there is no free id's
	 */
	public int nextId()
	{
		try
		{
			lock.lock();

			int id;
			if(nextMinId == Integer.MIN_VALUE)
			{
				// Error will be thrown few lines later, we have no more free id's.
				// BitSet will throw IllegalArgumentException if nextMinId is negative
				id = Integer.MIN_VALUE;
			}
			else
			{
				id = idList.nextClearBit(nextMinId);
			}

			// If BitSet reached Integer.MAX_VALUE size and returned last free id before - it will return
			// Intger.MIN_VALUE as the next id, so we must catch such case and throw error (no free id's left)
			if(id == Integer.MIN_VALUE)
			{
				throw new IDFactoryError("All id's are used, please clear your database");
			}
			idList.set(id);

			// It ok to have Integer OverFlow here, on next ID request IDFactory will throw error
			nextMinId = id + 1;
			return id;
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * Locks given ids.
	 * 
	 * @param ids
	 *            ids to lock
	 * @throws IDFactoryError
	 *             if some of the id's were locked before
	 */
	public void lockIds(int... ids)
	{
		try
		{
			lock.lock();
			for(int id : ids)
			{
				boolean status = idList.get(id);
				if(status)
				{
					throw new IDFactoryError("ID " + id + " is already taken, fatal error!!!");
				}
				idList.set(id);
			}
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * Locks given ids.
	 * 
	 * @param ids
	 *            ids to lock
	 * @throws IDFactoryError
	 *             if some of the id's were locked before
	 */
	public void lockIds(Iterable<Integer> ids)
	{
		try
		{
			lock.lock();
			for(int id : ids)
			{
				boolean status = idList.get(id);
				if(status)
				{
					throw new IDFactoryError("ID " + id + " is already taken, fatal error!!!");
				}
				idList.set(id);
			}
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * Releases given id
	 * 
	 * @param id
	 *            id to release
	 * @throws IDFactoryError
	 *             if id was not taken earlier
	 */
	public void releaseId(int id)
	{
		try
		{
			lock.lock();
			boolean status = idList.get(id);
			if(!status)
			{
				throw new IDFactoryError("ID " + id + " is not taken, can't release it.");
			}
			idList.clear(id);
			if(id < nextMinId || nextMinId == Integer.MIN_VALUE)
			{
				nextMinId = id;
			}
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * Returns amount of used ids
	 * 
	 * @return amount of used ids
	 */
	public int getUsedCount()
	{
		try
		{
			lock.lock();
			return idList.cardinality();
		}
		finally
		{
			lock.unlock();
		}
	}
}
