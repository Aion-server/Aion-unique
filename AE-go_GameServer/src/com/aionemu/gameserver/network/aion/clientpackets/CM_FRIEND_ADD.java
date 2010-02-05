/*
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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_RESPONSE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.SocialService;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * Received when a user tries to add someone as his friend
 * 
 * @author Ben
 *
 */
public class CM_FRIEND_ADD extends AionClientPacket
{
	private String 				targetName;
	@Inject
	private SocialService 		socialService;
	@Inject
	private World				world;
	
	public CM_FRIEND_ADD(int opcode) {
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		targetName = readS();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		
		final Player activePlayer = getConnection().getActivePlayer();
		final Player targetPlayer = world.findPlayer(targetName);
		
	
		if (targetName.equalsIgnoreCase(activePlayer.getName()))
		{
				//Adding self to friend list not allowed - Its blocked by the client by default, so no need to send an error
		}
		//if offline
		else if (targetPlayer == null) 
		{	
			sendPacket(new SM_FRIEND_RESPONSE(targetName, SM_FRIEND_RESPONSE.TARGET_OFFLINE));
		}
		else if (activePlayer.getFriendList().getFriend(targetPlayer.getObjectId()) != null)
		{
			sendPacket(new SM_FRIEND_RESPONSE(targetPlayer.getName(), SM_FRIEND_RESPONSE.TARGET_ALREADY_FRIEND));
		}
		else if (activePlayer.getFriendList().isFull())
		{
			sendPacket(SM_SYSTEM_MESSAGE.BUDDYLIST_LIST_FULL);
		}
		else if (targetPlayer.getFriendList().isFull())
		{
			sendPacket(new SM_FRIEND_RESPONSE(targetPlayer.getName(),SM_FRIEND_RESPONSE.TARGET_LIST_FULL));
		}
		else if (activePlayer.getBlockList().contains(targetPlayer.getObjectId()))
		{
			sendPacket(new SM_FRIEND_RESPONSE(targetPlayer.getName(), SM_FRIEND_RESPONSE.TARGET_BLOCKED));
		}
		else if (targetPlayer.getBlockList().contains(activePlayer.getObjectId()))
		{
			sendPacket(SM_SYSTEM_MESSAGE.YOU_ARE_BLOCKED_BY(targetName));
		}
		else // Send request
		{
			RequestResponseHandler responseHandler = new RequestResponseHandler(activePlayer) {
				
				@Override
				public void acceptRequest(Creature requester, Player responder)
				{
					if (!targetPlayer.getCommonData().isOnline())
					{
						sendPacket(new SM_FRIEND_RESPONSE(targetName , SM_FRIEND_RESPONSE.TARGET_OFFLINE));
					}
					else if (activePlayer.getFriendList().isFull() || 
						responder.getFriendList().isFull())
					{
						return;	
					}
						
					else
					{
						socialService.makeFriends((Player)requester, responder);
					}
					
				}

				@Override
				public void denyRequest(Creature requester, Player responder)
				{
					sendPacket(new SM_FRIEND_RESPONSE(targetName, SM_FRIEND_RESPONSE.TARGET_DENIED));
					
				}
			};
			
			boolean requested = targetPlayer.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_BUDDYLIST_ADD_BUDDY_REQUETS,responseHandler);
			//If the player is busy and could not be asked
			if (!requested)
			{
				sendPacket(SM_SYSTEM_MESSAGE.BUDDYLIST_BUSY);
			}
			else 
			{
				//Send question packet to buddy
				targetPlayer.getClientConnection()
					.sendPacket(new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_BUDDYLIST_ADD_BUDDY_REQUETS, activePlayer.getObjectId(), activePlayer.getName()));
			}
		}
	}
	
}
