package common;/*
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

import java.util.HashSet;
import java.util.Set;
import java.lang.reflect.Modifier;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.scripting.ScriptClassLoader;
import com.aionemu.commons.scripting.metadata.OnClassLoad;
import com.aionemu.commons.scripting.metadata.OnClassUnload;
import com.aionemu.commons.utils.ClassUtils;

/**
 * Utility class that loads all DAO's in classPath of this script context.<br>
 * DAO should be public, not abstract, not interface, must have default no-arg public constructor.
 * 
 * @author SoulKeeper
 */
public class DAOLoader
{

	/**
	 * Logger
	 */
	private static final Logger	log	= Logger.getLogger(DAOLoader.class);

	/**
	 * Loads all DAO's that were loaded by this class loader
	 */
	@SuppressWarnings( { "UnusedDeclaration" })
	@OnClassLoad
	private static void loadDAOs()
	{
		for (Class<? extends DAO> c : getSuitableClasses())
		{
			try
			{
				DAOManager.registerDAO(c);
			}
			catch (Exception e)
			{
				log.error("Can't register DAO class", e);
			}
		}
	}

	/**
	 * Unloads all DAO's that were loaded by this classloader
	 */
	@SuppressWarnings( { "UnusedDeclaration" })
	@OnClassUnload
	private static void unloadDAOs()
	{
		for (Class<? extends DAO> c : getSuitableClasses())
		{
			DAOManager.unregisterDAO(c);
		}
	}

	/**
	 * Returns list of sutable DAO classes to load/unload
	 * 
	 * @return list of DAO classes to load/unload
	 */
	@SuppressWarnings( { "unchecked" })
	private static Set<Class<? extends DAO>> getSuitableClasses()
	{
		ScriptClassLoader cl = (ScriptClassLoader) DAOLoader.class.getClassLoader();
		Set<Class<? extends DAO>> classes = new HashSet<Class<? extends DAO>>();
		for (String s : cl.getCompiledClasses())
		{
			try
			{
				Class clazz = Class.forName(s, true, cl);

				if (!ClassUtils.isSubclass(clazz, DAO.class))
				{
					continue;
				}

				if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers()))
				{
					continue;
				}

				if (!Modifier.isPublic(clazz.getModifiers()))
				{
					continue;
				}

				if (clazz.isAnnotationPresent(DisabledDAO.class))
				{
					continue;
				}

				classes.add(clazz);

			}
			catch (ClassNotFoundException e)
			{
				log.error("Can't resolve loaded class", e);
			}
		}

		return classes;
	}
}
