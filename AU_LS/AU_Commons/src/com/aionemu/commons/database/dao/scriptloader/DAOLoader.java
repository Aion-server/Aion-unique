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

package com.aionemu.commons.database.dao.scriptloader;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.utils.ClassUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * DAOLoader for database script context
 * 
 * @author SoulKeeper
 */
public abstract class DAOLoader
{

	/**
	 * Logger
	 */
	private static final Logger	log	= Logger.getLogger(DAOLoader.class);

	/**
	 * DAOManager that will be used to register/unregister classes
	 */
	private final DAOManager	daoManager;

	/**
	 * Creates new instance of DAOLoader
	 * 
	 * @param daoManager
	 *            daomanager that will be used to register/unregister DAO classes
	 */
	public DAOLoader(DAOManager daoManager)
	{
		this.daoManager = daoManager;
	}

	/**
	 * Registers suitable DAOs in DAOManager
	 * 
	 * @param classes
	 *            classes that were loaded
	 */
	public void postLoad(Class<?>... classes)
	{

		for (Class<? extends DAO> c : getSuitableClasses(classes))
		{
			try
			{
				daoManager.registerDAO(c);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Unregisters suitable DAO's from DAOManager
	 * 
	 * @param classes
	 *            classes that were loaded
	 */
	public void preUnload(Class<?>... classes)
	{
		for (Class<? extends DAO> c : getSuitableClasses(classes))
		{
			daoManager.unregisterDAO(c);
		}
	}

	/**
	 * Returns list of sutable DAO classes to load/unload.<br>
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
	 * @param classes
	 *            array of classes to check
	 * @return list of DAO classes to load/unload
	 */
	@SuppressWarnings( { "unchecked" })
	private static Set<Class<? extends DAO>> getSuitableClasses(Class<?>[] classes)
	{
		Set<Class<? extends DAO>> result = new HashSet<Class<? extends com.aionemu.commons.database.dao.DAO>>();
		for (Class clazz : classes)
		{
			if (!ClassUtils.isSubclass(clazz, DAO.class))
			{
				log.debug("Class " + clazz.getName() + " skipped, reason: Is not DAO subclass");
				continue;
			}

			if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers()))
			{
				log.debug("Class " + clazz.getName() + " skipped, reason: Is abstract or interface");
				continue;
			}

			if (!Modifier.isPublic(clazz.getModifiers()))
			{
				log.debug("Class " + clazz.getName() + " skipped, reason: Is not public");
				continue;
			}

			if (clazz.isAnnotationPresent(DisabledDAO.class))
			{
				log.debug("Class " + clazz.getName() + " skipped, reason: Is disabled");
				continue;
			}

			if (!clazz.isAnnotationPresent(DAOInfo.class))
			{
				log.debug("Class " + clazz.getName() + " skipped, reason: Has no annotation DAOInfo present");
				continue;
			}

			log.debug("Treating class " + clazz.getName() + " as DAO class");
			result.add(clazz);
		}

		return result;
	}
}
