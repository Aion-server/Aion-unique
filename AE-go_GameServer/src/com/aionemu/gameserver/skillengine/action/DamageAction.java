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
package com.aionemu.gameserver.skillengine.action;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.SkillElement;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL_END;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.StatFunctions;


/**
 * @author ATracer
 *  
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DamageAction")
public class DamageAction
    extends Action
{

    @XmlAttribute(required = true)
    protected int value;
    
    @XmlAttribute
    protected int delta;
    
    @XmlAttribute(name="type")
    protected DamageType damageType;

	@Override
	public void act(Skill skill)
	{
		Player effector = skill.getEffector();
		VisibleObject firstTarget = skill.getFirstTarget();
		SkillTemplate template = skill.getSkillTemplate();
		
		int valueWithDelta = value + delta * skill.getSkillLevel();
		
		if(damageType == null)
			damageType = DamageType.valueOf(skill.getSkillTemplate().getType().name());
		
		//TODO this is probably incorrect
		int sumDamage = 0;
		
		for(Creature effected : skill.getEffectedList())
		{
			if(effected == null)
				continue;
			
			int damage = 0;
			switch(damageType)
			{
				case PHYSICAL:
					damage = StatFunctions.calculatePhysicDamageToTarget(effector, effected, valueWithDelta);
					break;
				case MAGICAL:
					damage = StatFunctions.calculateMagicDamageToTarget(effector, effected, valueWithDelta, SkillElement.NONE);
					break;
				default:
					damage = StatFunctions.calculateBaseDamageToTarget(effector, effected);
			}
			sumDamage += damage;
			
			effected.getController().onAttack(effector, skill.getSkillTemplate().getSkillId(), damage);
		}
		
		int unk = 0;
		PacketSendUtility.broadcastPacket(effector,
			new SM_CASTSPELL_END(effector.getObjectId(), template.getSkillId(), skill.getSkillLevel(),
				unk, firstTarget.getObjectId(), sumDamage, template.getCooldown()), true);	
		
	}
}
