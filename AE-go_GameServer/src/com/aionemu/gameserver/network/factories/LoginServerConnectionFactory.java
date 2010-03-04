/*
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
package com.aionemu.gameserver.network.factories;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import com.aionemu.commons.network.Dispatcher;
import com.aionemu.gameserver.network.loginserver.LoginServerConnection;

/**
 * An interface of LoginServerConnectionFactorz
 * 
 * @author Luno
 * 
 */
public interface LoginServerConnectionFactory
{
	/**
	 * Creates and returns new LoginServerConnection object.
	 * 
	 * @param sc
	 * @param d
	 * @return LoginServerConnection
	 * @throws IOException
	 */
	public LoginServerConnection createConnection(SocketChannel sc, Dispatcher d)
		throws IOException;
}