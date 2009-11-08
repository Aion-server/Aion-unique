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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOKATOBJECT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;
import org.apache.log4j.Logger;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.templates.BindPointTemplate;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerDAO;

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
    	private BindPointTemplate 			bplist;
	
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

			/*
			* Obelisk question window.
			*/

 			Npc npc = (Npc) world.findAionObject(targetObjectId);
			int npcId = npc.getNpcId();

			bplist = DataManager.BIND_POINT_DATA.getBindPointTemplate(npcId);
			
			if (("obelisk").equalsIgnoreCase(o.getName()))
			{
				if (bplist != null) {
					RequestResponseHandler responseHandler = new RequestResponseHandler(player){
					@Override
						public void acceptRequest(Player requester, Player responder)
						{
							Player player = getConnection().getActivePlayer();
							Npc npc = (Npc) world.findAionObject(targetObjectId);
							int npcId = npc.getNpcId();
							bplist = DataManager.BIND_POINT_DATA.getBindPointTemplate(npcId);

							if (player.getInventory().getKinahItem().getItemCount()>=bplist.getPrice())
							{
								sendPacket(new SM_MESSAGE(0, null, "You have successfully binded to this location.", null, ChatType.ANNOUNCEMENTS));
								player.getInventory().getKinahItem().decreaseItemCount(bplist.getPrice());
								PacketSendUtility.sendPacket(player, new SM_UPDATE_ITEM(player.getInventory().getKinahItem()));
								player.getCommonData().setBindPoint(bplist.getBindId());
							}
							else
							{
								sendPacket(new SM_MESSAGE(0, null, "You don't have enough Kinah", null, ChatType.ANNOUNCEMENTS));
							}
						}
						public void denyRequest(Player requester, Player responder)
						{
							//do nothing
						}
					};

					boolean requested = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_BIND_TO_LOCATION,responseHandler);
					if (!requested){
						//do nothing
					}
					else 
					{
		                        	bplist = DataManager.BIND_POINT_DATA.getBindPointTemplate(npcId);
				    		String price = Integer.toString(bplist.getPrice());
						PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_BIND_TO_LOCATION, price));
					}
				} else {
					log.info("There is no bind point template for npc: " + npcId);
				}
            		} else

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
