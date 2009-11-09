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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.Phases;
import org.codehaus.groovy.tools.GroovyClass;

import com.aionemu.commons.scripting.ScriptCompiler;
import com.aionemu.commons.scripting.ScriptClassLoader;
import com.aionemu.commons.scripting.CompilationResult;

import groovy.lang.GroovyClassLoader;

/**
 * @author SoulKeeper
 */
public class GroovyScriptCompilerImpl implements ScriptCompiler {

	private static final String[] SUPPORTED_FILE_TYPES = {"java", "groovy"};

	private ScriptClassLoader parentClassLoader;

	private Iterable<File> libraries;

	@Override
	public void setParentClassLoader(ScriptClassLoader classLoader) {
		this.parentClassLoader = classLoader;
	}

	@Override
	public void setLibraires(Iterable<File> files) {
		this.libraries = files;
	}

	@Override
	public CompilationResult compile(String className, String sourceCode) {
		return compile(new String[]{className}, new String[]{sourceCode});
	}

	@Override
	public CompilationResult compile(String[] className, String[] sourceCode) throws IllegalArgumentException {
		CompilationUnit unit = createCompilationUnit();
		for(int i = 0; i < className.length; i++){
			unit.addSource(className[i], new ByteArrayInputStream(sourceCode[i].getBytes()));
		}
		return doCompile(unit);
	}

	@Override
	public CompilationResult compile(Iterable<File> compilationUnits) {
		CompilationUnit unit = createCompilationUnit();
		for(File f : compilationUnits){
			unit.addSource(f);
		}
		return doCompile(unit);
	}

	protected CompilationUnit createCompilationUnit() throws RuntimeException {
		GroovyClassLoader cl;
        cl = parentClassLoader != null ? new GroovyClassLoader(parentClassLoader) : new GroovyClassLoader();
        if (libraries != null) {
			for (File f : libraries) {
				try {
					cl.addURL(f.toURI().toURL());
				} catch (MalformedURLException e) {
					throw new RuntimeException(e);
				}
			}
		}

		CompilationUnit unit = new CompilationUnit(cl);
		CompilerConfiguration cc = new CompilerConfiguration();
		cc.setRecompileGroovySource(true);
		cc.setSourceEncoding("UTF-8");
		unit.setConfiguration(cc);
		return unit;
	}

	@SuppressWarnings({"unchecked"})
	protected CompilationResult doCompile(CompilationUnit unit){
        unit.compile(Phases.CLASS_GENERATION);

		GroovyClassLoader cl = unit.getClassLoader();

		List<GroovyClass> groovyClasses = unit.getClasses();
		GroovyScriptClassLoader gscl = new GroovyScriptClassLoader(cl);
		gscl.setGroovyClasses(groovyClasses);

		Class[] classes = new Class[gscl.getCompiledClasses().size()];
		String[] classNames = gscl.getCompiledClasses().toArray(new String[classes.length]);
		for(int i = 0; i < classNames.length; i++){
			try {
				classes[i] = gscl.loadClass(classNames[i]);
			} catch (ClassNotFoundException e) {
				// shouldn't happen
				throw new RuntimeException(e);
			}
		}

        return new CompilationResult(classes, gscl);
	}

	@Override
	public String[] getSupportedFileTypes() {
		return SUPPORTED_FILE_TYPES;
	}
}
