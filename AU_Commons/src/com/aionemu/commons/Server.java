package com.aionemu.commons;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.LoggingService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Author: SoulKeeper
 */
public abstract class Server {

	private static Server server;

	private ClassPathXmlApplicationContext applicationContext;

	private Thread shutdownHook;

	private boolean shutdownInProcess;

	protected void start() {
		LoggingService.init();
		server = this;
		applicationContext = new ClassPathXmlApplicationContext("/META-INF/server.xml");

		shutdownHook = new Thread() {
			public void run() {
				Server.this.stop();
			}
		};
	}

	protected void stop() {

		synchronized (this) { // to avoid stop by shutdown hook and manual in the same time
			if (shutdownInProcess) {
				return;
			}

			shutdownInProcess = true;
		}

		if (Thread.currentThread() != shutdownHook) {
			Runtime.getRuntime().removeShutdownHook(shutdownHook);
			shutdownHook = null;
		}

		onShutdown();

		// Getting link on DAOManager and DatabaseFactory
		// We have to make sure that all stuff can be saved into DB
		DAOManager daoManager = applicationContext.getBean(DAOManager.class);
		DatabaseFactory dbf = applicationContext.getBean(DatabaseFactory.class);

		// Server shutdown
		applicationContext.close();

		// And actual database shutdown
		daoManager.shutdown();
		dbf.shutdown();
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	protected abstract void onShutdown();
}
