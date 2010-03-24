/*
* This file is part of aion-unique <aion-unique.org>.
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
package quest.eltnen;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.google.inject.Inject;

/**
* @author Nephis and quest helper team
*
*/
public class _1371FlowersForIsson extends QuestHandler
{
   @Inject
   ItemService itemService;

   private final static int   questId   = 1371;

   public _1371FlowersForIsson()
   {
      super(questId);
   }

   @Override
   public void register()
   {

      qe.setNpcQuestData(203949).addOnQuestStart(questId);
      qe.setNpcQuestData(203949).addOnTalkEvent(questId);
      qe.setNpcQuestData(730039).addOnTalkEvent(questId);
   }

   @Override
   public boolean onDialogEvent(QuestEnv env)
   {
      final Player player = env.getPlayer();
      int targetId = 0;
      if(env.getVisibleObject() instanceof Npc)
         targetId = ((Npc) env.getVisibleObject()).getNpcId();
      QuestState qs = player.getQuestStateList().getQuestState(questId);
      if(targetId == 203949)
      {
         if(qs == null || qs.getStatus() == QuestStatus.NONE)
         {
            if(env.getDialogId() == 25)
               return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
            else
               return defaultQuestStartDialog(env);
         }
      
         else if (qs.getStatus() == QuestStatus.START)
         {
            int var = qs.getQuestVarById(0);
            switch(env.getDialogId())
            {
               case 25:
               if(var == 0)
                     return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
                     
               case 33:
               if(var == 5)
                  player.getInventory().removeFromBagByItemId(152000601, 5);
                     return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1353);
               case 10000:
                  updateQuestStatus(player, qs);
                  qs.setQuestVarById(0, var + 1);
                     return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1354);   
                     

            }
         return false;
         }

         else if( qs != null && qs.getStatus() == QuestStatus.REWARD)
         {
            return defaultQuestEndDialog(env);
         }
      }
      
      else if(targetId == 730039)
      {
         if(qs.getStatus() == QuestStatus.START)
         {
         
                  final int targetObjectId = env.getVisibleObject().getObjectId();
                  qs.setStatus(QuestStatus.REWARD);
                  updateQuestStatus(player, qs);
                  
                  PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000,
                     1));
                  PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 37, 0,
                     targetObjectId), true);
                  ThreadPoolManager.getInstance().schedule(new Runnable(){
                     @Override
                     public void run()
                     {
                        if(player.getTarget() == null || player.getTarget().getObjectId() != targetObjectId)
                           return;
                        PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(),
                           targetObjectId, 3000, 0));
                        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 38, 0,targetObjectId), true);

                     }}, 3000);
         }
      }
      return false;
   }
}
