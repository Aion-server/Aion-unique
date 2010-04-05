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
package com.aionemu.gameserver.model.gameobjects.stats;

import java.util.TreeSet;

import com.aionemu.commons.callbacks.EnhancedObject;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.stats.listeners.StatChangeListener;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.StatModifier;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.model.items.NpcEquippedGear;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.stats.NpcStatsTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author xavier
 * 
 */
public class NpcGameStats extends CreatureGameStats<Npc>
{
	public NpcGameStats(Npc owner)
	{
		super(owner);
		// TODO set other stats
		NpcStatsTemplate nst = owner.getObjectTemplate().getStatsTemplate();

		initStat(StatEnum.MAXHP, nst.getMaxHp()
			+ Math.round((owner.getObjectTemplate().getHpGauge() * 1.5f) * (int) owner.getLevel()));
		initStat(StatEnum.MAXMP, nst.getMaxMp());
		// TODO: Npc Attack Speed
		// initStat(StatEnum.ATTACK_SPEED, Math.round(nst.getAttackSpeed() * 1000));
		initStat(StatEnum.ATTACK_SPEED, 2000);
		initStat(StatEnum.PHYSICAL_DEFENSE, Math.round(((nst.getPdef() / (int) owner.getLevel()) - 1) * nst.getPdef()
			+ 10 * (int) owner.getLevel()));
		initStat(StatEnum.EVASION, Math.round(nst.getEvasion() * 2.3f + (int) owner.getLevel() * 10));
		initStat(StatEnum.MAGICAL_RESIST, Math.round(nst.getMdef()));
		initStat(StatEnum.MAIN_HAND_POWER, nst.getPower());
		initStat(StatEnum.MAIN_HAND_ACCURACY, Math.round(nst.getAccuracy() * 2.3f + (int) owner.getLevel() * 10));
		initStat(StatEnum.MAIN_HAND_CRITICAL, Math.round(nst.getCrit()));
		initStat(StatEnum.SPEED, Math.round(nst.getRunSpeedFight() * 1000));

		initStatsFromEquipment(owner);

		addRecomputeListener(owner);
	}

	/**
	 * I hope one day we will have all stats from equip applied automatically
	 * 
	 * @param owner
	 */
	private void initStatsFromEquipment(Npc owner)
	{
		NpcEquippedGear equipment = owner.getObjectTemplate().getEquipment();
		if(equipment != null)
		{
			equipment.init();
			
			ItemTemplate itemTemplate = equipment.getItem(ItemSlot.MAIN_HAND);
			if(itemTemplate != null)
			{
				TreeSet<StatModifier> modifiers = itemTemplate.getModifiers();
				if(modifiers != null)
				{
					for(StatModifier modifier : modifiers)
					{
						if(modifier.getStat() == StatEnum.ATTACK_RANGE)
							initStat(StatEnum.ATTACK_RANGE, modifier.apply(0, 0));
					}
				}
			}
		}

		/**
		 * ATTACK_RANGE should be set to default 2000 if there is no equipment
		 */
		if(getCurrentStat(StatEnum.ATTACK_RANGE) == 0)
			initStat(StatEnum.ATTACK_RANGE, 2000);
	}

	/**
	 * Adds listener that will check SPEED changes
	 * 
	 * @param owner
	 */
	private void addRecomputeListener(final Npc owner)
	{
		((EnhancedObject) this).addCallback(new StatChangeListener(this){

			private int	currentRunSpeed	= 0;

			@Override
			protected void onRecompute()
			{
				int newRunSpeed = gameStats.getCurrentStat(StatEnum.SPEED);

				if(newRunSpeed != currentRunSpeed)
				{
					owner.getMoveController().setSpeed(newRunSpeed / 1000f);
					PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, 30, 0, 0));
				}

				this.currentRunSpeed = newRunSpeed;
			}
		});
	}
}