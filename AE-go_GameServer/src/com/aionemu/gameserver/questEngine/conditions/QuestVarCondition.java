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
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;


/**
 * <p>Java class for QuestVarCondition complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QuestVarCondition">
 *   &lt;complexContent>
 *     &lt;extension base="{}Condition">
 *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="var_id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestVarCondition")
public class QuestVarCondition
    extends QuestCondition
{

    @XmlAttribute(required = true)
    protected int value;
    @XmlAttribute(name = "var_id", required = true)
    protected int varId;

	@Override
	public boolean doCheck(QuestEnv env)
	{
		QuestState qs = env.getPlayer().getQuestStateList().getQuestState(env.getQuestId());
		if (qs == null)
		{
			return false;
		}
		int var = qs.getQuestVars().getQuestVarById(varId);
		switch (getOp())
		{
			case EQUAL:
				return var == value;
			case GREATER:
				return var > value;
			case GREATER_EQUAL:
				return var >= value;
			case LESSER:
				return var < value;
			case LESSER_EQUAL:
				return var <= value;
			case NOT_EQUAL:
				return var != value;
			default:
				return false;
		}
	}
}
