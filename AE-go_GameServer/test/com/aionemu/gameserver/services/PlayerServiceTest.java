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

package com.aionemu.gameserver.services;

import org.junit.Before;
import org.junit.Test;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.services.LoggingService;
import com.aionemu.gameserver.configs.Config;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;

/**
 * Test for PlayerService
 *
 * @author EvilSpirit
 */
public class PlayerServiceTest
{
	@Before
	public void init()
	{
		System.out.println("Loading config and database");
		LoggingService.init();
		Config.load();
		DatabaseFactory.init();
	}

    /**
     * Test for nickname validation. It should have from 2 to 10
     * english letters (as retail)
     */
	@Test
	public void testNicknameValidation()
	{
		String[]	names = new String[]{"", "A", "Ab", "Noname", "Русский", "Name1", "Your_name", "@Nome"};

		System.out.println("Testing PlayerService name validator.. ");

		PlayerService playerService = new PlayerService(null, null);
		for(String name : names)
		{
			if(playerService.isValidName(name))
			{
				System.out.println("Name " + name + " is valid");
			}
			else
			{
				System.out.println("Name " + name + " is invalid");
			}
		}
	}

}
