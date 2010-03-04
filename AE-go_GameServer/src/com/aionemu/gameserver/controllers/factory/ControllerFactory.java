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
package com.aionemu.gameserver.controllers.factory;

import com.aionemu.gameserver.controllers.ActionitemController;
import com.aionemu.gameserver.controllers.BindpointController;
import com.aionemu.gameserver.controllers.CitizenController;
import com.aionemu.gameserver.controllers.GatherableController;
import com.aionemu.gameserver.controllers.MonsterController;
import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.controllers.PlayerController;
import com.aionemu.gameserver.controllers.PostboxController;
import com.aionemu.gameserver.controllers.RiftController;
import com.aionemu.gameserver.controllers.StaticObjectController;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.google.inject.Inject;

/**
 * @author ATracer
 *
 */
public class ControllerFactory
{
	@Inject
	private ActionitemControllerFactory actionitemControllerFactory;	
	@Inject
	private BindpointControllerFactory bindpointControllerFactory;	
	@Inject
	private CitizenControllerFactory citizenControllerFactory;	
	@Inject
	private GatherableControllerFactory gatherableControllerFactory;
	@Inject
	private MonsterControllerFactory monsterControllerFactory;
	@Inject
	private NpcControllerFactory npcControllerFactory;
	@Inject
	private PlayerControllerFactory playerControllerFactory;
	@Inject
	private PostboxControllerFactory postboxControllerFactory;
	@Inject
	private RiftControllerFactory riftControllerFactory;
	@Inject
	private StaticObjectControllerFactory staticObjectControllerFactory;
	
	/**
	 * 
	 * @return actionitemControllerFactory
	 */
	public ActionitemController createActionitemController()
	{
		return actionitemControllerFactory.create();
	}
	
	/**
	 * 
	 * @return bindpointControllerFactory
	 */
	public BindpointController createBindpointController()
	{
		return bindpointControllerFactory.create();
	}
	
	/**
	 * @return citizenControllerFactory
	 */
	public CitizenController createCitizenController()
	{
		return citizenControllerFactory.create();
	}
	
	/**
	 * @return gatherableControllerFactory
	 */
	public GatherableController createGatherableController()
	{
		return gatherableControllerFactory.create();
	}
	
	/**
	 * @return monsterControllerFactory
	 */
	public MonsterController createMonsterController()
	{
		return monsterControllerFactory.create();
	}
	
	/**
	 * @return npcControllerFactory
	 */
	public NpcController createNpcController()
	{
		return npcControllerFactory.create();
	}
	
	/**
	 * @return playerControllerFactory
	 */
	public PlayerController createPlayerController()
	{
		return playerControllerFactory.create();
	}
	
	/**
	 * @return postboxControllerFactory
	 */
	public PostboxController createPostboxController()
	{
		return postboxControllerFactory.create();
	}
	
	/**
	 * 
	 * @param slave
	 * @param maxEntries
	 * @param maxLevel
	 * @return riftControllerFactory
	 */
	public RiftController createRiftController(Npc slave, int maxEntries, int maxLevel)
	{
		return riftControllerFactory.create(slave, maxEntries, maxLevel);
	}
	
	/**
	 * @return staticObjectControllerFactory
	 */
	public StaticObjectController createStaticObjectController()
	{
		return staticObjectControllerFactory.create();
	}
}
