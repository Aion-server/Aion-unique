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

package com.aionemu.commons.scripting.impl;

import com.aionemu.commons.scripting.CompilationResult;
import com.aionemu.commons.scripting.ScriptCompiler;
import com.aionemu.commons.scripting.ScriptContext;
import com.aionemu.commons.scripting.ScriptingSpringApplicationContext;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is actual implementation of {@link com.aionemu.commons.scripting.ScriptContext}
 * 
 * @author SoulKeeper
 */
public class ScriptContextImpl implements ScriptContext
{

	/**
	 * logger for this class
	 */
	private static final Logger	log	= Logger.getLogger(ScriptContextImpl.class);

	/**
	 * Script context that is parent for this script context
	 */
	private final ScriptContext	parentScriptContext;

	/**
	 * Libraries (list of jar files) that have to be loaded class loader
	 */
	private Iterable<File>		libraries;

	/**
	 * Root directory of this script context. It and it's subdirectories will be scanned for .java files.
	 */
	private final File			root;

	/**
	 * Result of compilation of script context
	 */
	private CompilationResult	compilationResult;

	/**
	 * List of child script contexts
	 */
	private Set<ScriptContext>	childScriptContexts;

	/**
	 * Class name of the compiler that will be used to compile sources
	 */
	private String				compilerClassName;

    /**
     * Parent Spring ApplicationContext for this ScriptContext.<br>
     * Used only in case if {@link #parentScriptContext} == null
     */
    private ApplicationContext parentApplicationContext;

    /**
     * Current ApplicationContext for this ScriptContext
     */
    private ConfigurableApplicationContext applicationContext;

    /**
     * File that points to applicationContext xml location
     */
    private File applicationContextDescriptor;

	/**
	 * Creates new scriptcontext with given root file
	 * 
	 * @param root
	 *            file that represents root directory of this script context
	 * @param applicationContextDescriptor
     *              Spring applicationContext descriptor for scripts.
     * @param parent
     *              parent ApplicationContext
     * @throws NullPointerException
	 *             if root is null
	 * @throws IllegalArgumentException
	 *             if root directory doesn't exists or is not a directory
	 */
	public ScriptContextImpl(File root, File applicationContextDescriptor, ApplicationContext parent)
	{
		this(root, applicationContextDescriptor, (ScriptContext)null);
        this.parentApplicationContext = parent;
	}

	/**
	 * Creates new ScriptContext with given file as root and another ScriptContext as parent
	 * 
	 * @param root
	 *            file that represents root directory of this script context
     * @param applicationContextDescriptor
     *              applicationContextDescriptor path for spring
	 * @param parent
	 *            parent ScriptContex. It's classes and libraries will be accessible for this script context
     * @throws NullPointerException
	 *             if root is null
	 * @throws IllegalArgumentException
	 *             if root directory doesn't exists or is not a directory
	 */
	public ScriptContextImpl(File root, File applicationContextDescriptor, ScriptContext parent)
	{
		if (root == null)
		{
			throw new NullPointerException("Root file must be specified");
		}

		if (!root.exists() || !root.isDirectory())
		{
			throw new IllegalArgumentException("Root directory not exists or is not a directory");
		}

		this.root = root;
		this.parentScriptContext = parent;
        this.applicationContextDescriptor = applicationContextDescriptor;
	}

	@Override
	@SuppressWarnings( { "ThrowableInstanceNeverThrown" })
	public synchronized void init()
	{

		if (compilationResult != null)
		{
			log.error(new Exception("Init request on initialized ScriptContext"));
			return;
		}

		ScriptCompiler scriptCompiler = instantiateCompiler();

		@SuppressWarnings("unchecked")
		Collection<File> files = FileUtils.listFiles(root, scriptCompiler.getSupportedFileTypes(), true);

		if (parentScriptContext != null)
		{
			scriptCompiler.setParentClassLoader(parentScriptContext.getCompilationResult().getClassLoader());
		}

		scriptCompiler.setLibraires(libraries);
		compilationResult = scriptCompiler.compile(files);

		applicationContext = new ScriptingSpringApplicationContext(applicationContextDescriptor, compilationResult.getClassLoader(), getParentApplicationContext());

		if (childScriptContexts != null)
		{
			for (ScriptContext context : childScriptContexts)
			{
				context.init();
			}
		}
	}

	@Override
	@SuppressWarnings( { "ThrowableInstanceNeverThrown" })
	public synchronized void shutdown()
	{

		if (compilationResult == null)
		{
			log.error("Shutdown of not initialized stript context", new Exception());
			return;
		}

		if (childScriptContexts != null)
		{
			for (ScriptContext child : childScriptContexts)
			{
				child.shutdown();
			}
		}

		applicationContext.close();
        applicationContext = null;
		compilationResult = null;
	}

	@Override
	public void reload()
	{
		shutdown();
		init();
	}

	@Override
	public File getRoot()
	{
		return root;
	}

	@Override
	public CompilationResult getCompilationResult()
	{
		return compilationResult;
	}

	@Override
	public synchronized boolean isInitialized()
	{
		return compilationResult != null;
	}

	@Override
	public void setLibraries(Iterable<File> files)
	{
		this.libraries = files;
	}

	@Override
	public Iterable<File> getLibraries()
	{
		return libraries;
	}

	@Override
	public ScriptContext getParentScriptContext()
	{
		return parentScriptContext;
	}

	@Override
	public Collection<ScriptContext> getChildScriptContexts()
	{
		return childScriptContexts;
	}

	@Override
	public void addChildScriptContext(ScriptContext context)
	{

		synchronized (this)
		{
			if (childScriptContexts == null)
			{
				childScriptContexts = new HashSet<ScriptContext>();
			}

			if (childScriptContexts.contains(context))
			{
				log.error("Double child definition, root: " + root.getAbsolutePath() + ", child: "
					+ context.getRoot().getAbsolutePath());
				return;
			}

			if (isInitialized())
			{
				context.init();
			}
		}

		childScriptContexts.add(context);
	}

    @Override
    public ConfigurableApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    @Override
    public ApplicationContext getParentApplicationContext()
    {
        if(getParentScriptContext() != null)
        {
            return getParentScriptContext().getApplicationContext();
        }
        else
        {
            return parentApplicationContext;
        }
    }

    @Override
	public void setCompilerClassName(String className)
	{
		this.compilerClassName = className;
	}

	@Override
	public String getCompilerClassName()
	{
		return this.compilerClassName;
	}

	/**
	 * Creates new instance of ScriptCompiler that should be used with this ScriptContext
	 * 
	 * @return instance of ScriptCompiler
	 * @throws RuntimeException
	 *             if failed to create instance
	 */
	protected ScriptCompiler instantiateCompiler() throws RuntimeException
	{
		ClassLoader cl = getClass().getClassLoader();
		if (getParentScriptContext() != null)
		{
			cl = getParentScriptContext().getCompilationResult().getClassLoader();
		}

		ScriptCompiler sc;
		try
		{
			sc = (ScriptCompiler) Class.forName(getCompilerClassName(), true, cl).newInstance();
		}
		catch (Exception e)
		{
			RuntimeException e1 = new RuntimeException("Can't create instance of compiler", e);
			log.error(e1);
			throw e1;
		}

		return sc;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ScriptContextImpl))
		{
			return false;
		}

		ScriptContextImpl another = (ScriptContextImpl) obj;

		if (parentScriptContext == null)
		{
			return another.getRoot().equals(root);
		}
		else
		{
			return another.getRoot().equals(root) && parentScriptContext.equals(another.parentScriptContext);
		}
	}

	@Override
	public int hashCode()
	{
		int result = parentScriptContext != null ? parentScriptContext.hashCode() : 0;
		result = 31 * result + root.hashCode();
		return result;
	}

	@Override
	public void finalize() throws Throwable
	{
		if (compilationResult != null)
		{
			log.error("Finalization of initialized ScriptContext. Forcing context shutdown.");
			shutdown();
		}
		super.finalize();
	}
}
