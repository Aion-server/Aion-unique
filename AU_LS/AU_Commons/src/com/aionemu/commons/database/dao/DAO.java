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

package com.aionemu.commons.database.dao;

import com.aionemu.commons.database.PersistentObject;

/**
 * This class represents basic DAO.<br>
 * DAO implementation must match the set of conditions, check the
 * {@link com.aionemu.commons.database.dao.scriptloader.DAOLoader#getSuitableClasses(Class[])} for details.<br>
 * DAO subclass must have public no-arg constructor, in other case {@link InstantiationException} will be thrown by
 * {@link com.aionemu.commons.database.dao.DAOManager}<br>
 * 
 * DAO implementation should contain only three methods: save, load and remove.
 * 
 * @author SoulKeeper
 */
public interface DAO<TYPE extends PersistentObject<?>, ID_TYPE>
{

	/**
	 * Generic SAVE action
	 * 
	 * @param object
	 *            object to save
	 */
	public void save(TYPE object);

	/**
	 * Generic LOAD action
	 * 
	 * @param id
	 *            - primary key
	 * @return loads object by primary key
	 */
	public TYPE load(ID_TYPE id);

	/**
	 * Removes PersistentObject by id from DB.
	 * 
	 * @param id
	 *            Object primary key
	 */
	public void remove(ID_TYPE id);
}
