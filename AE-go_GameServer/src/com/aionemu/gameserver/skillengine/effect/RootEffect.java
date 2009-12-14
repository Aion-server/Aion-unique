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
package com.aionemu.gameserver.skillengine.effect;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.stats.StatEffect;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;

/**
 * @author ATracer
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RootEffect")
public class RootEffect extends EffectTemplate
{
	/** duration is in seconds **/
	@XmlAttribute(required = true)
    protected int duration;
	
	@Override
	public void apply(Skill skill)
	{		
		SkillTemplate template = skill.getSkillTemplate();

		Effect effect = new Effect(skill.getEffector().getObjectId(), template.getSkillId(),
			skill.getSkillLevel(), duration, this);
		
		List<Creature> effectedList = skill.getEffectedList();
		for(Creature effected : effectedList)
		{
			effected.getEffectController().addEffect(effect);
		}
		
	}

	@Override
	public StatEffect startEffect(Creature effected, int skillId, int skillLvl)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void endEffect(Creature effected, StatEffect effect, int skillId)
	{
		// TODO Auto-generated method stub
		
	}
	
	
}
