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
package com.aionemu.gameserver.controllers;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 *
 */
public class FlyController
{
	@SuppressWarnings("unused")
	private static final Logger	log	= Logger.getLogger(FlyController.class);

	private Player player;

	public FlyController(Player player)
	{
		this.player = player;
	}

	/**
	 * 
	 */
	public void onStopGliding()
	{
		if(player.isInState(CreatureState.GLIDING))
		{
			player.unsetState(CreatureState.GLIDING);

			if(player.isInState(CreatureState.FLYING))
			{
				player.setFlyState(1);
			}
			else
			{
				player.setFlyState(0);
				player.getLifeStats().triggerFpRestore();
			}

			PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
		}
	}

	/**
	 *  Ends flying
	 *  1) by CM_EMOTION (pageDown or fly button press)
	 *  2) from server side during teleportation (abyss gates should not break flying)
	 *  3) when FP is decreased to 0
	 */
	public void endFly()
	{
		// unset flying and gliding
		if(player.isInState(CreatureState.FLYING) || player.isInState(CreatureState.GLIDING))
		{
			PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 9, 0, 0), true);
			player.unsetState(CreatureState.FLYING);
			player.unsetState(CreatureState.GLIDING);
			player.setFlyState(0);

			// this is probably needed to change back fly speed into speed.
			PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 30, 0, 0), true);
			PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));

			player.getLifeStats().triggerFpRestore();
		}
	}

	/**
	 *  This method is called to start flying 
	 *  (called by CM_EMOTION when pageUp or pressed fly button)
	 */
	public void startFly()
	{
		player.setState(CreatureState.FLYING);
		player.setFlyState(1);
		player.getLifeStats().triggerFpReduce();
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 30, 0, 0), true);
		PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
	}

	/**
	 *  Switching to glide mode
	 *  (called by CM_MOVE with VALIDATE_GLIDE movement type)
	 *  
	 *  1) from standing state
	 *  2) from flying state
	 *  
	 *  If from stand to glide - start fp reduce + emotions/stats
	 *  if from fly to glide - only emotions/stats
	 */
	public void switchToGliding()
	{
		if(!player.isInState(CreatureState.GLIDING))
		{
			player.setState(CreatureState.GLIDING);
			if(player.getFlyState() == 0)
				player.getLifeStats().triggerFpReduce();
			player.setFlyState(2);

			PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
		}
	}
}
