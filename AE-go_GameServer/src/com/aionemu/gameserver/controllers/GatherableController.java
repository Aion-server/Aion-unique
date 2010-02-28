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

import java.util.List;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.controllers.movement.StartMovingListener;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.GatherableTemplate;
import com.aionemu.gameserver.model.templates.gather.Material;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE;
import com.aionemu.gameserver.skillengine.task.GatheringTask;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 *
 */
public class GatherableController extends VisibleObjectController<Gatherable>
{
	private int gatherCount;
	
	private int currentGatherer;
	
	private GatheringTask task;
	
	public enum GatherState
	{
		GATHERED,
		GATHERING,
		IDLE
	}

	private GatherState state = GatherState.IDLE;
	
	/**
	 *  Start gathering process
	 *  
	 * @param player
	 */
	public void onStartUse(final Player player)
	{
		//basic actions, need to improve here
		final GatherableTemplate template = this.getOwner().getObjectTemplate();
		
		if(!checkPlayerSkill(player, template))
			return;
		
		List<Material> materials = template.getMaterials().getMaterial();
		final Material material = materials.get(0);

		if(state != GatherState.GATHERING)
		{
			state = GatherState.GATHERING;
			currentGatherer = player.getObjectId();
			player.getObserveController().attach(new StartMovingListener(){
				
				@Override
				public void moved()
				{
					finishGathering(player);				
				}
			});
			
			task = new GatheringTask(player, getOwner(), material);
			task.start();
		}
	}
	
	/**
	 *  Checks whether player have needed skill for gathering and skill level is sufficient
	 *  
	 * @param player
	 * @param template
	 * @return
	 */
	private boolean checkPlayerSkill(final Player player, final GatherableTemplate template)
	{
		int harvestSkillId = template.getHarvestSkill();

		//check skill is available
		if(!player.getSkillList().isSkillPresent(harvestSkillId))
		{
			//TODO send some message ?
			return false;
		}
		if(player.getSkillList().getSkillLevel(harvestSkillId) < template.getSkillLevel())
		{
			//TODO send some message ?
			return false;
		}
		return true;
	}
	
	public void completeInteraction()
	{
		state = GatherState.IDLE;
		gatherCount++;
		if(gatherCount == getOwner().getObjectTemplate().getHarvestCount())
			onDie();
	}
	
	/**
	 *  Adds item to inventory on successful gathering
	 *  
	 * @param material
	 * @param player
	 */
	public void addItem(Material material, Player player)
	{
		sp.getItemService().addItem(player, material.getItemid(), 1, false);
	}
	
	public void rewardPlayer(Player player)
	{
		if(player != null)
		{
			int skillLvl = getOwner().getObjectTemplate().getSkillLevel();
			int xpReward = skillLvl * 10 + Rnd.get(0, skillLvl * 10/2);
			player.getCommonData().addExp(xpReward);
			
			player.getSkillList().addSkillXp(player, getOwner().getObjectTemplate().getHarvestSkill(), xpReward);
		}
	}
	
	/**
	 *  Called by client when some action is performed or on finish gathering
	 *  Called by move observer on player move
	 *  
	 * @param player
	 */
	public void finishGathering(Player player)
	{
		if(currentGatherer == player.getObjectId())
		{
			if(state == GatherState.GATHERING)
			{
				task.abort();
			}
			currentGatherer = 0;
			state = GatherState.IDLE;
		}	
	}
	
	private void onDie()
	{
		Gatherable owner = getOwner();		
		PacketSendUtility.broadcastPacket(owner, new SM_DELETE(owner));			
		sp.getRespawnService().scheduleRespawnTask(owner);
		sp.getWorld().despawn(owner);
	}


	@Override
	public void onRespawn()
	{
		this.gatherCount = 0;
	}

	@Override
	public Gatherable getOwner()
	{
		return (Gatherable) super.getOwner();
	}
}
