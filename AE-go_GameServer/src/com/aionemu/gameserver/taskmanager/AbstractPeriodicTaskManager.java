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
package com.aionemu.gameserver.taskmanager;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.aionemu.commons.taskmanager.AbstractLockManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.GameServer.StartupHook;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author lord_rex and MrPoke
 * 
 */
public abstract class AbstractPeriodicTaskManager<T> extends AbstractLockManager implements Runnable, StartupHook
{
	protected static final Logger	log		= Logger.getLogger(AbstractPeriodicTaskManager.class);
	private final ArrayList<T>	LIST	= new ArrayList<T>();
	private final int			PERIOD;

	public AbstractPeriodicTaskManager(int period)
	{
		PERIOD = period;

		GameServer.addStartupHook(this);

		log.info(getClass().getSimpleName() + ": Initialized.");
	}

	public final void add(T t)
	{
		writeLock();
		try
		{
			LIST.add(t);
		}
		finally
		{
			writeUnlock();
		}
	}

	private final T getFirst()
	{
		writeLock();
		try
		{
			T next = null;
			Iterator<T> it = LIST.iterator();
			if(it.hasNext())
				next = it.next();
			return next;

		}
		finally
		{
			writeUnlock();
		}
	}

	private final T removeFirst()
	{
		writeLock();
		try
		{
			T next = null;
			Iterator<T> it = LIST.iterator();
			if(it.hasNext())
			{
				next = it.next();
				LIST.remove(next);
			}
			return next;
		}
		finally
		{
			writeUnlock();
		}
	}

	@Override
	public final void onStartup()
	{
		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 1000 + Rnd.get(PERIOD),
			Rnd.get(PERIOD - 5, PERIOD + 5));
	}

	@Override
	public final void run()
	{
		for(T task; (task = getFirst()) != null;)
		{
			try
			{
				callTask(task);
			}
			catch(RuntimeException e)
			{
				log.error("", e);
			}
			finally
			{
				removeFirst();
			}
		}
	}

	protected abstract void callTask(T task);
}
