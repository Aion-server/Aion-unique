/**
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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;
import java.util.Random;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
/**
 * @author -Avol-
 * 
 */
public class SM_ATTACK extends AionServerPacket
{

	private int	receiver;
	
	
	public SM_ATTACK(int receiver)
	{
		this.receiver = receiver;
	}


	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		Random generator = new Random();
		int randomIndex = generator.nextInt(100)+ 1;
		randomIndex = randomIndex * -1;
		writeD(buf, receiver);
		writeD(buf, randomIndex);
		writeD(buf, 7);
	}
}