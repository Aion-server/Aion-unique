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
import com.aionemu.gameserver.configs.Config;
import com.aionemu.gameserver.controllers.CreatureController;
import com.aionemu.gameserver.controllers.EffectController;
import com.aionemu.gameserver.controllers.MoveController;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.stats.CreatureGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.CreatureLifeStats;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.spawn.SpawnTemplate;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.taskmanager.PacketBroadcaster;
import com.aionemu.gameserver.taskmanager.PacketBroadcaster.BroadcastMode;
import com.aionemu.gameserver.utils.PacketSendUtility;
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
	private MoveController moveController;
	
	private int state = CreatureState.ACTIVE.getId();
	
	private boolean isRooted;
	private boolean isSleep;
	private boolean isPoisoned;
	private boolean isStumbled;
	private boolean isStunned;
	
	private Skill castingSkill;
	
	private int transformedModelId;

	public Creature(int objId, CreatureController<? extends Creature> controller,
		SpawnTemplate spawnTemplate, VisibleObjectTemplate objectTemplate, WorldPosition position)
	{
		super(objId, controller, spawnTemplate, objectTemplate, position);
		initializeAi();
		this.moveController = new MoveController(this);
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

	

	/**
	 * @return the isRooted
	 */
	public boolean isRooted()
	{
		return isRooted;
	}

	/**
	 * @param isRooted the isRooted to set
	 */
	public void setRooted(boolean isRooted)
	{
		this.isRooted = isRooted;
	}

	/**
	 * @return the isSleep
	 */
	public boolean isSleep()
	{
		return isSleep;
	}

	/**
	 * @param isSleep the isSleep to set
	 */
	public void setSleep(boolean isSleep)
	{
		this.isSleep = isSleep;
	}

	/**
	 * @return the isPoisoned
	 */
	public boolean isPoisoned()
	{
		return isPoisoned;
	}

	/**
	 * @param isPoisoned the isPoisoned to set
	 */
	public void setPoisoned(boolean isPoisoned)
	{
		this.isPoisoned = isPoisoned;
	}

	/**
	 * @return the isStumbled
	 */
	public boolean isStumbled()
	{
		return isStumbled;
	}

	/**
	 * @param isStumbled the isStumbled to set
	 */
	public void setStumbled(boolean isStumbled)
	{
		this.isStumbled = isStumbled;
	}

	/**
	 * @return the isStunned
	 */
	public boolean isStunned()
	{
		return isStunned;
	}

	/**
	 * @param isStunned the isStunned to set
	 */
	public void setStunned(boolean isStunned)
	{
		this.isStunned = isStunned;
	}
	
	public boolean isCasting()
	{
		return castingSkill != null;
	}
	
	public void setCasting(Skill castingSkill)
	{
		this.castingSkill = castingSkill;
	}
	
	public int getCastingSkillId()
	{
		return castingSkill != null ? castingSkill.getSkillTemplate().getSkillId() : 0;
	}
	
	public boolean canPerformMove()
	{
		return !(isRooted || isSleep || isStumbled || isStunned);
	}
	
	public boolean canAttack()
	{
		return !(isSleep || isStunned || isStumbled || isCasting());
	}

	/**
	 * @return
	 */
	public int getState()
	{
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(CreatureState state)
	{
		this.state |= state.getId();
	}

	public void unsetState(CreatureState state)
	{
		this.state &= ~state.getId();
	}

	public boolean isInState(CreatureState state)
	{
		int isState = this.state & state.getId();

		if(isState == state.getId())
			return true;

		return false;
	}

	/**
	 * @return the transformedModelId
	 */
	public int getTransformedModelId()
	{
		return transformedModelId;
	}

	/**
	 * @param transformedModelId the transformedModelId to set
	 */
	public void setTransformedModelId(int transformedModelId)
	{
		this.transformedModelId = transformedModelId;
	}

	/**
	 * @return the moveController
	 */
	public MoveController getMoveController()
	{
		return moveController;
	}
	
	private volatile byte packetBroadcastMask;

	public final void addPacketBroadcastMask(BroadcastMode mode)
	{
		packetBroadcastMask |= mode.mask();

		PacketBroadcaster.getInstance().add(this);
		
		if(Config.DEBUG_PACKET_BROADCASTER)
			PacketSendUtility.sendMessage(((Player)this), "PacketBroadcast: " + mode.name() + " added.");
	}

	public final void removePacketBroadcastMask(BroadcastMode mode)
	{
		packetBroadcastMask &= ~mode.mask();
		
		if(Config.DEBUG_PACKET_BROADCASTER)
			PacketSendUtility.sendMessage(((Player)this), "PacketBroadcast: " + mode.name() + " removed.");
	}

	public final byte getPacketBroadcastMask()
	{
		return packetBroadcastMask;
	}
}
