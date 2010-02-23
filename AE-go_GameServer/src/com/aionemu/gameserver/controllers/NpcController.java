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

import java.util.concurrent.Future;

import com.aionemu.gameserver.ai.events.Event;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.gameobjects.player.StorageType;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.stats.NpcLifeStats;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SELL_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TELEPORT_MAP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TRADELIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_WAREHOUSE_INFO;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.CubeExpandService;
import com.aionemu.gameserver.services.DecayService;
import com.aionemu.gameserver.services.DropService;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.services.RespawnService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class is for controlling Npc's
 * 
 * @author -Nemesiss-, ATracer (2009-09-29)
 */
public class NpcController extends CreatureController<Npc>
{
	protected Future<?>		decayTask;

	protected DropService	dropService;

	public void setDropService(DropService dropService)
	{
		this.dropService = dropService;
	}

	@Override
	public void onRespawn()
	{
		this.decayTask = null;
		// TODO based on template
		this.getOwner().unsetState(CreatureState.DEAD);
		this.getOwner().setState(CreatureState.NPC_IDLE);
		this.getOwner().setLifeStats(new NpcLifeStats(getOwner()));
	}

	public void onDespawn(boolean forced)
	{
		if(forced && decayTask != null)
			decayTask.cancel(true);

		Npc owner = getOwner();
		if(owner == null || !owner.isSpawned())
			return;

		PacketSendUtility.broadcastPacket(owner, new SM_DELETE(owner));
		if(owner.getAi() != null)
			owner.getAi().handleEvent(Event.DESPAWN);
		owner.getPosition().getWorld().despawn(owner);
		decayTask = null;
	}

	@Override
	public void onDie()
	{
		super.onDie();
		if(decayTask == null)
		{
			decayTask = DecayService.getInstance().scheduleDecayTask(this.getOwner());
		}
		int instanceId = getOwner().getInstanceId();
		if(!getOwner().getSpawn().isNoRespawn(instanceId))
		{
			RespawnService.getInstance().scheduleRespawnTask(this.getOwner());
		}
	}

	@Override
	public Npc getOwner()
	{
		return (Npc) super.getOwner();
	}

	@Override
	public void onDialogRequest(Player player)
	{
		if(QuestEngine.getInstance().onDialog(new QuestEnv(getOwner(), player, 0, -1)))
			return;
		// TODO need check here
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 10));
	}

	/**
	 * This method should be called to make forced despawn of NPC and delete it from the world
	 */
	public void onDelete()
	{
		if(getOwner().isInWorld())
		{
			this.onDespawn(true);
			this.delete();
		}
	}

	/**
	 * Handle dialog
	 * 
	 * @return true if target is npc
	 */
	public void onDialogSelect(int dialogId, final Player player, int questId, LegionService legionService,
		CubeExpandService cubeExpandService)
	{

		Npc npc = getOwner();
		int targetObjectId = npc.getObjectId();

		if(QuestEngine.getInstance().onDialog(new QuestEnv(npc, player, questId, dialogId)))
			return;

		switch(dialogId)
		{
			case 2:
				PacketSendUtility.sendPacket(player, new SM_TRADELIST(npc));
				break;
			case 3:
				PacketSendUtility.sendPacket(player, new SM_SELL_ITEM(player, targetObjectId));
				break;
			case 4:
				// stigma
				PacketSendUtility.sendPacket(player, new SM_MESSAGE(0, null, "This feature is not available yet",
					ChatType.ANNOUNCEMENTS));
				break;
			case 5:
				// create legion
				if(MathUtil.isInRange(npc, player, 10)) // avoiding exploit with sending fake dialog_select packet
				{
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 2));
				}
				else
				{
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.LEGION_CREATE_TOO_FAR_FROM_NPC());
				}
				break;
			case 6:
				// disband legion
				if(MathUtil.isInRange(npc, player, 10)) // avoiding exploit with sending fake dialog_select packet
				{
					legionService.disbandLegion(npc, player);
				}
				else
				{
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.LEGION_DISPERSE_TOO_FAR_FROM_NPC());
				}
				break;
			case 7:
				// recreate legion
				if(MathUtil.isInRange(npc, player, 10)) // voiding exploit with sending fake client dialog_select
				// packet
				{
					legionService.recreateLegion(npc, player);
				}
				else
				{
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.LEGION_DISPERSE_TOO_FAR_FROM_NPC());
				}
				break;
			case 20:
				// warehouse
				if(MathUtil.isInRange(npc, player, 10)) // voiding exploit with sending fake client dialog_select
				// packet
				{
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 26));
					PacketSendUtility.sendPacket(player,
						new SM_WAREHOUSE_INFO(player.getStorage(StorageType.REGULAR_WAREHOUSE.getId())
							.getStorageItems(), StorageType.REGULAR_WAREHOUSE.getId()));
					PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(null, StorageType.REGULAR_WAREHOUSE
						.getId())); // strange
					// retail
					// way of sending
					// warehouse packets
					PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(player.getStorage(
						StorageType.ACCOUNT_WAREHOUSE.getId()).getAllItems(), StorageType.ACCOUNT_WAREHOUSE.getId()));
					PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(null, StorageType.ACCOUNT_WAREHOUSE
						.getId()));
				}
				break;
			case 27:
				// Consign trade?? npc karinerk, koorunerk
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 13));
				break;
			case 29:
				// soul healing
				RequestResponseHandler responseHandler = new RequestResponseHandler(npc){
					@Override
					public void acceptRequest(Creature requester, Player responder)
					{
						Long lossexp = responder.getCommonData().getExpRecoverable();
						if(player.getInventory().getKinahItem().getItemCount() > lossexp)
						{
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.EXP(String.valueOf(lossexp
								.intValue())));// TODO check
							// SM_SYSTEM_MESSAGE
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.SOUL_HEALED());
							player.getCommonData().resetRecoverableExp();
							player.getInventory().decreaseKinah(lossexp.intValue());
						}
						// TODO not enought kinah message
					}

					@Override
					public void denyRequest(Creature requester, Player responder)
					{
						// no message
					}
				};
				if(player.getCommonData().getExpRecoverable() > 0)
				{
					boolean result = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_SOUL_HEALING,
						responseHandler);
					if(result)
					{
						PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(
							SM_QUESTION_WINDOW.STR_SOUL_HEALING, 0, String.valueOf(player.getCommonData()
								.getExpRecoverable())));
					}
				}
				else
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.DONT_HAVE_RECOVERED_EXP());
				break;
			case 35:
				// Godstone socketing
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 21));
				break;
			case 36:
				// remove mana stone
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 20));
				break;
			case 37:
				// modify appearance
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 19));
				break;
			case 38:
				// flight and teleport
				// if (player.getCommonData().isFlying() == true)
				// {
				// PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300696));
				// return;
				// }
				PacketSendUtility.sendPacket(player, new SM_TELEPORT_MAP(player, targetObjectId));
				break;
			case 39:
				// improve extraction skill npc cornelius, jhaelas in sanctum
				PacketSendUtility.sendPacket(player, new SM_MESSAGE(0, null, "This feature is not available yet",
					ChatType.ANNOUNCEMENTS));
				break;
			case 40:
				// learn tailoring armor smithing etc...
				PacketSendUtility.sendPacket(player, new SM_MESSAGE(0, null, "This feature is not available yet",
					ChatType.ANNOUNCEMENTS));
				break;
			case 41:
				// expand cube
				cubeExpandService.expandCube(player, npc);
				break;
			case 47:
				// legion warehouse
				if(MathUtil.isInRange(npc, player, 10)) // voiding exploit with sending fake client dialog_select
				// packet
				{
					legionService.openLegionWarehouse(player);
				}
				break;
			case 50:
				// WTF??? Quest dialog packet
				break;
			case 52:
				// work order from lerning npc in sanctum
				PacketSendUtility.sendPacket(player, new SM_MESSAGE(0, null, "This feature is not available yet",
					ChatType.ANNOUNCEMENTS));
				break;
			case 53:
				// coin reward
				PacketSendUtility.sendPacket(player, new SM_MESSAGE(0, null, "This feature is not available yet",
					ChatType.ANNOUNCEMENTS));
				break;
			default:
				if(questId > 0)
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, dialogId, questId));
				else
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, dialogId));
				break;
		}
	}
}