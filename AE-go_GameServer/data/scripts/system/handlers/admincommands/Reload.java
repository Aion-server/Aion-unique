/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.qparser.QuestParser;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;



public class Reload extends AdminCommand
{
	public Reload()
	{
		super("reload");
	}

	/*
	*  (non-Javadoc)
	* @see com.aionemu.gameserver.utils.chathandlers.admincommands.AdminCommand#executeCommand(com.aionemu.gameserver.gameobjects.Player, java.lang.String[])
	*/

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(params == null || params.length != 1)
		{
			PacketSendUtility.sendMessage(admin, "syntax //reload <quest>");
			return;
		}
		if(params[0].equals("quest"))
		{
			try
			{
				QuestEngine.getInstance().clear();
				QuestParser.getInstance().reload();
			}
			catch (Exception ex)
			{
				PacketSendUtility.sendMessage(admin, "Quest reload failed!");
			}
			finally
			{
				PacketSendUtility.sendMessage(admin, "Quest reload Success!");
			}
		}
		else 
			PacketSendUtility.sendMessage(admin, "syntax //reload <quest>");

	}
}
