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
package com.aionemu.gameserver.skillengine.properties;

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.MathUtil;


/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TargetRangeProperty")
public class TargetRangeProperty
extends Property
{

	@XmlAttribute(required = true)
	protected TargetRangeAttribute value;

	@XmlAttribute
	protected int distance;
	
	@XmlAttribute
	protected int maxcount;

	/**
	 * Gets the value of the value property.
	 *     
	 */
	public TargetRangeAttribute getValue() {
		return value;
	}

	@Override
	public boolean set(Skill skill)
	{
		List<Creature> effectedList = skill.getEffectedList();
		switch(value)
		{
			case ONLYONE:
				skill.getEffectedList().add(skill.getFirstTarget());
				break;			
			case AREA:	
				Creature firstTarget = skill.getFirstTarget();
				Iterator<VisibleObject> iterator = firstTarget.getKnownList().iterator();
				int counter = 0;
				while(iterator.hasNext() && counter < maxcount)
				{
					VisibleObject nextCreature = iterator.next();

					if(nextCreature instanceof Creature 
						&& MathUtil.isInRange(firstTarget, nextCreature, distance))
					{
						effectedList.add((Creature) nextCreature);
						counter++;
					}
				}
				break;
			case PARTY:
			case NONE:
				break;
			
			//TODO other enum values
		}
		return true;
	}
}
