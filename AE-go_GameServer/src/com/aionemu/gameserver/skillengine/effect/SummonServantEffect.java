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
package com.aionemu.gameserver.skillengine.effect;

import java.util.concurrent.Future;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.dataholders.loadingutils.XmlServiceProxy;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Servant;
import com.aionemu.gameserver.model.templates.spawn.SpawnTemplate;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummonServantEffect")
public class SummonServantEffect extends SummonEffect
{

	@XmlAttribute(name = "skill_id", required = true)
	protected int	skillId;
	@XmlAttribute(name = "hp_ratio", required = true)
	protected int	hpRatio;

	@Override
	public void applyEffect(Effect effect)
	{
		Creature effector = effect.getEffector();
		SpawnEngine spawnEngine = xsp.getSpawnEngine();
		float x = effector.getX();
		float y = effector.getY();
		float z = effector.getZ();
		byte heading = effector.getHeading();
		int worldId = effector.getWorldId();
		int instanceId = effector.getInstanceId();

		SpawnTemplate spawn = spawnEngine.addNewSpawn(worldId, instanceId, npcId, x, y, z, heading, 0, 0, true, true);
		final Servant servant = spawnEngine.spawnServant(spawn, instanceId, effector, skillId, hpRatio);
		
		Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable(){

			@Override
			public void run()
			{
				servant.getController().onDespawn(true);
			}
		}, 60 * 1000);
		servant.getController().addTask(TaskId.DESPAWN, task);
	}

	@Override
	public void calculate(Effect effect)
	{
		super.calculate(effect);
	}

	/**
	 * 
	 * @param u
	 * @param parent
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		xsp = u.getAdapter(XmlServiceProxy.class);
	}

}
