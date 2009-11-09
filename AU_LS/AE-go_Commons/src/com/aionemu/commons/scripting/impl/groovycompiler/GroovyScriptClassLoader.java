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

package com.aionemu.commons.scripting.impl.groovycompiler;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.groovy.tools.GroovyClass;

import com.aionemu.commons.scripting.ScriptClassLoader;

/**
 * @author SoulKeeper
 */
public class GroovyScriptClassLoader extends ScriptClassLoader
{

	protected Map<String, ClassContainer> classes;

	public GroovyScriptClassLoader() {
		super(new URL[]{});
	}

	public GroovyScriptClassLoader(ClassLoader parent) {
		super(new URL[]{}, parent);
	}

	public void setGroovyClasses(List<GroovyClass> groovyClasses)
	{
		Map<String, ClassContainer> result = new HashMap<String, ClassContainer>();
		for(GroovyClass c : groovyClasses){
			result.put(c.getName(), new ClassContainer(c.getBytes()));
		}
		classes = result;
	}

	@Override
	public Set<String> getCompiledClasses() {
		if(classes == null){
			return Collections.emptySet();
		} else {
			return Collections.unmodifiableSet(classes.keySet());
		}
	}

	@Override
	public byte[] getByteCode(String className) {
		byte[] org = classes.get(className).getBytecode();
		byte[] copy = new byte[org.length];
		System.arraycopy(org, 0, copy, 0, org.length);
		return copy;
	}

	@Override
	public Class<?> getDefinedClass(String name) {
		ClassContainer cc = classes.get(name);
		if(cc != null){
			return cc.getDefinedClass();
		}

		return null;
	}

	@Override
	public void setDefinedClass(String name, Class<?> clazz) {
		ClassContainer cc = classes.get(name);
		if(cc == null){
			throw new IllegalArgumentException("Attempt to set defined class for class that was not compiled?");
		}

		cc.setDefinedClass(clazz);
	}
}
