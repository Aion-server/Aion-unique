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
package com.aionemu.gameserver.skillengine;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aionemu.commons.scripting.scriptmanager.ScriptManager;
import com.aionemu.gameserver.GameServerError;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.SkillTemplate;
import com.aionemu.gameserver.skillengine.handlers.GeneralSkillHandlerFactory;
import com.aionemu.gameserver.skillengine.loader.SkillHandlerLoader;
import com.aionemu.gameserver.skillengine.model.SkillHandlerType;
import com.google.inject.Injector;

/**
 * @author ATracer
 *
 */
public class SkillEngine
{
	private static final Logger log = Logger.getLogger(SkillEngine.class);
	
	public static final File SKILL_DESCRIPTOR_FILE = new File("./data/scripts/system/skills.xml");
	
	public static final SkillEngine skillEngine = new SkillEngine();
	
	private static final Map<Integer, SkillHandler> skillHandlers = new HashMap<Integer, SkillHandler>();

	/**
	 * should not be instantiated directly
	 */
	private SkillEngine()
	{	
	}
	
	public void registerSkill(SkillHandler skillHandler)
	{
		int skillId = skillHandler.getSkillId();
		skillHandler.setSkillTemplate(DataManager.SKILL_DATA.getSkillTemplate(skillId));
		skillHandlers.put(skillId, skillHandler);
	}
	
	public int getHandlersCount()
	{
		return skillHandlers.size();
	}
	
	public SkillHandler getSkillHandlerFor(int skillId)
	{
		SkillHandler skillHandler =  skillHandlers.get(skillId);
		if(skillHandler == null)
		{
			log.info("There is no skill handler for skillId: " + skillId);
			return null;
		}
		return skillHandler;
	}
	
	public void registerAllSkills(Injector injector)
	{
		loadCustomHandlers(injector);		
		loadGeneralHandlers();
		log.info("Loaded " + skillHandlers.size() + " skill handlers");
	}

	private void loadCustomHandlers(Injector injector) throws GameServerError
	{
		ScriptManager sm = new ScriptManager();

		sm.setGlobalClassListener(new SkillHandlerLoader(injector, this));

		try
		{
			sm.load(SKILL_DESCRIPTOR_FILE);
		}
		catch (Exception e)
		{
			throw new GameServerError("Can't initialize skill handlers.", e);
		}
	}
	
	/**
	 * General Handlers will be loaded after custom java handlers and will not override them
	 */
	private void loadGeneralHandlers()
	{
		List<SkillTemplate> skillTemplates= DataManager.SKILL_DATA.getSkillTemplates();
		for(SkillTemplate skillTemplate : skillTemplates)
		{
			int skillId = skillTemplate.getSkillId();
			
			if(!skillHandlers.keySet().contains(skillId))
			{
				SkillHandlerType skillHandlerType = 
					SkillHandlerType.valueOf(skillTemplate.getHandlerType());
				
				SkillHandler skillHandler = GeneralSkillHandlerFactory.createSkillHandler(skillHandlerType);
				skillHandler.setSkillTemplate(skillTemplate);
				skillHandler.setSkillId(skillId);
				skillHandlers.put(skillId, skillHandler);
			}
		}
	}
	
	public static SkillEngine getInstance()
	{
		return skillEngine;
	}
}
