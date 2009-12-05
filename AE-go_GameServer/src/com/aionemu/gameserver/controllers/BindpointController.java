/*
 * This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.controllers;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.templates.BindPointTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 *
 */
public class BindpointController extends NpcController
{
	private static Logger log = Logger.getLogger(BindpointController.class);

	private BindPointTemplate bindPointTemplate;

	/**
	 * @param bindPointTemplate the bindPointTemplate to set
	 */
	public void setBindPointTemplate(BindPointTemplate bindPointTemplate)
	{
		this.bindPointTemplate = bindPointTemplate;
	}

	@Override
	public void onDialogRequest(Player player)
	{
		if (bindPointTemplate == null)
		{
			log.info("There is no bind point template for npc: " + getOwner().getNpcId());
			return;
		}
		
		RequestResponseHandler responseHandler = new RequestResponseHandler(player)
		{
			@Override
			public void acceptRequest(Player requester, Player responder)
			{
				if (requester.getCommonData().getBindPoint() != bindPointTemplate.getBindId()) 
				{
					if (requester.getInventory().getKinahItem().getItemCount()>= bindPointTemplate.getPrice())
					{
						PacketSendUtility.sendPacket(requester, new SM_MESSAGE(0, null, "You have successfully binded to this location.", null, ChatType.ANNOUNCEMENTS));
						requester.getInventory().getKinahItem().decreaseItemCount(bindPointTemplate.getPrice());
						PacketSendUtility.sendPacket(requester, new SM_UPDATE_ITEM(requester.getInventory().getKinahItem()));
						requester.getCommonData().setBindPoint(bindPointTemplate.getBindId());
					}
					else
					{
						PacketSendUtility.sendPacket(requester, new SM_MESSAGE(0, null, "You don't have enough Kinah.", null, ChatType.ANNOUNCEMENTS));
					}
				}
				else
				{
					PacketSendUtility.sendPacket(requester, new SM_MESSAGE(0, null, "You are already binded to this location.", null, ChatType.ANNOUNCEMENTS));
				}
			}
			@Override
			public void denyRequest(Player requester, Player responder)
			{
				//do nothing
			}
		};

		boolean requested = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_BIND_TO_LOCATION,responseHandler);
		if (requested)
		{
			String price = Integer.toString(bindPointTemplate.getPrice());
			PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_BIND_TO_LOCATION, price));
		}
	}

}
