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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.controllers.movement.ActionObserver;
import com.aionemu.gameserver.controllers.movement.ActionObserver.ObserverType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TARGET_IMMOBILIZE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RootEffect")
public class RootEffect extends EffectTemplate
{
	@Override
	public void applyEffect(Effect effect)
	{
		effect.addToEffectedController();
	}

	@Override
	public void calculate(Effect effect)
	{
		effect.increaseSuccessEffect();	
	}

	@Override
	public void startEffect(final Effect effect)
	{
		final Creature effected = effect.getEffected();
		effect.getEffected().getEffectController().setAbnormal(EffectId.ROOT.getEffectId());
		
		PacketSendUtility.broadcastPacket(effected, new SM_TARGET_IMMOBILIZE(effected));
		
		effected.getObserveController().attach(
			new ActionObserver(ObserverType.ATTACKED)
			{
				@Override
				public void attacked()
				{
					effected.getEffectController().removeEffect(effect.getSkillId());
				}			
			}
		);
	}

	@Override
	public void endEffect(Effect effect)
	{
		effect.getEffected().getEffectController().unsetAbnormal(EffectId.ROOT.getEffectId());
	}
}
