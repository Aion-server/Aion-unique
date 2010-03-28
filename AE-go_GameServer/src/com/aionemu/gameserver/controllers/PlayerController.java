/*
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
package com.aionemu.gameserver.controllers;

import java.util.List;
import java.util.concurrent.Future;

import com.aionemu.gameserver.controllers.attack.AttackResult;
import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Monster;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticObject;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.SkillListEntry;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GATHERABLE_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEVEL_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NEARBY_QUESTS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NPC_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PRIVATE_STORE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_CANCEL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.ZoneService.ZoneUpdateMode;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.HealType;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.zone.ZoneInstance;

/**
 * This class is for controlling players.
 * 
 * @author -Nemesiss-, ATracer (2009-09-29), xavier
 * 
 */
public class PlayerController extends CreatureController<Player>
{
	private boolean			isInShutdownProgress;

	/**
	 * Zone update mask
	 */
	private volatile byte	zoneUpdateMask;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void see(VisibleObject object)
	{
		super.see(object);
		if(object instanceof Player)
		{
			PacketSendUtility.sendPacket(getOwner(), new SM_PLAYER_INFO((Player) object, isEnemy((Player) object)));
			getOwner().getEffectController().sendEffectIconsTo((Player) object);
		}
		else if(object instanceof Npc)
		{
			boolean update = false;
			Npc npc = ((Npc) object);
			PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO(npc, getOwner()));
			for(int questId : sp.getQuestEngine().getNpcQuestData(npc.getNpcId()).getOnQuestStart())
			{
				if(sp.getQuestService().checkStartCondition(new QuestEnv(object, getOwner(), questId, 0)))
				{
					if(!getOwner().getNearbyQuests().contains(questId))
					{
						update = true;
						getOwner().getNearbyQuests().add(questId);
					}
				}
			}
			if(update)
				updateNearbyQuestList();
		}
		else if(object instanceof Gatherable || object instanceof StaticObject)
		{
			PacketSendUtility.sendPacket(getOwner(), new SM_GATHERABLE_INFO(object));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notSee(VisibleObject object)
	{
		super.notSee(object);
		if(object instanceof Npc)
		{
			boolean update = false;
			for(int questId : sp.getQuestEngine().getNpcQuestData(((Npc) object).getNpcId()).getOnQuestStart())
			{
				if(sp.getQuestService().checkStartCondition(new QuestEnv(object, getOwner(), questId, 0)))
				{
					if(getOwner().getNearbyQuests().contains(questId))
					{
						update = true;
						getOwner().getNearbyQuests().remove(getOwner().getNearbyQuests().indexOf(questId));
					}
				}
			}
			if(update)
				updateNearbyQuestList();
			
			if(((Npc)object).getLifeStats().isAlreadyDead())
				return;
		}
		else if(object instanceof Gatherable && !((Gatherable)object).isSpawned())
			return;

		PacketSendUtility.sendPacket(getOwner(), new SM_DELETE(object, 0));
	}

	public void updateNearbyQuests()
	{
		getOwner().getNearbyQuests().clear();
		for(VisibleObject obj : getOwner().getKnownList())
		{
			if(obj instanceof Npc)
			{
				for(int questId : sp.getQuestEngine().getNpcQuestData(((Npc) obj).getNpcId()).getOnQuestStart())
				{
					if(sp.getQuestService().checkStartCondition(new QuestEnv(obj, getOwner(), questId, 0)))
					{
						if(!getOwner().getNearbyQuests().contains(questId))
						{
							getOwner().getNearbyQuests().add(questId);
						}
					}
				}
			}
		}
		updateNearbyQuestList();
	}

	/**
	 * Will be called by ZoneManager when player enters specific zone
	 * 
	 * @param zoneInstance
	 */
	public void onEnterZone(ZoneInstance zoneInstance)
	{
		sp.getQuestEngine()
			.onEnterZone(new QuestEnv(null, this.getOwner(), 0, 0), zoneInstance.getTemplate().getName());
	}

	/**
	 * Will be called by ZoneManager when player leaves specific zone
	 * 
	 * @param zoneInstance
	 */
	public void onLeaveZone(ZoneInstance zoneInstance)
	{

	}

	/**
	 * Set zone instance as null (Where no zones defined)
	 */
	public void resetZone()
	{
		getOwner().setZoneInstance(null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Should only be triggered from one place (life stats)
	 */
	@Override
	public void onDie(Creature lastAttacker)
	{		
		Player player = this.getOwner();


		if(lastAttacker instanceof Player)
		{

			if(sp.getDuelService().isDueling(lastAttacker.getObjectId()))
			{
				sp.getDuelService().onDie(player, (Player) lastAttacker);
				return;
			}
			else
			{
				doReward(lastAttacker);
			}
		}

		super.onDie(lastAttacker);
		
		if(lastAttacker instanceof Npc)
		{
			if(player.getLevel() > 4)
				player.getCommonData().calculateExpLoss();
		}

		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 13, 0, lastAttacker == null ? 0 : lastAttacker
			.getObjectId()), true);
		PacketSendUtility.sendPacket(player, new SM_DIE(ReviveType.BIND_REVIVE));
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.DIE);
		sp.getQuestEngine().onDie(new QuestEnv(null, player, 0, 0));
	}

	@Override
	public void doReward(Creature creature)
	{
		sp.getAbyssService().doReward(getOwner(), (Player) creature);
	}

	@Override
	public void attackTarget(int targetObjectId)
	{
		super.attackTarget(targetObjectId);

		Player player = getOwner();

		if(!player.canAttack())
			return;

		PlayerGameStats gameStats = player.getGameStats();

		Creature target = (Creature) sp.getWorld().findAionObject(targetObjectId);

		// check player attack Z distance
		if(Math.abs(player.getZ() - target.getZ()) > 6)
			return;

		if(!RestrictionsManager.canAttack(player, target))
			return;

		List<AttackResult> attackResult = AttackUtil.calculateAttackResult(player, target);

		int damage = 0;
		for(AttackResult result : attackResult)
		{
			damage += result.getDamage();
		}

		long time = System.currentTimeMillis();
		int attackType = 0; // TODO investigate attack types
		PacketSendUtility.broadcastPacket(player, new SM_ATTACK(player, target, gameStats.getAttackCounter(),
			(int) time, attackType, attackResult), true);

		target.getController().onAttack(player, damage);

		gameStats.increaseAttackCounter();
	}

	@Override
	public void onAttack(Creature creature, int skillId, TYPE type, int damage)
	{
		if(getOwner().getLifeStats().isAlreadyDead())
			return;

		super.onAttack(creature, skillId, type, damage);

		if(getOwner().isInvul())
			damage = 0;

		getOwner().getLifeStats().reduceHp(damage, creature);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_ATTACK_STATUS(getOwner(), type, skillId, damage), true);
	}

	/**
	 * 
	 * @param skillId
	 */
	public void useSkill(int skillId)
	{
		Player player = getOwner();

		Skill skill = SkillEngine.getInstance().getSkillFor(player, skillId, player.getTarget());
		if(skill != null)
		{
			if(!RestrictionsManager.canUseSkill(player, skill))
				return;

			// later differentiate between skills
			getOwner().getObserveController().notifyAttackObservers();

			skill.useSkill();
		}
	}

	@Override
	public void onMove()
	{
		super.onMove();
		addZoneUpdateMask(ZoneUpdateMode.ZONE_UPDATE);
	}

	@Override
	public void onStopMove()
	{
		super.onStopMove();
	}

	@Override
	public void onStartMove()
	{
		if(this.getOwner().isCasting())
		{
			this.getOwner().setCasting(null);
			PacketSendUtility.sendPacket(this.getOwner(), new SM_SKILL_CANCEL(this.getOwner()));
			PacketSendUtility.sendPacket(this.getOwner(), SM_SYSTEM_MESSAGE.STR_SKILL_CANCELED());
		}
		super.onStartMove();
	}

	/**
	 * 
	 */
	public void updatePassiveStats()
	{
		Player player = getOwner();
		for(SkillListEntry skillEntry : player.getSkillList().getAllSkills())
		{
			Skill skill = SkillEngine.getInstance().getSkillFor(player, skillEntry.getSkillId(), player.getTarget());
			if(skill != null && skill.isPassive())
			{
				skill.useSkill();
			}
		}
	}

	@Override
	public Player getOwner()
	{
		return (Player) super.getOwner();
	}

	@Override
	public void onRestore(HealType healType, int value)
	{
		super.onRestore(healType, value);
		switch(healType)
		{
			case DP:
				getOwner().getCommonData().addDp(value);
				break;
		}
	}
	
	/**
	 * Player enemies:<br>
	 * - different race<br>
	 * - duel partner<br>
	 * 
	 * @param player
	 * @return
	 */
	public boolean isEnemy(Player player)
	{
		return player.getCommonData().getRace() != getOwner().getCommonData().getRace()
			|| sp.getDuelService().isDueling(player.getObjectId(), getOwner().getObjectId());
	}

	/**
	 * Npc enemies:<br>
	 * - monsters<br>
	 * - aggressive npcs<br>
	 * @param npc
	 * @return
	 */
	public boolean isEnemy(Npc npc)
	{
		return npc instanceof Monster || npc.isAggressiveTo(getOwner().getCommonData().getRace());
	}

	public void updateNearbyQuestList()
	{
		getOwner().addPacketBroadcastMask(BroadcastMode.UPDATE_NEARBY_QUEST_LIST);
	}

	public void updateNearbyQuestListImpl()
	{
		PacketSendUtility.sendPacket(getOwner(), new SM_NEARBY_QUESTS(getOwner().getNearbyQuests()));
	}

	public boolean isInShutdownProgress()
	{
		return isInShutdownProgress;
	}

	public void setInShutdownProgress(boolean isInShutdownProgress)
	{
		this.isInShutdownProgress = isInShutdownProgress;
	}

	/**
	 * Handle dialog
	 */
	public void onDialogSelect(int dialogId, Player player, int questId)
	{
		switch(dialogId)
		{
			case 2:
				PacketSendUtility.sendPacket(player, new SM_PRIVATE_STORE(getOwner().getStore()));
				break;
		}
	}

	/**
	 * @param level
	 */
	public void upgradePlayer(int level)
	{
		Player player = getOwner();

		PlayerStatsTemplate statsTemplate = sp.getPlayerService().getPlayerStatsData().getTemplate(player);
		player.setPlayerStatsTemplate(statsTemplate);
		// update stats after setting new template
		player.getGameStats().doLevelUpgrade();
		player.getLifeStats().synchronizeWithMaxStats();

		PacketSendUtility.broadcastPacket(player, new SM_LEVEL_UPDATE(player.getObjectId(), 0, level), true);

		// Temporal
		ClassChangeService.showClassChangeDialog(player);

		sp.getQuestEngine().onLvlUp(new QuestEnv(null, player, 0, 0));
		updateNearbyQuests();
		
		PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));

		// add new skills
		sp.getSkillLearnService().addNewSkills(player, false);
		if(level == 10)
		{
			player.getSkillList().removeSkill(30001);
			PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player));
		}

		/** update member list packet if player is legion member **/
		if(player.isLegionMember())
			sp.getLegionService().updateMemberInfo(player);
	}

	/**
	 * After entering game player char is "blinking" which means that it's in under some protection, after making an
	 * action char stops blinking. - Starts protection active - Schedules task to end protection
	 */
	public void startProtectionActiveTask()
	{
		getOwner().setVisualState(CreatureVisualState.BLINKING);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_PLAYER_STATE(getOwner()), true);
		Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable(){

			@Override
			public void run()
			{
				stopProtectionActiveTask();
			}
		}, 60000);
		addTask(TaskId.PROTECTION_ACTIVE.ordinal(), task);
	}

	/**
	 * Stops protection active task after first move or use skill
	 */
	public void stopProtectionActiveTask()
	{
		cancelTask(TaskId.PROTECTION_ACTIVE.ordinal());
		Player player = getOwner();
		if(player != null && player.isSpawned())
		{
			player.unsetVisualState(CreatureVisualState.BLINKING);
			PacketSendUtility.broadcastPacket(player, new SM_PLAYER_STATE(player), true);
		}
	}

	/**
	 * When player arrives at destination point of flying teleport
	 */
	public void onFlyTeleportEnd()
	{
		Player player = getOwner();
		player.unsetState(CreatureState.FLIGHT_TELEPORT);
		player.setState(CreatureState.ACTIVE);
		addZoneUpdateMask(ZoneUpdateMode.ZONE_REFRESH);
	}

	/**
	 * Zone update mask management
	 * 
	 * @param mode
	 */
	public final void addZoneUpdateMask(ZoneUpdateMode mode)
	{
		zoneUpdateMask |= mode.mask();
		sp.getZoneService().add(getOwner());
	}

	public final void removeZoneUpdateMask(ZoneUpdateMode mode)
	{
		zoneUpdateMask &= ~mode.mask();
	}

	public final byte getZoneUpdateMask()
	{
		return zoneUpdateMask;
	}

	/**
	 * Update zone taking into account the current zone
	 */
	public void updateZoneImpl()
	{
		sp.getZoneService().checkZone(getOwner());
	}

	/**
	 * Refresh completely zone irrespective of the current zone
	 */
	public void refreshZoneImpl()
	{
		sp.getZoneService().findZoneInCurrentMap(getOwner());
	}

	/**
	 * 
	 */
	public void ban()
	{
		// sp.getTeleportService().teleportTo(this.getOwner(), 510010000, 256f, 256f, 49f, 0);
	}

	/**
	 * Check water level (start drowning) and map death level (die)
	 */
	public void checkWaterLevel()
	{
		Player player = getOwner();
		World world = sp.getWorld();
		float z = player.getZ();
		
		if(player.getLifeStats().isAlreadyDead())
			return;
		
		if(z < world.getWorldMap(player.getWorldId()).getDeathLevel())
		{
			die();
			return;
		}
		
		ZoneInstance currentZone = player.getZoneInstance();
		if(currentZone != null && currentZone.isBreath())
			return;
		
		//TODO need fix character height
		float playerheight = player.getPlayerAppearance().getHeight() * 1.6f;
		if(z < world.getWorldMap(player.getWorldId()).getWaterLevel() - playerheight)
			sp.getZoneService().startDrowning(player);
		else
			sp.getZoneService().stopDrowning(player);
	}

	/**
	 * 
	 * @param player
	 * @param targetTemplateId
	 * @param recipeId
	 * @param targetObjId
	 * @param items
	 */
	public void startCrafting(int targetTemplateId, int recipeId, int targetObjId)
	{
		sp.getCraftService().startCrafting(getOwner(), targetTemplateId, recipeId, targetObjId);
	}
	
	/**
	 * 
	 * @param player
	 * @param recipetemplate
	 * @param critical
	 */
	public void finishCrafting(RecipeTemplate recipetemplate, boolean critical)
	{
		sp.getCraftService().finishCrafting(getOwner(), recipetemplate, critical);
	}
}