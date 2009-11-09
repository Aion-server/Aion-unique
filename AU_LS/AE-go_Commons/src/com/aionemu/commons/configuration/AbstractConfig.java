package com.aionemu.commons.configuration;

import com.aionemu.commons.utils.PropertiesUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * Abstract class for all configuration files in server.
 *
 * Author: SoulKeeper
 */
public abstract class AbstractConfig {

	/**
	 * Returns system properties key that can be used to specify location of properties file
	 * @return system properties key
	 */
	protected abstract String getSystemPropertiesKey();

	/**
	 * Returns location of default properties file that will be used in case if system property not found
	 * @return default properties location
	 */
	protected abstract String getDefaultPropertiesLocation();

	/**
	 * Default load method for properties
	 * @throws IOException if can't read given properties
	 */
	public void load() throws IOException {
		PropertiesUtils.load(System.getProperty(getSystemPropertiesKey(), getDefaultPropertiesLocation()));
	}

	/**
	 * Actually reads properties and sets values
	 * @param properties properties to read
	 */
	public void load(Properties properties) {
		ConfigurableProcessor.process(this, properties);
	}
}
