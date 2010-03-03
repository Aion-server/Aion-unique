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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.SystemMessageId;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.ExchangeService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author -Avol-
 * 
 */
public class CM_EXCHANGE_REQUEST extends AionClientPacket
{
	public Integer			targetObjectId;
	@Inject	
	private World			world;
	@Inject
	private ExchangeService exchangeService;

	public CM_EXCHANGE_REQUEST(int opcode)
	{
		super(opcode);
	}

	@Override
	protected void readImpl()
	{
		targetObjectId = readD();
	}


	@Override
	protected void runImpl()
	{
		final Player activePlayer = getConnection().getActivePlayer();
		final Player targetPlayer = world.findPlayer(targetObjectId);

		/**
		 * check if not trading with yourself.
		 */
		if (activePlayer != targetPlayer) 
		{
			/**
			 * check if trade partner exists or is he/she a player.
			 */
			if (targetPlayer!=null) 
			{
				sendPacket(SM_SYSTEM_MESSAGE.REQUEST_TRADE(targetPlayer.getName()));

				RequestResponseHandler responseHandler = new RequestResponseHandler(activePlayer){
					@Override
					public void acceptRequest(Creature requester, Player responder)
					{
						exchangeService.registerExchange(activePlayer, targetPlayer);
					}

					@Override
					public void denyRequest(Creature requester, Player responder)
					{
						PacketSendUtility.sendPacket(activePlayer, new SM_SYSTEM_MESSAGE(SystemMessageId.EXCHANGE_HE_REJECTED_EXCHANGE, targetPlayer.getName()));
					}
				};

				boolean requested = targetPlayer.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_EXCHANGE_DO_YOU_ACCEPT_EXCHANGE,responseHandler);
				if (requested)
				{
					PacketSendUtility.sendPacket(targetPlayer, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_EXCHANGE_DO_YOU_ACCEPT_EXCHANGE, targetPlayer.getObjectId(), activePlayer.getName()));
				}
			}
		} 
		else
		{
			//TODO: send message, cannot trade with yourself.
		}
	}
}