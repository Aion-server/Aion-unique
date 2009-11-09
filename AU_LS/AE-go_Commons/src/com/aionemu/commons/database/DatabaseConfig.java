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

package com.aionemu.commons.database;

import com.aionemu.commons.configuration.AbstractConfig;
import com.aionemu.commons.configuration.Property;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

/**
 * This class holds all configuration of database
 *
 * @author SoulKeeper
 */
@Service
public class DatabaseConfig extends AbstractConfig {

	/**
	 * Logger for database config
	 */
	private static final Logger log = Logger.getLogger(DatabaseConfig.class);

	/**
	 * Default system property for location of database properties
	 */
	public static final String DATABASE_PROPERTIES_KEY = "";

	/**
	 * Default location of database properties
	 */
	public static final String DATABASE_PROPERTIES_LOCATION = "";

	/**
	 * Default database url.
	 */
	@Property(key = "database.url", defaultValue = "jdbc:mysql://localhost:3306/aion_emu")
	public String url;

	/**
	 * Name of database Driver
	 */
	@Property(key = "database.driver", defaultValue = "com.mysql.jdbc.Driver")
	public String driver;

	/**
	 * Default database user
	 */
	@Property(key = "database.user", defaultValue = "root")
	public String user;

	/**
	 * Default database password
	 */
	@Property(key = "database.password", defaultValue = "root")
	public String password;

	/**
	 * Minimum amount of connections that are always active
	 */
	@Property(key = "database.connections.min", defaultValue = "2")
	public int minConnections;

	/**
	 * Maximum amount of connections that are allowed to use
	 */
	@Property(key = "database.connections.max", defaultValue = "10")
	public int maxConnections;

	/**
	 * Location of database script context descriptor
	 */
	@Property(key = "database.scriptcontext.descriptor", defaultValue = "./data/scripts/system/database.xml")
	public File scriptContextDescriptor;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getSystemPropertiesKey() {
		return "com.aionemu.commons.database.properties";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDefaultPropertiesLocation() {
		return "./config/database.properties";
	}

	/**
	 * {@inheritDoc}
	 */
	@PostConstruct
	public void load() throws IOException {
		super.load();
	}

	/**
	 * Returns database url
	 *
	 * @return database url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Returns database driver class name
	 *
	 * @return database driver class name
	 */
	public String getDriver() {
		return driver;
	}

	/**
	 * Returns database user
	 *
	 * @return database user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Returns database password
	 *
	 * @return database password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Returns minimal amount of connections that will be used
	 *
	 * @return minimal amount of connections
	 */
	public int getMinConnections() {
		return minConnections;
	}

	/**
	 * Returns maximal amount of connections that will be used @ the same time
	 *
	 * @return maximal amout of connections
	 */
	public int getMaxConnections() {
		return maxConnections;
	}

	/**
	 * Returns pointer to script context descriptor
	 *
	 * @return pointer to script context descriptor
	 */
	public File getScriptContextDescriptor() {
		return scriptContextDescriptor;
	}
}
