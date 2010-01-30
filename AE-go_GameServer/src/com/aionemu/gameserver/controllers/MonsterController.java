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

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.ai.events.Event;
import com.aionemu.gameserver.ai.npcai.MonsterAi;
import com.aionemu.gameserver.ai.state.AIState;
import com.aionemu.gameserver.controllers.attack.AttackResult;
import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Monster;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.stats.CreatureGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.NpcLifeStats;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.DecayService;
import com.aionemu.gameserver.services.RespawnService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.StatFunctions;
import com.aionemu.gameserver.world.World;

/**
 * @author ATracer
 *
 */
public class MonsterController extends NpcController
{
	private static Logger log = Logger.getLogger(MonsterController.class);

	@Override
	public void doDrop(Player player)
	{
		super.doDrop(player);
		/*		PlayerGroup pg = player.getPlayerGroup();
		if(pg != null)
		{
			Player winner = pg.lootDistributionService(player);
			dropService.registerDrop((Monster) getOwner() , winner);
		}
		else*/
		dropService.registerDrop((Monster) getOwner() , player);			
		PacketSendUtility.broadcastPacket(this.getOwner(), new SM_LOOT_STATUS(this.getOwner().getObjectId(), 0));
	}
	
	@Override
	public void doReward(Creature creature)
	{
		super.doReward(creature);

		if(creature instanceof Player)
		{
			Player player = (Player) creature;
			//TODO may be introduce increaseExpBy method in PlayerCommonData
			if(player.getPlayerGroup() == null) //solo
			{
				long currentExp = player.getCommonData().getExp();

				long xpReward = StatFunctions.calculateSoloExperienceReward(player, getOwner());				
				player.getCommonData().setExp(currentExp + xpReward);				

				PacketSendUtility.sendPacket(player,SM_SYSTEM_MESSAGE.EXP(Long.toString(xpReward)));

				//DPreward
				int currentDp = player.getCommonData().getDp();
				int dpReward = StatFunctions.calculateSoloDPReward(player, getOwner());
				player.getCommonData().setDp(dpReward + currentDp);

			}
			else
			{
				Iterator pgit = player.getPlayerGroup().getGroupMemberIterator();
				while(pgit.hasNext())
				{
					Player member = (Player)pgit.next();
					long currentExp = member.getCommonData().getExp();

					long xpReward = StatFunctions.calculateGroupExperienceReward(member, getOwner());
					member.getCommonData().setExp(currentExp + xpReward);

					PacketSendUtility.sendPacket(member,SM_SYSTEM_MESSAGE.EXP(Long.toString(xpReward)));

					//DPreward
					int currentDp = member.getCommonData().getDp();
					int dpReward = StatFunctions.calculateGroupDPReward(member, getOwner());
					member.getCommonData().setDp(dpReward + currentDp);

				}
			}
			//TODO group quest, and group member's quests
			QuestEngine.getInstance().onKill(new QuestEnv(getOwner(), player, 0 , 0));
		}
	}

	@Override
	public void onAttack(Creature creature, int skillId, TYPE type,  int damage)
	{
		//temp fix for XP farming from dead mobs
		if(getOwner().getLifeStats().isAlreadyDead())
			return;

		Monster monster = getOwner();
		if (creature instanceof Player)
			if (QuestEngine.getInstance().onAttack(new QuestEnv(monster, (Player)creature, 0 , 0)))
				return;
		monster.getAggroList().addDamageHate(creature, damage, 0);
		monster.getLifeStats().reduceHp(damage);

		MonsterAi monsterAi = monster.getAi();
		monsterAi.handleEvent(Event.ATTACKED);
		PacketSendUtility.broadcastPacket(monster, new SM_ATTACK_STATUS(monster, type, skillId, damage));
	}

	public void attackTarget(int targetObjectId)
	{
		Monster monster = getOwner();

		if (monster == null || monster.getLifeStats().isAlreadyDead())
			return;

		MonsterAi monsterAi = monster.getAi();
		CreatureGameStats<? extends Creature> npcGameStats = monster.getGameStats();

		int attackType = 0; //TODO investigate attack types	(0 or 1)

		World world = monster.getActiveRegion().getWorld();
		//TODO refactor to possibility npc-npc fight
		Player player = (Player) world.findAionObject(targetObjectId);
		//if player disconnected - IDLE state
		if(player == null || player.getLifeStats().isAlreadyDead())
		{
			monsterAi.setAiState(AIState.THINKING);
			return;
		}

		List<AttackResult> attackList = AttackUtil.calculateAttackResult(monster, player);

		int damage = 0;
		for(AttackResult result : attackList)
		{
			damage += result.getDamage();
		}

		//wtf is 274 - invetigate
		PacketSendUtility.broadcastPacket(player,
			new SM_ATTACK(monster.getObjectId(), player.getObjectId(),
				npcGameStats.getAttackCounter(), 274, attackType, attackList), true);

		player.getController().onAttack(monster, damage);
		npcGameStats.increaseAttackCounter();

	}

	@Override
	public void onDie()
	{
		super.onDie();
		//TODO move to creature controller after duel will be moved out of onDie
		this.getOwner().setState(CreatureState.DEAD);
		//TODO change - now reward is given to target only
		Player target = (Player) this.getOwner().getTarget();

		PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), 13, 0, target == null?0:target.getObjectId()));

		if(target == null)
			target = (Player) getOwner().getAggroList().getMostHated();//TODO based on damage;

		this.doReward(target);
		this.doDrop(target);
		this.getOwner().getAi().handleEvent(Event.DIED);
		
		if(decayTask == null)
		{
			decayTask = DecayService.getInstance().scheduleDecayTask(this.getOwner());
			RespawnService.getInstance().scheduleRespawnTask(this.getOwner());
		}	

		//deselect target at the end
		getOwner().setTarget(null);
	}

	@Override
	public void onRespawn()
	{
		super.onRespawn();
		dropService.unregisterDrop(getOwner());
		getOwner().getAggroList().clear();
		this.getOwner().getAi().handleEvent(Event.RESPAWNED);
	}

	@Override
	public Monster getOwner()
	{
		return (Monster) super.getOwner();
	}

	@Override
	public boolean isAttackable()
	{
		return true;
	}

	@Override
	public void notSee(VisibleObject object)
	{
		super.notSee(object);
		if (object instanceof Creature)
			getOwner().getAggroList().remove((Creature)object);
		if(object instanceof Player)
			getOwner().getAi().handleEvent(Event.NOT_SEE_PLAYER);
	}

	@Override
	public void see(VisibleObject object)
	{
		super.see(object);
		if(object instanceof Player)
			getOwner().getAi().handleEvent(Event.SEE_PLAYER);
	}
}