/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * 
 * @author alexa026, Sweetkr
 * 
 */
public class SM_CASTSPELL_END extends AionServerPacket
{
	private Creature		attacker;
	private Creature		target;
	private int				spellid;
	private int				level;
	private int				cooldown;
	private List<Effect>	effects;
	private int 			spellStatus;


	public SM_CASTSPELL_END(Creature attacker, int spellid, int level, Creature target,
		List<Effect> effects, int cooldown, int spellStatus)
	{
		this.attacker = attacker;
		this.target = target;
		this.spellid = spellid;// empty
		this.level = level;
		this.effects = effects;
		this.cooldown = cooldown * 10;
		this.spellStatus = spellStatus;
	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, attacker.getObjectId());
		writeC(buf, 0);
		writeD(buf, target.getObjectId());
		writeH(buf, spellid);
		writeC(buf, level);
		writeD(buf, cooldown);
		writeH(buf, 560); // time?
		writeC(buf, 0); // unk

		/**
		 * 0 : chain skill (counter too)
		 * 16 : no damage to all target like dodge, resist or effect size is 0
		 * 32 : regular
		 */
		writeH(buf, 32);

		/**
		 * Dash Type
		 * 
		 * 1 : teleport to back (1463)
		 * 2 : dash (816)
		 * 4 : assault (803)
		 */
		writeC(buf, 0);

	// TODO refactor skill engine
	/*	switch(attacker.getDashType().getId())
		{
			case 1:
			case 2:
			case 4:
				writeC(buf, heading);
				writeF(buf, x);
				writeF(buf, y);
				writeF(buf, z);
				break;
			default:
				break;
		}*/

		writeH(buf, effects.size());
		for(Effect effect : effects)
		{
			writeD(buf, effect.getEffected().getObjectId());
			writeC(buf, 0); // unk

			int attackerMaxHp = attacker.getLifeStats().getMaxHp();
			int attackerCurrHp = attacker.getLifeStats().getCurrentHp();
			int targetMaxHp = target.getLifeStats().getMaxHp();
			int targetCurrHp = target.getLifeStats().getCurrentHp();

			writeC(buf, 100 * targetCurrHp / targetMaxHp); // target %hp
			writeC(buf, 100 * attackerCurrHp / attackerMaxHp); // attacker %hp
			
			
			/**
			 * Spell Status
			 * 
			 * 1 : stumble
			 * 2 : knockback
			 * 4 : open aerial
			 * 8 : close aerial
			 * 16 : spin
			 * 32 : block
			 * 64 : parry
			 * 128 : dodge
			 * 256 : resist
			 */
			writeC(buf, this.spellStatus);

			// TODO refactor skill engine
			switch(this.spellStatus)
			{
				case 1:
				case 2:
				case 4:
				case 8:
					writeF(buf, target.getX());
					writeF(buf, target.getY());
					writeF(buf, target.getZ());
					break;
				case 16:
					writeC(buf, target.getHeading());
					break;
				default:
					break;
			}

			writeC(buf, 16); // unk
			writeC(buf, 0); // current carve signet count

			writeC(buf, 1); // unk always 1
			writeC(buf, 0); // be 1 - when use Mana Treatment
			writeD(buf, effect.getReserved1()); // damage
			writeC(buf, effect.getAttackStatus().getId());
			writeC(buf, effect.getShieldDefense());

			switch(effect.getShieldDefense())
			{
				case 1: // reflect shield
					writeD(buf, 0x00);
					writeD(buf, 0x00);
					writeD(buf, 0x00);
					writeD(buf, 0x00); // reflect damage
					writeD(buf, 0x00); // skill id
					break;
				case 2: // normal shield
				default:
					break;
			}
		}
	}
}
