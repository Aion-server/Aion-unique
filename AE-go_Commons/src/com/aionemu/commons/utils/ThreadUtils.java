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
package com.aionemu.commons.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * This class contains collection of thread utilities
 * 
 * @author SoulKeeper
 */
public class ThreadUtils
{
	/**
	 * Returns stacktrace of current thread represented as String
	 * 
	 * @return stacktrace of current thread represented as String
	 */
	public static String getStackTrace()
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(baos);
		// noinspection ThrowableInstanceNeverThrown
		new Exception("Stack trace").printStackTrace(printStream);
		printStream.close();
		return new String(baos.toByteArray());
	}
}