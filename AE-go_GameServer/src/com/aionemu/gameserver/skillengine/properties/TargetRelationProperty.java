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
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
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
		
		Creature lastAttacker = skill.getEffector().getController().getLastAttacker();
		Player effector = skill.getEffector();
		
		switch(value)
		{
			case ALL:
				break;
			case ENEMY:			
				for(Iterator<Creature> iter = effectedList.iterator(); iter.hasNext();)
				{
					Creature nextEffected = iter.next();
					
					if(nextEffected instanceof Npc && effector.getController().isEnemy((Npc) nextEffected))
						continue;
					
					if(lastAttacker != null && lastAttacker.getObjectId() == nextEffected.getObjectId())
						continue;
					
					if(nextEffected instanceof Player 
						&& ((Player) nextEffected).getController().isEnemy(skill.getEffector()))
						continue;
					
					iter.remove();
				}
				break;
			case FRIEND:
				for(Iterator<Creature> iter = effectedList.iterator(); iter.hasNext();)
				{
					Creature nextEffected = iter.next();
					
					//TODO refactor here for duel support
					if(nextEffected instanceof Player 
						&& !((Player)nextEffected).getController().isEnemy(skill.getEffector()))
						continue;
					
					//TODO here also needs refactoring
					if(nextEffected instanceof Player
						&& lastAttacker != null 
						&& lastAttacker.getObjectId() != nextEffected.getObjectId())
						continue;
					
					if(nextEffected instanceof Npc && !effector.getController().isEnemy((Npc) nextEffected))
						continue;
					
					iter.remove();			
				}
				
				if(effectedList.size() == 0)
				{
					skill.setFirstTarget(skill.getEffector());
					effectedList.add(skill.getEffector());
				}
				else
				{
					skill.setFirstTarget(effectedList.get(0));
				}
					
				break;
			
			//TODO other enum values
		}
		return true;
	}
}
