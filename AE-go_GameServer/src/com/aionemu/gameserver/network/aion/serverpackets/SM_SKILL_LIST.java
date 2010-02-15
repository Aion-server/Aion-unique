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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.SkillListEntry;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * In this packet Server is sending Skill Info?
 * 
 * @author -Nemesiss-
 * 
 * modified by ATracer,MrPoke
 * 
 */
public class SM_SKILL_LIST extends AionServerPacket
{

	private SkillListEntry[] skillList;
	private int messageId;
	private int skillNameId;
	private String skillLvl;
	public static final int YOU_LEARNED_SKILL = 1300050;

	/**
	 *  This constructor is used on player entering the world
	 *  
 	 * Constructs new <tt>SM_SKILL_LIST </tt> packet
 	 */

	public SM_SKILL_LIST(Player player)
 	{
		this.skillList = player.getSkillList().getAllSkills();
		this.messageId = 0;
 	}
	
	public SM_SKILL_LIST(SkillListEntry skillListEntry, int messageId)
 	{
		this.skillList = new SkillListEntry[]{skillListEntry};
		this.messageId = messageId;
		this.skillNameId = DataManager.SKILL_DATA.getSkillTemplate(skillListEntry.getSkillId()).getNameId();
		this.skillLvl = String.valueOf(skillListEntry.getSkillLevel());
 	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{

		final int size = skillList.length;
		writeH(buf, size);//skills list size
		
		if (size > 0)
		{
			for (SkillListEntry entry : skillList)
			{
				writeH(buf, entry.getSkillId());//id
				writeD(buf, entry.getSkillLevel());//lvl
				writeD(buf, 0);//use time? [s]
				writeC(buf, 0);//unk
			}
		}
		writeD(buf, messageId);
		if (messageId != 0);
		{
			writeH(buf, 0x24); //unk
			writeD(buf, skillNameId);
			writeH(buf, 0x00);
			writeS(buf, skillLvl);
		}
	}
}