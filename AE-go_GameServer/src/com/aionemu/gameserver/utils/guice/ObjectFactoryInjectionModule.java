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

import com.aionemu.gameserver.controllers.factory.ObjectControllerFactory;
import com.aionemu.gameserver.skillengine.task.SkillTaskFactory;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * @author ATracer
 *
 */
public class ObjectFactoryInjectionModule extends AbstractModule
{
	@Override
	protected void configure()
	{
		install(new FactoryModuleBuilder().build(ObjectControllerFactory.class));
		install(new FactoryModuleBuilder().build(SkillTaskFactory.class));
	}	
}
