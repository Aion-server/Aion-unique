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

import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author lord_rex and MrPoke
 * 
 */
public class PacketBroadcaster implements Runnable
{
	private static final Logger		log		= Logger.getLogger(PacketBroadcaster.class);

	private final HashSet<Creature>	SET	= new HashSet<Creature>();

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
		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 100, 100);

		log.info("PacketBroadcaster: Initialized.");
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

	public void add(Creature creature)
	{
		synchronized(SET)
		{
			SET.add(creature);
		}
	}

	@Override
	public void run()
	{
		Iterator<Creature> it = SET.iterator();
		while(it.hasNext())
		{
			Creature creature = it.next();
			synchronized(SET)
			{
				SET.remove(creature);
			}
			for(byte mask; (mask = creature.getPacketBroadcastMask()) != 0;)
			{
				for(BroadcastMode mode : VALUES) {
					mode.trySendPacket(creature, mask);
				}
			}
		}
	}
}
