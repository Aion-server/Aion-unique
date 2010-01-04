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

import javolution.util.FastMap;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.ai.AIState;
import com.aionemu.gameserver.ai.events.AttackEvent;
import com.aionemu.gameserver.ai.npcai.MonsterAi;
import com.aionemu.gameserver.controllers.attack.AttackResult;
import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Monster;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.CreatureGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.CreatureLifeStats;
import com.aionemu.gameserver.model.gameobjects.stats.NpcLifeStats;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
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

	public final class AggroInfo
	{
		protected Creature _attacker;
		protected int _hate;
		protected int _damage;

		AggroInfo(Creature pAttacker)
		{
			_attacker = pAttacker;
		}
	}

	private FastMap<Creature, AggroInfo> _aggroList = new FastMap<Creature, AggroInfo>().setShared(true);

	public final FastMap<Creature, AggroInfo> getAggroList()
	{
		return _aggroList;
	}

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
				}
			}
			//TODO group quest, and group member's quests
			QuestEngine.getInstance().onKill(new QuestEnv(getOwner(), player, 0 , 0));
		}
	}

	@Override
	public boolean onAttack(Creature creature)
	{
		super.onAttack(creature);

		Monster monster = getOwner();
		CreatureLifeStats<? extends Creature> lifeStats = monster.getLifeStats();

		if(lifeStats.isAlreadyDead())
		{
			return false;
		}

		MonsterAi monsterAi = monster.getAi();
		monsterAi.handleEvent(new AttackEvent(creature));

		return true;
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
		if(player == null)
		{
			monsterAi.setAiState(AIState.IDLE);
		}

		boolean attackSuccess = player.getController().onAttack(monster);

		if(attackSuccess)
		{
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
			
			player.getLifeStats().reduceHp(damage);
			npcGameStats.increaseAttackCounter();
		}
		
		if(player.getLifeStats().isAlreadyDead())
		{
			monsterAi.setAiState(AIState.IDLE);
		}
	}

	@Override
	public void onDie()
	{
		super.onDie();

		MonsterAi monsterAi = getOwner().getAi();
		monsterAi.setAiState(AIState.NONE);

		//TODO change - now reward is given to target only
		Player target = (Player) this.getOwner().getTarget();

		PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(this.getOwner().getObjectId(), 13, 0, target == null?0:target.getObjectId()));

		this.doReward(target);
		this.doDrop(target);

		if(decayTask == null)
		{
			RespawnService.getInstance().scheduleRespawnTask(this.getOwner());
			decayTask = DecayService.getInstance().scheduleDecayTask(this.getOwner());
		}	

		//deselect target at the end
		getOwner().setTarget(null);
	}

	@Override
	public void onRespawn()
	{
		this.decayTask = null;
		dropService.unregisterDrop(getOwner());
		getAggroList().clear();
		this.getOwner().setLifeStats(new NpcLifeStats(getOwner()));
		if (this.getOwner().hasWalkRoutes() || this.getOwner().isAggressive())
		{
			this.getOwner().getAi().setAiState(AIState.ACTIVE);
		}
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

	public void addDamageHate(Creature attacker, int damage, int aggro)
	{
		if (attacker == null)
			return;

		AggroInfo ai = getAggroList().get(attacker);
		if (ai == null)
		{
			ai = new AggroInfo(attacker);
			getAggroList().put(attacker, ai);

			ai._damage = 0;
			ai._hate = 0;
		}
		ai._damage += damage;

		if (aggro == 0)
			ai._hate++;
		else
			ai._hate += aggro;
	}

	public Creature getMostHated()
	{
		if (getAggroList().isEmpty() || getOwner().getLifeStats().isAlreadyDead()) return null;

		Creature mostHated = null;
		int maxHate = 0;

		synchronized (getAggroList())
		{
			for (AggroInfo ai : getAggroList().values())
			{
				if (ai == null)
					continue;

				if
				(
					ai._attacker.getLifeStats().isAlreadyDead()
				)
					ai._hate = 0;

				if (ai._hate > maxHate)
				{
					mostHated = ai._attacker;
					maxHate = ai._hate;
				}
			}
		}
		return mostHated;
	}

	@Override
	public void notSee(VisibleObject object)
	{
		super.notSee(object);
		if (object instanceof Creature)
			getAggroList().remove((Creature)object);
	}
}