package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL_END;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOVE;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.controllers.movement.MovementType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.world.World;
/**
 * 
 * @author alexa026
 * 
 */
public class CM_CASTSPELL extends AionClientPacket
{
	/**
	 * Target object id that client wants to TALK WITH or 0 if wants to unselect
	 */
	private int					targetObjectId;
	private int					spellid;
	private int					time;
	private int					level;
	private int					unk;
	private World				world;
    private float 				x, y, z, x2, y2, z2;
	/**
	 * Constructs new instance of <tt>CM_CM_REQUEST_DIALOG </tt> packet
	 * @param opcode
	 */
	public CM_CASTSPELL(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		spellid = readH();// empty
		level = readC();// empty
		unk = readC();// empty
		targetObjectId = readD();// empty
		time = readH();// empty
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		//Player player = getConnection().getActivePlayer();
		//if(player == null)
		//	return;
		Player player = getConnection().getActivePlayer();
		int playerobjid = player.getObjectId();
		
		sendPacket(new SM_CASTSPELL(playerobjid,spellid,level,unk,targetObjectId));
		sendPacket(new SM_CASTSPELL_END(playerobjid,spellid,level,unk,targetObjectId));
		
		if(playerobjid != targetObjectId)
		{
			if(targetObjectId != 0)
			{
				//VisibleObject obj = world.findAionObject(targetObjectId);
				 x = player.getTarget().getX();
				 y = player.getTarget().getY();
				 z = player.getTarget().getZ();
				 x2 = player.getX();
				 y2 = player.getY();
				 z2 = player.getZ();
				

					 MovementType type = MovementType.getMovementTypeById((byte)0xE0);

				sendPacket(new SM_MOVE((Creature) player.getTarget(),x,y,z,x2,y2,z2,player.getTarget().getHeading(),type));
				sendPacket(new SM_ATTACK_STATUS(targetObjectId,12));
				
				//log.info(String.format("Attacking target with object id: %d", targetObjectId));
				
			}
		}

		/*
		sendPacket(new SM_ATTACK(playerobjid,targetObjectId,attackno,time,type));
		if (attackno % 2 == 0) 
		{
			sendPacket(new SM_ATTACK(targetObjectId,playerobjid,attackno+2,time,type));
			
		}
		sendPacket(new SM_ATTACK_STATUS(targetObjectId,attackno));
		if (attackno % 5 == 0) 
		{
			sendPacket(new SM_EMOTION(targetObjectId,13,playerobjid));
			sendPacket(new SM_LOOT_STATUS(targetObjectId,0));
			
		}
		*/
	}
}
