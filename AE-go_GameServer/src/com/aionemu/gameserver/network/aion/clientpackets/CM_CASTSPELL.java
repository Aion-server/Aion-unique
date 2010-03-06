package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
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
	@SuppressWarnings("unused")
	private int					targetObjectId;
	private int					spellid;
	@SuppressWarnings("unused")
	private int					time;
	@SuppressWarnings("unused")
	private int					level;
	@SuppressWarnings("unused")
	private int					unk;
	@SuppressWarnings("unused")
	private World				world;
	@SuppressWarnings("unused")
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
		Player player = getConnection().getActivePlayer();
		
		if(player.isProtectionActive())
		{
			player.getController().stopProtectionActiveTask();
		}
		
		if(!player.getLifeStats().isAlreadyDead())
		{
			player.getController().useSkill(spellid);
		}
	}
}
