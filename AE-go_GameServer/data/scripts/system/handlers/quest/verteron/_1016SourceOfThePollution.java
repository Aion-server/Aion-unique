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
package quest.verteron;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
* @author Nephis
*
*/
public class _1016SourceOfThePollution extends QuestHandler
{

   private final static int   questId   = 1016;
   private final static int[]   mob_ids   = { 210318 };

   public _1016SourceOfThePollution()
   {
      super(questId);
   }

   @Override
   public void register()
   {
      qe.setNpcQuestData(203149).addOnTalkEvent(questId);
      qe.setNpcQuestData(203148).addOnTalkEvent(questId);
      qe.setNpcQuestData(203832).addOnTalkEvent(questId);
      qe.setNpcQuestData(203705).addOnTalkEvent(questId);
      qe.setNpcQuestData(203822).addOnTalkEvent(questId);
      qe.setNpcQuestData(203761).addOnTalkEvent(questId);
      qe.setNpcQuestData(203098).addOnTalkEvent(questId);
      qe.setNpcQuestData(203195).addOnTalkEvent(questId);
      qe.addQuestLvlUp(questId);
      for(int mob_id : mob_ids)
         qe.setNpcQuestData(mob_id).addOnKillEvent(questId);
   }

   @Override
   public boolean onLvlUpEvent(QuestEnv env)
   {
      Player player = env.getPlayer();
      QuestState qs = player.getQuestStateList().getQuestState(questId);
      if(qs == null || player.getCommonData().getLevel() < 11 || qs.getStatus() != QuestStatus.LOCKED)
         return false;
      qs.setStatus(QuestStatus.START);
      updateQuestStatus(player, qs);
      return true;
   }

   @Override
   public boolean onDialogEvent(QuestEnv env)
   {
      Player player = env.getPlayer();
      QuestState qs = player.getQuestStateList().getQuestState(questId);
      if(qs == null)
         return false;

      int var = qs.getQuestVarById(0);
      int targetId = 0;
      if(env.getVisibleObject() instanceof Npc)
         targetId = ((Npc) env.getVisibleObject()).getNpcId();

      if(qs.getStatus() == QuestStatus.START)
      {
         if(targetId == 203149)
         {
            switch(env.getDialogId())
            {
               case 25:
                  if(var == 0)
                     return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
               case 10000:
                  if(var == 2)
                  {
                     qs.setQuestVarById(0, var + 1);
                     updateQuestStatus(player, qs);
                     return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1693);
                  }
               case 10005:
                  if(var == 7)
                  {
                     qs.setQuestVarById(0, var + 1);
                     updateQuestStatus(player, qs);
                     return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 3400);
                  }
            }
         }

         if(targetId == 203148)
         {
            switch(env.getDialogId())
            {
               case 25:
                  if(var == 1)
                     return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
               case 10001:
                  if(var == 1)
                  {
                     qs.setQuestVarById(0, var + 1);
                     updateQuestStatus(player, qs);
                     PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
                        .getObjectId(), 10));
                     return true;
                  }
            }
         }
         
         if(targetId == 203149)
         {
            switch(env.getDialogId())
            {
               case 25:
                  if(var == 0)
                     return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
                  else if(var == 8)
                     return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
                  else if(var >= 20)
                  {
                     qs.setStatus(QuestStatus.REWARD);
                     updateQuestStatus(player, qs);
                     return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2375);
                  }
               case 10000:
               case 10001:
                  if(var == 0 || var == 8)
                  {
                     qs.setQuestVarById(0, var + 1);
                     updateQuestStatus(player, qs);
                     PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
                        .getObjectId(), 10));
                     return true;
                  }
            }
         }
         
         if(targetId == 203832)
         {
            switch(env.getDialogId())
            {
               case 25:
                  if(var == 3)
                     return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2034);
               case 10000:
               case 10003:
                  if(var == 3)
                  {
                     qs.setQuestVarById(0, var + 1);
                     updateQuestStatus(player, qs);
                     PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
                        .getObjectId(), 10));
                     return true;
                  }
            }
         }
         
         if(targetId == 203705)
         {
            switch(env.getDialogId())
            {
               case 25:
                  if(var == 4)
                     return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2375);
               case 10000:
               case 10004:
                  if(var == 4)
                  {
                     qs.setQuestVarById(0, var + 1);
                     updateQuestStatus(player, qs);
                     PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
                        .getObjectId(), 2376));
                     return true;
                  }
            }
         }

         if(targetId == 203822)
         {
            switch(env.getDialogId())
            {
               case 25:
                  if(var == 5)
                     return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2716);
               case 10000:
               case 10005:
                  if(var == 5)
                  {
                     qs.setQuestVarById(0, var + 1);
                     updateQuestStatus(player, qs);
                     PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
                        .getObjectId(), 2717));
                     return true;
                  }
            }
         }
         
         if(targetId == 203761)
         {
            switch(env.getDialogId())
            {
               case 25:
                  if(var == 6)
                     return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2716);
               case 10000:
               case 10005:
                  if(var == 6)
                  {
                     qs.setQuestVarById(0, var + 1);
                     updateQuestStatus(player, qs);
                     PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
                        .getObjectId(), 2717));
                     return true;
                  }
            }
         }
         
         if(targetId == 203195)
         {
            switch(env.getDialogId())
            {
               case 25:
                  if(var == 9)
                     return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2716);
               case 10000:
               case 10005:
                  if(var == 9)
                  {
                     qs.setStatus(QuestStatus.REWARD);
                     updateQuestStatus(player, qs);
                     PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
                        .getObjectId(), 2717));
                     return true;
                  }
            }
         }
         
         if(targetId == 203098)
         {
            switch(env.getDialogId())
            {
               case 25:
                  if(var == 10)
                     return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2716);
               case 10000:
               case 10005:
                  if(var == 10)
                  {
                     qs.setStatus(QuestStatus.REWARD);
                     updateQuestStatus(player, qs);
                     PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
                        .getObjectId(), 2717));
                     return true;
                  }
            }
         }
      }
      else if(qs.getStatus() == QuestStatus.REWARD)
      {
         if(targetId == 203098)
            return defaultQuestEndDialog(env);
      }
      return false;
   }

   @Override
   public boolean onKillEvent(QuestEnv env)
   {
      Player player = env.getPlayer();
      QuestState qs = player.getQuestStateList().getQuestState(questId);
      if(qs == null || qs.getStatus() != QuestStatus.START)
         return false;

      int var = qs.getQuestVarById(0);
      int targetId = 0;
      if(env.getVisibleObject() instanceof Npc)
         targetId = ((Npc) env.getVisibleObject()).getNpcId();
      switch(targetId)
      {
         case 210318:
            if(var >= 8 && var <= 9)
            {
               qs.setQuestVarById(0, var + 1);
               updateQuestStatus(player, qs);
               return true;
            }
      }
      return false;
   }
}