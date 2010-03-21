/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package quest.sanctum;

import java.util.Collections;

import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.Storage;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.inject.Inject;

/* @author edynamic90 */
 
public class _1908UlaguruSpeaks extends QuestHandler
	{
	private final static int questId = 1908;
	private final static int[] quest_xp = {950,50,10};
	private final static int[] quest_kinahs = {500,200,50};
	
	@Inject
	ItemService itemService; 

	public _1908UlaguruSpeaks()
		{
		super(questId);
		}

	@Override
	public void register()
		{
		qe.setNpcQuestData(203864).addOnQuestStart(questId);
		qe.setNpcQuestData(203864).addOnTalkEvent(questId);
		qe.setNpcQuestData(203890).addOnTalkEvent(questId);
		}		
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
		{
		final Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc) targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int var = qs.getQuestVarById(0);
		switch(targetId)
			{
			case 203864:
				if(env.getDialogId() == 25)
					{
					if(var == 1)
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2375);
					else
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
					}
				else if(env.getDialogId() == 10020 || env.getDialogId() == 10021 || env.getDialogId() == 10022)
					{
					qs.setQuestVar(2);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(player, qs);
					player.getCommonData().addExp(quest_xp[env.getDialogId()-10020]);
					Storage inventory = player.getInventory();
					Item KinahsItemPlayer = inventory.getKinahItem();
					Integer KinahsPlayer = KinahsItemPlayer.getItemCount();
					KinahsItemPlayer.increaseItemCount(quest_kinahs[env.getDialogId()-10020]);
					PacketSendUtility.sendPacket(player,new SM_UPDATE_ITEM(KinahsItemPlayer));
					qs.setQuestVar(3);
					qs.setStatus(QuestStatus.COMPLITE);
					qs.setCompliteCount(1);
					updateQuestStatus(player, qs);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
					}
				else
					return defaultQuestStartDialog(env);
			case 203890:
				if(qs != null && qs.getStatus() == QuestStatus.START)
					{
					switch(env.getDialogId())
						{
						case 25:
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
						case 10000:
							qs.setQuestVar(1);
							updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						default:
							return defaultQuestStartDialog(env);
						}
					}
			
			}
		return false;
		}
	}
