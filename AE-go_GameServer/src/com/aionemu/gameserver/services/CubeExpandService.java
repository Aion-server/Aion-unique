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
package com.aionemu.gameserver.services;

import java.util.ArrayList;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.templates.CubeExpandTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 *
 */
public class CubeExpandService
{
	/**
	 *  Shows Question window and expands on positive response
	 *  
	 * @param player
	 * @param npc
	 */
	public void expandCube(Player player, Npc npc)
	{	
		final CubeExpandTemplate expandTemplate = DataManager.CUBEEXPANDER_DATA.getCubeExpandListTemplate(npc.getNpcId());

		if ((expandTemplate != null) && (expandTemplate.getNpcId()!=0) && validatePlayerCube(player, expandTemplate))
		{
			//check price in inventory
			final int price = getPriceForExpansion(player, expandTemplate);
			final Item kinahItem = player.getInventory().getKinahItem();
			if(price > kinahItem.getItemCount())
			{
				PacketSendUtility.sendPacket(player, new SM_MESSAGE(0, null, "You don't have enough Kinah.", ChatType.ANNOUNCEMENTS));
				return;
			}
			
			RequestResponseHandler responseHandler = new RequestResponseHandler(npc) 
			{				
				@Override
				public void acceptRequest(Creature requester, Player responder)
				{
					expand(responder, expandTemplate);
					kinahItem.decreaseItemCount(price);
					PacketSendUtility.sendPacket(responder, new SM_UPDATE_ITEM(kinahItem));
					DAOManager.getDAO(InventoryDAO.class).store(kinahItem, responder.getObjectId());
				}

				@Override
				public void denyRequest(Creature requester, Player responder)
				{
					//nothing to do
				}
			};

			boolean result = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_WAREHOUSE_EXPAND_WARNING,responseHandler);
			if(result)
			{
				PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_WAREHOUSE_EXPAND_WARNING, 0, String.valueOf(price)));
			}
		}
		else
		{
			PacketSendUtility.sendPacket(player, new SM_MESSAGE(0, null, "NPC Template for this cube Expander is missing.", ChatType.ANNOUNCEMENTS));
		}
	}
	
	/**
	 * 
	 * @param player
	 * @param expandTemplate
	 * @return
	 */
	private int getPriceForExpansion(Player player, final CubeExpandTemplate expandTemplate)
	{
		int nextCubeSize = player.getCubeSize() + 1;
		int price = getPriceByLevel(expandTemplate, nextCubeSize);
		return price;
	}

	/**
	 * 
	 * @param player
	 * @param clist
	 */
	private void expand(Player player, CubeExpandTemplate clist)
	{
		// wtf announcemnet?
		PacketSendUtility.sendPacket(player, new SM_MESSAGE(0, null, "Cube Upgraded to Level "+(player.getCubeSize()+1)+".", ChatType.ANNOUNCEMENTS));
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300431, "9"));// 9 Slots added
		player.setCubesize(player.getCubeSize()+1);
		player.getInventory().setLimit(player.getInventory().getLimit()+9);
		PacketSendUtility.sendPacket(player, new SM_INVENTORY_INFO(new ArrayList<Item>(), player.getCubeSize()));

	}

	/**
	 * 
	 * @param player
	 * @param clist
	 * @return
	 */
	private boolean validatePlayerCube(Player player, CubeExpandTemplate clist)
	{
		int currentCubeSize = player.getCubeSize();

		//check max level
		if(currentCubeSize > clist.getMaxLevel())
		{
			if(player.getCubeSize()!=9)
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300437, clist.getName(), clist.getMaxLevel() + 1));
			else
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300430));//Cannot upgrade anymore.
			return false;
		}

		//check min level
		if(currentCubeSize < clist.getMinLevel())
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300436, clist.getName(), clist.getMinLevel()));
			return false;
		}

		return true;
	}

	/**
	 *  The guy who created cube template should blame himself :)
	 *  One day I will rewrite them
	 *  
	 * @param template
	 * @param level
	 * @return
	 */
	private int getPriceByLevel(CubeExpandTemplate template, int level)
	{
		switch(level)
		{
			case 1:
				return template.getPrice1();
			case 2:
				return template.getPrice2();
			case 3:
				return template.getPrice3();
			case 4:
				return template.getPrice4();
			case 5:
				return template.getPrice5();
			case 6:
				return template.getPrice6();
			case 7:
				return template.getPrice7();
			case 8:
				return template.getPrice8();
			case 9:
				return template.getPrice9();
			default:
				throw new IllegalArgumentException("Invalid expand level supplied: " + level);
		}
	}
}
