/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
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

import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOKATOBJECT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;
import org.apache.log4j.Logger;

/**
 * 
 * @author alexa026, Avol
 * 
 */
public class CM_SHOW_DIALOG extends AionClientPacket
{

	private static final Logger log	= Logger.getLogger(CM_SHOW_DIALOG.class);

	/**
	* Target object id that client wants to TALK WITH or 0 if wants to unselect
	*/
	private int					targetObjectId;
	
	@Inject
	private World world;
	/**
	* Constructs new instance of <tt>CM_CM_REQUEST_DIALOG </tt> packet
	* @param opcode
	*/
	public CM_SHOW_DIALOG(int opcode)
	{
		super(opcode);
	}

	/**
	* {@inheritDoc}
	*/
	@Override
	protected void readImpl()
	{
		targetObjectId = readD();
	}

	/**
	* {@inheritDoc}
	*/
	@Override
	protected void runImpl()
	{
		if (world.findPlayer(targetObjectId) ==null) {
			Player player = getConnection().getActivePlayer();
			if(player == null)
				return;
		
			AionObject o = world.findAionObject(targetObjectId);
			if ( o == null)
			return;
			
			sendPacket(new SM_LOOKATOBJECT(targetObjectId, player.getObjectId(), Math.abs(128 - player.getHeading())));
		
			if (o.getName().startsWith("Mail"))
			{
				sendPacket(new SM_DIALOG_WINDOW(targetObjectId, 18));
			}
			else
			{
				sendPacket(new SM_DIALOG_WINDOW(targetObjectId, 10));
			}
		}
	}
}
