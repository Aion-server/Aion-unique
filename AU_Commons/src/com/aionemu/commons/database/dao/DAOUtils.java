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

import com.aionemu.commons.database.DatabaseInfo;
import com.aionemu.commons.database.dao.scriptloader.DAOInfo;
import com.aionemu.commons.database.dao.scriptloader.SupportedDB;

/**
 * DAO utility class
 * 
 * @author SoulKeeper
 */
public class DAOUtils
{

	/**
	 * Checks if DAO supports current DB
	 * 
	 * @param clazz
	 *            DAO class
	 * @param databaseInfo
	 *            Information about DB
	 * @return true if supports, false in other case
	 * @throws IllegalArgumentException
	 *             if DAO class is not marked with {@link com.aionemu.commons.database.dao.scriptloader.DAOInfo}
	 *             annotation
	 */
	public static boolean isDBSupportedByDAO(Class<? extends DAO> clazz, DatabaseInfo databaseInfo)
	{
		if (!clazz.isAnnotationPresent(DAOInfo.class))
		{
			throw new IllegalArgumentException("Class " + clazz.getName()
				+ " should be marked with @DAOInfo annotation");
		}

		DAOInfo info = clazz.getAnnotation(DAOInfo.class);
		SupportedDB[] databases = info.supportedDBs();

		for (SupportedDB db : databases)
		{
			if (!db.name().equals(databaseInfo.getProductName()))
			{
				continue;
			}

			if (!db.majorVersionComparator().compare(db.majorVersion(), databaseInfo.getMajorVersion()))
			{
				continue;
			}

			if (!db.minorVersionComparator().compare(db.minorVersion(), databaseInfo.getMinorVersion()))
			{
				continue;
			}

			return true;
		}

		return false;
	}

	/**
	 * Returns base class for dao class.
	 * 
	 * @param clazz
	 *            class to check
	 * @return base class for dao
	 * @throws IllegalArgumentException
	 *             if argument is not marked with {@link DAOInfo} annotation
	 */
	public static Class<? extends DAO> getBaseClass(Class<? extends DAO> clazz)
	{
		if (!clazz.isAnnotationPresent(DAOInfo.class))
		{
			throw new IllegalArgumentException("Class " + clazz.getName()
				+ " should be marked with @DAOInfo annotation");
		}

		return clazz.getAnnotation(DAOInfo.class).baseClass();
	}
}
