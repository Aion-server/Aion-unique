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

package com.aionemu.commons.database.dao.scriptloader;

/**
 * Type of comparator that should be used to compare DB versions
 * 
 * @author SoulKeeper
 */
public enum ComparatorType
{

	/**
	 * <
	 */
	LESS
	{
		public boolean compare(int a, int b)
		{
			return a < b;
		}
	},

	/**
	 * <=
	 */
	LESS_OR_EQUAL
	{
		public boolean compare(int a, int b)
		{
			return a <= b;
		}
	},

	/**
	 * ==
	 */
	EQUAL
	{
		public boolean compare(int a, int b)
		{
			return a == b;
		}
	},

	/**
	 * >=
	 */
	GREATER_OR_EQUAL
	{
		public boolean compare(int a, int b)
		{
			return a >= b;
		}
	},

	/**
	 * >
	 */
	GREATER
	{
		public boolean compare(int a, int b)
		{
			return a > b;
		}
	};

	/**
	 * Retrns true if 'a <ENUM ACTION> b'
	 * 
	 * @param a
	 *            first integer
	 * @param b
	 *            second integer
	 * @return result
	 */
	public boolean compare(int a, int b)
	{
		throw new UnsupportedOperationException("This exception should be never thrown");
	}
}
