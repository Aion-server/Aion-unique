/*
 * This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.skillengine.model;

import com.aionemu.gameserver.controllers.EffectController;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.effect.EffectTemplate;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 *
 */
public class Effect
{
	private int skillId;

	private int skillLevel;

	private int duration;

	private EffectController controller;

	private EffectTemplate effectTemplate;

	private Creature effectedObject;

	public Effect(int skillId, int skillLevel, int duration, EffectTemplate effectTemplate)
	{
		this.skillId = skillId;
		this.skillLevel = skillLevel;
		this.duration = duration;
		this.effectTemplate = effectTemplate;
	}

	/**
	 * @return the skillId
	 */
	public int getSkillId()
	{
		return skillId;
	}

	/**
	 * @return the skillLevel
	 */
	public int getSkillLevel()
	{
		return skillLevel;
	}

	/**
	 * @return the duration
	 */
	public int getDuration()
	{
		return duration;
	}

	/**
	 * @param controller the controller to set
	 */
	public void setController(EffectController controller)
	{
		this.controller = controller;
		this.effectedObject = controller.getOwner();
	}

	public void startEffect()
	{
		//TODO
		//effectTemplate.perform

		ThreadPoolManager.getInstance().scheduleEffect((new Runnable()
		{
			@Override
			public void run()
			{				
				endEffect();
			}

		}), duration * 1000);
	}


	public void endEffect()
	{
		controller.removeEffect(skillId);
	}
}
