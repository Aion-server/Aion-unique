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
package admincommands;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.StatEnum;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
* @author Mrakobes
*
*/
public class Heal extends AdminCommand
{
   public Heal()
   {
      super("heal");
   }
   
   @Override
   public void executeCommand(Player admin, String[] params)
   {
        if (admin.getAccessLevel() < AdminConfig.COMMAND_HEAL)
        {
            PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
            return;
        }

      VisibleObject target = admin.getTarget();
      if(target == null)
      {
         PacketSendUtility.sendMessage(admin, "No target selected");
         return;
      }
      
      if(target instanceof Creature)
      {
         Creature creature = (Creature) target;
         creature.getLifeStats().increaseHp(creature.getLifeStats().getMaxHp()+1); 
         creature.getLifeStats().increaseMp(creature.getLifeStats().getMaxMp()+1); 
         
         if(target instanceof Player)
         {
        	 ((Player) creature).getCommonData().setDp(creature.getGameStats().getCurrentStat(StatEnum.MAXDP));
         }
         
         PacketSendUtility.sendMessage(admin, creature.getName() + " has been refreshed !");
      }   
      
      
   }
}