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
package com.aionemu.gameserver.questEngine.conditions;


import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.questEngine.model.ConditionUnionType;
import com.aionemu.gameserver.questEngine.model.QuestEnv;

/**
 * @author MrPoke
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestConditions", propOrder = {
    "questConditions"
})
public class QuestConditions {

    @XmlElements({
        @XmlElement(name = "pc_level", type = PcLevelCondition.class),
        @XmlElement(name = "pc_race", type = PcRaceCondition.class),
        @XmlElement(name = "quest_var", type = QuestVarCondition.class),
        @XmlElement(name = "quest_status", type = QuestStatusCondition.class),
        @XmlElement(name = "pc_inventory", type = PcInventoryCondition.class),
        @XmlElement(name = "npc_id", type = NpcIdCondition.class),
        @XmlElement(name = "dialog_id", type = DialogIdCondition.class)
    })
    protected List<QuestCondition> questConditions;
    @XmlAttribute(required = true)
    protected ConditionUnionType operate;

	public boolean checkConditionOfSet(QuestEnv env)
	{
		boolean inCondition = (operate == ConditionUnionType.AND);
		for (QuestCondition cond : questConditions)
		{
			boolean bCond = cond.doCheck(env);
			switch (operate)
			{
				case AND:
					if (!bCond) return false;
					inCondition = inCondition && bCond;
					break;
				case OR:
					if (bCond) return true;
					inCondition = inCondition || bCond;
					break;
			}
		}
		return inCondition;
	}
}
