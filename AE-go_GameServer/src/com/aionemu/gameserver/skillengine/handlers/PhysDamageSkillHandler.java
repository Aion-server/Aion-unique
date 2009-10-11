/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
package com.aionemu.gameserver.skillengine.handlers;

import java.util.HashMap;
import java.util.Map;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.SkillEffectData;
import com.aionemu.gameserver.model.templates.SkillEffectTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL_END;
import com.aionemu.gameserver.skillengine.effect.AbstractEffect;
import com.aionemu.gameserver.skillengine.effect.SkillEffectType;
import com.aionemu.gameserver.utils.PacketSendUtility;


/**
 * @author ATracer
 * 
 */
public class PhysDamageSkillHandler extends TemplateSkillHandler
{

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.skillengine.handlers.TemplateSkillHandler#performAction(com.aionemu.gameserver.model.gameobjects.Creature)
	 */
	@Override
	protected void performAction(Creature creature)
	{
		Player player = (Player) creature;
		//TODO extract method 
        Creature target = creature.getTarget();
        if(target == null)
        {
            return;
        }

        int unk = 0;
        
        //TODO temporary solution
        //Each effect has result - damage points for instance
        Map<String, Integer> incluenceResult = new HashMap<String, Integer>();
        
        SkillEffectData skillEffectData = getSkillTemplate().getSkillEffectData();
        if(skillEffectData != null)
        {
        	 for(SkillEffectTemplate effectTemplate : skillEffectData.getSkillEffects())
             {
             	AbstractEffect effect = SkillEffectType.getEffectByName(effectTemplate);
             	//TODO player.onEffectInfluence(AbstractEffect)
             	int result = effect.influence(player, target);
             	incluenceResult.put(effectTemplate.getName(), result);
             }
        }

        target.getController().onAttack(player);
        
        //TODO now spell ends after damage is done. 
        PacketSendUtility.broadcastPacket(player,
            new SM_CASTSPELL_END(player.getObjectId(), getSkillId(), getSkillTemplate().getLevel(),
            	unk, target.getObjectId(), incluenceResult.get(SkillEffectType.DAMAGE.getName())), true);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.skillengine.handlers.TemplateSkillHandler#startUsage(com.aionemu.gameserver.model.gameobjects.Creature)
	 */
	@Override
	protected void startUsage(Creature creature)
	{
		Player player = (Player) creature;
		//TODO extract method 
        Creature target = creature.getTarget();
        if(target == null)
        {
            return;
        }

		final int unk = 0;

		PacketSendUtility.broadcastPacket(player, 
			new SM_CASTSPELL(player.getObjectId(), getSkillId(), getSkillTemplate().getLevel(),
				unk, target.getObjectId(), getSkillTemplate().getDuration()), true);

		performAction(player);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.skillengine.handlers.TemplateSkillHandler#useSkill(com.aionemu.gameserver.model.gameobjects.Creature)
	 */
	@Override
	public void useSkill(Creature creature)
	{
		// TODO Auto-generated method stub
		super.useSkill(creature);
	}
	
	
}
