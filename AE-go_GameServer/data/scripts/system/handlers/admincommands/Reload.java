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

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.aionemu.gameserver.configs.AdminConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.QuestsData;
import com.aionemu.gameserver.dataholders.StaticData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandlers;
import com.aionemu.gameserver.questEngine.handlers.QuestHandlersManager;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author MrPoke
 *
 */
public class Reload extends AdminCommand
{
	public Reload()
	{
		super("reload");
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(admin.getCommonData().getAdminRole() < AdminConfig.COMMAND_RELOAD)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
			return;
		}
		
		if(params == null || params.length != 1)
		{
			PacketSendUtility.sendMessage(admin, "syntax //reload <quest>");
			return;
		}
		if(params[0].equals("quest"))
		{
			File xml = new File("./data/static_data/quest_data/quest_data.xml");
			try
			{
				QuestEngine.getInstance().clear();
				QuestHandlers.clearQuestHandlers();
				JAXBContext jc = JAXBContext.newInstance(StaticData.class);
				Unmarshaller un = jc.createUnmarshaller();
				un.setSchema(getSchema("./data/static_data/static_data.xsd"));

				DataManager.QUEST_DATA =  (QuestsData) un.unmarshal(xml);
				QuestHandlersManager.shutdown();
				QuestHandlersManager.init();
			}
			catch(Exception e)
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

	private Schema getSchema(String xml_schema)
	{
		Schema schema = null;
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		try
		{
			schema = sf.newSchema(new File(xml_schema));
		}
		catch(SAXException saxe)
		{
			throw new Error("Error while getting schema", saxe);
		}

		return schema;
	}
}
