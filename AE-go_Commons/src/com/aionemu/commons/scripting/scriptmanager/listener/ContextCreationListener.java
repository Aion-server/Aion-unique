/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.commons.scripting.scriptmanager.listener;

import com.aionemu.commons.callbacks.Callback;
import com.aionemu.commons.callbacks.CallbackResult;
import com.aionemu.commons.scripting.ScriptContext;

/**
 * ScriptContext creation listener for ScriptManager.<br>
 * 
 * @see com.aionemu.commons.scripting.scriptmanager.ScriptManager#createContext(com.aionemu.commons.scripting.scriptmanager.ScriptInfo
 *      , com.aionemu.commons.scripting.ScriptContext)
 * 
 * @author SoulKeeper
 */
@SuppressWarnings("unchecked")
public abstract class ContextCreationListener implements Callback
{
	@Override
	public final CallbackResult beforeCall(Object obj, Object[] args)
	{
		return CallbackResult.newContinue();
	}

	@Override
	public final CallbackResult afterCall(Object obj, Object[] args, Object methodResult)
	{
		contextCreated((ScriptContext) methodResult);
		return CallbackResult.newContinue();
	}

	/**
	 * Invoked when new contexts was created
	 * 
	 * @param context
	 *            object that was created
	 */
	protected abstract void contextCreated(ScriptContext context);

	@Override
	public final Class getBaseClass()
	{
		return ContextCreationListener.class;
	}
}
