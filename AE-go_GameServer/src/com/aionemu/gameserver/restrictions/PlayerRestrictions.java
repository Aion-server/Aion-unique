/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.restrictions;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author lord_rex
 * 
 */
public class PlayerRestrictions extends AbstractRestrictions
{
	@Override
	public boolean canUseSkill(Player player, VisibleObject target)
	{
		if(((Creature) target).getLifeStats().isAlreadyDead())
		{
			PacketSendUtility.sendMessage(player, "You cannot use skill on your target if it's dead!"); // TODO: Need retail message.
			return false;
		}
		// TODO: We have to add the exception skills, 
		// what's can be used on dead target.

		return true;
	}
}
