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
package com.aionemu.gameserver.model.gameobjects.player.listeners;

import com.aionemu.commons.callbacks.Callback;
import com.aionemu.commons.callbacks.CallbackResult;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * This listener is invoked when player logs into the game. 
 * @author Luno
 *
 */
public abstract class PlayerLoggedInListener implements Callback<Player>
{
	/**
	 * This method is invoked when player enters the game.<br>
	 * This method must be overriden by the implementing listener
	 * 
	 * @param player
	 */
	protected abstract void onLoggedIn(Player player);
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CallbackResult<?> afterCall(Player player, Object[] args, Object methodResult)
	{
		onLoggedIn(player);
		return CallbackResult.newContinue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CallbackResult<?> beforeCall(Player obj, Object[] args)
	{
		return CallbackResult.newContinue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Class<? extends Callback<Player>> getBaseClass()
	{
		return PlayerLoggedInListener.class;
	}

}
