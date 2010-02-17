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
package com.aionemu.gameserver.questEngine.model;

import com.aionemu.gameserver.model.gameobjects.PersistentState;


/**
 * @author MrPoke
 */
public class QuestState
{
       private final int questId;
       private QuestVars questVars;
       private QuestStatus status;
       private int compliteCount;
       private PersistentState persistentState;

       public QuestState(int questId)
       {
               this.questId = questId;
               status = QuestStatus.START;
               questVars = new QuestVars();
               compliteCount = 0;
               persistentState = PersistentState.NEW;
       }

       public QuestState(int questId, QuestStatus status, int questVars, int compliteCount)
       {
               this.questId = questId;
               this.status = status;
               this.questVars = new QuestVars(questVars);
               this.compliteCount = compliteCount;
               this.persistentState = PersistentState.NEW;
       }

       public QuestVars getQuestVars()
       {
    	   return questVars;
       }

       public QuestStatus getStatus()
       {
    	   return status;
       }

       public void setStatus(QuestStatus status)
       {
    	   this.status = status;
    	   setPersistentState(PersistentState.UPDATE_REQUIRED);
       }

       public int getQuestId()
       {
    	   return questId;
       }

       public void setCompliteCount(int compliteCount)
       {
    	   this.compliteCount = compliteCount;
    	   setPersistentState(PersistentState.UPDATE_REQUIRED);
       }

       public int getCompliteCount()
       {
    	   return compliteCount;
       }
       
   	/**
   	 * @return the pState
   	 */
   	public PersistentState getPersistentState()
   	{
   		return persistentState;
   	}

   	/**
   	 * @param persistentState the pState to set
   	 */
   	public void setPersistentState(PersistentState persistentState)
   	{
		switch(persistentState)
		{
			case DELETED:
				if(this.persistentState == PersistentState.NEW)
					throw new IllegalArgumentException("Cannot change state to DELETED from NEW");
			case UPDATE_REQUIRED:
				if(this.persistentState == PersistentState.NEW)
					break;
			default:
				this.persistentState = persistentState;
		}
   	}
}
