package com.aionemu.loginserver;

import com.aionemu.commons.Server;
import com.aionemu.loginserver.utils.ThreadUncaughtExceptionHandler;

/**
 * Author: SoulKeeper
 */
public class LoginServer2 extends Server {

	public void start(){
		super.start();
		// TODO: Start network here
	}

	@Override
	protected void onShutdown() {
		//TODO: Stop network here
	}

	public static void main(String[] args) {
		Thread.currentThread().setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());
		new LoginServer2().start();
	}
}
