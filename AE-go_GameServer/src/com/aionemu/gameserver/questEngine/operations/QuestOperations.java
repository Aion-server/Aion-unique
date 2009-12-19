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
package com.aionemu.gameserver.questEngine.operations;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.questEngine.model.QuestEnv;

/**
 * @author MrPoke
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestOperations", propOrder = {
    "questOperations"
})
public class QuestOperations {

    @XmlElements({
        @XmlElement(name = "incrase_quest_var", type = IncraseQuestVarOperation.class),
        @XmlElement(name = "play_movie", type = PlayMovieOperation.class),
        @XmlElement(name = "take_item", type = TakeItemOperation.class),
        @XmlElement(name = "npc_dialog", type = NpcDialogOperation.class),
        @XmlElement(name = "give_exp", type = GiveExpOperation.class),
        @XmlElement(name = "set_quest_status", type = SetQuestStatusOperation.class),
        @XmlElement(name = "give_item", type = GiveItemOperation.class),
        @XmlElement(name = "start_quest", type = StartQuestOperation.class)
    })
    protected List<QuestOperation> questOperations;
    @XmlAttribute
    protected Boolean override;

    /**
     * Gets the value of the override property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isOverride() {
        if (override == null) {
            return true;
        } else {
            return override;
        }
    }

	public boolean operate(QuestEnv env)
	{
		if (questOperations != null)
		{
			for (QuestOperation oper : questOperations)
			{
					oper.doOperate(env);
			}
		}
		return isOverride();
	}
	
}
