/**
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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

import java.util.Random;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATUPDATE_EXP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
/**
 * 
 * @author alexa026
 * 
 */
public class CM_ATTACK extends AionClientPacket
{
	/**
	 * Target object id that client wants to TALK WITH or 0 if wants to unselect
	 */
	private int					targetObjectId;
	private int					attackno;
	private int					time;
	private int					type;
	private long                exp;
	private long                maxexp;
	private int					at;

	public CM_ATTACK(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		targetObjectId = readD();// empty
		attackno = readC();// empty
		time = readH();// empty
		type = readC();// empty
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{

//PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player.getObjectId(), unknown, emotion), true);
	


		Player player = getConnection().getActivePlayer();
		int playerobjid = player.getObjectId();
		PacketSendUtility.broadcastPacket(player, new SM_ATTACK(playerobjid,targetObjectId,attackno,time,type), true);

		if (attackno % 50 == 0) 
		{
			at = player.getatcount();
			PacketSendUtility.broadcastPacket(player, new SM_EMOTION(targetObjectId,30,playerobjid), true);
			PacketSendUtility.broadcastPacket(player, new SM_EMOTION(targetObjectId,19,playerobjid), true);
			sendPacket(new SM_ATTACK(targetObjectId,playerobjid,at,time,type));
			sendPacket(new SM_ATTACK_STATUS(playerobjid,99));
		  	at = at + 1;
		        player.setatcount(at);
		}
		
		sendPacket(new SM_ATTACK_STATUS(targetObjectId,attackno));
		if (attackno % 100 == 0) 
		{
			maxexp = player.getmaxExp();
			exp = player.getExp() + 50;
			player.setExp(exp);
			PacketSendUtility.broadcastPacket(player, new SM_EMOTION(targetObjectId,13,playerobjid), true);
			PacketSendUtility.broadcastPacket(player, new SM_LOOT_STATUS(targetObjectId,0), true);
			sendPacket(new SM_STATUPDATE_EXP(exp,0,maxexp));
			
			Random generator = new Random();
			int ran = generator.nextInt(50)+1;
			int kinah = player.getkinah() + ran;
			
			player.setkinah(kinah);
			//sendPacket(new SM_INVENTORY_UPDATE(0,ran));
			player.setatcount(1);
		}
	}
}
