/*
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

package com.aionemu.gameserver.controllers.attack;

import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 *
 * @author kosyachok
 * @author ATracer
 */
public class SkillAttackResult
{

	private int damage;

	private AttackStatus attackStatus;

	private Creature creature;

	public SkillAttackResult(Creature creature, int damage, AttackStatus attackStatus)
	{
		this.damage = damage;
		this.attackStatus = attackStatus;
		this.creature = creature;
	}

	/**
	 * @return SkillAttackResult creature
	 */
	public Creature getCreature()
	{
		return creature;
	}

	/**
	 * @return the damage
	 */
	public int getDamage()
	{
		return damage;
	}

	/**
	 * @return the attackStatus
	 */
	public AttackStatus getAttackStatus()
	{
		return attackStatus;
	}

}
