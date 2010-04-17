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
import com.aionemu.gameserver.model.templates.stats.SummonStatsTemplate;

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
	
	@XmlElement(name = "summon_stats", required = true)
	private List<SummonStatsType> summonTemplatesList = new ArrayList<SummonStatsType>();

	private final TIntObjectHashMap<PlayerStatsTemplate> playerTemplates = new TIntObjectHashMap<PlayerStatsTemplate>();
	private final TIntObjectHashMap<SummonStatsTemplate> summonTemplates = new TIntObjectHashMap<SummonStatsTemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (PlayerStatsType pt : templatesList)
		{
			int code = makeHash(pt.getRequiredPlayerClass(), pt.getRequiredLevel());
			playerTemplates.put(code, pt.getTemplate());
		}
		
		for (SummonStatsType st : summonTemplatesList)
		{
			int code1 = makeHash2(st.getNpcIdDark(), st.getRequiredLevel());
			summonTemplates.put(code1, st.getTemplate());
			int code2 = makeHash2(st.getNpcIdLight(), st.getRequiredLevel());
			summonTemplates.put(code2, st.getTemplate());
		}
		
		/** for unknown templates **/
		playerTemplates.put(makeHash(PlayerClass.WARRIOR, 0), new CalculatedPlayerStatsTemplate(PlayerClass.WARRIOR));
		playerTemplates.put(makeHash(PlayerClass.ASSASSIN, 0), new CalculatedPlayerStatsTemplate(PlayerClass.ASSASSIN));
		playerTemplates.put(makeHash(PlayerClass.CHANTER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.CHANTER));
		playerTemplates.put(makeHash(PlayerClass.CLERIC, 0), new CalculatedPlayerStatsTemplate(PlayerClass.CLERIC));
		playerTemplates.put(makeHash(PlayerClass.GLADIATOR, 0), new CalculatedPlayerStatsTemplate(PlayerClass.GLADIATOR));
		playerTemplates.put(makeHash(PlayerClass.MAGE, 0), new CalculatedPlayerStatsTemplate(PlayerClass.MAGE));
		playerTemplates.put(makeHash(PlayerClass.PRIEST, 0), new CalculatedPlayerStatsTemplate(PlayerClass.PRIEST));
		playerTemplates.put(makeHash(PlayerClass.RANGER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.RANGER));
		playerTemplates.put(makeHash(PlayerClass.SCOUT, 0), new CalculatedPlayerStatsTemplate(PlayerClass.SCOUT));
		playerTemplates.put(makeHash(PlayerClass.SORCERER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.SORCERER));
		playerTemplates.put(makeHash(PlayerClass.SPIRIT_MASTER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.SPIRIT_MASTER));
		playerTemplates.put(makeHash(PlayerClass.TEMPLAR, 0), new CalculatedPlayerStatsTemplate(PlayerClass.TEMPLAR));
		
		templatesList.clear();
		templatesList = null;
	}

	/**
	 * 
	 * @param player
	 * @return
	 */
	public PlayerStatsTemplate getTemplate(Player player)
	{
		PlayerStatsTemplate template = getTemplate(player.getCommonData().getPlayerClass(), player.getLevel());
		if(template == null)
			template = getTemplate(player.getCommonData().getPlayerClass(), 0);
		return template;
	}

	/**
	 * 
	 * @param playerClass
	 * @param level
	 * @return
	 */
	public PlayerStatsTemplate getTemplate(PlayerClass playerClass, int level)
	{
		PlayerStatsTemplate template =  playerTemplates.get(makeHash(playerClass, level));
		if(template == null)
			template = getTemplate(playerClass, 0);
		return template;
	}
	
	/**
	 * 
	 * @param npcId
	 * @param level
	 * @return
	 */
	public SummonStatsTemplate getSummonTemplate(int npcId, int level)
	{
		SummonStatsTemplate template =  summonTemplates.get(makeHash2(npcId, level));
		if(template == null)
			template = summonTemplates.get(makeHash2(201011, 10));//TEMP till all templates are done
		return template;
	}

	/**
	 * Size of player templates
	 * 
	 * @return
	 */
	public int size()
	{
		return playerTemplates.size();
	}
	
	/**
	 * Size of summon templates
	 * 
	 * @return
	 */
	public int size2()
	{
		return summonTemplates.size();
	}

	@XmlRootElement(name="playerStatsTemplateType")
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
	
	@XmlRootElement(name="summonStatsTemplateType")
	private static class SummonStatsType
	{
		@XmlAttribute(name = "npc_id_dark", required = true)
		private int npcIdDark;
		@XmlAttribute(name = "npc_id_light", required = true)
		private int npcIdLight;
		@XmlAttribute(name = "level", required = true)
		private int requiredLevel;

		@XmlElement(name="stats_template")
		private SummonStatsTemplate template;

		/**
		 * @return the npcIdDark
		 */
		public int getNpcIdDark()
		{
			return npcIdDark;
		}

		/**
		 * @return the npcIdLight
		 */
		public int getNpcIdLight()
		{
			return npcIdLight;
		}

		/**
		 * 
		 * @return requiredLevel
		 */
		public int getRequiredLevel()
		{
			return requiredLevel;
		}

		/**
		 * 
		 * @return template
		 */
		public SummonStatsTemplate getTemplate()
		{
			return template;
		}
	}

	/**
	 * 
	 * @param playerClass
	 * @param level
	 * @return
	 */
	private static int makeHash(PlayerClass playerClass, int level)
	{
		return level << 8 | playerClass.ordinal();
	}
	
	/**
	 *  Note:<br>
	 *  max level is 255
	 *  
	 * @param npcId
	 * @param level
	 * @return
	 */
	private static int makeHash2(int npcId, int level)
	{
		return npcId << 8 | level;
	}
}
