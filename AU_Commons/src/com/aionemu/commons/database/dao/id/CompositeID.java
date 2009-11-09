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

package com.aionemu.commons.database.dao.id;

/**
 * Abstract class that designed to be used as ID in case if there are more than one primary key for the table.
 *
 * @author SoulKeeper
 */
public abstract class CompositeID {

	/**
	 * Composite id should override equals implementation
	 *
	 * @param obj another object to compare
	 * @return objects are equal or not
	 */
	public abstract boolean equals(Object obj);

	/**
	 * CompositeId should override hashCode for correct map storage.<br>
	 * Please note that implementation should always return same hashCode even if id's are in the different order.
	 *
	 * @return hashcode
	 */
	public abstract int hashCode();
}
