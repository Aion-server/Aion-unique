package com.aionemu.commons.database.dao.scriptloader;

import com.aionemu.commons.callbacks.EnhancedObject;
import com.aionemu.commons.database.DatabaseConfig;
import com.aionemu.commons.database.DatabaseInfo;
import com.aionemu.commons.database.dao.DAO;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.database.dao.DAOUtils;
import com.aionemu.commons.scripting.ScriptContext;
import com.aionemu.commons.scripting.scriptmanager.ScriptManager;
import com.aionemu.commons.scripting.scriptmanager.listener.ContextReloadListener;
import com.aionemu.commons.services.ScriptService;
import com.aionemu.commons.utils.ClassUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Author: SoulKeeper
 */
public class DAOLoader {

	private static final Logger log = Logger.getLogger(DAOLoader.class.getName());

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	private final DAOManager daoManager;

	private final ScriptService scriptService;

	private final DatabaseConfig databaseConfig;

	private final DatabaseInfo databaseInfo;

	public DAOLoader(DAOManager daoManager, ScriptService scriptService, DatabaseConfig databaseConfig,
					 DatabaseInfo databaseInfo) {
		this.daoManager = daoManager;
		this.scriptService = scriptService;
		this.databaseConfig = databaseConfig;
		this.databaseInfo = databaseInfo;
	}

	public void initDAOs() {
		scriptService.load(databaseConfig.getScriptContextDescriptor());
		ScriptManager mgr = scriptService.getLoadedScriptManagers().get(databaseConfig.getScriptContextDescriptor());
		ScriptContext ctx = mgr.getScriptContexts().iterator().next();
		loadDAOs(ctx);

		((EnhancedObject)mgr).addCallback(new ContextReloadListener(){

			private Collection<Class<? extends DAO>> oldClasses;

			@Override
			protected synchronized void beforeReload(ScriptContext ctx) {
				DAOLoader.this.lock.writeLock().lock();

				if(oldClasses != null){
					Error e = new Error("Concurrent reload of DAO classes not allowed!");
					log.fatal(e);
					throw e;
				}

				oldClasses = DAOLoader.this.getSuitableDAOClasses(ctx.getCompilationResult().getCompiledClasses());
			}

			@Override
			protected synchronized void afterReload(ScriptContext ctx) {
				try{
					DAOLoader.this.onShutdown(oldClasses);
					DAOLoader.this.loadDAOs(ctx);
					oldClasses = null;
				} finally{
					DAOLoader.this.lock.writeLock().unlock();
				}
			}
		});
	}

	public void shutdown(){
		ScriptManager mgr = scriptService.getLoadedScriptManagers().get(databaseConfig.getScriptContextDescriptor());
		ScriptContext ctx = mgr.getScriptContexts().iterator().next();
		onShutdown(DAOLoader.this.getSuitableDAOClasses(ctx.getCompilationResult().getCompiledClasses()));
	}

	protected void onShutdown(Collection<Class<? extends DAO>> daoClasses){
		for(Class<? extends DAO> c : daoClasses){
			daoManager.unregisterDAO(c);
		}
	}

	protected void loadDAOs(ScriptContext ctx) {
		Class[] classes = ctx.getCompilationResult().getCompiledClasses();
		ApplicationContext actx = ctx.getApplicationContext();

		Collection<Class<? extends DAO>> daoClasses = getSuitableDAOClasses(classes);
		for (Class c : daoClasses) {
			try {
				daoManager.registerDAO((DAO) actx.getBean(actx.getBeanNamesForType(c)[0]));
			} catch (Exception e) {
				throw new RuntimeException("Exception while initializing DAOs", e);
			}
		}
	}

	protected Collection<Class<? extends DAO>> getSuitableDAOClasses(Class[] classes) {
		List<Class<? extends DAO>> result = new ArrayList<Class<? extends DAO>>();

		for (Class c : classes) {
			if (!ClassUtils.isSubclass(c, DAO.class)) {
				continue;
			}

			//noinspection unchecked
			if (!DAOUtils.isDBSupportedByDAO(c, databaseInfo)) {
				continue;
			}

			//noinspection unchecked
			result.add(c);
		}

		return result;
	}

	public ReentrantReadWriteLock getLock() {
		return lock;
	}
}
