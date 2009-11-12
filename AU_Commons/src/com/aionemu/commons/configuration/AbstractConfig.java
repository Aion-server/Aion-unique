package com.aionemu.commons.configuration;

import com.aionemu.commons.utils.PropertiesUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * Abstract class for all configuration files in server.
 * <p/>
 * Author: SoulKeeper
 */
public abstract class AbstractConfig {

	/**
	 * Default load method for properties
	 *
	 * @throws IOException	  if can't read default properties
	 * @throws RuntimeException if something went wrong during property initialization
	 */
	public AbstractConfig() throws IOException, RuntimeException {
		this(null);
	}

	/**
	 * Default load method for properties
	 *
	 * @param properties properties to use. Will use default properties in case of null
	 * @throws IOException	  if can't read default properties
	 * @throws RuntimeException if something went wrong during property initialization
	 */
	public AbstractConfig(Properties properties) throws IOException, RuntimeException {
		if (properties == null) {
			properties = PropertiesUtils.load(System.getProperty(getSystemPropertiesKey(), getDefaultPropertiesLocation()));
		}

		ConfigurableProcessor.process(this, properties);
	}

	/**
	 * Returns system properties key that can be used to specify location of properties file
	 *
	 * @return system properties key
	 */
	protected abstract String getSystemPropertiesKey();

	/**
	 * Returns location of default properties file that will be used in case if system property not found
	 *
	 * @return default properties location
	 */
	protected abstract String getDefaultPropertiesLocation();
}
