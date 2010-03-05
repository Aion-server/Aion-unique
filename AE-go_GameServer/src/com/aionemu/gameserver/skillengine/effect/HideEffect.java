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
package com.aionemu.gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.controllers.movement.ActionObserver;
import com.aionemu.gameserver.controllers.movement.ActionObserver.ObserverType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Sweetkr
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HideEffect")
public class HideEffect extends EffectTemplate
{
	@XmlAttribute
	protected int value;

	@Override
	public void applyEffect(Effect effect)
	{
		effect.addToEffectedController();
	}

	@Override
	public void calculate(Effect effect)
	{
		//TODO calc probability
		effect.increaseSuccessEffect();		
	}

	@Override
	public void endEffect(Effect effect)
	{
		Creature effected = effect.getEffected();
		effected.getEffectController().unsetAbnormal(EffectId.INVISIBLE_RELATED.getEffectId());

		CreatureVisualState visualState;

		switch(value)
		{
			case 1:
				visualState = CreatureVisualState.HIDE1;
				break;
			case 2:
				visualState = CreatureVisualState.HIDE2;
				break;
			case 3:
				visualState = CreatureVisualState.HIDE3;
				break;
			case 10:
				visualState = CreatureVisualState.HIDE10;
				break;
			case 13:
				visualState = CreatureVisualState.HIDE13;
				break;
			case 20:
				visualState = CreatureVisualState.HIDE20;
				break;
			default:
				visualState = CreatureVisualState.VISIBLE;
				break;
		}
		effected.unsetVisualState(visualState);

		if(effected instanceof Player)
		{
			PacketSendUtility.broadcastPacket((Player)effected, new SM_PLAYER_STATE((Player)effected), true);
		}
	}

	@Override
	public void startEffect(final Effect effect)
	{
		final Creature effected = effect.getEffected();
		effected.getEffectController().setAbnormal(EffectId.INVISIBLE_RELATED.getEffectId());

		CreatureVisualState visualState;

		switch(value)
		{
			case 1:
				visualState = CreatureVisualState.HIDE1;
				break;
			case 2:
				visualState = CreatureVisualState.HIDE2;
				break;
			case 3:
				visualState = CreatureVisualState.HIDE3;
				break;
			case 10:
				visualState = CreatureVisualState.HIDE10;
				break;
			case 13:
				visualState = CreatureVisualState.HIDE13;
				break;
			case 20:
				visualState = CreatureVisualState.HIDE20;
				break;
			default:
				visualState = CreatureVisualState.VISIBLE;
				break;
		}
		effected.setVisualState(visualState);

		if(effected instanceof Player)
		{
			PacketSendUtility.broadcastPacket((Player)effected, new SM_PLAYER_STATE((Player)effected), true);
		}
		
		effected.getObserveController().attach(
			new ActionObserver(ObserverType.ATTACK)
			{
				@Override
				public void attack()
				{
					effected.getEffectController().removeEffect(effect.getSkillId());
				}			
			}
		);
	}
}
