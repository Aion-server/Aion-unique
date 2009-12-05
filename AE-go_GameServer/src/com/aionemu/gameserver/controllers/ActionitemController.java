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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 *
 */
public class ActionitemController extends NpcController
{

	/**
	 * 0 - clear object
	 * 1 - use object
	 * 3 - convert object
	 */
	@Override
	public void onDialogRequest(final Player player)
	{
		// TODO for now just hardcoded action without any logic
		
		final int defaultUseTime = 3000;
		
		PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), 
			getOwner().getObjectId(), defaultUseTime, 1));
		
		ThreadPoolManager.getInstance().schedule(new Runnable(){
			@Override
			public void run()
			{
				PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), 
					getOwner().getObjectId(), defaultUseTime, 0));
			}
		}, defaultUseTime);
		
	}


}
