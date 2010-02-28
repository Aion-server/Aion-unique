/**
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
package com.aionemu.gameserver.network.aion.clientpackets;


import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.group.LootDistribution;
import com.aionemu.gameserver.model.group.LootGroupRules;
import com.aionemu.gameserver.model.group.LootRuleType;
import com.aionemu.gameserver.model.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.AionClientPacket;

/**
 * @author Lyahim, Simple
 */
public class CM_DISTRIBUTION_SETTINGS extends AionClientPacket
{
		
	private LootRuleType lootrules; //0-free-for-all, 1-round-robin 2-leader
	private LootDistribution autodistribution;
	//rare item distribution
	//0-normal, 2-Roll-dice,3-bid
	private int common_item_above;
	private int superior_item_above;
	private int heroic_item_above;
	private int fabled_item_above;
	private int ethernal_item_above;
	private int over_ethernal;
	private int over_over_ethernal;
	
	public CM_DISTRIBUTION_SETTINGS(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		switch(readD())
		{
			case 0:
				this.lootrules = LootRuleType.FREEFORALL;break;
			case 1:
				this.lootrules = LootRuleType.ROUNDROBIN;break;
			case 2:
				this.lootrules = LootRuleType.LEADER;break;
		}
		switch(readD())
		{
			case 0:
				this.autodistribution = LootDistribution.NORMAL;break;
			case 2:
				this.autodistribution = LootDistribution.ROLL_DICE;break;
			case 3:
				this.autodistribution = LootDistribution.BID;break;				
		}
		this.common_item_above = readD();
		this.superior_item_above = readD();
		this.heroic_item_above = readD();
		this.fabled_item_above = readD();
		this.ethernal_item_above = readD();
		this.over_ethernal = readD();
		this.over_over_ethernal = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		Player leader = getConnection().getActivePlayer();
		if(leader != null)
		{
			PlayerGroup pg = leader.getPlayerGroup();
			if(pg != null)
				pg.setLootGroupRules(new LootGroupRules(this.lootrules, 
						this.autodistribution, this.common_item_above,
						this.superior_item_above, this.heroic_item_above,
						this.fabled_item_above, this.ethernal_item_above,
						this.over_ethernal, this.over_over_ethernal));
		}
	}
}
