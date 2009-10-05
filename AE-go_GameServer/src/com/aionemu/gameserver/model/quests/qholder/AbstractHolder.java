/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.quests.qholder;

import org.apache.log4j.Logger;

/**
 * @autor: Blackmouse
 */
public abstract class AbstractHolder
{
	protected Logger _log = Logger.getLogger(getClass().getSimpleName());

	public void log()
	{
		_log.info(String.format("[%s] load %d %s count.", getClass().getSimpleName(), size(), getClass().getSimpleName().replace("Holder", "").toLowerCase()));
	}

	public abstract int size();

	public abstract void clear();
}