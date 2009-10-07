/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
package com.aionemu.gameserver.skillengine.loader;

import java.lang.reflect.Modifier;

import org.apache.log4j.Logger;

import com.aionemu.commons.scripting.classlistener.ClassListener;
import com.aionemu.commons.scripting.classlistener.DefaultClassListener;
import com.aionemu.commons.utils.ClassUtils;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.SkillHandler;
import com.google.inject.Injector;

/**
 * @author ATracer
 *
 */
public class SkillHandlerLoader extends DefaultClassListener
		implements ClassListener
{
	private static final Logger logger = Logger.getLogger(SkillHandlerLoader.class);

	private final Injector injector;
	
	private final SkillEngine skillEngine;

	public SkillHandlerLoader(Injector injector, SkillEngine skillEngine)
	{
		this.injector = injector;
		this.skillEngine = skillEngine;
	}

	@Override
	public void postLoad(Class<?>[] classes)
	{
		for (Class<?> c : classes)
		{
			if (logger.isDebugEnabled())
				logger.debug("Load class " + c.getName());

			if (!isValidClass(c))
				continue;

			if (ClassUtils.isSubclass(c, SkillHandler.class))
			{
				skillEngine.registerSkill((SkillHandler) injector.getInstance(c));
			}
		}

		super.postLoad(classes);
	}

	@Override
	public void preUnload(Class<?>[] classes)
	{

	}

	public boolean isValidClass(Class<?> clazz)
	{
		final int modifiers = clazz.getModifiers();

		if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers))
			return false;

		if (!Modifier.isPublic(modifiers))
			return false;

		return true;
	}

}
