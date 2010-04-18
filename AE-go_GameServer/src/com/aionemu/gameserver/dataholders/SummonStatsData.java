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

import com.aionemu.gameserver.model.templates.stats.SummonStatsTemplate;

/**
 * @author ATracer
 *
 */
@XmlRootElement(name = "summon_stats_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class SummonStatsData
{
	@XmlElement(name = "summon_stats", required = true)
	private List<SummonStatsType> summonTemplatesList = new ArrayList<SummonStatsType>();
	
	private final TIntObjectHashMap<SummonStatsTemplate> summonTemplates = new TIntObjectHashMap<SummonStatsTemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (SummonStatsType st : summonTemplatesList)
		{
			int code1 = makeHash(st.getNpcIdDark(), st.getRequiredLevel());
			summonTemplates.put(code1, st.getTemplate());
			int code2 = makeHash(st.getNpcIdLight(), st.getRequiredLevel());
			summonTemplates.put(code2, st.getTemplate());
		}		
	}
	
	/**
	 * 
	 * @param npcId
	 * @param level
	 * @return
	 */
	public SummonStatsTemplate getSummonTemplate(int npcId, int level)
	{
		SummonStatsTemplate template =  summonTemplates.get(makeHash(npcId, level));
		if(template == null)
			template = summonTemplates.get(makeHash(201022, 10));//TEMP till all templates are done
		return template;
	}
	
	/**
	 * Size of summon templates
	 * 
	 * @return
	 */
	public int size()
	{
		return summonTemplates.size();
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
	 *  Note:<br>
	 *  max level is 255
	 *  
	 * @param npcId
	 * @param level
	 * @return
	 */
	private static int makeHash(int npcId, int level)
	{
		return npcId << 8 | level;
	}
}
