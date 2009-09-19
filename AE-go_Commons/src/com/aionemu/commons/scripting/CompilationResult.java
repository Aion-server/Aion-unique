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

/**
 * This class represents compilation result of script context
 * 
 * @author SoulKeeper
 */
public class CompilationResult
{

	/**
	 * List of classes that were compiled by compiler
	 */
	private final Class[]			compiledClasses;

	/**
	 * Classloader that was used to load classes
	 */
	private final ScriptClassLoader	classLoader;

	/**
	 * Creates new instance of CompilationResult with classes that has to be parsed and classloader that was used to
	 * load classes
	 * 
	 * @param compiledClasses
	 *            classes compiled by compiler
	 * @param classLoader
	 *            classloader that was used by compiler
	 */
	public CompilationResult(Class[] compiledClasses, ScriptClassLoader classLoader)
	{
		this.compiledClasses = compiledClasses;
		this.classLoader = classLoader;
	}

	/**
	 * Returns classLoader that was used by compiler
	 * 
	 * @return classloader that was used by compiler
	 */
	public ScriptClassLoader getClassLoader()
	{
		return classLoader;
	}

	/**
	 * Retunrs list of classes that were compiled
	 * 
	 * @return list of classes that were compiled
	 */
	public Class[] getCompiledClasses()
	{
		return compiledClasses;
	}
}
