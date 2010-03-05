/*
 * This file is part of aion-unique <aion-unique.com>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package admincommands;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.WeatherService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.WorldMapType;
import com.google.inject.Inject;

/**
 * Admin command allowing to change weathers of the world.
 * 
 * @author Kwazar
 * 
 */

public class Weather extends AdminCommand
{

	@Inject
	private WeatherService		weatherService;

	private final static String	RESET	= "reset";

	private final static String	COMMAND	= "weather";

	public Weather()
	{
		super(COMMAND);
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		// Check restriction level
		if(admin.getAccessLevel() < AdminConfig.COMMAND_WEATHER)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
			return;
		}

		if(params.length == 0 || params.length > 2)
		{
			// Syntax :
			// - //weather poeta 0 -> to set clear sky in this region
			// - //weather reset -> to change randomly all weathers in the world

			PacketSendUtility.sendMessage(admin, "syntax //" + COMMAND
				+ "<regionName(poeta, ishalgen, etc ...)> <value(0->8)> OR //" + COMMAND + " " + RESET + "");
			return;
		}

		String regionName = null;
		int weatherType = -1;

		regionName = new String(params[0]);

		if(params.length == 2)
		{
			try
			{
				weatherType = Integer.parseInt(params[1]);
			}
			catch(NumberFormatException e)
			{
				PacketSendUtility.sendMessage(admin, "weather type parameter need to be an integer [0-8].");
				return;
			}
		}

		if(regionName.equals(RESET))
		{
			weatherService.resetWeather();
			return;
		}

		// Retrieving regionId by name
		WorldMapType region = null;
		for(WorldMapType worldMapType : WorldMapType.values())
		{
			if(worldMapType.name().toLowerCase().equals(regionName.toLowerCase()))
			{
				region = worldMapType;
			}
		}

		if(region != null)
		{
			if(weatherType > -1 && weatherType < 9)
			{
				weatherService.changeRegionWeather(region.getId(), new Integer(weatherType));
			}
			else
			{
				PacketSendUtility.sendMessage(admin, "Weather type must be between 0 and 8");
				return;
			}
		}
		else
		{
			PacketSendUtility.sendMessage(admin, "Region " + regionName + " not found");
			return;
		}

	}
}
