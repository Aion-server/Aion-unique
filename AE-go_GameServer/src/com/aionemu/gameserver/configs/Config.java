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
package com.aionemu.gameserver.configs;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.aionemu.commons.configuration.ConfigurableProcessor;
import com.aionemu.commons.utils.PropertiesUtils;
import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.main.CacheConfig;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.configs.main.LegionConfig;
import com.aionemu.gameserver.configs.main.RateConfig;
import com.aionemu.gameserver.configs.main.ShutdownConfig;
import com.aionemu.gameserver.configs.main.TaskManagerConfig;
import com.aionemu.gameserver.configs.network.IPConfig;
import com.aionemu.gameserver.configs.network.NetworkConfig;

/**
 * @author -Nemesiss-
 * @author SoulKeeper
 */
public class Config
{
	/**
	 * Logger for this class.
	 */
	protected static final Logger	log				= Logger.getLogger(Config.class);
	
	/**
	 * Initialize all configs in com.aionemu.gameserver.configs package
	 */
	public static void load()
	{
		try
		{
			Properties[] props = PropertiesUtils.loadAllFromDirectory("./config");

			ConfigurableProcessor.process(Config.class, props);
			
			// Administration
			String administration = "./config/administration";
			Properties[] adminProps = PropertiesUtils.loadAllFromDirectory(administration);
			ConfigurableProcessor.process(AdminConfig.class, adminProps);
			log.info("Loading: " + administration + "/admin.properties");
			
			// Main
			String main = "./config/main";
			Properties[] mainProps = PropertiesUtils.loadAllFromDirectory(main);
			ConfigurableProcessor.process(LegionConfig.class, mainProps);
			log.info("Loading: " + main + "/legion.properties");
			ConfigurableProcessor.process(RateConfig.class, mainProps);
			log.info("Loading: " + main + "/rates.properties");
			ConfigurableProcessor.process(CacheConfig.class, mainProps);
			log.info("Loading: " + main + "/cache.properties");
			ConfigurableProcessor.process(ShutdownConfig.class, mainProps);
			log.info("Loading: " + main + "/shutdown.properties");
			ConfigurableProcessor.process(TaskManagerConfig.class, mainProps);
			log.info("Loading: " + main + "/taskmanager.properties");
			ConfigurableProcessor.process(GroupConfig.class, mainProps);
			log.info("Loading: " + main + "/group.properties");
			ConfigurableProcessor.process(CustomConfig.class, mainProps);
			log.info("Loading: " + main + "/custom.properties");
			ConfigurableProcessor.process(GSConfig.class, mainProps);
			log.info("Loading: " + main + "/gameserver.properties");
			
			// Network
			String network = "./config/network";
			Properties[] networkProps = PropertiesUtils.loadAllFromDirectory(network);
			ConfigurableProcessor.process(NetworkConfig.class, networkProps);
			log.info("Loading: " + network + "/database.properties");
			log.info("Loading: " + network + "/network.properties");
		}
		catch(Exception e)
		{
			log.fatal("Can't load gameserver configuration", e);

			throw new Error("Can't load gameserver configuration", e);
		}

		IPConfig.load();
	}
}