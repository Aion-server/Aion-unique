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

import com.aionemu.gameserver.configs.Config;
import com.aionemu.gameserver.configs.administration.AdminConfig;
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
		if("config".equalsIgnoreCase(className))
		{
			classToMofify = Config.class;
		}
		else if("adminconfig".equalsIgnoreCase(className))
		{
			classToMofify = AdminConfig.class;
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
