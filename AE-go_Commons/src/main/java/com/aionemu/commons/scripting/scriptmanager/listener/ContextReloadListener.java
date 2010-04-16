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
 * Context reload listener
 * 
 * @see com.aionemu.commons.scripting.scriptmanager.ScriptManager#reloadContext(com.aionemu.commons.scripting.ScriptContext)
 * 
 * @author SoulKeeper, Aquanox
 */
@SuppressWarnings("unchecked")
public abstract class ContextReloadListener implements Callback
{
	/** {@inheritDoc} */
	@Override
	public final CallbackResult beforeCall(Object obj, Object[] args)
	{
		beforeReload((ScriptContext) args[0]);
		return CallbackResult.newContinue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CallbackResult afterCall(Object obj, Object[] args, Object methodResult)
	{
		afterReload((ScriptContext) args[0]);
		return CallbackResult.newContinue();
	}

	/**
	 * Invoke before context reload procedure.
	 * 
	 * @param ctx
	 */
	protected void beforeReload(ScriptContext ctx)
	{
		// empty
	}

	/**
	 * Invoke after context reload procedure.
	 * 
	 * @param ctx
	 */
	protected void afterReload(ScriptContext ctx)
	{
		// empty
	}

	/** {@inheritDoc} */
	@Override
	public final Class getBaseClass()
	{
		return ContextReloadListener.class;
	}
}