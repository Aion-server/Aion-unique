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

package com.aionemu.commons.database;

/**
 * Object that contains information about database.<br>
 * It's package-private, so the only valid way to obtain it is to call {@link DatabaseFactory#getDatabaseInfo()}
 * 
 * @author SoulKeeper
 */
public class DatabaseInfo
{

	/**
	 * Database product name
	 */
	private final String	productName;

	/**
	 * Database major version
	 */
	private final int		majorVersion;

	/**
	 * Database minor version
	 */
	private final int		minorVersion;

	/**
	 * Creates DatabaseInfo object
	 * 
	 * @param productName
	 *            database name, like Postgres or MySQL
	 * @param majorVersion
	 *            database implementation major version
	 * @param minorVersion
	 *            database implementation minor version
	 */
	DatabaseInfo(String productName, int majorVersion, int minorVersion)
	{
		this.productName = productName;
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
	}

	/**
	 * Returns database product name
	 * 
	 * @return database product name
	 */
	public String getProductName()
	{
		return productName;
	}

	/**
	 * Returns database major version
	 * 
	 * @return database major version
	 */
	public int getMajorVersion()
	{
		return majorVersion;
	}

	/**
	 * Returns database minor version
	 * 
	 * @return database minor version
	 */
	public int getMinorVersion()
	{
		return minorVersion;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (!(o instanceof DatabaseInfo))
			return false;

		DatabaseInfo that = (DatabaseInfo) o;
		return majorVersion == that.majorVersion && minorVersion == that.minorVersion
			&& productName.equals(that.productName);

	}

	@Override
	public int hashCode()
	{
		int result = productName.hashCode();
		result = 31 * result + majorVersion;
		result = 31 * result + minorVersion;
		return result;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(productName).append(" ").append(majorVersion).append('.').append(minorVersion);
		return sb.toString();
	}
}
