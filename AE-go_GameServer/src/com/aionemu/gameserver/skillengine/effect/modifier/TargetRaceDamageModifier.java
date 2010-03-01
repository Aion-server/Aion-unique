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
package com.aionemu.gameserver.skillengine.effect.modifier;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTargetRace;

/**
 * @author ATracer
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TargetRaceDamageModifier")
public class TargetRaceDamageModifier extends ActionModifier
{
	@XmlAttribute(name = "race")
	private SkillTargetRace skillTargetRace;
	@XmlAttribute(required = true)
	protected int delta;
	@XmlAttribute(required = true)
	protected int value;
	
	@Override
	public int analyze(Effect effect, int originalValue)
	{
		Creature effected = effect.getEffected();
		
		if(effected instanceof Player)
		{
			int newValue = originalValue + value + effect.getSkillLevel() * delta;
			Player player = (Player) effected;
			switch(skillTargetRace)
			{
				case ASMODIANS:
					if(player.getCommonData().getRace() == Race.ASMODIANS)
						return newValue;
					break;
				case ELYOS:
					if(player.getCommonData().getRace() == Race.ELYOS)
						return newValue;
			}
		}

		return originalValue;	
	}

	@Override
	public boolean check(Effect effect)
	{
		Creature effected = effect.getEffected();
		if(effected instanceof Player)
		{
			
			Player player = (Player) effected;
			Race race =  player.getCommonData().getRace();
			return (race == Race.ASMODIANS && skillTargetRace == SkillTargetRace.ASMODIANS)
				|| (race == Race.ELYOS && skillTargetRace == SkillTargetRace.ELYOS);
		}
		return false;
	}

}
