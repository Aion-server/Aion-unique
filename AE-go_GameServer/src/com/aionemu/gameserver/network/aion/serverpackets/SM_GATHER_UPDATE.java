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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.model.templates.GatherableTemplate;
import com.aionemu.gameserver.model.templates.gather.Material;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author ATracer, orz
 *
 */
public class SM_GATHER_UPDATE extends AionServerPacket
{
	private GatherableTemplate template;
	private int action;
	private int itemId;
	private int success;
	private int failure;
	private int nameId;

	public SM_GATHER_UPDATE(GatherableTemplate template, Material material, int success, int failure, int action)
	{
		this.action = action;
		this.template = template;
		this.itemId = material.getItemid();
		this.success = success;
		this.failure = failure;
		this.nameId = material.getNameid();
	}

	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeH(buf, template.getSkillLevel());
		writeC(buf, action);
		writeD(buf, itemId);

		switch(action)
		{
			case 0:
			{
				writeD(buf, template.getSuccessAdj());
				writeD(buf, template.getFailureAdj());
				writeD(buf, 0);
				writeD(buf, 1200);        //timer??
				writeD(buf, 1330011); //??text??skill??
				writeH(buf, 0x24); //0x24
				writeD(buf, nameId); 
				writeH(buf, 0); //0x24
				break;
			}
			case  1:
			{
				writeD(buf, success);
				writeD(buf, failure);
				writeD(buf, 700);        //unk timer??
				writeD(buf, 1200);      //unk timer??
				writeD(buf, 0); //unk timer??writeD(buf, 700);
				writeH(buf, 0);
				break;
			}
			case 2:
			{
				writeD(buf, template.getSuccessAdj());
				writeD(buf, failure);
				writeD(buf, 700);//unk timer??
				writeD(buf, 1200); //unk timer??
				writeD(buf, 0); //unk timer??writeD(buf, 700);
				writeH(buf, 0);
				break;
			}
			case 5: // you have stopped gathering
			{
				writeD(buf, 0);
				writeD(buf, 0);
				writeD(buf, 700);//unk timer??
				writeD(buf, 1200); //unk timer??
				writeD(buf, 1330011); //unk timer??writeD(buf, 700);
				writeH(buf, 0);
				break;
			}
			case 6:
			{
				writeD(buf, template.getSuccessAdj());
				writeD(buf, failure);
				writeD(buf, 700); //unk timer??
				writeD(buf, 1200); //unk timer??
				writeD(buf, 0); //unk timer??writeD(buf, 700);
				writeH(buf, 0);
				break;
			}
			case 7:
			{
				writeD(buf, success);
				writeD(buf, template.getFailureAdj());
				writeD(buf, 0);
				writeD(buf, 1200); //timer??
				writeD(buf, 1330011); //??text??skill??
				writeH(buf, 0x24); //0x24
				writeD(buf, nameId);
				writeH(buf, 0); //0x24
				break;
			}
		}
	}

}
