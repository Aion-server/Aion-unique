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
package com.aionemu.gameserver.questEngine.conditions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.questEngine.model.ConditionOperation;
import com.aionemu.gameserver.questEngine.model.QuestEnv;

/**
 * @author MrPoke
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestCondition")
@XmlSeeAlso({
    PcRaceCondition.class,
    PcLevelCondition.class,
    NpcIdCondition.class,
    DialogIdCondition.class,
    PcInventoryCondition.class,
    QuestVarCondition.class,
    QuestStatusCondition.class
})
public abstract class QuestCondition {

    @XmlAttribute(required = true)
    protected ConditionOperation op;

    /**
     * Gets the value of the op property.
     * 
     * @return
     *     possible object is
     *     {@link ConditionOp }
     *     
     */
    public ConditionOperation getOp() {
        return op;
    }

    public abstract boolean doCheck(QuestEnv env);

}
