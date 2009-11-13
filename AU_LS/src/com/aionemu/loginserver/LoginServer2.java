package com.aionemu.loginserver;

import com.aionemu.commons.Server;
import com.aionemu.loginserver.network.netty.LoginNettyServer;
import com.aionemu.loginserver.utils.ThreadUncaughtExceptionHandler;

/**
 * Author: SoulKeeper
 */
public class LoginServer2 extends Server {

	public void start(){
		super.start();
		// Start network 
		LoginNettyServer.getInstance().initialize();
	}

	@Override
	protected void onShutdown() {
		//Stop network
		LoginNettyServer.getInstance().shutdownAll();
	}

	public static void main(String[] args) {
		Thread.currentThread().setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());
		new LoginServer2().start();
	}
}
