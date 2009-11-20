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
import com.aionemu.commons.database.dao.scriptloader.ComparatorType;
import com.aionemu.commons.database.dao.scriptloader.DAOInfo;
import com.aionemu.commons.database.dao.scriptloader.DisabledDAO;
import com.aionemu.commons.database.dao.scriptloader.SupportedDB;
import com.aionemu.commons.utils.ClassUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Modifier;

/**
 * DAO utility class
 *
 * @author SoulKeeper
 */
public class DAOUtils
{

	/**
	 * Logger
	 */
	private static final Logger log = Logger.getLogger(DAOUtils.class);

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

			return ComparatorType.compare(new ComparatorType[]{db.majorVersionComparator(), db.minorVersionComparator()},
					new ComparatorType.Pair[]{
							new ComparatorType.Pair(databaseInfo.getMajorVersion(), db.majorVersion()),
							new ComparatorType.Pair(databaseInfo.getMajorVersion(), db.minorVersion())
					}
			);
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

	/**
	 * Checks if class is suitable DAO classes to load/unload.<br>
	 * Class is counted as DAO if the following conditions are met:
	 * <ul>
	 * <li>Class is subclass of {@link com.aionemu.commons.database.dao.DAO}</li>
	 * <li>Class is not abstract</li>
	 * <li>Class is not interface</li>
	 * <li>Class is public</li>
	 * <li>Class doesn't have {@link DisabledDAO} annotation present</li>
	 * <li>Class is marked with {@link DAOInfo} annotation</li>
	 * </ul>
	 *
	 * @param clazz
	 *            class to check
	 * @return list of DAO classes to load/unload
	 */
	public static boolean isSuitableClass(Class clazz) {
		if (!ClassUtils.isSubclass(clazz, DAO.class)) {
			log.debug("Class " + clazz.getName() + " skipped, reason: Is not DAO subclass");
			return false;
		}

		if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
			log.debug("Class " + clazz.getName() + " skipped, reason: Is abstract or interface");
			return false;
		}

		if (!Modifier.isPublic(clazz.getModifiers())) {
			log.debug("Class " + clazz.getName() + " skipped, reason: Is not public");
			return false;
		}

		if (clazz.isAnnotationPresent(DisabledDAO.class)) {
			log.debug("Class " + clazz.getName() + " skipped, reason: Is disabled");
			return false;
		}

		if (!clazz.isAnnotationPresent(DAOInfo.class)) {
			log.debug("Class " + clazz.getName() + " skipped, reason: Has no annotation DAOInfo present");
			return false;
		}

		log.debug("Class " + clazz.getName() + " skipped, reason: Has no annotation DAOInfo present");
		return true;
	}
}
