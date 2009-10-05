package com.aionemu.gameserver.model.quests;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.quests.types.QuestStatus;

public class QuestState
{

       private final Player player;
       private final Quest quest;
       private long questStep;
       private QuestStatus status;

       public QuestState(Player player, Quest quest)
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

       public final Player getPlayer()
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