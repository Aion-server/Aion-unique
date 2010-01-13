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

import java.util.List;
import java.util.Collections;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL_END;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.controllers.attack.SkillAttackResult;
import com.aionemu.gameserver.controllers.attack.AttackStatus;


/**
 * @author ATracer
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HealMpAction")
public class HealMpAction
    extends Action
{

    @XmlAttribute(required = true)
    protected int value;
    
    @XmlAttribute
    protected int delta;

    /**
     * Gets the value of the value property.
     * 
     */
    public int getValue() 
    {
        return value;
    }

    
	@Override
	public void act(Skill skill)
	{
		Player effector = skill.getEffector();
		SkillTemplate template = skill.getSkillTemplate();

		int valueWithDelta = value + delta * skill.getSkillLevel();
		
		int unk = 0;
		PacketSendUtility.broadcastPacket(effector,
			new SM_CASTSPELL_END(effector.getObjectId(), template.getSkillId(), skill.getSkillLevel(),
				unk, skill.getFirstTarget().getObjectId(), Collections.singletonList(new SkillAttackResult(skill.getFirstTarget(), -valueWithDelta, AttackStatus.NORMALHIT)), template.getCooldown()), true);
		
		List<Creature> effectedList = skill.getEffectedList();
		for(Creature effected : effectedList)
		{
			effected.getLifeStats().increaseMp(valueWithDelta);
		}
		
	}
}
