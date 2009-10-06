/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
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
package com.aionemu.gameserver.model.quests;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.quests.types.QuestStatus;

/**
 * @author Blackmouse
 */
public class QuestState
{

       private final PlayerCommonData player;
       private final Quest quest;
       private long questStep;
       private QuestStatus status;

       public QuestState(PlayerCommonData player, Quest quest)
       {
               this.player = player;
               this.quest = quest;
               status = QuestStatus.START;
               questStep = 1L;
       }

       public final long getQuestStep()
       {
               return questStep;
       }

       public void setQuestStep(long questStepId)
       {
               this.questStep = questStepId;
       }

       public final PlayerCommonData getPlayer()
       {
               return player;
       }

       public final QuestStatus getStatus()
       {
               return status;
       }

       public void setStatus(QuestStatus status)
       {
               this.status = status;
       }

       public final Quest getQuest()
       {
               return quest;
       }
}