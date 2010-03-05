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

import com.aionemu.commons.utils.AEInfos;
import com.aionemu.gameserver.ShutdownHook;
import com.aionemu.gameserver.ShutdownHook.ShutdownMode;
import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.AEVersions;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author lord_rex
 * 
 * //sys info - System Informations
 * //sys memory - Memory Informations
 * //sys gc - Garbage Collector
 * //sys shutdown <seconds> <announceInterval> - Shutdowner
 * //sys restart <seconds> <announceInterval> - Restarter
 */
public class AESystem extends AdminCommand
{
	public AESystem()
	{
		super("sys");
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(admin.getAccessLevel() < AdminConfig.COMMAND_SYSTEM)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command!");
			return;
		}

		if(params == null || params.length < 1)
		{
			PacketSendUtility.sendMessage(admin, "Usage: //sys info | //sys memory | //sys gc | //sys restart <countdown time> <announce delay> | //sys shutdown <countdown time> <announce delay>");
			return;
		}

		if(params[0].equals("info"))
		{
			// Time
			PacketSendUtility.sendMessage(admin, "System Informations at: " + AEInfos.getRealTime().toString());
			
			// Version Infos
			for(String line : AEVersions.getFullVersionInfo())
				PacketSendUtility.sendMessage(admin, line);
			
			// OS Infos
			for(String line : AEInfos.getOSInfo())
				PacketSendUtility.sendMessage(admin, line);
			
			// CPU Infos
			for(String line : AEInfos.getCPUInfo())
				PacketSendUtility.sendMessage(admin, line);
		}

		else if(params[0].equals("memory"))
		{
			// Memory Infos
			for(String line : AEInfos.getMemoryInfo())
				PacketSendUtility.sendMessage(admin, line);
		}

		else if(params[0].equals("gc"))
		{
			long time = System.currentTimeMillis();
			PacketSendUtility.sendMessage(admin, "RAM Used (Before): "
				+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576));
			System.gc();
			PacketSendUtility.sendMessage(admin, "RAM Used (After): "
				+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576));
			System.runFinalization();
			PacketSendUtility.sendMessage(admin, "RAM Used (Final): "
				+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576));
			PacketSendUtility.sendMessage(admin, "Garbage Collection and Finalization finished in: "
				+ (System.currentTimeMillis() - time) + " milliseconds...");
		}
		else if(params[0].equals("shutdown"))
		{
			try
			{
				int val = Integer.parseInt(params[1]);
				int announceInterval = Integer.parseInt(params[2]);
				ShutdownHook.doShutdown(val, announceInterval, ShutdownMode.SHUTDOWN);
				PacketSendUtility.sendMessage(admin, "Server will shutdown in " + val + " seconds.");
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				PacketSendUtility.sendMessage(admin, "Numbers only!");
			}
			catch(NumberFormatException e)
			{
				PacketSendUtility.sendMessage(admin, "Numbers only!");
			}
		}
		else if(params[0].equals("restart"))
		{
			try
			{
				int val = Integer.parseInt(params[1]);
				int announceInterval = Integer.parseInt(params[2]);
				ShutdownHook.doShutdown(val, announceInterval, ShutdownMode.RESTART);
				PacketSendUtility.sendMessage(admin, "Server will restart in " + val + " seconds.");
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				PacketSendUtility.sendMessage(admin, "Numbers only!");
			}
			catch(NumberFormatException e)
			{
				PacketSendUtility.sendMessage(admin, "Numbers only!");
			}
		}
	}
}