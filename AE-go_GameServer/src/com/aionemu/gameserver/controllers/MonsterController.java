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
package com.aionemu.gameserver.controllers;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Monster;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_STATUS;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.StatFunctions;
import com.aionemu.gameserver.world.WorldMapType;

/**
 * @author ATracer
 *
 */
public class MonsterController extends NpcController
{
	@Override
	public void doDrop(Player player)
	{
		super.doDrop(player);
		sp.getDropService().registerDrop(getOwner() , player);			
		PacketSendUtility.broadcastPacket(this.getOwner(), new SM_LOOT_STATUS(this.getOwner().getObjectId(), 0));
	}
	
	@Override
	public void doReward(Creature creature)
	{
		super.doReward(creature);

		if(creature instanceof Player)
		{
			Player player = (Player) creature;
			if(player.getPlayerGroup() == null) //solo
			{
				long xpReward = StatFunctions.calculateSoloExperienceReward(player, getOwner());				
				player.getCommonData().addExp(xpReward);				

				//DPreward
				int currentDp = player.getCommonData().getDp();
				int dpReward = StatFunctions.calculateSoloDPReward(player, getOwner());
				player.getCommonData().setDp(dpReward + currentDp);
				//AP reward in abyss basic
				if(player.getWorldId() == WorldMapType.RESHANTA.getId())
					sp.getAbyssService().doReward(getOwner(), player);
			}
			else
			{
				sp.getGroupService().doReward(player.getPlayerGroup(), getOwner());
			}
			//TODO group quest, and group member's quests
			QuestEngine.getInstance().onKill(new QuestEnv(getOwner(), player, 0 , 0));
		}
	}
	
	@Override
	public void onRespawn()
	{
		super.onRespawn();
		sp.getDropService().unregisterDrop(getOwner());
	}

	@Override
	public Monster getOwner()
	{
		return (Monster) super.getOwner();
	}
}