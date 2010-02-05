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
import java.util.concurrent.Future;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.controllers.movement.MoveObserver;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.SkillListEntry;
import com.aionemu.gameserver.model.templates.GatherableTemplate;
import com.aionemu.gameserver.model.templates.gather.Material;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GATHER_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GATHER_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.services.RespawnService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * @author ATracer
 *
 */
public class GatherableController extends VisibleObjectController<Gatherable>
{

	private ItemService itemService;
	
	private int gatherCount;
	
	private int currentGatherer;
	
	public enum GatherState
	{
		GATHERED,
		GATHERING,
		IDLE
	}

	private GatherState state = GatherState.IDLE;

	private Future<?> gatheringTask;

	/**
	 * @param itemService the itemService to set
	 */
	public void setItemService(ItemService itemService)
	{
		this.itemService = itemService;
	}
	
	/**
	 *  Start gathering process
	 *  
	 * @param player
	 */
	public void onStartUse(final Player player)
	{
		//basic actions, need to improve here
		final GatherableTemplate template = this.getOwner().getTemplate();
		
		if(!checkPlayerSkill(player, template))
			return;
		
		List<Material> materials = template.getMaterials().getMaterial();
		final Material material = materials.get(0);

		if(state != GatherState.GATHERING)
		{
			state = GatherState.GATHERING;
			currentGatherer = player.getObjectId();
			player.getController().attach(new MoveObserver(){
				
				@Override
				public void moved()
				{
					finishGathering(player);				
				}
			});
			
			PacketSendUtility.sendPacket(player, new SM_GATHER_UPDATE(template, material, 0, 0, 0));
			PacketSendUtility.broadcastPacket(player, new SM_GATHER_STATUS(player.getObjectId(), getOwner().getObjectId(), 0), true);
			PacketSendUtility.broadcastPacket(player, new SM_GATHER_STATUS(player.getObjectId(), getOwner().getObjectId(), 1), true);
			scheduleGathering(player, template, material);
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
		//check skill is available
		SkillListEntry skillEntry = player.getSkillList().getSkillMap().get(template.getHarvestSkill());
		if(skillEntry == null)
		{
			//TODO send some message ?
			return false;
		}
		if(skillEntry.getSkillLevel() < template.getSkillLevel())
		{
			//TODO send some message ?
			return false;
		}
		return true;
	}
	
	/**
	 *  Schedules gathering process animation
	 *  
	 * @param player
	 * @param template
	 * @param material
	 */
	private void scheduleGathering(final Player player,
		final GatherableTemplate template, final Material material)
	{

		gatheringTask = ThreadPoolManager.getInstance().scheduleAtFixedRate((new Runnable()
		{
			private int successCounter = 0;
			private int failureCounter = 0;

			@Override
			public void run()
			{					
				if(player == null)
					return;
				
				switch(state)
				{
					case GATHERING:
						PacketSendUtility.sendPacket(player, new SM_GATHER_UPDATE(template, material, successCounter, failureCounter, 1));
						analyze();
						break;
					case GATHERED:
						if(successCounter == template.getSuccessAdj())
						{
							PacketSendUtility.sendPacket(player, new SM_GATHER_UPDATE(template, material, successCounter, failureCounter, 2));					
							PacketSendUtility.sendPacket(player, new SM_GATHER_UPDATE(template, material, successCounter, failureCounter, 6));
							PacketSendUtility.broadcastPacket(player, new SM_GATHER_STATUS(player.getObjectId(), getOwner().getObjectId(), 2), true);
							PacketSendUtility.sendPacket(player,SM_SYSTEM_MESSAGE.Gather_Success(Integer.toString(60)));
							addItem(material, player);
							gatherCount++;
						}
						else if(failureCounter == template.getFailureAdj())
						{
							PacketSendUtility.sendPacket(player, new SM_GATHER_UPDATE(template, material, successCounter, failureCounter, 1));					
							PacketSendUtility.sendPacket(player, new SM_GATHER_UPDATE(template, material, successCounter, failureCounter, 7));
							PacketSendUtility.broadcastPacket(player, new SM_GATHER_STATUS(player.getObjectId(), getOwner().getObjectId(), 3), true);
							gatherCount++;
						}
						if(gatherCount == getOwner().getTemplate().getHarvestCount())
							onDie();
						finishGathering();
						break;
						
				}
			}

			private void analyze()
			{
				if(successCounter == template.getSuccessAdj() || failureCounter == template.getFailureAdj())
				{
					state = GatherState.GATHERED;
					return;
				}
				
				if(Rnd.nextBoolean())
				{
					successCounter += Rnd.get(20,60);
				}
				else
				{
					failureCounter += Rnd.get(0,30);
				}
				
				if(successCounter >= template.getSuccessAdj())
				{
					successCounter = template.getSuccessAdj();
				}
				else if(failureCounter >= template.getFailureAdj())
				{
					failureCounter = template.getFailureAdj();
				}
			}

		}), 1000, 2500);
	}
	
	/**
	 *  Adds item to inventory on successful gathering
	 *  
	 * @param material
	 * @param player
	 */
	private void addItem(Material material, Player player)
	{
		itemService.addItem(player, material.getItemid(), 1, false);
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
				GatherableTemplate template = this.getOwner().getTemplate();		
				List<Material> materials = template.getMaterials().getMaterial();
				Material material = materials.get(0);//TODO current material
				PacketSendUtility.sendPacket(player, new SM_GATHER_UPDATE(template, material, 0, 0, 5));
				//TODO this packet is incorrect cause i need to find emotion of aborted gathering
				PacketSendUtility.broadcastPacket(player, new SM_GATHER_STATUS(player.getObjectId(), getOwner().getObjectId(), 2));
			}
			finishGathering();
		}	
	}

	private void finishGathering()
	{
		currentGatherer = 0;
		state = GatherState.IDLE;
		if(gatheringTask != null && !gatheringTask.isCancelled())
		{
			gatheringTask.cancel(true);
			gatheringTask = null;
		}
	}
	
	private void onDie()
	{
		Gatherable owner = getOwner();		
		PacketSendUtility.broadcastPacket(owner, new SM_DELETE(owner));			
		RespawnService.getInstance().scheduleRespawnTask(owner);
		final World world = owner.getActiveRegion().getWorld();
		world.despawn(owner);
	}


	@Override
	public void onRespawn()
	{
		this.gatherCount = 0;
	}
}
