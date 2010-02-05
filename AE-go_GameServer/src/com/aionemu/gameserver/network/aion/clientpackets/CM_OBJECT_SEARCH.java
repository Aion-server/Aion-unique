/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.gameserver.network.aion.clientpackets;


import com.aionemu.gameserver.dataholders.SpawnsData;
import com.aionemu.gameserver.model.templates.spawn.SpawnTemplate;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SHOW_NPC_ON_MAP;
import com.google.inject.Inject;

/**
 * @author Lyahim
 */
public class CM_OBJECT_SEARCH extends AionClientPacket
{
	private int objid;

	@Inject
	private SpawnsData spawnsData;
	/**
	 * Constructs new client packet instance.
	 * @param opcode
	 */
	public CM_OBJECT_SEARCH(int opcode)
	{
		super(opcode);

	}

	/**
	 * Nothing to do
	 */
	@Override
	protected void readImpl()
	{
		this.objid = readD();
	}

	/**
	 * Logging
	 */
	@Override
	protected void runImpl()
	{		
		SpawnTemplate spawnTemplate = spawnsData.getFirstSpawnByNpcId(objid);
		if(spawnTemplate != null)
		{
			sendPacket(new SM_SHOW_NPC_ON_MAP(objid, spawnTemplate.getWorldId(),
				spawnTemplate.getX(), spawnTemplate.getY(), spawnTemplate.getZ()));
		}
	}
}
