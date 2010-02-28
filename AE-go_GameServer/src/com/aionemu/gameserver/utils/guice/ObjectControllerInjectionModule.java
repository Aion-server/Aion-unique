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

import com.aionemu.gameserver.controllers.ActionitemController;
import com.aionemu.gameserver.controllers.BindpointController;
import com.aionemu.gameserver.controllers.CitizenController;
import com.aionemu.gameserver.controllers.GatherableController;
import com.aionemu.gameserver.controllers.MonsterController;
import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.controllers.PlayerController;
import com.aionemu.gameserver.controllers.PostboxController;
import com.aionemu.gameserver.controllers.RiftController;
import com.aionemu.gameserver.controllers.StaticObjectController;
import com.aionemu.gameserver.controllers.factory.ActionitemControllerFactory;
import com.aionemu.gameserver.controllers.factory.BindpointControllerFactory;
import com.aionemu.gameserver.controllers.factory.CitizenControllerFactory;
import com.aionemu.gameserver.controllers.factory.ControllerFactory;
import com.aionemu.gameserver.controllers.factory.GatherableControllerFactory;
import com.aionemu.gameserver.controllers.factory.MonsterControllerFactory;
import com.aionemu.gameserver.controllers.factory.NpcControllerFactory;
import com.aionemu.gameserver.controllers.factory.PlayerControllerFactory;
import com.aionemu.gameserver.controllers.factory.PostboxControllerFactory;
import com.aionemu.gameserver.controllers.factory.RiftControllerFactory;
import com.aionemu.gameserver.controllers.factory.StaticObjectControllerFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
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
		bind(ActionitemControllerFactory.class).toProvider(
			FactoryProvider.newFactory(ActionitemControllerFactory.class, ActionitemController.class));
		bind(BindpointControllerFactory.class).toProvider(
			FactoryProvider.newFactory(BindpointControllerFactory.class, BindpointController.class));
		bind(CitizenControllerFactory.class).toProvider(
			FactoryProvider.newFactory(CitizenControllerFactory.class, CitizenController.class));
		bind(GatherableControllerFactory.class).toProvider(
			FactoryProvider.newFactory(GatherableControllerFactory.class, GatherableController.class));
		bind(MonsterControllerFactory.class).toProvider(
			FactoryProvider.newFactory(MonsterControllerFactory.class, MonsterController.class));
		bind(NpcControllerFactory.class).toProvider(
			FactoryProvider.newFactory(NpcControllerFactory.class, NpcController.class));
		bind(PlayerControllerFactory.class).toProvider(
			FactoryProvider.newFactory(PlayerControllerFactory.class, PlayerController.class));
		bind(RiftControllerFactory.class).toProvider(
			FactoryProvider.newFactory(RiftControllerFactory.class, RiftController.class));
		bind(PostboxControllerFactory.class).toProvider(
			FactoryProvider.newFactory(PostboxControllerFactory.class, PostboxController.class));
		bind(StaticObjectControllerFactory.class).toProvider(
			FactoryProvider.newFactory(StaticObjectControllerFactory.class, StaticObjectController.class));
		
		bind(ControllerFactory.class).in(Scopes.SINGLETON);
	}

}
