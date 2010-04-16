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
package com.aionemu.commons.scripting.scriptmanager;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.aionemu.commons.callbacks.Enhancable;
import com.aionemu.commons.scripting.ScriptContext;
import com.aionemu.commons.scripting.ScriptContextFactory;
import com.aionemu.commons.scripting.classlistener.ClassListener;
import com.aionemu.commons.scripting.scriptmanager.listener.ContextCreationListener;
import com.aionemu.commons.scripting.scriptmanager.listener.ContextReloadListener;
import com.aionemu.commons.scripting.scriptmanager.listener.ReloadListener;

/**
 * Class that represents managers of script contexes. It loads, reloads and unload script contexes. In the future it may
 * be extended to support programatic manipulation of contexes, but for now it's not needed. <br />
 * Example:
 * 
 * <pre>
 *      ScriptManager sm = new ScriptManager();
 *      sm.load(new File(&quot;st/contexts.xml&quot;));
 *      ...
 *      sm.shutdown();
 * </pre>
 * 
 * {@link ScriptContext} object creation listener can be added by using
 * {@link com.aionemu.commons.scripting.scriptmanager.listener.ContextCreationListener} {@link ScriptContext} object
 * reload listener can be added by using
 * {@link com.aionemu.commons.scripting.scriptmanager.listener.ContextReloadListener} {@link ScriptManager} reload
 * listener can be added by using {@link com.aionemu.commons.scripting.scriptmanager.listener.ReloadListener}
 * 
 * @author SoulKeeper, Aquanox
 */
public class ScriptManager
{
	/**
	 * Logger for script context
	 */
	private static final Logger	log			= Logger.getLogger(ScriptManager.class);

	/**
	 * Collection of script contexts
	 */
	private Set<ScriptContext>	contexts	= new HashSet<ScriptContext>();

	/**
	 * Global ClassListener instance. Autocatically setted for each new context. Fires after each successful
	 * compilation.
	 */
	private ClassListener		globalClassListener;

	/**
	 * Loads script contexes from descriptor
	 * 
	 * @param scriptDescriptor
	 *            xml file that describes contexes
	 * @throws Exception
	 *             if can't load file
	 */
	public synchronized void load(File scriptDescriptor) throws Exception
	{
		JAXBContext c = JAXBContext.newInstance(ScriptInfo.class, ScriptList.class);
		Unmarshaller u = c.createUnmarshaller();

		ScriptList list = (ScriptList) u.unmarshal(scriptDescriptor);

		for(ScriptInfo si : list.getScriptInfos())
		{
			ScriptContext context = createContext(si, null);
			if(context != null)
			{
				contexts.add(context);
				context.init();
			}
		}
	}

	/**
	 * Creates new context and checks to not produce copies
	 * 
	 * @param si
	 *            script context descriptor
	 * @param parent
	 *            parent script context
	 * @return created script context
	 * @throws Exception
	 *             if can't create context
	 */
	@Enhancable(callback = ContextCreationListener.class)
	public ScriptContext createContext(ScriptInfo si, ScriptContext parent) throws Exception
	{
		ScriptContext context = ScriptContextFactory.getScriptContext(si.getRoot(), parent);
		context.setLibraries(si.getLibraries());
		context.setCompilerClassName(si.getCompilerClass());

		if(parent == null && contexts.contains(context))
		{
			log.warn("Double root script context definition: " + si.getRoot().getAbsolutePath());
			return null;
		}

		if(si.getScriptInfos() != null && !si.getScriptInfos().isEmpty())
		{
			for(ScriptInfo child : si.getScriptInfos())
			{
				createContext(child, context);
			}
		}

		if(parent == null && globalClassListener != null)
			context.setClassListener(globalClassListener);

		return context;
	}

	/**
	 * Initializes shutdown on all contexes
	 */
	public synchronized void shutdown()
	{
		for(ScriptContext context : contexts)
		{
			context.shutdown();
		}

		contexts.clear();
	}

	/**
	 * Reloads all contexes
	 */
	@Enhancable(callback = ReloadListener.class)
	public synchronized void reload()
	{
		for(ScriptContext context : contexts)
		{
			reloadContext(context);
		}
	}

	/**
	 * Reloads specified context.
	 * 
	 * @param ctx
	 *            Script context instance.
	 */
	@Enhancable(callback = ContextReloadListener.class)
	public void reloadContext(ScriptContext ctx)
	{
		ctx.reload();
	}

	/**
	 * Returns unmodifiable set with script contexes
	 * 
	 * @return unmodifiable set of script contexes
	 */
	public synchronized Collection<ScriptContext> getScriptContexts()
	{
		return Collections.unmodifiableSet(contexts);
	}

	/**
	 * Set Global class listener instance.
	 * 
	 * @param instance
	 *            listener instance.
	 */
	public void setGlobalClassListener(ClassListener instance)
	{
		this.globalClassListener = instance;
	}
}
