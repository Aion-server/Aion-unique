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

package com.aionemu.loginserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.loginserver.model.Account;

/**
 * DAO that manages accounts.
 *
 * @author SoulKeeper
 */
public abstract class AccountDAO implements DAO<Account, Integer>
{
	/**
	 * Returns account by name.
	 *
	 * @param name
	 *            account name
	 * @return account object or null
	 */
	public abstract Account load(String name);
}
