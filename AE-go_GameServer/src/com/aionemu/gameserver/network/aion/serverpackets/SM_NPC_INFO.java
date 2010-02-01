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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;
import java.util.Map.Entry;

import com.aionemu.gameserver.model.NpcType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.model.items.NpcEquippedGear;
import com.aionemu.gameserver.model.templates.NpcTemplate;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is displaying visible npc/monsters.
 * 
 * @author -Nemesiss-
 * 
 */
public class SM_NPC_INFO extends AionServerPacket
{
	/**
	 * Visible npc
	 */
	private final Npc		npc;
	
	private final boolean isAggressive;

	/**
	 * Constructs new <tt>SM_NPC_INFO </tt> packet
	 * 
	 * @param npc
	 *            visible npc.
	 * @param player 
	 */
	public SM_NPC_INFO(Npc npc, Player player)
	{
		this.npc = npc;
		
		String playerTribe = player.getCommonData().getRace() == Race.ELYOS ? "PC" : "PC_DARK";
		isAggressive = npc.isAggressiveTo(playerTribe);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		NpcTemplate npcTemplate = npc.getTemplate();
		writeF(buf, npc.getX());// x
		writeF(buf, npc.getY());// y
		writeF(buf, npc.getZ());// z
		writeD(buf, npc.getObjectId());
		writeD(buf, npc.getNpcId());
		writeD(buf, npc.getNpcId());
		
		if(isAggressive)
			writeC(buf, NpcType.AGGRESSIVE.getId());
		else
			writeC(buf, npcTemplate.getNpcType().getId());// 0-monster, 38 - (non attackable), 8- pre-emptive attack (aggro monsters)
		
		writeH(buf, npc.getState());// unk 65=normal,0x47 (71)= [dead npc ?]no drop,0x21(33)=fight state,0x07=[dead monster?]
								// no drop
								// 3,19 - wings spread (NPCs)
								// 5,6,11,21 - sitting (NPC)
								// 7,23 - dead (no drop)
								// 8,24 - [dead][NPC only] - looks like some orb of light (no normal mesh)
								// 32,33 - fight mode
		
		writeC(buf, npc.getHeading());
		writeD(buf, npcTemplate.getNameId());

		writeD(buf, npcTemplate.getTitleId());// titleID
		writeD(buf, 0x00);// unk
		writeD(buf, 0x00);// unk
		writeD(buf, 0x00);// unk
		writeC(buf, 0x00);// unk
		writeC(buf, 100);// %hp

		writeD(buf, 2961);// unk 172, 143, 2961, 199, 28396...
		writeC(buf, npc.getLevel());// lvl
		
		NpcEquippedGear gear = npcTemplate.getEquipment();
		if(gear == null)
			writeH(buf, 0x00);
		else
		{
			writeH(buf, gear.getItemsMask());
			for(Entry<ItemSlot,ItemTemplate> item: gear) // getting it from template ( later if we make sure that npcs actually use items, we'll make Item from it )
			{
				writeD(buf, item.getValue().getItemId());
				writeD(buf, 0x00);
				writeD(buf, 0x00);
			}
		}
		
		
		writeD(buf, 1051931443);// unk
		writeF(buf, npcTemplate.getHeight());// 0x3F7AE148
		writeF(buf, 0.3f);// speed

		writeH(buf, 2000);// 0x834 (depends on speed ? )
		writeH(buf, 2000);// 0x834
		
		writeC(buf, 0x00);// unk

		/**
		 * Movement
		 */
		writeF(buf, npc.getX());// x
		writeF(buf, npc.getY());// y
		writeF(buf, npc.getZ());// z
		writeC(buf, 0x00); // move type

		writeC(buf, 0);
		writeC(buf, 0);
		writeC(buf, 0);
		writeC(buf, 0); // all unknown
		writeC(buf, 0);
		writeC(buf, 0);
		writeC(buf, 0);
		writeC(buf, 0);
		writeC(buf, 0);
		writeC(buf, 0);
		writeC(buf, 0);

		writeH(buf, 1);// unk
		writeC(buf, 0x00);// unk
	}
}
