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
public class SM_INVENTORY_UPDATE extends AionServerPacket
{
	private int	itemId;
	private int	count = 0;
 
	
	public SM_INVENTORY_UPDATE(int itemId, int count)
	{
		this.itemId = itemId;
		this.count = count;

		
	}

	/**
	 * {@inheritDoc} dc
	 */
	
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{	
		/*
		writeD(buf, targetObjectId);
		writeC(buf, 0x01); 
		writeC(buf, 0x00);
		writeC(buf, 0xc4);
		writeC(buf, 0x68);
		writeC(buf, 0xf7);
		
		writeC(buf, 0x05);
		writeC(buf, 0x01);
		writeD(buf, 0x00);
		writeC(buf, 0x00);
		*/

		if (itemId != 0)
		{
		Random generator = new Random();
		int ran = generator.nextInt(100)+1;
		
		writeH(buf, 25); 
		writeH(buf, 1); //count
		
		writeD(buf, 5380+ ran); //itemid
		writeD(buf, itemId);
		writeH(buf, 36); //always 0x24
	
		writeD(buf, 2211043+ ran); //Item Nameid <- it require a good database for itemid and nameid
		writeH(buf, 0);
		writeH(buf, 0x16); //length of item details
		writeC(buf, 0);
		writeH(buf, 0xa3e);
		writeD(buf, 1); //count
		
		//dummy
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeH(buf, 0);
		writeC(buf, 0);
		
		writeC(buf, 0xff);
		writeC(buf, 0xff);
		writeC(buf, 0);
		}
		else
		{
			//CE316480 0134DF0A 2400 DB691500 0000 1600 00 1E03 ffffffff 000000000000000000000000000000 FF FF 00
			//http://aion-emu.com/index.php/topic,1327.0.html
			Random generator = new Random();
			int ran = generator.nextInt(200)+1;
			
			
			writeH(buf, 25); 
			writeH(buf, 1); //count
			
			writeD(buf, 5290+ran); 
			writeD(buf, 182400001);
			writeH(buf, 36); //always 0x24
			writeD(buf, 1403355); //Item Nameid
			writeH(buf, 0);
			writeH(buf, 0x16); //length of item details
			writeC(buf, 0);
			writeH(buf, 798);
			writeD(buf, count); //count
			//dummy
			writeD(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);
			writeH(buf, 0);
			writeC(buf, 0);
			
			writeC(buf, 0xff);
			writeC(buf, 0xff);
			writeC(buf, 0);
			
		}

		


	}	
}