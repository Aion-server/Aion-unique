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
import com.aionemu.gameserver.model.gameobjects.Monster;
import com.aionemu.gameserver.skillengine.model.Skill;


/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TargetRelationProperty")
public class TargetRelationProperty
    extends Property
{

    @XmlAttribute(required = true)
    protected TargetRelationAttribute value;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link TargetRelationAttribute }
     *     
     */
    public TargetRelationAttribute getValue() {
        return value;
    }
    
    @Override
	public boolean set(Skill skill)
	{
    	List<Creature> effectedList = skill.getEffectedList();
    	
    	
		switch(value)
		{
			case ALL:
				break;
			case ENEMY:
				Creature lastAttacker = skill.getEffector().getController().getLastAttacker();		
				for(Iterator<Creature> iter = effectedList.iterator(); iter.hasNext();)
				{
					Creature nextEffected = iter.next();
					
					if(nextEffected instanceof Monster)
						continue;
					
					if(lastAttacker != null && lastAttacker.getObjectId() == nextEffected.getObjectId())
						continue;

					iter.remove();
					//need to implement in a more robust way
					//TODO duel
					//TODO different race				
				}
			
			//TODO other enum values
		}
		return true;
	}
}
