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

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL_END;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.StatFunctions;


/**
 * @author ATracer
 *
 */
public class MagDamageSkillHandler extends TemplateSkillHandler
{
	private static final Logger log = Logger.getLogger(MagDamageSkillHandler.class);

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
        
        //TODO investigate unk
        int unk = 0;
        
        int damage = StatFunctions.calculateMagicDamageToTarget(player, target, getSkillTemplate());
        target.getLifeStats().reduceHp(damage);
        target.getController().onAttack(player);
        
        PacketSendUtility.broadcastPacket(player,
            new SM_CASTSPELL_END(player.getObjectId(), getSkillId(), getSkillTemplate().getLevel(),
            	unk, target.getObjectId(), damage), true);
	}

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

		schedulePerformAction(player, getSkillTemplate().getDuration());
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.skillengine.handlers.TemplateSkillHandler#useSkill(com.aionemu.gameserver.model.gameobjects.Creature)
	 */
	@Override
	public void useSkill(Creature creature)
	{
		super.useSkill(creature);
	}

}
