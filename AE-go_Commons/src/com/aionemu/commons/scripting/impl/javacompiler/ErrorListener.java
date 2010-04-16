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
package com.aionemu.commons.scripting.impl.javacompiler;

import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

import org.apache.log4j.Logger;

/**
 * This class is simple compiler error listener that forwards errors to log4j logger
 * 
 * @author SoulKeeper
 */
public class ErrorListener implements DiagnosticListener<JavaFileObject>
{
	/**
	 * Logger for this class
	 */
	private static final Logger	log	= Logger.getLogger(ErrorListener.class);

	/**
	 * Reports compilation errors to log4j
	 * 
	 * @param diagnostic
	 *            compiler errors
	 */
	@Override
	public void report(Diagnostic<? extends JavaFileObject> diagnostic)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("Java Compiler ").append(diagnostic.getKind()).append(": ").append(
			diagnostic.getMessage(Locale.ENGLISH)).append("\n").append("Source: ").append(
			diagnostic.getSource().getName()).append("\n").append("Line: ").append(diagnostic.getLineNumber()).append(
			"\n").append("Column: ").append(diagnostic.getColumnNumber());
		log.error(buffer.toString());
	}
}
