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
package com.aionemu.gameserver.configs.main;

import java.util.regex.Pattern;

import com.aionemu.commons.configuration.Property;

public class GSConfig
{
	/**
	 * Server name
	 */
	@Property(key = "gameserver.name", defaultValue = "aion private")
	public static String			SERVER_NAME;

	/**
	 * Character name pattern (checked when character is being created)
	 */
	@Property(key = "gameserver.character.name.pattern", defaultValue = "[a-zA-Z]{2,16}")
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
}
