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
package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.ai.AI;
import com.aionemu.gameserver.controllers.CreatureController;
import com.aionemu.gameserver.controllers.EffectController;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.stats.CreatureGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.CreatureLifeStats;
import com.aionemu.gameserver.model.templates.spawn.SpawnTemplate;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * This class is representing movable objects, its base class for all in game objects that may move
 * 
 * @author -Nemesiss-
 * 
 */
public abstract class Creature extends VisibleObject
{

	/**
	 * Reference to AI
	 */
	protected AI<? extends Creature> ai;

	private CreatureLifeStats<? extends Creature> lifeStats;

	private CreatureGameStats<? extends Creature> gameStats;

	private EffectController effectController;
	
	private CreatureState state;
	
	private boolean isRooted = false;

	private boolean isSleep = false;

	public Creature(int objId, CreatureController<? extends Creature> controller,
		SpawnTemplate spawnTemplate, WorldPosition position)
	{
		super(objId, controller, spawnTemplate, position);
		initializeAi();
	}

	/**
	 * Return CreatureController of this Creature object.
	 * 
	 * @return CreatureController.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CreatureController getController()
	{
		return (CreatureController) super.getController();
	}

	/**
	 * @return the lifeStats
	 */
	public CreatureLifeStats<? extends Creature> getLifeStats()
	{
		return  lifeStats;
	}

	/**
	 * @param lifeStats the lifeStats to set
	 */
	public void setLifeStats(CreatureLifeStats<? extends Creature> lifeStats)
	{
		lifeStats.setOwner(this);
		if(this.lifeStats != null)
		{
			this.lifeStats.cancelRestoreTask();
			this.lifeStats.setOwner(null);
		}
		this.lifeStats = lifeStats;
	}

	/**
	 * @return the gameStats
	 */
	public CreatureGameStats<? extends Creature> getGameStats()
	{
		return gameStats;
	}

	/**
	 * @param gameStats the gameStats to set
	 */
	public void setGameStats(CreatureGameStats<? extends Creature> gameStats)
	{
		this.gameStats = gameStats;
	}

	public abstract byte getLevel();

	public abstract void initializeAi();

	/**
	 * @return the effectController
	 */
	public EffectController getEffectController()
	{
		return effectController;
	}

	/**
	 * @param effectController the effectController to set
	 */
	public void setEffectController(EffectController effectController)
	{
		this.effectController = effectController;
	}

	/**
	 * @return the npcAi
	 */
	public AI<? extends Creature> getAi()
	{
		return ai;
	}

	/**
	 * @param ai the ai to set
	 */
	public void setAi(AI<? extends Creature> ai)
	{
		this.ai = ai;
	}

	public void setIsRooted(boolean isRooted)
	{
		this.isRooted = isRooted;
	}

	public boolean isRooted()
	{
		return isRooted;
	}

	public void setSleep(boolean isSleep)
	{
		this.isSleep = isSleep;
	}

	public boolean isSleep()
	{
		return isSleep;
	}

	public boolean canPerformMove()
	{
		return !(isRooted || isSleep);
	}

	/**
	 * @return
	 */
	public CreatureState getState()
	{
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(CreatureState state)
	{
		this.state = state;
	}
}
