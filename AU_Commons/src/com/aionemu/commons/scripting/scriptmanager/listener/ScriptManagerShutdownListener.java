package com.aionemu.commons.scripting.scriptmanager.listener;

import com.aionemu.commons.callbacks.Callback;
import com.aionemu.commons.callbacks.CallbackResult;
import com.aionemu.commons.scripting.scriptmanager.ScriptManager;

/**
 * Author: SoulKeeper
 */
public class ScriptManagerShutdownListener implements Callback<ScriptManager>{

	@Override
	public final CallbackResult beforeCall(ScriptManager obj, Object[] args) {
		beforeShutdown(obj);
		return CallbackResult.newContinue();
	}

	@Override
	public final CallbackResult afterCall(ScriptManager obj, Object[] args, Object methodResult) {
		afterShutdown(obj);
		return CallbackResult.newContinue();
	}

	public void beforeShutdown(ScriptManager obj){

	}

	public void afterShutdown(ScriptManager obj){

	}

	@Override
	public final Class<? extends Callback> getBaseClass() {
		return ScriptManagerShutdownListener.class;
	}
}
