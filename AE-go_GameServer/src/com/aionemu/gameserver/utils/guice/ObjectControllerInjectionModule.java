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
package com.aionemu.gameserver.utils.guice;

import com.aionemu.gameserver.controllers.PlayerController;
import com.aionemu.gameserver.controllers.RiftController;
import com.aionemu.gameserver.controllers.StaticObjectController;
import com.aionemu.gameserver.controllers.factory.PlayerControllerFactory;
import com.aionemu.gameserver.controllers.factory.RiftControllerFactory;
import com.aionemu.gameserver.controllers.factory.StaticObjectControllerFactory;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryProvider;

/**
 * @author ATracer
 *
 */
public class ObjectControllerInjectionModule extends AbstractModule
{

	@Override
	protected void configure()
	{
		bind(PlayerControllerFactory.class).toProvider(
			FactoryProvider.newFactory(PlayerControllerFactory.class, PlayerController.class));
		bind(RiftControllerFactory.class).toProvider(
			FactoryProvider.newFactory(RiftControllerFactory.class, RiftController.class));
		bind(StaticObjectControllerFactory.class).toProvider(
			FactoryProvider.newFactory(StaticObjectControllerFactory.class, StaticObjectController.class));
	}

}
