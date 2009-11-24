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
package com.aionemu.gameserver.controllers;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.controllers.movement.MoveObserver;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.GatherableTemplate;
import com.aionemu.gameserver.model.templates.gather.Material;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GATHER_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GATHER_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 *
 */
public class GatherableController extends VisibleObjectController<Gatherable>
{

	private ItemService itemService;
	
	public enum GatherState
	{
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

	public void onStartUse(final Player player)
	{
		//basic actions, need to improve here
		final GatherableTemplate template = this.getOwner().getTemplate();
		List<Material> materials = template.getMaterials().getMaterial();
		final Material material = materials.get(0);

		if(player != null && state != GatherState.GATHERING)
		{
			state = GatherState.GATHERING;
			player.getController().attach(new MoveObserver(){
				
				@Override
				public void moved()
				{
					PacketSendUtility.sendPacket(player, new SM_GATHER_UPDATE(template, material, 0, 0, 5));
					//TODO this packet is incorrect cause i need to find emotion of aborted gathering
					PacketSendUtility.broadcastPacket(player, new SM_GATHER_STATUS(player.getObjectId(), getOwner().getObjectId(), 2));
					finishGathering();
				}
			});
			
			PacketSendUtility.sendPacket(player, new SM_GATHER_UPDATE(template, material, 0, 0, 0));
			PacketSendUtility.broadcastPacket(player, new SM_GATHER_STATUS(player.getObjectId(), getOwner().getObjectId(), 0), true);
			PacketSendUtility.broadcastPacket(player, new SM_GATHER_STATUS(player.getObjectId(), getOwner().getObjectId(), 1), true);
			scheduleGathering(player, template, material);
		}
	}

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
				if(player != null)
				{							
					PacketSendUtility.sendPacket(player, new SM_GATHER_UPDATE(template, material, successCounter, failureCounter, 1));
					analyze();
				}
			}

			private void analyze()
			{
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
					PacketSendUtility.sendPacket(player, new SM_GATHER_UPDATE(template, material, successCounter, failureCounter, 2));					
					PacketSendUtility.sendPacket(player, new SM_GATHER_UPDATE(template, material, successCounter, failureCounter, 6));
					PacketSendUtility.broadcastPacket(player, new SM_GATHER_STATUS(player.getObjectId(), getOwner().getObjectId(), 2), true);
					addItem(material, player);
					finishGathering();
				}
				else if(failureCounter >= template.getFailureAdj())
				{
					PacketSendUtility.sendPacket(player, new SM_GATHER_UPDATE(template, material, successCounter, failureCounter, 1));					
					PacketSendUtility.sendPacket(player, new SM_GATHER_UPDATE(template, material, successCounter, failureCounter, 7));
					PacketSendUtility.broadcastPacket(player, new SM_GATHER_STATUS(player.getObjectId(), getOwner().getObjectId(), 3), true);
					finishGathering();
				}
			}

		}), 1000, 2500);
	}
	
	private void addItem(Material material, Player player)
	{
		Item item = itemService.newItem(material.getItemid(), 1);//TODO count always 1 ?
		if(item != null)
		{
			Item addedItem = player.getInventory().addToBag(item);
			if(addedItem != null)
			{
				//TODO modify SM_INVENTORY_UPDATE for update count - not all item count
				PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE(Collections.singletonList(addedItem)));
			}
		}
	}
	
	public void finishGathering(Player player)
	{
		finishGathering();
	}

	private void finishGathering()
	{
		state = GatherState.IDLE;
		if(gatheringTask != null && !gatheringTask.isCancelled())
		{
			gatheringTask.cancel(true);
			gatheringTask = null;
		}
	}

}
