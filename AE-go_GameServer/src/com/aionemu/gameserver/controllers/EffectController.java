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
package com.aionemu.gameserver.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABNORMAL_EFFECT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABNORMAL_STATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 *
 */
public class EffectController
{
	private Creature owner;
	
	private Map<String, Effect> passiveEffectMap;
	private ConcurrentMap<String, Effect> effectMap;

	private int abnormals;

	public EffectController(Creature owner)
	{
		super();
		this.owner = owner;
		this.effectMap = new ConcurrentHashMap<String, Effect>();
		this.passiveEffectMap = new HashMap<String, Effect>();
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

		Map<String, Effect> mapToUpdate = effect.isPassive() ? passiveEffectMap : effectMap;
		
		if(mapToUpdate.containsKey(effect.getStack()))
		{
			Effect existingEffect = mapToUpdate.get(effect.getStack());
			
			//check stack level
			if(existingEffect.getSkillStackLvl() > effect.getSkillStackLvl())
				return;
			//check skill level (when stack level same)
			if(existingEffect.getSkillStackLvl() == effect.getSkillStackLvl() 
				&& existingEffect.getSkillLevel() > effect.getSkillLevel())
				return;
			
			existingEffect.endEffect();
		}
		
		mapToUpdate.put(effect.getStack(), effect);
		effect.startEffect();
		
		if(owner instanceof Player && owner.isSpawned())
		{
			updatePlayerStats();
		}		
		
		if(!effect.isPassive())
		{
			// effect icon updates
			if(owner instanceof Player)
			{
				updatePlayerEffectIcons();				
			}
			broadCastEffects();	
		}	
	}

	/**
	 *  Broadcasts current effects to all visible objects
	 */
	public void broadCastEffects()
	{	
		PacketSendUtility.broadcastPacket(getOwner(),
			new SM_ABNORMAL_EFFECT(getOwner().getObjectId(),
				effectMap.values().toArray(new Effect[effectMap.size()])));	
	}

	/**
	 *  Used when player see new player
	 *  
	 * @param player
	 */
	public void sendEffectIconsTo(Player player)
	{
		PacketSendUtility.sendPacket(player, new SM_ABNORMAL_EFFECT(getOwner().getObjectId(),
			effectMap.values().toArray(new Effect[effectMap.size()])));
	}

	/**
	 *  Adds icon of effect to owner (only for Player objects)
	 *  
	 * @param effect
	 */
	public void updatePlayerEffectIcons()
	{
		PacketSendUtility.sendPacket((Player) owner,
			new SM_ABNORMAL_STATE(effectMap.values().toArray(new Effect[effectMap.size()])));
	}

	/**
	 * Updates player stats in UI
	 */
	private void updatePlayerStats()
	{
		Player player = (Player) owner;
		PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
	}

	/**
	 * 
	 * @param effect
	 */
	public void removeEffect(Effect effect)
	{
		effectMap.remove(effect.getStack());

		broadCastEffects();
		if(owner instanceof Player)
		{
			updatePlayerEffectIcons();
			updatePlayerStats();
		}
	}

	/**
	 * Removes all effects from controllers and ends them appropriately
	 * Passive effect will not be removed
	 */
	public void removeAllEffects()
	{
		for(Effect effect : effectMap.values())
		{
			effect.endEffect();
		}
		effectMap.clear();
	}

	/**
	 *  ABNORMAL EFFECTS
	 */

	public void setAbnormal(int mask)
	{
		abnormals |= mask;
	}

	public void unsetAbnormal(int mask)
	{
		abnormals &= ~mask;
	}

	public int getAbnormals()
	{
		return abnormals;
	}
}
