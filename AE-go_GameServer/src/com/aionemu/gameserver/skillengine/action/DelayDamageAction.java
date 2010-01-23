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
package com.aionemu.gameserver.skillengine.action;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.controllers.attack.SkillAttackResult;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL_END;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 *  
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DelayDamageAction")
public class DelayDamageAction extends DamageAction
{
	
	@XmlAttribute
	protected int delay;

	@Override
	public void act(Skill skill)
	{
		final Player effector = skill.getEffector();
		SkillTemplate template = skill.getSkillTemplate();
		final int skillId = template.getSkillId();

		int valueWithDelta = value + delta * skill.getSkillLevel();

		if(damageType == null)
			damageType = DamageType.valueOf(skill.getSkillTemplate().getType().name());

		final List<SkillAttackResult> skillAttackList = calculateAttackResult(skill, effector, valueWithDelta);

		int unk = 0;
		PacketSendUtility.broadcastPacket(effector,
			new SM_CASTSPELL_END(effector.getObjectId(), template.getSkillId(), skill.getSkillLevel(),
				unk, skill.getFirstTarget().getObjectId(), Collections.singletonList(new SkillAttackResult(skill.getFirstTarget(), 0, AttackStatus.NORMALHIT)), template.getCooldown()), true);

		ThreadPoolManager.getInstance().schedule(new Runnable(){		
			@Override
			public void run()
			{
				for (SkillAttackResult skillAttack : skillAttackList)
				{
					Creature effected = skillAttack.getCreature();
					if(effected != null && !effected.getLifeStats().isAlreadyDead())
					{
						effected.getController().onAttack(effector, skillId, TYPE.REGULAR, skillAttack.getDamage());
					}				
				}
			}
		}, delay);	
	}
}
