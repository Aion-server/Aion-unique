/**
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
package com.aionemu.loginserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.loginserver.GameServer;

import java.util.Map;

/**
 * DAO that manages GameServers
 * 
 * @author -Nemesiss-
 */
public abstract class GameServerDAO implements DAO<GameServer, Byte>
{

	/**
	 * Throws {@link UnsupportedOperationException}. Servers should be loaded once on start.
	 * @param id
	 *            - primary key
	 * @return nothing
	 * @throws UnsupportedOperationException Always
	 */
	@Override
	public final GameServer load(Byte id) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Use #getAllGameServers() once on load instead");
	}

	/**
	 * Returns all gameservers from database.
	 * 
	 * @return all gameservers from database.
	 */
	public abstract Map<Byte, GameServer> getAllGameServers();
}
