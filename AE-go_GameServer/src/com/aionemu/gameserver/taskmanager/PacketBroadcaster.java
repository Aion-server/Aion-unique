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
package com.aionemu.gameserver.taskmanager;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * @author lord_rex and MrPoke
 * 
 */
public class PacketBroadcaster extends AbstractPeriodicTaskManager<Creature>
{
	private static final class SingletonHolder
	{
		private static final PacketBroadcaster	INSTANCE	= new PacketBroadcaster();
	}

	public static PacketBroadcaster getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private PacketBroadcaster()
	{
		super(100);
	}

	public static enum BroadcastMode
	{
		UPDATE_PLAYER_HP_STAT {
			@Override
			public void sendPacket(Creature creature)
			{
				((Player) creature).getLifeStats().sendHpPacketUpdateImpl();
			}
		},
		UPDATE_PLAYER_MP_STAT {
			@Override
			public void sendPacket(Creature creature)
			{
				((Player) creature).getLifeStats().sendMpPacketUpdateImpl();
			}
		},
		UPDATE_PLAYER_EFFECT_ICONS {
			@Override
			public void sendPacket(Creature creature)
			{
				creature.getEffectController().updatePlayerEffectIconsImpl();
			}
		},
		UPDATE_NEARBY_QUEST_LIST {
			@Override
			public void sendPacket(Creature creature)
			{
				((Player) creature).getController().updateNearbyQuestListImpl();
			}
		},
		// TODO: more packets
		;

		private final byte	MASK;

		private BroadcastMode()
		{
			MASK = (byte) (1 << ordinal());
		}

		public byte mask()
		{
			return MASK;
		}

		protected abstract void sendPacket(Creature creature);

		protected final void trySendPacket(Creature creature, byte mask)
		{
			if((mask & mask()) == mask())
			{
				sendPacket(creature);

				creature.removePacketBroadcastMask(this);
			}
		}
	}

	private static final BroadcastMode[]	VALUES	= BroadcastMode.values();

	@Override
	protected void callTask(Creature creature)
	{
		for(byte mask; (mask = creature.getPacketBroadcastMask()) != 0;)
		{
			for(BroadcastMode mode : VALUES)
			{
				mode.trySendPacket(creature, mask);
			}
		}
	}
}
