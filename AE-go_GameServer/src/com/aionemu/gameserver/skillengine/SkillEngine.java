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
package com.aionemu.gameserver.skillengine;

import java.io.File;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.condition.ConditionChangeListener;
import com.aionemu.gameserver.skillengine.model.Env;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.world.World;

/**
 * @author ATracer
 *
 */
public class SkillEngine
{
	private static final Logger log = Logger.getLogger(SkillEngine.class);
	
	public static final File SKILL_DESCRIPTOR_FILE = new File("./data/scripts/system/skills.xml");
	
	public static final SkillEngine skillEngine = new SkillEngine();
	
	private World world;
	
	/**
	 * should not be instantiated directly
	 */
	private SkillEngine()
	{	
	}
	
	public void setWorld(World world)
	{
		this.world = world;
	}
	
	public Skill getSkillFor(Player player, int skillId)
	{
		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		
		if(template == null)
			return null;
		
		//player doesn't have such skill
		if(!player.getSkillList().isSkillPresent(skillId))
			return null;
		
		Env env = new Env(player, template, new ConditionChangeListener(), world);
		return new Skill(env);
	}

	public static SkillEngine getInstance()
	{
		return skillEngine;
	}
}
