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
package admincommands;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.aionemu.gameserver.ShutdownHook.ShutdownManager;
import com.aionemu.gameserver.ShutdownHook.ShutdownManager.ShutdownMode;
import com.aionemu.gameserver.configs.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author lord_rex
 * 
 * @param //system info - System Informations
 * @param //system memory - Memory Informations
 * @param //system gc - Garbage Collector
 * @param //system shutdown <seconds> - Shutdowner
 * @param //system restart <seconds> - Restarter
 */
public class AESystem extends AdminCommand
{
   public AESystem()
   {
      super("system");
   }

   @Override
   public void executeCommand(Player admin, String[] params)
   {
      if (admin.getCommonData().getAdminRole() < AdminConfig.COMMAND_SYSTEM) 
      {
         PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command!");
         return;
      }
      
      if (params == null || params.length < 1)
      {
         PacketSendUtility.sendMessage(admin, "Usage: //system info | //system memory | //system gc");
         return;
      }
      
      if (params[0].equals("info")) 
      {
         for (String line : getSystemInfo())
            PacketSendUtility.sendMessage(admin, line);
      }
      
      else if (params[0].equals("memory")) 
      {
         for (String line : getMemoryInfo())
            PacketSendUtility.sendMessage(admin, line);
      }
      
      else if (params[0].equals("gc")) 
      {
         long time = System.currentTimeMillis();
         PacketSendUtility.sendMessage(admin, "RAM Used (Before): " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576));
         System.gc();
         PacketSendUtility.sendMessage(admin, "RAM Used (After): " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576));
         System.runFinalization();
         PacketSendUtility.sendMessage(admin, "RAM Used (Final): " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576));
         PacketSendUtility.sendMessage(admin, "Garbage Collection and Finalization finished in: " + (System.currentTimeMillis() - time) + " milliseconds...");
      }
      else if (params[0].equals("shutdown"))
      {
    	  int val = Integer.parseInt(params[1]);
    	  ShutdownManager.doShutdown(admin.getName(), val, ShutdownMode.SHUTDOWN);
    	  PacketSendUtility.sendMessage(admin, "Server will be shutdown in " + val + " seconds.");
      }
      else if (params[0].equals("restart"))
      {
    	  int val = Integer.parseInt(params[1]);
    	  ShutdownManager.doShutdown(admin.getName(), val, ShutdownMode.RESTART);
    	  PacketSendUtility.sendMessage(admin, "Server will be restart in " + val + " seconds.");
      }
      else if (params[0].equals("stop"))
      {
    	  ShutdownManager.stopShutdown(admin.getName());
    	  PacketSendUtility.sendMessage(admin, "Server shutdown/restart is stopped.");
      }
   }
   
   private static String[] getSystemInfo()
   {
      return new String[] {
         "System Informations at " + getRealTime().toString() + ":",
         "Avaible CPU(s): " + Runtime.getRuntime().availableProcessors(),
         "Processor(s) Identifier: " + System.getenv("PROCESSOR_IDENTIFIER"),
         "OS: " + System.getProperty("os.name") + " Build: " + System.getProperty("os.version"),
         "OS Arch: " + System.getProperty("os.arch"),
         "RAM Used: " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576)
      };
   }
   
   private static String[] getMemoryInfo()
   {
      double max = Runtime.getRuntime().maxMemory() / 1024; // maxMemory is the upper limit the jvm can use
      double allocated = Runtime.getRuntime().totalMemory() / 1024; // totalMemory the size of the current allocation pool
      double nonAllocated = max - allocated; // non allocated memory till jvm limit
      double cached = Runtime.getRuntime().freeMemory() / 1024; // freeMemory the unused memory in the allocation pool
      double used = allocated - cached; // really used memory
      double useable = max - used; // allocated, but non-used and non-allocated memory
      DecimalFormat df = new DecimalFormat(" (0.0000'%')");
      DecimalFormat df2 = new DecimalFormat(" # 'KB'");
      return new String[] {
            "Global Memory Informations at " + getRealTime().toString() + ":",
            "Allowed Memory: " + df2.format(max),
            "Allocated Memory: " + df2.format(allocated) + df.format(allocated / max * 100), 
            "Non-Allocated Memory: " + df2.format(nonAllocated) + df.format(nonAllocated / max * 100), 
            "Allocated Memory: " + df2.format(allocated),
            "Used Memory: " + df2.format(used) + df.format(used / max * 100),
            "Unused Memory: " + df2.format(cached) + df.format(cached / max * 100), 
            "Useable Memory: " + df2.format(useable) + df.format(useable / max * 100)
      };
   }
   
   private static String getRealTime()
   {
      SimpleDateFormat String = new SimpleDateFormat("H:mm:ss");
      return String.format(new Date());
   }

}