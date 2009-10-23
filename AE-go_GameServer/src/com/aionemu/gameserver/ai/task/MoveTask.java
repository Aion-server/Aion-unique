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
package com.aionemu.gameserver.ai.task;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.ai.AIState;
import com.aionemu.gameserver.ai.desires.AttackDesire;
import com.aionemu.gameserver.ai.desires.MoveDesire;
import com.aionemu.gameserver.ai.npcai.NpcAi;
import com.aionemu.gameserver.controllers.movement.MovementType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOVE;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Pinguin
 *
 */
public class MoveTask extends AiTask
{
	private static Logger log = Logger.getLogger(MoveTask.class);
	public static final int PRIORITY = 2;
	
	Npc npc;
	Creature toplayer;
	double dist;
	float x2;
	float y2;
	float z2;
	byte heading2;

	/**
	 * @param delay
	 */
	public MoveTask(Npc npc, Creature toplayer, int delay)
	{
		super(delay);
		this.npc = npc;
		this.toplayer = toplayer;
		npc.getNpcAi().setAiState(AIState.MOVING);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.ai.task.AiTask#getPriority()
	 */
	@Override
	public int getPriority()
	{
		return PRIORITY;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		NpcAi npcAi = npc.getNpcAi();
		dist = MathUtil.getDistance(npc.getX(), npc.getY(), npc.getZ(), toplayer.getX(), toplayer.getY(), toplayer.getZ())  ;
		while(taskValid && (dist > 1.5)){
			
		/*	x2 = (float) (npc.getX() + (toplayer.getX() - npc.getX())/dist) ;
			y2 = (float) (npc.getY() + (toplayer.getY() - npc.getY())/dist) ;
			z2 = (float) (npc.getZ() + (toplayer.getZ() - npc.getZ())/dist) ; */

			x2 = (float) (((toplayer.getX() - npc.getX())/dist) * 2.4) ;
			y2 = (float) (((toplayer.getY() - npc.getY())/dist) * 2.4) ;
			z2 = (float) (((toplayer.getZ() - npc.getZ())/dist) * 2.4) ; 
			

			
			heading2 = (byte) (Math.toDegrees(Math.atan2(y2, x2))/3) ;
			
			if(npc.getHeading() != heading2){
				PacketSendUtility.broadcastPacket(npc, new SM_MOVE(npc, npc.getX(), npc.getY(), npc.getZ(),(float) (x2 * 3.5/2.4) , (float) (y2 * 3.5/2.4) , 0 , heading2, MovementType.MOVEMENT_START_KEYBOARD));
			}
			npc.getActiveRegion().getWorld().updatePosition(npc, npc.getX() + x2, npc.getY() + y2, npc.getZ() + z2, heading2);
			
			
			
			
			if(!npc.getNpcAi().getDesireQueue().isEmpty()){
				if (!npcAi.getDesireQueue().peek().equals(npcAi.getDesireProcessor().desire)){
					this.setTaskValid(false);
				}
			}				
			
			
			
			
			
			try
			{
				Thread.sleep(672);
			}
			catch(InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dist = MathUtil.getDistance(npc.getX(), npc.getY(), npc.getZ(), toplayer.getX(), toplayer.getY(), toplayer.getZ())  ;
		
		
		}
		PacketSendUtility.broadcastPacket(npc, new SM_MOVE(npc, npc.getX(), npc.getY(), npc.getZ(), 0, 0, 0, (byte) 0, MovementType.MOVEMENT_STOP));
		
		if (npcAi.getAiState() != AIState.DEAD ) npcAi.getDesireQueue().addDesire(new AttackDesire(toplayer, npc.getNpcAi().getDesireQueue().peek().getDesirePower() ));
		
		this.setTaskValid(false);
		
		
	}
	
	

}
