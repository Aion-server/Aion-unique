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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABNORMAL_EFFECT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABNORMAL_STATE;
import com.aionemu.gameserver.skillengine.effect.EffectId;
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
	private Map<String, Effect> noshowEffects;
	private Map<String, Effect> abnormalEffectMap;

	private int abnormals;

	public EffectController(Creature owner)
	{
		this.owner = owner;
		this.abnormalEffectMap = new ConcurrentHashMap<String, Effect>();
		this.passiveEffectMap = new ConcurrentHashMap<String, Effect>();
		this.noshowEffects = new ConcurrentHashMap<String, Effect>();
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
		Map<String, Effect> mapToUpdate = getMapForEffect(effect);
		
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
	 * 
	 * @param effect
	 * @return
	 */
	private Map<String, Effect> getMapForEffect(Effect effect)
	{
		if(effect.isPassive())
			return passiveEffectMap;
		
		if(effect.isToggle())
			return noshowEffects;
		
		return abnormalEffectMap;
	}
	
	/**
	 * 
	 * @param stack
	 * @return abnormalEffectMap
	 */
	public Effect getAnormalEffect(String stack)
	{
		return abnormalEffectMap.get(stack);
	}

	/**
	 *  Broadcasts current effects to all visible objects
	 */
	public void broadCastEffects()
	{	
		PacketSendUtility.broadcastPacket(getOwner(),
			new SM_ABNORMAL_EFFECT(getOwner().getObjectId(), abnormals,
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
			abnormals, abnormalEffectMap.values().toArray(new Effect[abnormalEffectMap.size()])));
	}

	/**
	 * 
	 * @param effect
	 */
	public void clearEffect(Effect effect)
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
	 * 
	 * @param effectId
	 */
	public void removeEffectByEffectId(int effectId)
	{
		for(Effect effect : abnormalEffectMap.values()){
			if(effect.containsEffectId(effectId)){
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
				passiveEffectMap.remove(effect.getStack());
				effect.endEffect();
			}
		}
	}
	
	/**
	 * 
	 * @param skillid
	 */
	public void removeNoshowEffect(int skillid)
	{
		for(Effect effect : noshowEffects.values()){
			if(effect.getSkillId()==skillid){
				noshowEffects.remove(effect.getStack());
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
	
	public void updatePlayerEffectIcons()
	{
		getOwner().addPacketBroadcastMask(BroadcastMode.UPDATE_PLAYER_EFFECT_ICONS);
	}
	
	public void updatePlayerEffectIconsImpl()
	{
		PacketSendUtility.sendPacket((Player) owner,
			new SM_ABNORMAL_STATE(abnormalEffectMap.values().toArray(new Effect[abnormalEffectMap.size()]), abnormals));
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
	
	/**
	 *  Used for checking unique abnormal states
	 *  
	 * @param effectId
	 * @return
	 */
	public boolean isAbnoramlSet(EffectId effectId)
	{
		return (abnormals & effectId.getEffectId()) == effectId.getEffectId();
	}
	
	/**
	 *  Used for compound abnormal state checks
	 *  
	 * @param effectId
	 * @return
	 */
	public boolean isAbnormalState(EffectId effectId)
	{
		int state = abnormals & effectId.getEffectId();
		return state > 0 && state <= effectId.getEffectId();
	}

	public int getAbnormals()
	{
		return abnormals;
	}

}
