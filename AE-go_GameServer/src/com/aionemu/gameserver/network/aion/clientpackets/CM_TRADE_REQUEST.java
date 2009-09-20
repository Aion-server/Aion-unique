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


package com.aionemu.gameserver.network.aion.clientpackets;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TRADE_REQUEST;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author -Avol-
 * 
 */
public class CM_TRADE_REQUEST extends AionClientPacket
{

	private static final Logger	log	= Logger.getLogger(CM_TRADE_REQUEST.class);

	public Integer			ObjectId;
	@Inject	
	private World			world;
	private String receiver;

	public CM_TRADE_REQUEST(int opcode)
	{
		super(opcode);
	}

	@Override
	protected void readImpl()
	{
		ObjectId = readD();
	}


	@Override
	protected void runImpl()
	{
		final Player activePlayer = getConnection().getActivePlayer();
		final Player targetPlayer = world.findPlayer(ObjectId);

		RequestResponseHandler responseHandler = new RequestResponseHandler(activePlayer){
			@Override
			public void acceptRequest(Player requester, Player responder)
			{
				receiver = activePlayer.getName();
				targetPlayer.getClientConnection().sendPacket(new SM_TRADE_REQUEST(receiver));
				receiver = targetPlayer.getName();
				sendPacket(new SM_TRADE_REQUEST(receiver));
				// responser = targetPlayer.getName();
			}

			public void denyRequest(Player requester, Player responder)
			{
				activePlayer.getClientConnection()
					.sendPacket(
						new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_EXCHANGE_HE_REJECTED_EXCHANGE, targetPlayer
							.getName()));
			}
		};
		
		boolean requested = targetPlayer.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_EXCHANGE_DO_YOU_ACCEPT_EXCHANGE,responseHandler);
		if (!requested){
			// Can't trade with player.
			// TODO: Need to check why and send a error.
		}
		else {
			targetPlayer.getClientConnection()
					.sendPacket(new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_EXCHANGE_DO_YOU_ACCEPT_EXCHANGE, activePlayer.getName()));
		}
	}
}