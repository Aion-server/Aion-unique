/**
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
package com.aionemu.commons.database;

// Common SQL

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

/**
 * <b>Database Factory</b><br>
 * <br>
 * This file is used for creating a pool of connections for the server.<br>
 * It utilizes database.properties and creates a pool of connections and automatically recycles them when closed.<br>
 * <br>
 * DB.java utilizes the class.<br>
 *
 * @author Disturbing
 * @author SoulKeeper
 */
@Service
public class DatabaseFactory
{

	/**
	 * Logger for this class
	 */
	private static final Logger			log				= Logger.getLogger(DatabaseFactory.class);

	/**
	 * Data Source Generates all Connections
	 */
	private DataSource			dataSource;

	/**
	 * Connection Pool holds all connections - Idle or Active
	 */
	private GenericObjectPool	connectionPool;

    /**
     * Database information
     */
    private DatabaseInfo databaseInfo;

    /**
     * Default constructor for databaseFactory
     * @param databaseConfig databse config instance
     */
    @Autowired
    public DatabaseFactory(DatabaseConfig databaseConfig){

        Driver driver;
		try
		{
			driver = (Driver) Class.forName(databaseConfig.getDriver()).newInstance();
		}
		catch (Exception e)
		{
			log.fatal("Error obtaining DB driver", e);
			throw new Error("DB Driver doesn't exist!");
		}

		connectionPool = new GenericObjectPool();

		if (databaseConfig.getMinConnections() > databaseConfig.getMaxConnections())
		{
            throw new Error("Please check your database configuration. Minimum amount of connections is > maximum");
		}

		connectionPool.setMaxIdle(databaseConfig.getMinConnections());
		connectionPool.setMaxActive(databaseConfig.getMaxConnections());

		/* test if connection is still valid before returning */
		connectionPool.setTestOnBorrow(true);

		try
		{
            Properties connectionProperties = new Properties();
            connectionProperties.put("user", databaseConfig.getUser());
            connectionProperties.put("password", databaseConfig.getPassword());
            ConnectionFactory cf = new DriverConnectionFactory(driver, databaseConfig.getUrl(), connectionProperties);
            // Makes Connection Factory Pool-able (Wrapper for two objects)
		    // We are using our own implementation of PoolableConnectionFactory that use 1.6 Connection.isValid(timeout) for
		    // validation check instead dbcp manual query.
		    new PoolableConnectionFactoryAE(cf, connectionPool, null, 1, false, true);
			dataSource = new PoolingDataSource(connectionPool);
			Connection c = getConnection();
			DatabaseMetaData dmd = c.getMetaData();
            databaseInfo = new DatabaseInfo(dmd.getDatabaseProductName(), dmd.getDatabaseMajorVersion(),
                    dmd.getDriverMinorVersion());
			c.close();
		}
		catch (Exception e)
		{
			throw new Error("Error with connection string: " + databaseConfig.getUrl(), e);
		}

		log.info("Successfully connected to database: " + databaseInfo.toString());
	}

	/**
	 * Returns an active connection from pool. This function utilizes the dataSource which grabs an object from the
	 * ObjectPool within its limits. The GenericObjectPool.borrowObject()' function utilized in
	 * 'DataSource.getConnection()' does not allow any connections to be returned as null, thus a null check is not
	 * needed. Throws SQLException in case of a Failed Connection
	 *
	 * @return Connection pooled connection
	 * @throws java.sql.SQLException
	 *             if can't get connection
	 */
	public Connection getConnection() throws SQLException
	{
		return dataSource.getConnection();
	}

	/**
	 * Returns number of active connections in the pool.
	 *
	 * @return int Active DB Connections
	 */
	public int getActiveConnections()
	{
		return connectionPool.getNumActive();
	}

	/**
	 * Returns number of Idle connections. Idle connections represent the number of instances in Database Connections
	 * that have once been connected and now are closed and ready for re-use. The 'getConnection' function will grab
	 * idle connections before creating new ones.
	 *
	 * @return int Idle DB Connections
	 */
	public int getIdleConnections()
	{
		return connectionPool.getNumIdle();
	}

	/**
	 * Shuts down pool and closes connections.
     * Invoked by spring
	 */
    @PreDestroy
	public synchronized void shutdown()
	{
        log.error("DBFactory - > shutdown");
		try
		{
			connectionPool.close();
		}
		catch (Exception e)
		{
			log.warn("Failed to shutdown DatabaseFactory", e);
		}
	}

    /**
     * Returns database information; what database is used by this server
     * @return database information
     */
    public DatabaseInfo getDatabaseInfo() {
        return databaseInfo;
    }
}
