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
package com.aionemu.gameserver.questEngine.events;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.questEngine.conditions.QuestConditions;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.operations.QuestOperations;

/**
 * @author MrPoke
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestEvent", propOrder = {
    "conditions",
    "operations"
})
@XmlSeeAlso({
    OnKillEvent.class,
    OnItemUseEvent.class,
    OnLvlUpEvent.class,
    OnTalkEvent.class
})
public abstract class QuestEvent {

    protected QuestConditions conditions;
    protected QuestOperations operations;
    @XmlAttribute
    protected List<Integer> ids;


    public List<Integer> getIds() {
        if (ids == null) {
            ids = new ArrayList<Integer>();
        }
        return this.ids;
    }

	public boolean operate(QuestEnv env)
	{
		if (conditions.checkConditionOfSet(env))
		{
			if (operations != null)
			{
				return operations.operate(env);
			}
		}
		return false;
	}

}
