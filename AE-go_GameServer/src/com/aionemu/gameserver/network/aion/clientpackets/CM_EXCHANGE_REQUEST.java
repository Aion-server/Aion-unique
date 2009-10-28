/**
 * This file is part of aion-emu <aion-unique.com>.
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
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */


package com.aionemu.gameserver.network.aion.clientpackets;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EXCHANGE_REQUEST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author -Avol-
 * 
 */
public class CM_EXCHANGE_REQUEST extends AionClientPacket
{

	private static final Logger	log	= Logger.getLogger(CM_EXCHANGE_REQUEST.class);

	public Integer			targetObjectId;
	@Inject	
	private World			world;
	private String receiver;

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

		/*
		* check if not trading with yourself.
		*/

		if (activePlayer != targetPlayer) {
			
			/*
			* check if trade partner exists or is he/she a player.
			*/

			if (targetPlayer!=null) {
				sendPacket(SM_SYSTEM_MESSAGE.REQUEST_TRADE(targetPlayer.getName()));
	
				RequestResponseHandler responseHandler = new RequestResponseHandler(activePlayer){
					@Override
					public void acceptRequest(Player requester, Player responder)
					{
						/*
						* set exchange partners
						*/

						activePlayer.getExchangeList().setExchangePartner(targetPlayer.getObjectId());
						targetPlayer.getExchangeList().setExchangePartner(activePlayer.getObjectId());
					
						/*
						* prepare for a new trade
						*/

						activePlayer.getExchangeList().setExchangeItemList();
						targetPlayer.getExchangeList().setExchangeItemList();
						activePlayer.getExchangeList().setConfirm(false);
						targetPlayer.getExchangeList().setConfirm(false);
						activePlayer.getExchangeList().setExchangeKinah(0);
						targetPlayer.getExchangeList().setExchangeKinah(0);

						receiver = activePlayer.getName();
						PacketSendUtility.sendPacket(targetPlayer, new SM_EXCHANGE_REQUEST(receiver));
						receiver = targetPlayer.getName();
						PacketSendUtility.sendPacket(activePlayer, new SM_EXCHANGE_REQUEST(receiver));

					}

					public void denyRequest(Player requester, Player responder)
					{
						PacketSendUtility.sendPacket(activePlayer, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_EXCHANGE_HE_REJECTED_EXCHANGE, targetPlayer.getName()));
					}
				};
		
				boolean requested = targetPlayer.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_EXCHANGE_DO_YOU_ACCEPT_EXCHANGE,responseHandler);
				if (!requested){
					//cannot exchange
				}
				else {
					PacketSendUtility.sendPacket(targetPlayer, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_EXCHANGE_DO_YOU_ACCEPT_EXCHANGE, activePlayer.getName()));
				}
			}
		} 
		else
		{
			//TODO: send message, cannot trade with yourself.
		}
	}
}