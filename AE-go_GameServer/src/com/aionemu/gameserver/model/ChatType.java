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

package com.aionemu.gameserver.model;

/**
 * 
 * Chat types that are supported by aion.
 * 
 * @author SoulKeeper
 */
public enum ChatType
{

	/**
	 * Just normal chat
	 */
	NORMAL(0x00),

	/**
	 * Shout chat
	 */
	SHOUT(0x03),

	/**
	 * whisper chat
	 */
	WHISPER(0x04),

	/**
	 * Unknown, seded by official server
	 */
	UNKNOWN_0x18(0x18),

	/**
	 * Golden chat
	 */
	ANNOUNCEMENTS(0x19),

	/**
	 * Unknown
	UNKNOWN_0x1D(0x1D);
	*/
	
	SYSTEM_NOTICE(0x1D);

	/**
	 * Chat type storage
	 */
	private final int	intValue;

	/**
	 * Constructor
	 * 
	 * @param i
	 *            client chat type integer representation
	 */
	ChatType(int i)
	{
		intValue = i;
	}

	/**
	 * Converts ChatType value to integer representation
	 * 
	 * @return chat type in client
	 */
	public int toInteger()
	{
		return intValue;
	}

	/**
	 * Returns ChatType by it's integer representation
	 * 
	 * @param integerValue
	 *            integer value of chat type
	 * @return ChatType
	 * @throws IllegalArgumentException
	 *             if can't find suitable chat type
	 */
	public static ChatType getChatTypeByInt(int integerValue) throws IllegalArgumentException
	{
		for(ChatType ct : ChatType.values())
		{
			if(ct.toInteger() == integerValue)
			{
				return ct;
			}
		}

		throw new IllegalArgumentException("Unsupported chat type: " + integerValue);
	}
}
