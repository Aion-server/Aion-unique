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
package com.aionemu.gameserver.ai.events.handler;

import com.aionemu.gameserver.ai.AI;
import com.aionemu.gameserver.ai.events.Event;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.spawn.SpawnGroup;
import com.aionemu.gameserver.model.templates.spawn.SpawnTemplate;
import com.aionemu.gameserver.model.templates.spawn.SpawnTime;
import com.aionemu.gameserver.utils.gametime.DayTime;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;

/**
 * @author ATracer
 *
 */
public class DayTimeChangeEventHandler extends EventHandler
{
	@Override
	public Event getEvent()
	{
		return Event.DAYTIME_CHANGE;
	}

	@Override
	public void handleEvent(Event event, AI<?> ai)
	{
		Npc owner = (Npc) ai.getOwner();
		SpawnTemplate spawn = owner.getSpawn();

		SpawnGroup group = spawn.getSpawnGroup();
		SpawnTime spawnTime = group.getSpawnTime();
		if(spawnTime == null)
			return;

		int instanceId = owner.getInstanceId();

		DayTime dayTime = GameTimeManager.getGameTime().getDayTime();
		if(spawnTime.isAllowedDuring(dayTime) && spawn.isResting(instanceId))
		{
			owner.getController().scheduleRespawn();
			spawn.setResting(false, instanceId);
		}
		else if(!spawnTime.isAllowedDuring(dayTime) && !spawn.isResting(instanceId))
		{
			owner.getController().onDespawn(true);
			spawn.setResting(true, instanceId);
		}
	}
}
