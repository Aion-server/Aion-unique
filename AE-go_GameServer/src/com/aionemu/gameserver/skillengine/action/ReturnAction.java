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
package com.aionemu.gameserver.skillengine.action;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.PlayerInitialData.LocationData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL_END;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKF5;
import com.aionemu.gameserver.skillengine.model.Env;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;


/**
 * @author ATracer
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReturnAction")
public class ReturnAction
extends Action
{

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.skillengine.action.Action#act(com.aionemu.gameserver.skillengine.model.Env)
	 */
	@Override
	public void act(Env env)
	{

		Player player = (Player) env.getEffector();
		SkillTemplate template = env.getSkillTemplate();

		//TODO investigate unk
		int unk = 0;

		PacketSendUtility.broadcastPacket(player,
			new SM_CASTSPELL_END(player.getObjectId(), template.getSkillId(), template.getLevel(), unk, 0, 0), true);

		env.getWorld().despawn(player);
		LocationData locationData = DataManager.PLAYER_INITIAL_DATA.getSpawnLocation(player.getCommonData().getRace());

		env.getWorld().setPosition(player, locationData.getMapId(),
			locationData.getX(), locationData.getY(), locationData.getZ(), locationData.getHeading());

		player.setProtectionActive(true);
		PacketSendUtility.sendPacket(player, new SM_UNKF5(player));


	}
}
