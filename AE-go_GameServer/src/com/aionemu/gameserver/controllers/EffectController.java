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
package com.aionemu.gameserver.controllers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABNORMAL_EFFECT;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 *
 */
public class EffectController
{
	private Creature owner;
	
	private ConcurrentMap<Integer, Effect> effectMap;
	
	public EffectController(Creature owner)
	{
		super();
		this.owner = owner;
		this.effectMap = new ConcurrentHashMap<Integer, Effect>();
	}

	/**
	 * @return the owner
	 */
	public Creature getOwner()
	{
		return owner;
	}
	
	/**
	 * 
	 * @param effect
	 */
	public void addEffect(Effect effect)
	{
		effect.setController(this);
		//TODO effect and stack groups
		Effect resultEffect = effectMap.putIfAbsent(effect.getSkillId(), effect);
		if(resultEffect != null && resultEffect.getSkillLevel() < effect.getSkillLevel())
		{
			effectMap.replace(effect.getSkillId(), effect);	
			resultEffect.endEffect();		
		}
		effect.startEffect();
		//TODO
		broadCastEffects();
	}
	
	private void broadCastEffects()
	{
		PacketSendUtility.broadcastPacket(getOwner(),
			new SM_ABNORMAL_EFFECT(getOwner().getObjectId(),
				effectMap.values().toArray(new Effect[effectMap.size()])));
	}
	
	public void removeEffect(int skillId)
	{
		effectMap.remove(skillId);
		//TODO remove accomplished effects
		broadCastEffects();
	}
	
	//TODO removeAndStopEffect

}
