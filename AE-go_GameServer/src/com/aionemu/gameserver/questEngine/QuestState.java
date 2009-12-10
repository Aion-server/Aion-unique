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
package com.aionemu.gameserver.questEngine;

import com.aionemu.gameserver.questEngine.types.QuestStatus;

/**
 * @author MrPoke
 */
public class QuestState
{
       private final int questId;
       private QuestVars questVars;
       private QuestStatus status;
       private int compliteCount;
       public QuestState(Quest quest)
       {
               this.questId = quest.getId();
               status = QuestStatus.START;
               questVars = new QuestVars();
               compliteCount = 0;
       }

       public QuestState(Quest quest, QuestStatus status, int questVars, int compliteCount)
       {
               this.questId = quest.getId();
               this.status = status;
               this.questVars = new QuestVars(questVars);
               this.compliteCount = compliteCount;
       }

       public QuestVars getQuestVars()
       {
    	   return questVars;
       }

       public final QuestStatus getStatus()
       {
    	   return status;
       }

       public void setStatus(QuestStatus status)
       {
    	   this.status = status;
       }

       public int getQuestId()
       {
    	   return questId;
       }

       public void setCompliteCount(int compliteCount)
       {
    	   this.compliteCount = compliteCount;
       }

       public int getCompliteCount()
       {
    	   return compliteCount;
       }
}