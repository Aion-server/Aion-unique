/*
 * This file is part of aion-unique <aion-unique.org>.
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
package com.aionemu.gameserver.utils;

import org.apache.log4j.Logger;

import com.aionemu.commons.utils.AEInfos;
import com.aionemu.commons.versionning.Version;
import com.aionemu.gameserver.GameServer;

/**
 * @author lord_rex
 * 
 */
public class AEVersions
{
	private static final Logger		log			= Logger.getLogger(AEVersions.class);
	private static final Version	commons		= new Version(AEInfos.class);
	private static final Version	gameserver	= new Version(GameServer.class);

	private AEVersions()
	{

	}

	private static String getRevisionInfo(Version version)
	{
		return String.format("%-6s", version.getRevision());
	}

	private static String getDateInfo(Version version)
	{
		return String.format("[ %4s ]", version.getDate());
	}

	public static String[] getFullVersionInfo()
	{
		return new String[] { 
			"Commons Revision: " + getRevisionInfo(commons),
			"Commons Build Date: " + getDateInfo(commons), 
			"GS Revision: " + getRevisionInfo(gameserver),
			"GS Build Date: " + getDateInfo(gameserver) 
		};
	}

	public static void printFullVersionInfo()
	{
		for(String line : getFullVersionInfo())
			log.info(line);
	}
}
