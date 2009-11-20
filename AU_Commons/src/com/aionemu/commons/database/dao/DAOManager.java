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

import com.aionemu.commons.database.DatabaseConfig;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.DatabaseInfo;
import com.aionemu.commons.database.dao.scriptloader.DAOLoader;
import com.aionemu.commons.services.ScriptService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * This class manages {@link DAO} implementations, it resolves valid implementation for current database.<br>
 * In the end of use {@link #shutdown()} method should be called.
 * 
 * @author SoulKeeper
 */
@Service
public class DAOManager
{

	/**
	 * Logger for DAOManager class
	 */
	private static final Logger						log		= Logger.getLogger(DAOManager.class);

	/**
	 * Collection of registered DAOs
	 */
	private final Map<Class<? extends DAO>, DAO>	daoMap	= new HashMap<Class<? extends DAO>, DAO>();

	/**
	 * Object that manages DAO loading for this class
	 */
	private final DAOLoader daoLoader;


	/**
	 * Information about used database
	 */
	private final DatabaseInfo						databaseInfo;

	/**
	 * DatabaseConfig object reference
	 */
	private final DatabaseConfig					databaseConfig;

	/**
	 * ScriptService that will be used to load DAOs
	 */
	private ScriptService							scriptService;

	/**
	 * Creates new DAOManager
	 *
	 * @param databaseFactory
	 *            information about database that is used will be received using databaseFactory
	 * @param databaseConfig
	 *            database config that is used
     * @param scriptService
     *            Script service that will load DAOs
	 */
    @Autowired
	public DAOManager(DatabaseFactory databaseFactory, DatabaseConfig databaseConfig, ScriptService scriptService)
	{
		this.databaseInfo = databaseFactory.getDatabaseInfo();
		this.databaseConfig = databaseConfig;
        this.scriptService = scriptService;

		this.daoLoader = new DAOLoader(this, scriptService, databaseConfig, databaseInfo);
	}

    /**
     * Called after this bean was initialized.<br>
     * Launches script context load of DAO's
     */
    @PostConstruct
    public void initDAOs()
    {
		daoLoader.initDAOs();
    }

    /**
	 * Should be called in case of shutdown of DAOManager
	 */
	public void shutdown()
	{
        daoLoader.shutdown();
	}

	/**
	 * Returns DAO implementation by DAO class. Typical usage:
	 * <p/>
	 * 
	 * <pre>
	 * AccountDAO	dao	= DAOManager.getDAO(AccountDAO.class);
	 * </pre>
	 * 
	 * @param clazz
	 *            Base DAO class implementation of which was registered
	 * @param <T>
	 *            Subclass of DAO
	 * @return DAO implementation
	 * @throws DAONotFoundException
	 *             if DAO implementation not found
	 */
	@SuppressWarnings("unchecked")
	public <T extends DAO> T getDAO(Class<T> clazz) throws DAONotFoundException
	{
		DAO result;
		try {
			daoLoader.getLock().readLock().lock();
			result = daoMap.get(clazz);

			if (result == null) {
				String s = "DAO for class " + clazz.getName() + " not implemented";
				log.error(s);
				throw new DAONotFoundException(s);
			}
		} finally {
			daoLoader.getLock().readLock().unlock();
		}


		return (T) result;
	}

	/**
	 * Registers {@link DAO}.<br>
	 * First it checks if current DB is supported by calling {@link DAOUtils#isDBSupportedByDAO(Class, DatabaseInfo)},
	 * after that method ckecks if another DAO isn't in use already. If not - dao is successfully registered.<br>
	 * Should be called only from {@link com.aionemu.commons.database.dao.scriptloader.DAOLoader}
	 * 
	 * @param dao
	 *            DAO implementation object
	 * @throws DAOAlreadyRegisteredException
	 *             if DAO is already registered
	 * @throws IllegalAccessException
	 *             if something went wrong during instantiation of DAO
	 * @throws InstantiationException
	 *             if something went wrong during instantiation of DAO
	 * @throws DAOException
	 * 				if DAO metada is not valid(missing)
	 */
	public void registerDAO(DAO dao) throws DAOException, IllegalAccessException,
		InstantiationException
	{
		if(!DAOUtils.isSuitableClass(dao.getClass())){
			throw new DAOException("Something wrong with metadata, please check!");
		}

		if (!DAOUtils.isDBSupportedByDAO(dao.getClass(), databaseInfo))
		{
			log.debug("DAO " + dao.getClass().getName() + " skipped, " + databaseInfo + " is not supported.");
			return;
		}

		Class<? extends DAO> baseClass = DAOUtils.getBaseClass(dao.getClass());

		synchronized (this)
		{
			DAO oldDao = daoMap.get(baseClass);
			if (oldDao != null)
			{
				StringBuilder sb = new StringBuilder();
				sb.append("DAO with className ").append(baseClass).append(" is used by ");
				sb.append(oldDao.getClass().getName()).append(". Can't override with ");
				sb.append(dao.getClass().getName()).append(".");
				String s = sb.toString();
				log.error(s);
				throw new DAOAlreadyRegisteredException(s);
			}
			else
			{
                daoMap.put(baseClass, dao);
				log.info("Using DAO " + dao.getClass().getName() + " for " + databaseInfo);
			}
		}
	}

	/**
	 * Unregisters DAO class.<br>
	 * Should be called only from {@link com.aionemu.commons.database.dao.scriptloader.DAOLoader}
	 * 
	 * @param daoClass
	 *            DAO implementation of class to unregister
	 */
	@SuppressWarnings( { "unchecked" })
	public void unregisterDAO(Class<? extends DAO> daoClass)
	{
		Class<? extends DAO> baseClass = DAOUtils.getBaseClass(daoClass);

		synchronized (this)
		{
			for (DAO obj : daoMap.values())
			{
                if (obj.getClass() == daoClass)
				{
					daoMap.remove(baseClass);
					break;
				}
			}
		}
	}

	/**
	 * Just a check to ensure correct shutdown on DAO Manager
	 * 
	 * @throws Throwable
	 */
	@Override
	protected void finalize() throws Throwable
	{
		if (scriptService != null)
		{
			log.error("DAOManager was garbage collected, but shutdown wasn't called. Forcing shutdown.");
			shutdown();
		}
		super.finalize();
	}
}
