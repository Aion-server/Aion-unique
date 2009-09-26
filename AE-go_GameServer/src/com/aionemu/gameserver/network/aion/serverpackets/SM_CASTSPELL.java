package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import java.util.Random;

/**
 * 
 * @author alexa026
 * 
 */
public class SM_CASTSPELL extends AionServerPacket
{

	private int attackerobjectid;
	private int	targetObjectId;
	private int	spellid;
	private int	level;
	private int	unk; //can cast?? 
	
	public SM_CASTSPELL(int attackerobjectid ,int spellid,int level,int unk, int targetObjectId)
	{
		this.attackerobjectid = attackerobjectid;
		this.targetObjectId = targetObjectId;
		this.spellid = spellid ;// empty
		this.level = level ;
		this.unk = unk ;
	}

	/**
	 * {@inheritDoc}
	 */
	
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{		
	
		writeD(buf, attackerobjectid);
		writeH(buf, spellid); 
		writeC(buf, level);
		writeC(buf, unk);
		writeD(buf, targetObjectId); 
		writeH(buf, 2000); // CAST TIME
		writeC(buf, 0x00);//writeC(0);

	}	
}
