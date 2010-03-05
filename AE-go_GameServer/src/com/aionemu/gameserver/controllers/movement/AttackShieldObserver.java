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
package com.aionemu.gameserver.controllers.movement;

import java.util.List;

import com.aionemu.gameserver.controllers.attack.AttackResult;
import com.aionemu.gameserver.skillengine.model.Effect;


/**
 * @author ATracer
 *
 */
public class AttackShieldObserver extends AttackCalcObserver
{

	private int hit;
	private int totalHit;
	private Effect effect;
	private boolean percent;
	
	/**
	 * @param percent 
	 * @param value
	 * @param status
	 */
	public AttackShieldObserver(int hit, int totalHit, boolean percent, Effect effect)
	{
		this.hit = hit;
		this.totalHit = totalHit;
		this.effect = effect;
		this.percent = percent;
	}

	@Override
	public void checkShield(List<AttackResult> attackList)
	{
		for(AttackResult attackResult : attackList)
		{
			int damage = attackResult.getDamage();
			
			int absorbedDamage = 0;
			if(percent)
				absorbedDamage = damage * hit / 100;
			else
				absorbedDamage = damage >= hit ? hit : damage;
				
			absorbedDamage = absorbedDamage >= totalHit ? totalHit : absorbedDamage;
			totalHit -= absorbedDamage;
			
			if(absorbedDamage > 0)
				attackResult.setShieldType(2);//TODO investigate other shield types
			attackResult.setDamage(damage - absorbedDamage);
			
			if(totalHit <=0)
			{
				effect.endEffect();
				return;
			}
		}	
	}
}
