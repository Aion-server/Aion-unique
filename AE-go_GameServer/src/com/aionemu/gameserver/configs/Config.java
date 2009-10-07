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

package com.aionemu.gameserver.configs;

import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.aionemu.commons.configuration.ConfigurableProcessor;
import com.aionemu.commons.configuration.Property;
import com.aionemu.commons.utils.PropertiesUtils;

/**
 * @author -Nemesiss-
 * @author SoulKeeper
 */
public class Config
{
	/**
	 * Logger for this class.
	 */
	protected static final Logger	log				= Logger.getLogger(Config.class);

	/**
	 * Game Server port
	 */
	@Property(key = "gameserver.network.client.port", defaultValue = "7777")
	public static int				GAME_PORT;

	/**
	 * Game Server bind ip
	 */
	@Property(key = "gameserver.network.client.host", defaultValue = "*")
	public static String			GAME_BIND_ADDRESS;

	/**
	 * Max allowed online players
	 */
	@Property(key = "gameserver.network.client.maxplayers", defaultValue = "100")
	public static int				MAX_ONLINE_PLAYERS;

	/**
	 * LoginServer address
	 */
	@Property(key = "gameserver.network.login.address", defaultValue = "localhost:9014")
	public static InetSocketAddress	LOGIN_ADDRESS;

	/**
	 * GameServer id that this GameServer will request at LoginServer.
	 */
	@Property(key = "gameserver.network.login.gsid", defaultValue = "0")
	public static int				GAMESERVER_ID;

	/**
	 * Password for this GameServer ID for authentication at LoginServer.
	 */
	@Property(key = "gameserver.network.login.password", defaultValue = "")
	public static String			LOGIN_PASSWORD;

	/**
	 * Number of Threads that will handle io read (>= 0)
	 */
	@Property(key = "gameserver.network.nio.threads.read", defaultValue = "0")
	public static int				NIO_READ_THREADS;

	/**
	 * Number of Threads that will handle io write (>= 0)
	 */
	@Property(key = "gameserver.network.nio.threads.write", defaultValue = "0")
	public static int				NIO_WRITE_THREADS;

	/**
	 * Server name
	 */
	@Property(key = "gameserver.name", defaultValue = "aion private")
	public static String			SERVER_NAME;

	/**
	 * Character name pattern (checked when character is being created)
	 */
	@Property(key = "gameserver.character.name.pattern", defaultValue = "[a-zA-Z]{2,10}")
	public static Pattern			CHAR_NAME_PATTERN;

	/**
	 * Server Country Code
	 */
	@Property(key = "gameserver.country.code",defaultValue = "1")
	public static int				SERVER_COUNTRY_CODE;
	
	/*
	 * Server Mode
	 */
	@Property(key = "gameserver.mode",defaultValue = "1")
	public static int				SERVER_MODE;
	
	/*
	 * Exp Rate
	 */
	@Property(key = "ExpRate",defaultValue = "1")
	public static int				EXP_RATE;
	
	/**
	 * Initialize all configs in com.aionemu.gameserver.configs package
	 */
	public static void load()
	{
		try
		{
			Properties[] props = PropertiesUtils.loadAllFromDirectory("./config");

			ConfigurableProcessor.process(Config.class, props);
			ConfigurableProcessor.process(CacheConfig.class, props);
		}
		catch(Exception e)
		{
			log.fatal("Can't load gameserver configuration", e);

			throw new Error("Can't load gameserver configuration", e);
		}

		IPConfig.load();
	}
}
