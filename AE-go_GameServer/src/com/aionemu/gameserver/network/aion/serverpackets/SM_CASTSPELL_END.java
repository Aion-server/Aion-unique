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
public class SM_CASTSPELL_END extends AionServerPacket
{
	private int attackerobjectid;
	private int	targetObjectId;
	private int	spellid;
	private int	level;
	private int	unk; //can cast?? 
	private int damage;
	
	public SM_CASTSPELL_END(int attackerobjectid ,int spellid,int level,int unk, int targetObjectId, int damage)
	{
		this.attackerobjectid = attackerobjectid;
		this.targetObjectId = targetObjectId;
		this.spellid = spellid ;// empty
		this.level = level ;
		this.unk = unk ;
		this.damage = damage;
	}

	/**
	 * {@inheritDoc}
	 */
	
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{		
		writeD(buf, attackerobjectid);
		writeC(buf, 0);
		writeD(buf, targetObjectId);
		writeH(buf, spellid); 
		writeC(buf, level);
		writeD(buf, 20);
		writeC(buf, 0xFE); //unk??
		writeC(buf, 1); //unk??
		writeD(buf, 512); //unk??

		writeH(buf, 1); 
		writeD(buf, targetObjectId); 
		writeH(buf, 3072); // unk?? abnormal eff id ??
		writeH(buf, 100); // unk??
		writeH(buf, 16); // unk??
		
		writeH(buf, 1); // unk??
		writeD(buf, damage); // damage
		writeH(buf, 10);

	}	
}
