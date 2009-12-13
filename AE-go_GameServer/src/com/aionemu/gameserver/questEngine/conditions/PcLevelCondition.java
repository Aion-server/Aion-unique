/*
 * This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.questEngine.conditions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.model.QuestEnv;

/**
 * @author MrPoke
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PcLevelCondition")
public class PcLevelCondition
    extends QuestCondition
{

    @XmlAttribute(required = true)
    protected int value;

	@Override
	public boolean doCheck(QuestEnv env)
	{
		Player player = env.getPlayer();
		switch (getOp())
		{
			case EQUAL:
				return player.getLevel() == value;
			case GREATER:
				return player.getLevel() > value;
			case GREATER_EQUAL:
				return player.getLevel() >= value;
			case LESSER:
				return player.getLevel() < value;
			case LESSER_EQUAL:
				return player.getLevel() <= value;
			case NOT_EQUAL:
				return player.getLevel() != value;
			default:
				return false;
		}
	}
}
