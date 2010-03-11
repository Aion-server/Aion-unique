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

import java.lang.reflect.Field;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.main.CacheConfig;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.configs.main.LegionConfig;
import com.aionemu.gameserver.configs.main.PeriodicSaveConfig;
import com.aionemu.gameserver.configs.main.RateConfig;
import com.aionemu.gameserver.configs.main.ShutdownConfig;
import com.aionemu.gameserver.configs.main.TaskManagerConfig;
import com.aionemu.gameserver.configs.network.IPConfig;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author ATracer
 *
 */
public class Configure extends AdminCommand
{
	
	public Configure()
	{
		super("configure");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(admin.getAccessLevel() < AdminConfig.COMMAND_CONFIGURE)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
			return;
		}
		
		String command = "";
		if(params.length == 3)
		{
			//show
			command = params[0];
			if(!"show".equalsIgnoreCase(command))
			{
				PacketSendUtility.sendMessage(admin, "syntax //configure <set|show> <configname> <property> [<newvalue>]");
				return;
			}
		}else if(params.length == 4)
		{
			//set
			command = params[0];
			if(!"set".equalsIgnoreCase(command))
			{
				PacketSendUtility.sendMessage(admin, "syntax //configure <set|show> <configname> <property> [<newvalue>]");
				return;
			}
		}
		else
		{
			PacketSendUtility.sendMessage(admin, "syntax //configure <set|show> <configname> <property> [<newvalue>]");
			return;
		}
		
		Class classToMofify = null;
		String className = params[1];
		
		if("admin".equalsIgnoreCase(className))
		{
			classToMofify = AdminConfig.class;
		}
		else if("cache".equalsIgnoreCase(className))
		{
			classToMofify = CacheConfig.class;
		}
		else if("custom".equalsIgnoreCase(className))
		{
			classToMofify = CustomConfig.class;
		}
		else if("group".equalsIgnoreCase(className))
		{
			classToMofify = GroupConfig.class;
		}
		else if("gs".equalsIgnoreCase(className))
		{
			classToMofify = GSConfig.class;
		}
		else if("legion".equalsIgnoreCase(className))
		{
			classToMofify = LegionConfig.class;
		}
		else if("ps".equalsIgnoreCase(className))
		{
			classToMofify = PeriodicSaveConfig.class;
		}
		else if("rate".equalsIgnoreCase(className))
		{
			classToMofify = RateConfig.class;
		}
		else if("shutdown".equalsIgnoreCase(className))
		{
			classToMofify = ShutdownConfig.class;
		}
		else if("tm".equalsIgnoreCase(className))
		{
			classToMofify = TaskManagerConfig.class;
		}
		else if("ip".equalsIgnoreCase(className))
		{
			classToMofify = IPConfig.class;
		}
		else if("network".equalsIgnoreCase(className))
		{
			classToMofify = NetworkConfig.class;
		}
		
		if(command.equalsIgnoreCase("show"))
		{
			String fieldName = params[2];
			Field someField;
			try 
			{
				someField = classToMofify.getDeclaredField(fieldName.toUpperCase());
				PacketSendUtility.sendMessage(admin, "Current value is " + someField.get(null));
			} catch (Exception e) {
				PacketSendUtility.sendMessage(admin, "Something really bad happend :)");
				return;
			}
		}
		else if(command.equalsIgnoreCase("set"))
		{
			String fieldName = params[2];
			String newValue = params[3];
			if(classToMofify != null)
			{
				Field someField;
				try 
				{
					someField = classToMofify.getDeclaredField(fieldName.toUpperCase());
					Class classType = someField.getType();		
					if(classType == String.class)
					{
						someField.set(null, newValue); 
					}
					else if(classType == int.class || classType == Integer.class)
					{
						someField.set(null, Integer.parseInt(newValue));
					}
					else if(classType == Boolean.class || classType == boolean.class)
					{
						someField.set(null, Boolean.valueOf(newValue));
					}
					
				} catch (Exception e) {
					PacketSendUtility.sendMessage(admin, "Something really bad happend :)");
					return;
				}
			}
			PacketSendUtility.sendMessage(admin, "Property changed");
		}
		
		
	}
}
