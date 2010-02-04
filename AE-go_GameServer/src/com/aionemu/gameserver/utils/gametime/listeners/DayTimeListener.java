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
package com.aionemu.gameserver.utils.gametime.listeners;

import com.aionemu.commons.callbacks.Callback;
import com.aionemu.commons.callbacks.CallbackResult;
import com.aionemu.gameserver.utils.gametime.GameTime;

/**
 * @author ATracer
 *
 */
public abstract class DayTimeListener  implements Callback<GameTime>
{	
	protected abstract void onDayTimeChange(GameTime gameTime);

	@Override
	public final CallbackResult<?> afterCall(GameTime gameTime, Object[] args, Object methodResult)
	{
		onDayTimeChange(gameTime);
		return CallbackResult.newContinue();
	}

	@Override
	public CallbackResult<?> beforeCall(GameTime gameTime, Object[] arg1)
	{
		return CallbackResult.newContinue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Class<? extends Callback<GameTime>> getBaseClass()
	{
		return DayTimeListener.class;
	}

}
