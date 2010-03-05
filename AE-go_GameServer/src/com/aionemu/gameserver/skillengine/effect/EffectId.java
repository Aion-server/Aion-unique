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
package com.aionemu.gameserver.skillengine.effect;

/**
 * @author ATracer
 *
 */
public enum EffectId
{
	BUFF(0),
	POISON(1),
	BLEED(2),
	PARALYZE(4),
	SLEEP(8),
	ROOT(16), // ?? cannot move ?
	BLIND(32),
	UNKNOWN(64),
	DISEASE(128),
	SILENCE(256),
	FEAR(512), //Fear I
	CURSE(1024),
	CHAOS(2056),
	STUN(4096),
	PETRIFICATION(8192),
	STUMBLE(16384),
	STAGGER(32768),
	OPENAERIAL(65536),
	SNARE(131072),
	SLOW(262144),
	ROTATION(524288),
	BLOCKADE(1048576),
	UNKNOWN2(2097152), //(Curse of Roots I, Fear I)
	CANNOT_MOVE(4194304), //(Inescapable Judgment I)
	SHAPECHANGE(8388608), //cannot fly
	KNOCKBACK(16777216),
	INVISIBLE_RELATED(33554432),//hide
	
	/**
	 * Compound abnormal states
	 */
	CANT_ATTACK_STATE(SLEEP.effectId | STUN.effectId | STUMBLE.effectId),
	CANT_MOVE_STATE(ROOT.effectId | SLEEP.effectId | STUMBLE.effectId | STUN.effectId);
	
	private int effectId;
	
	private EffectId(int effectId)
	{
		this.effectId = effectId;
	}
	
	public int getEffectId()
	{
		return effectId;
	}
}
