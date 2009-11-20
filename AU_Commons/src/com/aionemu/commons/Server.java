package com.aionemu.commons;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.LoggingService;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

/**
 * Author: SoulKeeper
 */
public abstract class Server {

	private static final Logger log = Logger.getLogger(Server.class);

	private static Server server;

	private ClassPathXmlApplicationContext applicationContext;

	private Thread shutdownHook;

	private boolean shutdownInProcess;

	protected void start() {
		LoggingService.init();

		// TODO: Do we need DeadLock Detector?

		server = this;
		applicationContext = new ClassPathXmlApplicationContext("/META-INF/server.xml");

		shutdownHook = new Thread() {
			public void run() {
				Server.this.stop();
				log.info("Registered shutdown hook");
			}
		};
		printMemoryUsage();
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

	public ClassPathXmlApplicationContext getApplicationContext() {
		return applicationContext;
	}

	protected abstract void onShutdown();

	protected void printMemoryUsage() {
		MemoryUsage hm = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		MemoryUsage nhm = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();

		log.info("Heap Memory Usage: " + (hm.getUsed() / 1048576) + "/" + (hm.getMax() / 1048576) + " MB");
		log.info("NonHeap Memory Usage: " + (nhm.getUsed() / 1048576) + "/" + (nhm.getMax() / 1048576) + " MB");
	}
}
