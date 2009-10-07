/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
package com.aionemu.gameserver.skillengine.handlers;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.SkillHandler;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 */
public class TemplateSkillHandler extends SkillHandler
{
	
	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.skillengine.SkillHandler#useSkill(com.aionemu.gameserver.model.gameobjects.Creature)
	 */
	@Override
	public void useSkill(Creature creature)
	{
		startUsage(creature);
	}

	/**
	 * SkillEffectPart.START_EFFECT
	 */
	protected void startUsage(Creature creature){};

	/**
	 * SkillEffectPart.ACTION
	 */
	protected void performAction(Creature creature){};

	/**
	 * @param delay
	 */
	protected void schedulePerformAction(final Creature creature, int delay)
	{
		ThreadPoolManager.getInstance().schedule(new Runnable() 
		{
			public void run() 
			{
				performAction(creature);
			}   
		}, delay);
	}


}
