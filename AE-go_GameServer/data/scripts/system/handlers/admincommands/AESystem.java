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
		if(admin.getCommonData().getAdminRole() < AdminConfig.COMMAND_SYSTEM)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command!");
			return;
		}

		if(params == null || params.length < 1)
		{
			PacketSendUtility.sendMessage(admin, "Usage: //system info | //system memory | //system gc");
			return;
		}

		if(params[0].equals("info"))
		{
			PacketSendUtility.sendMessage(admin, AEInfos.getRealTime().toString());
			for(String line : AEInfos.getOSInfo())
				PacketSendUtility.sendMessage(admin, line);
			for(String line : AEInfos.getCPUInfo())
				PacketSendUtility.sendMessage(admin, line);
		}

		else if(params[0].equals("memory"))
		{
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
			int val = Integer.parseInt(params[1]);
			int announceInterval = Integer.parseInt(params[2]);
			ShutdownHook.doShutdown(val, announceInterval, ShutdownMode.SHUTDOWN);
			PacketSendUtility.sendMessage(admin, "Server will be shutdown in " + val + " seconds.");
		}
		else if(params[0].equals("restart"))
		{
			int val = Integer.parseInt(params[1]);
			int announceInterval = Integer.parseInt(params[2]);
			ShutdownHook.doShutdown(val, announceInterval, ShutdownMode.RESTART);
			PacketSendUtility.sendMessage(admin, "Server will be restart in " + val + " seconds.");
		}
	}
}