/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.controllers.attack.SkillAttackResult;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * 
 * @author alexa026
 * 
 */
public class SM_CASTSPELL_END extends AionServerPacket
{
	private int				attackerobjectid;
	private int				targetObjectId;
	private int				spellid;
	private int				level;
	@SuppressWarnings("unused")
	private int				unk;													// can cast??
	private int				cooldown;
	List<SkillAttackResult>	skillAttackList	= new ArrayList<SkillAttackResult>();

	public SM_CASTSPELL_END(int attackerobjectid, int spellid, int level, int unk, int targetObjectId,
		List<SkillAttackResult> skillAttackList, int cooldown)
	{
		this.attackerobjectid = attackerobjectid;
		this.targetObjectId = targetObjectId;
		this.spellid = spellid;// empty
		this.level = level;
		this.unk = unk;
		this.skillAttackList = skillAttackList;
		this.cooldown = cooldown * 10;
	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, attackerobjectid);
		writeC(buf, 0);
		writeD(buf, targetObjectId);
		writeH(buf, spellid);
		writeC(buf, level);
		writeD(buf, cooldown);
		writeC(buf, 0xFE); // unk??
		writeC(buf, 1); // unk??
		writeD(buf, 8192); // chain 2

		writeH(buf, skillAttackList.size());
		for(SkillAttackResult skillAttack : skillAttackList)
		{
			writeD(buf, skillAttack.getCreature().getObjectId());
			writeH(buf, 3072); // unk?? abnormal eff id ??
			writeH(buf, 100); // unk??
			writeH(buf, 16); // unk??

			writeH(buf, 1); // unk??
			writeD(buf, skillAttack.getDamage()); // damage
			writeC(buf, skillAttack.getAttackStatus().getId());
			writeC(buf, 0);
		}

	}
}
