package com.aionemu.commons.database;

import com.aionemu.commons.utils.PropertiesUtils;
import org.junit.After;
import org.junit.BeforeClass;

/**
 * Author: SoulKeeper
 */
public class ThreadLocalTransactionServiceTest {

	private DatabaseConfig dbc;

	private DatabaseFactory dbf;

	private ThreadLocalTransactionService tlts;

	@BeforeClass
	protected void before() throws Exception{
		dbc = new DatabaseConfig(PropertiesUtils.load("./config/database.properties"));
		dbf = new DatabaseFactory(dbc);
		tlts = new ThreadLocalTransactionService(dbf);
	}

	@After
	protected void afterTest(){

	}
}
