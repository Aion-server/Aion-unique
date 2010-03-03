/*
 * This file is part of aion-unique <aion-unique.org>.
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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FirstTargetRangeProperty")
public class FirstTargetRangeProperty extends Property
{
	
	@XmlAttribute(required = true)
	protected int value;

	@Override
	public boolean set(Skill skill)
	{
		if(!skill.isFirstTargetRangeCheck())
			return true;
		
		Creature effector = skill.getEffector();
		Creature firstTarget = skill.getFirstTarget();
		if(firstTarget == null)
			return false;
		//here value +4 till better move controller developed
		if(MathUtil.isInRange(effector, firstTarget, value + 4))
		{
			return true;
		}
		else
		{
			if(effector instanceof Player)
			{
				PacketSendUtility.sendPacket((Player) effector, SM_SYSTEM_MESSAGE.STR_ATTACK_TOO_FAR_FROM_TARGET());
			}
			return false;
		}
	}

}
