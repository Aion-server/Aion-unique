/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_WEATHER;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.MapRegion;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.google.inject.Inject;

/**
 * This service in future should schedule job that is changing weather sometimes in region and probably sends to all
 * players
 * 
 * @author ATracer
 * @author Kwazar
 */
public class WeatherService
{
	@Inject
	private World						world;

	private final long					WEATHER_DURATION	= 2 * 60 * 60 * 1000;	// 2 hours

	private final long					CHECK_INTERVAL		= 1 * 2 * 60 * 1000;	// 2 mins

	private Map<WeatherKey, Integer>	worldWeathers;

	public WeatherService()
	{
		worldWeathers = new HashMap<WeatherKey, Integer>();
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable(){
			/*
			 * (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run()
			{
				checkWeathersTime();
			}
		}, CHECK_INTERVAL, CHECK_INTERVAL);
	}

	/**
	 * Key class used to store date of key creation (for rolling weather usage)
	 * 
	 * @author Kwazar
	 * 
	 */
	private class WeatherKey
	{
		/**
		 * Date of creation of the weather for this region
		 */
		private Date		created;
		/**
		 * Region affected to this type of weather (in worldWeathers map)
		 */
		private WorldMap	map;

		/**
		 * Parametered Constructor
		 * 
		 * @param date
		 *            creation date
		 * @param worldMap
		 *            map to link
		 */
		public WeatherKey(Date date, WorldMap worldMap)
		{
			this.created = date;
			this.map = worldMap;
		}

		/**
		 * @return the map
		 */
		public WorldMap getMap()
		{
			return map;
		}

		/**
		 * Returns <code>true</code> if the key is out of date relating to constant WEATHER_DURATION, <code>false</code>
		 * either
		 * 
		 * @return true or false
		 */
		public boolean isOutDated()
		{
			Date now = new Date();
			long nowTime = now.getTime();
			long createdTime = created.getTime();
			long delta = nowTime - createdTime;
			return (delta > WEATHER_DURATION);
		}

	}

	/**
	 * triggered every CHECK_INTERVAL
	 */
	private void checkWeathersTime()
	{
		List<WeatherKey> toBeRefreshed = new ArrayList<WeatherKey>();
		for(WeatherKey key : worldWeathers.keySet())
		{
			if(key.isOutDated())
			{
				toBeRefreshed.add(key);
			}
		}
		for(WeatherKey key : toBeRefreshed)
		{
			worldWeathers.remove(key);
			onWeatherChange(key.getMap(), null);
		}
	}

	/**
	 * @return a random WeatherType as an integer (0->8)
	 */
	private int getRandomWeather()
	{
		return Rnd.get(0, 8);
	}

	/**
	 * When a player connects, it loads his weather
	 * 
	 * @param player
	 */
	public void loadWeather(Player player)
	{
		WorldMap worldMap = player.getActiveRegion().getParent().getParent();
		onWeatherChange(worldMap, player);
	}

	/**
	 * Return the correct key from the worldWeathers Map by the worldMap
	 * 
	 * @param map
	 * @return
	 */
	private WeatherKey getKeyFromMapByWorldMap(WorldMap map)
	{
		for(WeatherKey key : worldWeathers.keySet())
		{
			if(key.getMap().equals(map))
			{
				return key;
			}
		}
		WeatherKey newKey = new WeatherKey(new Date(), map);
		worldWeathers.put(newKey, getRandomWeather());
		return newKey;
	}

	/**
	 * @param worldMap
	 * @return the WeatherType of the {@link WorldMap} for this session
	 */
	private int getWeatherTypeByRegion(WorldMap worldMap)
	{
		WeatherKey key = getKeyFromMapByWorldMap(worldMap);
		return worldWeathers.get(key).intValue();
	}

	/**
	 * Allows server to reinitialize Weathers for all regions
	 */
	public void resetWeather()
	{
		Set<WeatherKey> loadedWeathers = new HashSet<WeatherKey>(worldWeathers.keySet());
		worldWeathers.clear();
		for(WeatherKey key : loadedWeathers)
		{
			onWeatherChange(key.getMap(), null);
		}
	}

	/**
	 * Allows server to change a specific {@link MapRegion}'s WeatherType
	 * 
	 * @param regionId
	 *            the regionId to be changed of WeatherType
	 * @param weatherType
	 *            the new WeatherType
	 */
	public void changeRegionWeather(int regionId, Integer weatherType)
	{
		WorldMap worldMap = world.getWorldMap(regionId);
		WeatherKey key = getKeyFromMapByWorldMap(worldMap);
		worldWeathers.put(key, weatherType);
		onWeatherChange(worldMap, null);
	}

	/**
	 * 
	 * triggers the update of weather to all players
	 * 
	 * @param world
	 * @param worldMap
	 * @param player
	 *            if null -> weather is broadcasted to all players in world
	 */
	private void onWeatherChange(WorldMap worldMap, Player player)
	{
		if(player == null)
		{
			for(Iterator<Player> playerIterator = world.getPlayersIterator(); playerIterator.hasNext();)
			{
				Player currentPlayer = playerIterator.next();
				WorldMap currentPlayerWorldMap = currentPlayer.getActiveRegion().getParent().getParent();
				if(currentPlayerWorldMap.equals(worldMap))
				{
					PacketSendUtility.sendPacket(currentPlayer, new SM_WEATHER(
						getWeatherTypeByRegion(currentPlayerWorldMap)));
				}
			}
		}
		else
		{
			PacketSendUtility.sendPacket(player, new SM_WEATHER(getWeatherTypeByRegion(worldMap)));
		}
	}
}