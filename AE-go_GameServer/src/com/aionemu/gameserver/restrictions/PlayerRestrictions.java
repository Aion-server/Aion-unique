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
package com.aionemu.gameserver.restrictions;

import com.aionemu.gameserver.model.gameobjects.Citizen;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Monster;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.effect.EffectId;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.SkillType;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author lord_rex
 * 
 */
public class PlayerRestrictions extends AbstractRestrictions
{	
	@Override
	public boolean canAffectBySkill(Player player, VisibleObject target)
	{
		if(((Creature) target).getLifeStats().isAlreadyDead())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.INVALID_TARGET());
			return false;
		}
		// TODO: We have to add the exception skills, 
		// what's can be used on dead target.
		
		if(!player.canAttack())
			return false;
		
		return true;
	}
	
	@Override
	public boolean canUseSkill(Player player, Skill skill)
	{
		// check if is casting to avoid multicast exploit
		// TODO cancel skill if other is used
		if(player.isCasting())
			return false;
		
		if(!player.canAttack())
			return false;
		
		if(skill.getSkillTemplate().getType() == SkillType.MAGICAL 
			&& player.getEffectController().isAbnoramlSet(EffectId.SILENCE))
			return false;

		return true;
	}

	@Override
	public boolean canInviteToGroup(Player player, Player target)
	{
		final PlayerGroup group = player.getPlayerGroup();
		
		if(group != null && group.isFull())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.FULL_GROUP());
			return false;
		}
		else if(group != null && player.getObjectId() != group.getGroupLeader().getObjectId())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.ONLY_GROUP_LEADER_CAN_INVITE());
			return false;
		}
		else if(target == null)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.INVITED_PLAYER_OFFLINE());
			return false;
		}
		else if(target.getCommonData().getRace() != player.getCommonData().getRace())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.CANT_INVITE_OTHER_RACE());
			return false;
		}
		else if(target.sameObjectId(player.getObjectId()))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.CANNOT_INVITE_YOURSELF());
			return false;
		}
		else if(target.getLifeStats().isAlreadyDead())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.SELECTED_TARGET_DEAD());
			return false;
		}
		else if(player.getLifeStats().isAlreadyDead())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.CANNOT_INVITE_BECAUSE_YOU_DEAD());
			return false;
		}
		else if(target.getPlayerGroup() != null)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.PLAYER_IN_ANOTHER_GROUP(target.getName()));
			return false;
		}
		
		return true;
	}

	@Override
	public boolean canAttack(Player player, VisibleObject target)
	{
		if(target == null)
			return false;
		
		if(!(target instanceof Creature))
			return false;
		
		Creature creature = (Creature) target;
		
		if(creature.getLifeStats().isAlreadyDead())
			return false;
		
		if(creature instanceof Monster)
			return true;
		
		if(creature instanceof Citizen)
		{
			Citizen citizen = (Citizen) creature;
			if(!citizen.isAggressiveTo(player.getCommonData().getRace()))
				return false;
		}
		return true;
	}

	@Override
	public boolean canUseWarehouse(Player player)
	{
		if(player == null || !player.isOnline())
			return false;
		
		//TODO retail message to requestor and player
		if(player.isTrading())
			return false;
		
		return true;
	}

	@Override
	public boolean canTrade(Player player)
	{
		if(player == null || !player.isOnline())
			return false;
		
		//TODO retail message to requestor and player
		if(player.isTrading())
			return false;
		
		return true;
	}
	
}
