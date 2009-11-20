/**
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.loginserver.utils;

import org.apache.log4j.Logger;

/**
 * @author -Nemesiss-
 */
public class ThreadUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler
{
	/**
	 * Logger for this class.
	 */
	private static final Logger	log	= Logger.getLogger(ThreadUncaughtExceptionHandler.class);

	@Override
	public void uncaughtException(Thread t, Throwable e)
	{
		if (e instanceof OutOfMemoryError)
		{
			log.fatal("Out of memory error!!!", e);
			// TODO: try to get more on restart
		} else {
			log.error("Critical Error - Thread: " + t.getName() + " terminated abnormaly: " + e, e);
		}
		// TODO! some threads should be "restarted" on error
	}
}
