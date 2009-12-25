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
package com.aionemu.gameserver.network.aion.clientpackets;


import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GROUP_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.utils.idfactory.IDFactoryAionObject;
import com.google.inject.Inject;

/**
 * 
 * @author Lyahim, ATracer
 * 
 */
public class CM_INVITE_TO_GROUP extends AionClientPacket
{
	@Inject
	private World world;
	
	@Inject
	@IDFactoryAionObject
	private IDFactory aionObjectsIDFactory;		
	
	private String playername;

	public CM_INVITE_TO_GROUP(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		readC();//unk
		playername = readS();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		final Player inviter = getConnection().getActivePlayer();
		final Player invited = world.findPlayer(this.playername);

		if(invited == null)
		{
			sendPacket(SM_SYSTEM_MESSAGE.INVITED_PLAYER_OFFLINE());
			return;
		}

		PlayerGroup group = inviter.getPlayerGroup();

		if(group == null)
		{
			group = new PlayerGroup(aionObjectsIDFactory, inviter);
		}
		
		if(group.isFull())
			sendPacket(SM_SYSTEM_MESSAGE.FULL_GROUP());
		else if(invited.getLifeStats().isAlreadyDead())
			sendPacket(SM_SYSTEM_MESSAGE.SELECTED_TARGET_DEAD());
		else if(inviter.getLifeStats().isAlreadyDead())
			sendPacket(SM_SYSTEM_MESSAGE.CANNOT_INVITE_BECAUSE_YOU_DEAD());
		else if(invited.getObjectId() == inviter.getObjectId())
			sendPacket(SM_SYSTEM_MESSAGE.CANNOT_INVITE_YOURSELF());
		else if(invited.getPlayerGroup() != null)
			sendPacket(SM_SYSTEM_MESSAGE.PLAYER_IN_ANOTHER_GROUP(playername));
		else if(inviter.getObjectId() != group.getGroupLeader().getObjectId())
			sendPacket(SM_SYSTEM_MESSAGE.ONLY_GROUP_LEADER_CAN_INVITE());
		else
		{
			sendPacket(SM_SYSTEM_MESSAGE.REQUEST_GROUP_INVITE(playername));

			RequestResponseHandler responseHandler = new RequestResponseHandler(inviter) {

				@Override
				public void acceptRequest(Player requester, Player responder)
				{
					inviter.getPlayerGroup().addPlayerToGroup(invited);
				}

				@Override
				public void denyRequest(Player requester, Player responder)
				{
					sendPacket(SM_SYSTEM_MESSAGE.REJECT_GROUP_INVITE(responder.getName()));
					if(requester.getPlayerGroup().size() == 1)
						inviter.getPlayerGroup().removePlayerFromGroup(inviter);
				}
			};

			boolean result = invited.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_REQUEST_GROUP_INVITE,responseHandler);
			if(result)
			{
				PacketSendUtility.sendPacket(invited, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_REQUEST_GROUP_INVITE, this.playername));
			}
			else
			{
				if(inviter.getPlayerGroup().size() == 1)
					inviter.getPlayerGroup().removePlayerFromGroup(inviter);
			}
		}
	}
}
