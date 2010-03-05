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

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.drop.DropList;
import com.aionemu.gameserver.model.drop.DropTemplate;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.DropService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.google.inject.Inject;
/**
 * 
 * @author ATracer
 *
 */
public class AddDrop extends AdminCommand
{
	@Inject
	private DropService dropService;

	public AddDrop()
	{
		super("adddrop");
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(admin.getAccessLevel() < AdminConfig.COMMAND_ADDDROP)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
			return;
		}

		if(params.length != 6)
		{
			PacketSendUtility.sendMessage(admin, "syntax //adddrop <mobid> <itemid> <min> <max> <chance> <quest>");
			return;
		}

		try
		{
			final int mobId = Integer.parseInt(params[0]);
			final int itemId = Integer.parseInt(params[1]);
			final int min = Integer.parseInt(params[2]);
			final int max = Integer.parseInt(params[3]);
			final int chance = Integer.parseInt(params[4]);
			final int quest = Integer.parseInt(params[5]);

			DropList dropList = dropService.getDropList();

			DropTemplate dropTemplate = new DropTemplate(mobId, itemId, min, max, chance, quest);
			dropList.addDropTemplate(mobId, dropTemplate);

			DB.insertUpdate("INSERT INTO droplist ("
				+ "`mobId`, `itemId`, `min`, `max`, `chance`, `quest`)" + " VALUES "
				+ "(?, ?, ?, ?, ?, ?)", new IUStH() {
					@Override
					public void handleInsertUpdate(PreparedStatement ps) throws SQLException
					{
						ps.setInt(1, mobId);
						ps.setInt(2, itemId);
						ps.setInt(3, min);
						ps.setInt(4, max);
						ps.setInt(5, chance);
						ps.setInt(6, quest);
						ps.execute();
					}
				});
		}
		catch(Exception ex)
		{
			PacketSendUtility.sendMessage(admin, "Only numbers are allowed");
			return;
		}



	}

}
