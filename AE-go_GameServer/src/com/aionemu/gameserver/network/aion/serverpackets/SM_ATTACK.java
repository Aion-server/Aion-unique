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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;
import java.util.List;

import com.aionemu.gameserver.controllers.attack.AttackResult;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * 
 * @author -Nemesiss-, Sweetkr
 * 
 */
public class SM_ATTACK extends AionServerPacket
{
	private int	attackno;
	private int	time;
	private int	type;
	private List<AttackResult> attackList;
	private Creature attacker;
	private Creature target;

	public SM_ATTACK(Creature attacker, Creature target, int attackno, int time, int type, List<AttackResult> attackList)
	{
		this.attacker = attacker;
		this.target = target;
		this.attackno = attackno;// empty
		this.time = time ;// empty
		this.type = type;// empty
		this.attackList = attackList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, attacker.getObjectId());
		writeC(buf, attackno); // unknown
		writeH(buf, time); // unknown
		writeC(buf, type); // 0, 1, 2
		writeD(buf, target.getObjectId());

		int attackerMaxHp = attacker.getLifeStats().getMaxHp();
		int attackerCurrHp = attacker.getLifeStats().getCurrentHp();
		int targetMaxHp = target.getLifeStats().getMaxHp();
		int targetCurrHp = target.getLifeStats().getCurrentHp();

		writeC(buf, 100 * targetCurrHp / targetMaxHp); // target %hp
		writeC(buf, 100 * attackerCurrHp / attackerMaxHp); // attacker %hp

		// TODO refactor attack controller
		switch(attackList.get(0).getAttackStatus().getId())    // Counter skills
		{
			case -60:  // case CRITICAL_BLOCK
			case 4:  // case BLOCK
				writeH(buf, 32);
				break;
			case -62:  // case CRITICAL_PARRY
			case 2:  // case PARRY
				writeH(buf, 64);
				break;
			case -64:  // case CRITICAL_DODGE
			case 0:  // case DODGE
				writeH(buf, 128);
				break;
			case -58:  // case CRITICAL_RESIST
			case 6:  // case RESIST
				writeH(buf, 256); // need more info becuz sometimes 0
				break;
			default:
				writeH(buf, 0);
				break;
		}

		writeC(buf, attackList.size());
		for (AttackResult attack : attackList)
		{
			writeD(buf, attack.getDamage());
			writeC(buf, attack.getAttackStatus().getId());
			writeC(buf, attack.getShieldType());

			switch(attack.getShieldType())
			{
				case 1: // reflect shield
					writeD(buf, 0x00);
					writeD(buf, 0x00);
					writeD(buf, 0x00);
					writeD(buf, 0); // reflect damage
					writeD(buf, 0); // skill id
					break;
				case 2: // normal shield
				default:
					break;
			}
		}
		writeC(buf, 0);
	}
}
