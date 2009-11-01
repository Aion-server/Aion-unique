/*
 * This file is part of aion-unique <aion-unique.com>.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.skillengine.model.SkillLearnTemplate;

/**
 * @author ATracer
 *
 */
@XmlRootElement(name = "skill_tree")
@XmlAccessorType(XmlAccessType.FIELD)
public class SkillTreeData
{
	private static final Logger log = Logger.getLogger(SkillTreeData.class);
	
	@XmlElement(name = "skill")
	private List<SkillLearnTemplate> skillTemplates;
	
	private final Map<Integer, ArrayList<SkillLearnTemplate>> templates = new HashMap<Integer, ArrayList<SkillLearnTemplate>>();
	
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(SkillLearnTemplate learnTemplate : skillTemplates)
		{
			addTemplate(learnTemplate);
		}
	}
	
	private void addTemplate(SkillLearnTemplate template)
	{
		int hash = makeHash(template);
		ArrayList<SkillLearnTemplate> value = templates.get(hash);
		if(value == null)
		{
			value = new ArrayList<SkillLearnTemplate>();
			templates.put(hash, value);
		}
			
		value.add(template);
	}
	
	public int size()
	{
		return skillTemplates.size();
	}
	
	private static int makeHash(SkillLearnTemplate template)
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + template.getClassId();
		result = prime * result + template.getMinLevel();
		return result;
	}
}
