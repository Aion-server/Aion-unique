/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.utils.guice;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dao.LegionDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.utils.idfactory.IDFactoryAionObject;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * This module is responsible for initializing and providing IDFactories.
 * 
 * @author Luno
 * 
 */
public class IDFactoriesInjectionModule extends AbstractModule
{
	@Override
	protected void configure()
	{
		// TODO Auto-generated method stub

	}

	@Provides
	@IDFactoryAionObject
	@Singleton
	IDFactory provideAionObjectIdFactory()
	{
		IDFactory idFactory = new IDFactory();
		idFactory.lockIds(0);
		// Here should be calls to all IDFactoryAwareDAO implementations to initialize
		// used values in IDFactory
		idFactory.lockIds(DAOManager.getDAO(PlayerDAO.class).getUsedIDs());
		idFactory.lockIds(DAOManager.getDAO(InventoryDAO.class).getUsedIDs());
		idFactory.lockIds(DAOManager.getDAO(LegionDAO.class).getUsedIDs());

		return idFactory;
	}

	@Provides
	IDFactory provideIDFactory()
	{
		throw new Error("Annotate IDFactory variable declarations!");
	}
}
