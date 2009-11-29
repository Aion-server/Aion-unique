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
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABNORMAL_EFFECT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABNORMAL_STATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 *
 */
// Global TODO - differentiate effect controllers to npcs and players ?
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
		//TODO stack groups and level check
		if(effectMap.containsKey(effect.getSkillId()))
		{
			effectMap.get(effect.getSkillId()).endEffect();
		}
		effectMap.put(effect.getSkillId(), effect);
		
		effect.startEffect();
		
		// effect icon updates
		if(owner instanceof Player)
		{
			updatePlayerEffectIcons();
			updatePlayerStats();
		}
		broadCastEffects();	
	}
	
	/**
	 *  Broadcasts current effects to all visible objects
	 */
	private void broadCastEffects()
	{	
		PacketSendUtility.broadcastPacket(getOwner(),
			new SM_ABNORMAL_EFFECT(getOwner().getObjectId(),
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
		effectMap.remove(effect.getSkillId());
		
		broadCastEffects();
		if(owner instanceof Player)
		{
			updatePlayerEffectIcons();
		}
	}

}
