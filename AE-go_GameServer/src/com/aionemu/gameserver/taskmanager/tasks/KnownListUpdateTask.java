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
package com.aionemu.gameserver.taskmanager.tasks;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.taskmanager.AbstractPeriodicTaskManager;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author sphinx
 * 
 */
public class KnownListUpdateTask extends AbstractPeriodicTaskManager<VisibleObject>
{
	private static final class SingletonHolder
	{
		private static final KnownListUpdateTask	INSTANCE	= new KnownListUpdateTask();
	}

	public static KnownListUpdateTask getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private KnownListUpdateTask()
	{
		super(100);
	}

	public static enum KnownListUpdateMode
	{
		KNOWNLIST_UPDATE
		{
			@Override
			public void knownListTask(VisibleObject visibleObject)
			{
				visibleObject.updateKnownlistImpl();
			}
		},
		KNOWNLIST_CLEAR
		{
			@Override
			public void knownListTask(VisibleObject visibleObject)
			{
				visibleObject.clearKnownlistImpl();
			}
		},

		;

		private final byte	MASK;

		private KnownListUpdateMode()
		{
			MASK = (byte) (1 << ordinal());
		}

		public byte mask()
		{
			return MASK;
		}

		protected abstract void knownListTask(VisibleObject visibleObject);

		protected final void tryUpdateKnownList(final VisibleObject visibleObject, byte mask)
		{
			if((mask & mask()) == mask())
			{
				ThreadPoolManager.getInstance().scheduleTaskManager((new Runnable()
				{
					@Override
					public void run()
					{
						knownListTask(visibleObject);
					}
				}), 0);
				
				visibleObject.removeKnownListUpdateMask(this);
			}
		}
	}

	private static final KnownListUpdateMode[]	VALUES	= KnownListUpdateMode.values();

	@Override
	protected void callTask(VisibleObject visibleObject)
	{
		if(visibleObject != null)
		{
			for(byte mask; (mask = visibleObject.getKnownListUpdateMask()) != 0;)
			{
				for(KnownListUpdateMode mode : VALUES)
				{
					mode.tryUpdateKnownList(visibleObject, mask);
				}
			}
		}
	}

}
