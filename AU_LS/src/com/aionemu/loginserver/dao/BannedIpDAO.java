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
import com.aionemu.loginserver.model.BannedIP;

import java.util.Set;

/**
 * DAO that manages Banned IPs
 * 
 * @author SoulKeeper
 */
public abstract class BannedIpDAO implements DAO<BannedIP, String>
{
	/**
	 * This method will always throw {@link UnsupportedOperationException}
	 * @param id
	 *            - primary key
	 * @return nothing
	 * @throws UnsupportedOperationException Always
	 */
	@Override
	public final BannedIP load(String id) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("No need to load bans by id, please load all using #getAllBans()");
	}

	/**
	 * Returns all bans from database.
	 * 
	 * @return all bans from database.
	 */
	public abstract Set<BannedIP> getAllBans();
}
