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
package com.aionemu.gameserver.controllers.effect;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABNORMAL_EFFECT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABNORMAL_STATE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 *
 */
public class EffectController
{
	private Creature owner;
	
	private Map<String, Effect> passiveEffectMap;
	private ConcurrentMap<String, Effect> abnormalEffectMap;

	private int abnormals;

	public EffectController(Creature owner)
	{
		super();
		this.owner = owner;
		this.abnormalEffectMap = new ConcurrentHashMap<String, Effect>();
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
		Map<String, Effect> mapToUpdate = effect.isPassive() ? passiveEffectMap : abnormalEffectMap;
		
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
				abnormalEffectMap.values().toArray(new Effect[abnormalEffectMap.size()])));	
	}

	/**
	 *  Used when player see new player
	 *  
	 * @param player
	 */
	public void sendEffectIconsTo(Player player)
	{
		PacketSendUtility.sendPacket(player, new SM_ABNORMAL_EFFECT(getOwner().getObjectId(),
			abnormalEffectMap.values().toArray(new Effect[abnormalEffectMap.size()])));
	}

	/**
	 * 
	 * @param effect
	 */
	public void removeEffect(Effect effect)
	{
		abnormalEffectMap.remove(effect.getStack());

		broadCastEffects();
		if(owner instanceof Player)
		{
			updatePlayerEffectIcons();
		}
	}
	
	/**
	 * Removes the effect by skillid.
	 * 
	 * @param skillid
	 */
	public void removeEffect(int skillid)
	{
		for(Effect effect : abnormalEffectMap.values()){
			if(effect.getSkillId()==skillid){
				abnormalEffectMap.remove(effect.getStack());
				effect.endEffect();
			}
		}
		
		broadCastEffects();
		if(owner instanceof Player)
		{
			updatePlayerEffectIcons();
		}
	}
	
	/**
	 * Removes the effect by skillid.
	 * 
	 * @param skillid
	 */
	public void removePassiveEffect(int skillid)
	{
		for(Effect effect : passiveEffectMap.values()){
			if(effect.getSkillId()==skillid){
				abnormalEffectMap.remove(effect.getStack());
				effect.endEffect();
			}
		}
	}
	/**
	 * Removes all effects from controllers and ends them appropriately
	 * Passive effect will not be removed
	 */
	public void removeAllEffects()
	{
		for(Effect effect : abnormalEffectMap.values())
		{
			effect.endEffect();
		}
		abnormalEffectMap.clear();
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

	public void updatePlayerEffectIcons()
	{
		getOwner().addPacketBroadcastMask(BroadcastMode.UPDATE_PLAYER_EFFECT_ICONS);
	}
	
	public void updatePlayerEffectIconsImpl()
	{
		PacketSendUtility.sendPacket((Player) owner,
			new SM_ABNORMAL_STATE(abnormalEffectMap.values().toArray(new Effect[abnormalEffectMap.size()])));
	}
}
