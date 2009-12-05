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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL_END;
import com.aionemu.gameserver.skillengine.condition.TargetAttribute;
import com.aionemu.gameserver.skillengine.model.Env;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;


/**
 * @author ATracer
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HealEffect")
public class HealEffect
    extends EffectTemplate
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
	public void apply(Env env)
	{
		//TODO calculate heal value
		Player effector = (Player) env.getEffector();
		Creature effected = env.getEffected();
		SkillTemplate template = env.getSkillTemplate();
		
		int unk = 0;
		
		int valueWithDelta = value + delta * env.getSkillLevel();
		
		PacketSendUtility.broadcastPacket(effector,
			new SM_CASTSPELL_END(effector.getObjectId(), template.getSkillId(), env.getSkillLevel(),
				unk, effected.getObjectId(), -valueWithDelta, template.getCooldown()), true);
		
		effected.getLifeStats().increaseHp(valueWithDelta);
	}

	@Override
	public void endEffect(Creature effected, int skillId)
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void startEffect(Creature effected, int skillId, int skillLvl)
	{
		// TODO Auto-generated method stub
		
	}
}
