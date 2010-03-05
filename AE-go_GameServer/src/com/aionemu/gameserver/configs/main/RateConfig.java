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

import com.aionemu.commons.configuration.Property;

/**
 * @author ATracer
 * 
 */
public class RateConfig
{
	/**
	 * GROUP rates section
	 */

	/**
	 * Group Xp Rate
	 */
	@Property(key = "gameserver.rate.group.xp", defaultValue = "1")
	public static int	GROUPXP_RATE;

	/**
	 * REGULAR personal rates section
	 */

	/**
	 * Xp Rate
	 */
	@Property(key = "gameserver.rate.regular.xp", defaultValue = "1")
	public static int	XP_RATE;

	/**
	 * Quest Xp Rate
	 */
	@Property(key = "gameserver.rate.regular.quest.xp", defaultValue = "1")
	public static int	QUEST_XP_RATE;

	/**
	 * Quest Kinah Rate
	 */
	@Property(key = "gameserver.rate.regular.quest.kinah", defaultValue = "1")
	public static int	QUEST_KINAH_RATE;

	/**
	 * Drop Rate
	 */
	@Property(key = "gameserver.rate.regular.drop", defaultValue = "1")
	public static int	DROP_RATE;

	/**
	 * Abyss points Rate
	 */
	@Property(key = "gameserver.rate.regular.ap", defaultValue = "1")
	public static float	AP_RATE;

	/**
	 * PREMIUM personal rates section
	 */

	@Property(key = "gameserver.rate.premium.xp", defaultValue = "2")
	public static int	PREMIUM_XP_RATE;

	@Property(key = "gameserver.rate.premium.quest.xp", defaultValue = "2")
	public static int	PREMIUM_QUEST_XP_RATE;

	@Property(key = "gameserver.rate.premium.quest.kinah", defaultValue = "2")
	public static int	PREMIUM_QUEST_KINAH_RATE;

	@Property(key = "gameserver.rate.premium.drop", defaultValue = "2")
	public static int	PREMIUM_DROP_RATE;

	@Property(key = "gameserver.rate.premium.ap", defaultValue = "2")
	public static float	PREMIUM_AP_RATE;
}
