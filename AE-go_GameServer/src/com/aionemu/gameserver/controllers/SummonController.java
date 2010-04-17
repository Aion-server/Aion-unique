/*
 * This file is part of aion-unique <aion-unique.org>.
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
package com.aionemu.gameserver.controllers;

import java.util.List;

import com.aionemu.gameserver.controllers.attack.AttackResult;
import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.Summon.SummonMode;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SUMMON_OWNER_REMOVE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SUMMON_PANEL_REMOVE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SUMMON_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 * 
 */
public class SummonController extends CreatureController<Summon>
{
	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange)
	{
		super.notSee(object, isOutOfRange);
		if(getOwner().getMaster() == null)
			return;
		
		if(object.getObjectId() == getOwner().getMaster().getObjectId())
		{
			release(UnsummonType.DISTANCE);
		}
	}

	@Override
	public Summon getOwner()
	{
		return (Summon) super.getOwner();
	}

	/**
	 * Release summon
	 */
	public void release(final UnsummonType unsummonType)
	{
		final Summon owner = getOwner();

		if(owner.getMode() == SummonMode.RELEASE)
			return;
		owner.setMode(SummonMode.RELEASE);

		final Player master = owner.getMaster();
		final int summonObjId = owner.getObjectId();

		switch(unsummonType)
		{
			case COMMAND:
				PacketSendUtility.sendPacket(master, SM_SYSTEM_MESSAGE.SUMMON_UNSUMMON(getOwner().getNameId()));
				PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(getOwner()));
				break;
			case DISTANCE:
				PacketSendUtility.sendPacket(getOwner().getMaster(), SM_SYSTEM_MESSAGE
					.SUMMON_UNSUMMON_BY_TOO_DISTANCE());
				PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(getOwner()));
				break;
			case LOGOUT:
			case UNSPECIFIED:
				break;
		}

		ThreadPoolManager.getInstance().schedule(new Runnable(){

			@Override
			public void run()
			{
				owner.setMaster(null);
				master.setSummon(null);
				owner.getController().delete();

				switch(unsummonType)
				{
					case COMMAND:
					case DISTANCE:
					case UNSPECIFIED:
						PacketSendUtility
							.sendPacket(master, SM_SYSTEM_MESSAGE.SUMMON_DISMISSED(getOwner().getNameId()));
						PacketSendUtility.sendPacket(master, new SM_SUMMON_OWNER_REMOVE(summonObjId));

						// TODO temp till found on retail
						PacketSendUtility.sendPacket(master, new SM_SUMMON_PANEL_REMOVE());
						break;
					case LOGOUT:				
						break;
				}
			}
		}, 5000);
	}

	/**
	 * Change to rest mode
	 */
	public void restMode()
	{
		getOwner().setMode(SummonMode.REST);
		Player master = getOwner().getMaster();
		PacketSendUtility.sendPacket(master, SM_SYSTEM_MESSAGE.SUMMON_RESTMODE(getOwner().getNameId()));
		PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(getOwner()));
	}

	/**
	 * Change to guard mode
	 */
	public void guardMode()
	{
		getOwner().setMode(SummonMode.GUARD);
		Player master = getOwner().getMaster();
		PacketSendUtility.sendPacket(master, SM_SYSTEM_MESSAGE.SUMMON_GUARDMODE(getOwner().getNameId()));
		PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(getOwner()));
	}

	/**
	 * Change to attackMode
	 */
	public void attackMode()
	{
		getOwner().setMode(SummonMode.ATTACK);
		Player master = getOwner().getMaster();
		PacketSendUtility.sendPacket(master, SM_SYSTEM_MESSAGE.SUMMON_ATTACKMODE(getOwner().getNameId()));
		PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(getOwner()));
	}

	@Override
	public void attackTarget(Creature target)
	{
		Summon summon = getOwner();
		Player master = getOwner().getMaster();
		if(!summon.canAttack())
			return;

		if(!RestrictionsManager.canAttack(master, target))
			return;

		if(!summon.isEnemy(target))
			return;
		/**
		 * notify attack observers
		 */
		super.attackTarget(target);

		List<AttackResult> attackList = AttackUtil.calculateAttackResult(summon, target);

		int damage = 0;
		for(AttackResult result : attackList)
		{
			damage += result.getDamage();
		}

		int attackType = 0;
		PacketSendUtility.broadcastPacket(summon, new SM_ATTACK(summon, target, summon.getGameStats()
			.getAttackCounter(), 274, attackType, attackList));

		target.getController().onAttack(summon, damage);
		summon.getGameStats().increaseAttackCounter();

	}

	@Override
	public void onAttack(Creature creature, int damage)
	{
		if(getOwner().getLifeStats().isAlreadyDead())
			return;
		
		//temp 
		if(getOwner().getMode() == SummonMode.RELEASE)
			return;
		
		super.onAttack(creature, damage);
		getOwner().getLifeStats().reduceHp(damage, creature);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_ATTACK_STATUS(getOwner(), TYPE.REGULAR, 0,
			damage));
		PacketSendUtility.sendPacket(getOwner().getMaster(), new SM_SUMMON_UPDATE(getOwner()));
	}
	
	@Override
	public void onDie(Creature lastAttacker)
	{
		release(UnsummonType.UNSPECIFIED);
		Summon owner = getOwner();
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, 13, 0, lastAttacker == null ? 0 : lastAttacker
			.getObjectId()));
	}



	public static enum UnsummonType
	{
		LOGOUT,
		DISTANCE,
		COMMAND,
		UNSPECIFIED
	}
}
