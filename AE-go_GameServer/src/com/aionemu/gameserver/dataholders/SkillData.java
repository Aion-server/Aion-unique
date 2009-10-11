/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.templates.SkillTemplate;

/**
 * @author ATracer
 *
 */
@XmlRootElement(name = "skill_data")
@XmlAccessorType(XmlAccessType.FIELD)
public class SkillData
{
	private static final Logger log = Logger.getLogger(SkillData.class);
	
	@XmlElement(name = "skill_template")
	private List<SkillTemplate> skillTemplates;
	/**
	 *  Map that contains skillId - SkillTemplate key-value pair
	 */
	private Map<Integer, SkillTemplate>	skillData	= new HashMap<Integer, SkillTemplate>();
	
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(SkillTemplate skillTempalte: skillTemplates)
		{
			skillData.put(skillTempalte.getSkillId(), skillTempalte);	
		}
	}
	
	/** 
	 * @param skillId
	 * @return
	 */
	public SkillTemplate getSkillTemplate(int skillId)
	{
		return skillData.get(skillId);
	}
	
	/**
	 * @return
	 */
	public List<SkillTemplate> getSkillTemplates()
	{
		return skillTemplates;
	}
	
	public int size()
	{
		return skillData.size();
	}
}
