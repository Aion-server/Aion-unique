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
package com.aionemu.gameserver.configs.network;

import java.net.InetSocketAddress;

import com.aionemu.commons.configuration.Property;

public class NetworkConfig
{
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
	 * Number of Threads that will handle io write (>= 0)
	 */
	@Property(key = "gameserver.network.display.unknownpackets", defaultValue = "false")
	public static boolean				DISPLAY_UNKNOWNPACKETS;
}
