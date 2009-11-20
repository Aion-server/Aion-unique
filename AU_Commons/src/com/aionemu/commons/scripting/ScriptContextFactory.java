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

package com.aionemu.commons.scripting;

import com.aionemu.commons.scripting.impl.ScriptContextImpl;
import com.aionemu.commons.scripting.scriptmanager.ScriptInfo;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * This class is script context provider. We can switch to any other ScriptContext implementation later, so it's good to
 * have factory class
 * 
 * @author SoulKeeper
 */
@Service
public final class ScriptContextFactory implements ApplicationContextAware
{
    /**
     * Root application context, injected by spring
     */
    private ApplicationContext parentApplicationContext;

	/**
	 * Creates script context, sets the root context. Adds child context if needed
	 * 
	 * @param si
	 *            information about script context
	 * @param parent
	 *            parent of new ScriptContext
	 * @return ScriptContext
	 * 			  with presetted root file
	 * @throws InstantiationException
	 *             if java compiler is not aviable
	 */
	public ScriptContext getScriptContext(ScriptInfo si, ScriptContext parent) throws InstantiationException
	{
		ScriptContextImpl ctx;
		if (parent == null)
		{
			ctx = new ScriptContextImpl(si.getRoot(), si.getSpringDescriptorFile(), parentApplicationContext);
		}
		else
		{
			ctx = new ScriptContextImpl(si.getRoot(), si.getSpringDescriptorFile(), parent);
			parent.addChildScriptContext(ctx);
		}
		return ctx;
	}

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.parentApplicationContext = applicationContext;
    }
}
