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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SELL_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TELEPORT_MAP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TRADELIST;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.CubeExpandService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.inject.Inject;

/**
 * 
 * @author KKnD , orz, avol
 * 
 */
public class CM_DIALOG_SELECT extends AionClientPacket
{
	/**
	 * Target object id that client wants to TALK WITH or 0 if wants to unselect
	 */
	private int					targetObjectId;
	private int					dialogId;
	@SuppressWarnings("unused")
	private int					unk1;
	@SuppressWarnings("unused")
	private int					lastPage;
	private int					questId;
	@Inject
	private CubeExpandService cubeExpandService;
	/**
	 * Constructs new instance of <tt>CM_CM_REQUEST_DIALOG </tt> packet
	 * @param opcode
	 */
	public CM_DIALOG_SELECT(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		targetObjectId = readD();// empty
		dialogId = readH(); //total no of choice
		unk1 = readH();
		lastPage = readH();
		questId = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if(player == null)
			return;
		
		if(targetObjectId == 0)
		{
			if (QuestEngine.getInstance().onDialog(new QuestEnv(null, player, questId, dialogId)))
				return;
			//FIXME client sends unk1=1, targetObjectId=0, dialogId=2 (trader) => we miss some packet to close window 
			ClassChangeService.changeClassToSelection(player, dialogId);
			return;
		}
		Npc npc = (Npc) player.getActiveRegion().getWorld().findAionObject(targetObjectId);

		if (QuestEngine.getInstance().onDialog(new QuestEnv(npc, player, questId, dialogId)))
			return;

		switch (dialogId)
		{
			case 2:
				sendPacket(new SM_TRADELIST(npc));
				break;
			case 3:
				sendPacket(new SM_SELL_ITEM(player, targetObjectId));
				break;
			case 4:
				//stigma
				sendPacket(new SM_MESSAGE(0, null, "This feature is not available yet", null, ChatType.ANNOUNCEMENTS));
				break;
			case 5:
				//create legion
				sendPacket(new SM_DIALOG_WINDOW(targetObjectId, 2));
				break;
			case 20:
				//warehouse
				sendPacket(new SM_DIALOG_WINDOW(targetObjectId, 26));
				break;
			case 27:
				//Consign trade?? npc karinerk, koorunerk
				sendPacket(new SM_DIALOG_WINDOW(targetObjectId, 13));
				break;
			case 29:
				//soul healing
				RequestResponseHandler responseHandler = new RequestResponseHandler(npc) 
				{				
					@Override
					public void acceptRequest(Creature requester, Player responder)
					{
						Long lossexp = responder.getCommonData().getExpRecoverable();
						if(player.getInventory().getKinahItem().getItemCount() > lossexp)
						{
							sendPacket(SM_SYSTEM_MESSAGE.EXP_RECOVEREBLED_BY_COUNT(String.valueOf(lossexp.intValue())));//TODO check SM_SYSTEM_MESSAGE
							sendPacket(SM_SYSTEM_MESSAGE.SOUL_HEALED());
							player.getCommonData().resetRecoverableExp();
							player.getInventory().decreaseKinah(lossexp.intValue());
						}
						//TODO not enought kinah message
					}
		
					@Override
					public void denyRequest(Creature requester, Player responder)
					{
						//no message
					}
				};
				if(player.getCommonData().getExpRecoverable() > 0)
				{
					boolean result = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_SOUL_HEALING,responseHandler);
					if(result)
					{
						PacketSendUtility.sendPacket(player, 
							new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_SOUL_HEALING, 0, String.valueOf(player.getCommonData().getExpRecoverable())));
					}
				}
				else sendPacket(SM_SYSTEM_MESSAGE.DONT_HAVE_RECOVERED_EXP());
				break;
			case 35:
				//Godstone socketing
				sendPacket(new SM_DIALOG_WINDOW(targetObjectId, 21));
				break;
			case 36:
				//remove mana stone 
				sendPacket(new SM_DIALOG_WINDOW(targetObjectId, 20));
				break;
			case 37:
				//modify appearance
				sendPacket(new SM_DIALOG_WINDOW(targetObjectId, 19));
				break;
			case 38:
				//flight and teleport
//				if (player.getCommonData().isFlying() == true)
//				{
//					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300696));
//					return;
//				}
				sendPacket(new SM_TELEPORT_MAP(player, targetObjectId));
				break;
			case 39:
				//improve extraction skill npc cornelius, jhaelas in sanctum
				sendPacket(new SM_MESSAGE(0, null, "This feature is not available yet", null, ChatType.ANNOUNCEMENTS));
				break;
			case 40:
				//learn tailoring armor smithing etc...
				sendPacket(new SM_MESSAGE(0, null, "This feature is not available yet", null, ChatType.ANNOUNCEMENTS));
				break;
			case 41:
				//expand cube
				cubeExpandService.expandCube(player, npc);
				break;
			case 50:
				// WTF??? Quest dialog packet
				break;
			case 52:
				//work order from lerning npc in sanctum
				sendPacket(new SM_MESSAGE(0, null, "This feature is not available yet", null, ChatType.ANNOUNCEMENTS));
				break;
			case 53:
				//coin reward
				sendPacket(new SM_MESSAGE(0, null, "This feature is not available yet", null, ChatType.ANNOUNCEMENTS));
				break;
			default:
				if (questId > 0)
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, dialogId, questId));
				else
					sendPacket(new SM_DIALOG_WINDOW(targetObjectId, dialogId));
				break;
		}
		//log.info("id: "+targetObjectId+" dialog type: " + unk1 +" other: " + unk2);
	}
}