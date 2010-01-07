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
package quest;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_STEP;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.events.OnKillEvent;
import com.aionemu.gameserver.questEngine.events.OnLvlUpEvent;
import com.aionemu.gameserver.questEngine.events.OnTalkEvent;
import com.aionemu.gameserver.questEngine.events.QuestEvent;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author MrPoke
 *
 */
public class TheKerubThreat extends QuestHandler
{
	private final static int questId = 1001;
	
	/**
	 * @param questId
	 */
	public TheKerubThreat()
	{
		super(questId);
		QuestEngine.getInstance().setNpcQuestData(210670).addOnKillEvent(questId);
		QuestEngine.getInstance().setNpcQuestData(203071).addOnTalkEvent(questId);
		QuestEngine.getInstance().setNpcQuestData(203067).addOnQuestEnd(questId);
		QuestEngine.getInstance().addQuestLvlUp(questId);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.questEngine.handlers.QuestHandler#onEvent(com.aionemu.gameserver.questEngine.events.QuestEvent)
	 */
	@Override
	public boolean onEvent(QuestEnv env, QuestEvent event)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		int var = qs.getQuestVars().getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc)env.getVisibleObject()).getNpcId();
		if (event instanceof OnKillEvent)
		{
			if (qs.getStatus() != QuestStatus.START)
				return false;
			if (targetId == 210670)
			{
				if (var > 0 && var < 6)
				{
					qs.getQuestVars().setQuestVarById(0, qs.getQuestVars().getQuestVarById(0) + 1);
					PacketSendUtility.sendPacket(player, new SM_QUEST_STEP(questId, qs.getStatus(), qs.getQuestVars().getQuestVars()));
					return true;
				}
			}
		}
		else if (event instanceof OnTalkEvent)
		{
			if (qs.getStatus() != QuestStatus.START)
				return false;
			if (targetId == 203071)
			{
				switch (env.getDialogId())
				{
					case 1013:
						PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 15));
						return false;
					case 25:
						if (var == 0)
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 1011, questId));
						else if (var == 6)
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 1352, questId));
						else if (var == 7)
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 1693, questId));
						return true;
					case 10002:
					case 33:
						if (var == 7)
						{
							int itemCount = player.getInventory().getItemCountByItemId(182200001);
							if (itemCount >= 5)
							{
								if (env.getDialogId() == 33)
									PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 1694, questId));
								else
								{
									player.getInventory().removeFromBagByItemId(182200001, itemCount);
									qs.getQuestVars().setQuestVarById(0, var+1);
									qs.setStatus(QuestStatus.REWARD);
									PacketSendUtility.sendPacket(player, new SM_QUEST_STEP(questId, qs.getStatus(), qs.getQuestVars().getQuestVars()));
									PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								}
							}
							else
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 1779, questId));
						}
						return true;
					case 10000:
					case 10001:
						if (var == 0 || var == 6)
						{
							qs.getQuestVars().setQuestVarById(0, var+1);
							PacketSendUtility.sendPacket(player, new SM_QUEST_STEP(questId, qs.getStatus(), qs.getQuestVars().getQuestVars()));
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						}
						return true;
					default:
						return false;
				}
			}
		}
		else if (event instanceof OnLvlUpEvent)
		{
			QuestState qs2 = player.getQuestStateList().getQuestState(1100);
			if (qs2 == null || qs2.getStatus() != QuestStatus.COMPLITE || player.getCommonData().getLevel() < 3 || qs.getStatus() != QuestStatus.LOCKED)
				return false;
			qs.setStatus(QuestStatus.START);
			PacketSendUtility.sendPacket(player, new SM_QUEST_STEP(questId, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			return true;
		}
		return false;
	}

}
