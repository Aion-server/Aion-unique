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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.condition.TargetAttribute;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Env;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BufEffect")
public class BufEffect extends EffectTemplate
{
	@XmlElements({
        @XmlElement(name = "change", type = Change.class)
    })
	protected List<Change> changes;
	
	@XmlAttribute(required = true)
	protected int duration;
	
	@XmlAttribute(required = false)
    protected TargetAttribute target;
	
	
	/**
	 * @return the changes
	 */
	public List<Change> getChanges()
	{
		return changes;
	}

	@Override
	public void apply(Env env)
	{
		Creature effected = env.getEffected();
		if(target == TargetAttribute.SELF)
		{
			effected = env.getEffector();
		}
		
		SkillTemplate template = env.getSkillTemplate();
		
		Effect effect = new Effect(template.getSkillId(),template.getLevel(), duration, this);
		effected.getEffectController().addEffect(effect);
	}
}
