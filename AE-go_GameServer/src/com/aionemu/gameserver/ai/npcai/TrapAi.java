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
package com.aionemu.gameserver.ai.npcai;

import com.aionemu.gameserver.ai.AI;
import com.aionemu.gameserver.ai.desires.AbstractDesire;
import com.aionemu.gameserver.ai.events.Event;
import com.aionemu.gameserver.ai.events.handler.EventHandler;
import com.aionemu.gameserver.ai.state.AIState;
import com.aionemu.gameserver.ai.state.handler.StateHandler;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Trap;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * @author ATracer
 * 
 */
public class TrapAi extends NpcAi
{
	public TrapAi()
	{
		/**
		 * Event handlers
		 */
		this.addEventHandler(new SeeObjectEventHandler());

		/**
		 * State handlers
		 */
		this.addStateHandler(new ActiveTrapStateHandler());
	}

	public class SeeObjectEventHandler implements EventHandler
	{
		@Override
		public Event getEvent()
		{
			return Event.SEE_CREATURE;
		}

		@Override
		public void handleEvent(Event event, AI<?> ai)
		{
			ai.setAiState(AIState.ACTIVE);
			if(!ai.isScheduled())
				ai.analyzeState();
		}

	}

	class ActiveTrapStateHandler extends StateHandler
	{
		@Override
		public AIState getState()
		{
			return AIState.ACTIVE;
		}

		@Override
		public void handleState(AIState state, AI<?> ai)
		{
			ai.clearDesires();
			Trap owner = (Trap) ai.getOwner();
			Creature trapCreator = owner.getCreator();

			int enemyCount = 0;
			for(VisibleObject visibleObject : owner.getKnownList())
			{
				if(trapCreator.isEnemy(visibleObject))
					enemyCount++;
			}

			if(enemyCount > 0)
			{
				ai.addDesire(new TrapExplodeDesire(owner, trapCreator, AIState.ACTIVE.getPriority()));
			}

			if(ai.desireQueueSize() == 0)
				ai.handleEvent(Event.NOTHING_TODO);
			else
				ai.schedule();
		}
	}

	class TrapExplodeDesire extends AbstractDesire
	{
		/**
		 * Trap object
		 */
		private Trap		owner;
		/**
		 * Owner of trap
		 */
		private Creature	creator;

		/**
		 * 
		 * @param desirePower
		 * @param owner
		 */
		private TrapExplodeDesire(Trap owner, Creature creator, int desirePower)
		{
			super(desirePower);
			this.owner = owner;
			this.creator = creator;
		}

		@Override
		public boolean handleDesire(AI<?> ai)
		{
			for(VisibleObject visibleObject : owner.getKnownList())
			{
				if(visibleObject == null)
					continue;

				if(visibleObject instanceof Creature)
				{
					final Creature creature = (Creature) visibleObject;

					if(!creature.getLifeStats().isAlreadyDead()
						&& MathUtil.isInRange(owner, creature, owner.getAggroRange())
						&& (Math.abs(creature.getZ() - owner.getZ()) < 30))
					{

						if(!creator.isEnemy(creature))
							continue;

						owner.getAi().setAiState(AIState.NONE);

						int skillId = owner.getSkillId();
						Skill skill = SkillEngine.getInstance().getSkill(owner, skillId, 1, creature);
						skill.useSkill();
						owner.getController().onDespawn(true);
						break;
					}
				}
			}
			return true;
		}

		@Override
		public int getExecutionInterval()
		{
			return 2;
		}

		@Override
		public void onClear()
		{
			// TODO Auto-generated method stub
		}
	}
}
