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

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.DropList;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_ITEMLIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_STATUS;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;
import java.util.Random;

/**
 * 
 * @author alexa026, Correted by Metos
 * 
 */
public class CM_START_LOOT extends AionClientPacket
{
	private static final Logger	log	= Logger.getLogger(CM_START_LOOT.class);

	/**
	 * Target object id that client wants to TALK WITH or 0 if wants to unselect
	 */
	
	private int					targetObjectId;
	private int					unk;
	private int					activePlayer;
	@Inject	
	private World			world;
	/**
	 * Constructs new instance of <tt>CM_CM_REQUEST_DIALOG </tt> packet
	 * @param opcode
	 */
	public CM_START_LOOT(int opcode) {
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		targetObjectId = readD();// empty
		unk = readC();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		PlayerGameStats playerGameStats = player.getGameStats();
		activePlayer = player.getObjectId();
		
		Npc npc = (Npc) world.findAionObject(targetObjectId);
		int monsterId = npc.getTemplate().getNpcId();
		
		//int [][] mytab = DropList.getInstance().getLootTable(monsterId);
		DropList.lootingList [] mytab = DropList.getInstance().getLootTable(monsterId);
		int [][] dropedlist = new int[mytab.length][2];
		Random rand = new Random();
		
		if (playerGameStats.getItemId() == 0 && mytab.length != 0) {
			int arrayLenght = 0;
			for(int i = 0; i < mytab.length; i++) {
				//if (rand.nextFloat(100) <= mytab[i][3]) {
				if (rand.nextFloat() * 100 <= mytab[i].mobId) {
					dropedlist[i][0] = mytab[i].itemId;
					if (mytab[i].max - mytab[i].min > 0) {
						dropedlist[i][1] = mytab[i].min + rand.nextInt(mytab[i].max - mytab[i].min);
					}
					else {
						dropedlist[i][1] = mytab[i].max;
					}
					playerGameStats.setItemId(dropedlist[i][0]); //toujours pas bien compri a quoi sa sert
					playerGameStats.setItemCount(dropedlist[i][1]);
					arrayLenght++;
				}
			}
			
			if (arrayLenght > 0) {
				sendPacket(new SM_LOOT_ITEMLIST(monsterId, targetObjectId, player, dropedlist, arrayLenght));
				sendPacket(new SM_LOOT_STATUS(targetObjectId, 2));
				sendPacket(new SM_EMOTION(targetObjectId, 35, 0));
			}
			else {
				sendPacket(new SM_LOOT_STATUS(targetObjectId, 3)); //i think is no loot icon mouse
				sendPacket(new SM_DELETE((Creature) player.getTarget())); // need deleted creature ?
				playerGameStats.setItemId(0);
			}
		}
		else { //nothing to loot	
			sendPacket(new SM_LOOT_STATUS(targetObjectId, 3)); //i think is no loot icon mouse
			sendPacket(new SM_DELETE((Creature) player.getTarget())); // need deleted creature ?
			playerGameStats.setItemId(0);
		}
	}
}