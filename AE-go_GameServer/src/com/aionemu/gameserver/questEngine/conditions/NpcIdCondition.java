/*
 * This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.questEngine.conditions;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.questEngine.model.QuestEnv;

/**
 * @author MrPoke
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NpcIdCondition")
public class NpcIdCondition
    extends QuestCondition
{

    @XmlAttribute(required = true)
    protected List<Integer> values;

	@Override
	public boolean doCheck(QuestEnv env)
	{
		int id = 0;
		VisibleObject visibleObject = env.getVisibleObject();
		if (visibleObject != null && visibleObject instanceof Npc)
		{
			id = ((Npc)visibleObject).getNpcId();
		}
		int npcId = values.get(0);
		switch (getOp())
		{
			case EQUAL:
				return id == npcId;
			case GREATER:
				return id > npcId;
			case GREATER_EQUAL:
				return id >= npcId;
			case LESSER:
				return id < npcId;
			case LESSER_EQUAL:
				return id <= npcId;
			case NOT_EQUAL:
				return id != npcId;
			case IN:
				return values.contains(id);
			case NOT_IN:
				return !(values.contains(id));
			default:
				return false;
		}
	}
}
