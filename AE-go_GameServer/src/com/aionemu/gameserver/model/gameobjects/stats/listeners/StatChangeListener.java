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
package com.aionemu.gameserver.model.gameobjects.stats.listeners;

import com.aionemu.commons.callbacks.Callback;
import com.aionemu.commons.callbacks.CallbackResult;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.stats.CreatureGameStats;

/**
 * @author ATracer
 *
 */
public abstract class StatChangeListener implements Callback<CreatureGameStats<Creature>>
{		
	protected CreatureGameStats<? extends Creature> gameStats;

	public StatChangeListener(CreatureGameStats<? extends Creature> gameStats)
	{
		this.gameStats = gameStats;
	}

	protected abstract void onRecompute();
	
	@Override
	public final CallbackResult<?> afterCall(CreatureGameStats<Creature> cgs, Object[] args, Object methodResult)
	{
		onRecompute();
		return CallbackResult.newContinue();
	}

	@Override
	public CallbackResult<?> beforeCall(CreatureGameStats<Creature> cgs, Object[] arg1)
	{
		return CallbackResult.newContinue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Class<? extends Callback<CreatureGameStats<Creature>>> getBaseClass()
	{
		return StatChangeListener.class;
	}

}
