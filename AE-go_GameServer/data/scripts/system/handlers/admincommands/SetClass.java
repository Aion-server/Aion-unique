/**
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package admincommands;

import java.util.Arrays;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author ATracer
 */
public class SetClass extends AdminCommand
{
	public SetClass()
	{
		super("setclass");
	}

	/*
	 *  (non-Javadoc)
	 * @see com.aionemu.gameserver.utils.chathandlers.admincommands.AdminCommand#executeCommand(com.aionemu.gameserver.gameobjects.player.Player, java.lang.String[])
	 */

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(params == null || params.length < 1)
		{
			PacketSendUtility.sendMessage(admin, "syntax //setclass <classid>");
			return;
		}

		PlayerClass	playerClass;

		try
		{
			playerClass = PlayerClass.getPlayerClassById(Byte.parseByte(params[0]));
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(admin, "You have entered invalid class id");
			return;
		}

		VisibleObject target = admin.getTarget();
		if(target == null)
		{
			PacketSendUtility.sendMessage(admin, "You should select a target first");
			return;
		}
		
		if(target instanceof Player)
		{
			Player player = (Player) target;
			PlayerClass oldClass = ((Player) target).getPlayerClass();
			
			// switch only after level 9
			int level = player.getLevel();
			if(level < 9)
			{
				PacketSendUtility.sendMessage(player, "You can switch class after your level reach 9");
				return;
			}
			
			// prevent second switch of class
			if(Arrays.asList(new int[]{1,2,4,5,7,8,10,11}).contains(oldClass.ordinal()))
			{
				PacketSendUtility.sendMessage(player, "You already switched class");
				return;
			}
			
			int newClassId = playerClass.ordinal();
			
			switch(oldClass.ordinal())
			{
				case 0:
					if(newClassId == 1 || newClassId == 2)
						break;
				case 3:
					if(newClassId == 4 || newClassId == 5)
						break;
				case 6:
					if(newClassId == 7 || newClassId == 8)
						break;
				case 9:
					if(newClassId == 10 || newClassId == 11)
						break;
				default:
					PacketSendUtility.sendMessage(player, "Invalid class switch chosen");
					return;
			}
			player.getCommonData().setPlayerClass(playerClass);
			player.getCommonData().upgradePlayer();
			PacketSendUtility.sendMessage(admin, "Set class to " + playerClass.name());
		}
	}

}
