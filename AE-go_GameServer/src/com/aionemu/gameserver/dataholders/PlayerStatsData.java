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
package com.aionemu.gameserver.dataholders;


import gnu.trove.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.stats.CalculatedPlayerStatsTemplate;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;

/**
 * Created on: 31.07.2009 14:20:03
 *
 * @author Aquanox
 */
@XmlRootElement(name = "stats_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlayerStatsData
{
	@XmlElement(name = "player_stats", required = true)
	private List<PlayerStatsType> templatesList = new ArrayList<PlayerStatsType>();

	private final TIntObjectHashMap<PlayerStatsTemplate> templates = new TIntObjectHashMap<PlayerStatsTemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (PlayerStatsType pt : templatesList)
		{
			int code = makeHash(pt.getRequiredPlayerClass(), pt.getRequiredLevel());

			templates.put(code, pt.getTemplate());
		}
		
		/** for unknown templates **/
		templates.put(makeHash(PlayerClass.WARRIOR, 0), new CalculatedPlayerStatsTemplate(PlayerClass.WARRIOR));
		templates.put(makeHash(PlayerClass.ASSASSIN, 0), new CalculatedPlayerStatsTemplate(PlayerClass.ASSASSIN));
		templates.put(makeHash(PlayerClass.CHANTER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.CHANTER));
		templates.put(makeHash(PlayerClass.CLERIC, 0), new CalculatedPlayerStatsTemplate(PlayerClass.CLERIC));
		templates.put(makeHash(PlayerClass.GLADIATOR, 0), new CalculatedPlayerStatsTemplate(PlayerClass.GLADIATOR));
		templates.put(makeHash(PlayerClass.MAGE, 0), new CalculatedPlayerStatsTemplate(PlayerClass.MAGE));
		templates.put(makeHash(PlayerClass.PRIEST, 0), new CalculatedPlayerStatsTemplate(PlayerClass.PRIEST));
		templates.put(makeHash(PlayerClass.RANGER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.RANGER));
		templates.put(makeHash(PlayerClass.SCOUT, 0), new CalculatedPlayerStatsTemplate(PlayerClass.SCOUT));
		templates.put(makeHash(PlayerClass.SORCERER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.SORCERER));
		templates.put(makeHash(PlayerClass.SPIRIT_MASTER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.SPIRIT_MASTER));
		templates.put(makeHash(PlayerClass.TEMPLAR, 0), new CalculatedPlayerStatsTemplate(PlayerClass.TEMPLAR));
		
		templatesList.clear();
		templatesList = null;
	}

	public PlayerStatsTemplate getTemplate(Player player)
	{
		PlayerStatsTemplate template = getTemplate(player.getCommonData().getPlayerClass(), player.getLevel());
		if(template == null)
			template = getTemplate(player.getCommonData().getPlayerClass(), 0);
		return template;
	}

	public PlayerStatsTemplate getTemplate(PlayerClass playerClass, int level)
	{
		PlayerStatsTemplate template =  templates.get(makeHash(playerClass, level));
		if(template == null)
			template = getTemplate(playerClass, 0);
		return template;
	}

	public int size()
	{
		return templates.size();
	}

	@XmlRootElement(name="player_stats")
	private static class PlayerStatsType
	{
		@XmlAttribute(name = "class", required = true)
		private PlayerClass requiredPlayerClass;
		@XmlAttribute(name = "level", required = true)
		private int requiredLevel;

		@XmlElement(name="stats_template")
		private PlayerStatsTemplate template;

		public PlayerClass getRequiredPlayerClass()
		{
			return requiredPlayerClass;
		}

		public int getRequiredLevel()
		{
			return requiredLevel;
		}

		public PlayerStatsTemplate getTemplate()
		{
			return template;
		}
	}

	private static int makeHash(PlayerClass playerClass, int level)
	{
		return level << 8 | playerClass.ordinal();
	}
}
