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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.skillengine.model.Skill;


/**
 * 
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FirstTargetProperty")
public class FirstTargetProperty
    extends Property
{

    @XmlAttribute(required = true)
    protected FirstTargetAttribute value;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link FirstTargetAttribute }
     *     
     */
    public FirstTargetAttribute getValue() {
        return value;
    }
    
    @Override
	public boolean set(Skill skill)
	{
		switch(value)
		{
			case ME:
				skill.setFirstTarget(skill.getEffector());
				break;			
			case TARGETORME:
				if(skill.getFirstTarget() == null)
					skill.setFirstTarget(skill.getEffector());
				break;
			case TARGET:
				if(skill.getFirstTarget() == null)
					return false;
				break;
			case MYPET:
				//after pet implementation
				break;
			case PASSIVE:
				skill.setFirstTarget(skill.getEffector());
				break;
			//TODO other enum values
		}

		if(skill.getFirstTarget() != null)
			skill.getEffectedList().add(skill.getFirstTarget());
		return true;
	}
}
