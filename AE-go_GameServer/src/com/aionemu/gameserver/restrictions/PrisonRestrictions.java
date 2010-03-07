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

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapType;

/**
 * @author lord_rex
 * 
 */
public class PrisonRestrictions extends AbstractRestrictions
{
	@Override
	public boolean isRestricted(Player player, Class<? extends Restrictions> callingRestriction)
	{
		if(isInPrison(player))
		{
			PacketSendUtility.sendMessage(player, "You are in prison!");
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean canAttack(Player player, VisibleObject target)
	{
		if(isInPrison(player))
		{
			PacketSendUtility.sendMessage(player, "You cannot attack in prison!");
			return false;
		}

		return true;
	}

	@Override
	public boolean canUseSkill(Player player, Skill skill)
	{
		if(isInPrison(player))
		{
			PacketSendUtility.sendMessage(player, "You cannot use skills in prison!");
			return false;
		}

		return true;
	}
	
	
	@Override
	public boolean canAffectBySkill(Player player, VisibleObject target)
	{
		return true;
	}

	@Override
	public boolean canChat(Player player)
	{
		if(isInPrison(player))
		{
			PacketSendUtility.sendMessage(player, "You cannot chat in prison!");
			return false;
		}

		return true;
	}

	@Override
	public boolean canInviteToGroup(Player player, Player target)
	{
		if(isInPrison(player))
		{
			PacketSendUtility.sendMessage(player, "You cannot invite members to group in prison!");
			return false;
		}

		return true;
	}

	@Override
	public boolean canChangeEquip(Player player)
	{
		if(isInPrison(player))
		{
			PacketSendUtility.sendMessage(player, "You cannot equip / unequip item in prison!");
			return false;
		}

		return true;
	}
	
	private boolean isInPrison(Player player)
	{
		return player.isInPrison() || player.getWorldId() == WorldMapType.PRISON.getId();
	}
}
