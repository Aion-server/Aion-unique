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
package com.aionemu.gameserver.ai.desires.impl;

import java.util.List;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai.AI;
import com.aionemu.gameserver.ai.desires.AbstractDesire;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.npcskill.NpcSkillList;
import com.aionemu.gameserver.model.templates.npcskill.NpcSkillTemplate;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * @author ATracer
 * 
 */
public class SkillUseDesire extends AbstractDesire
{

	protected Creature		owner;
	private NpcSkillList	skillList;

	/**
	 * @param owner
	 * @param desirePower
	 */
	public SkillUseDesire(Creature owner, int desirePower)
	{
		super(desirePower);
		this.owner = owner;
		this.skillList = ((Npc) owner).getNpcSkillList();
	}

	@Override
	public boolean handleDesire(AI<?> ai)
	{
		if(owner.isCasting())
			return true;
		
		/**
		 * Demo mode - take random skill
		 */
		List<NpcSkillTemplate> skills = skillList.getNpcSkills();
		NpcSkillTemplate npcSkill = skills.get(Rnd.get(0, skillList.getCount() - 1));

		/**
		 * Demo mode - use probability from template
		 */

		int skillProbability = npcSkill.getProbability();
		if(Rnd.get(0, 100) < skillProbability)
		{
			Skill skill = SkillEngine.getInstance().getSkill(owner, npcSkill.getSkillid(), npcSkill.getSkillLevel(),
				owner.getTarget());
			skill.useSkill();
		}
		
		
		return true;
	}

	@Override
	public void onClear()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public int getExecutionInterval()
	{
		return 1;
	}

}
