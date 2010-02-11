/*
 * This file is part of aion-unique <www.aion-unique.com>.
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
package com.aionemu.gameserver.itemengine.actions;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

/**
 * @author ATracer
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemActions")
public class ItemActions {

    @XmlElements({
        @XmlElement(name = "skilllearn", type = SkillLearnAction.class),
        @XmlElement(name = "skilluse", type = SkillUseAction.class),
        @XmlElement(name = "enchant", type = EnchantItemAction.class),
        @XmlElement(name = "queststart", type = QuestStartAction.class),
        @XmlElement(name = "dye", type = DyeAction.class),
        @XmlElement(name = "craftlearn", type = CraftLearnAction.class)

    })
    protected List<AbstractItemAction> itemActions;

    /**
     * Gets the value of the itemActions property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link SkillLearnAction }
     * {@link SkillUseAction }
     * 
     */
    public List<AbstractItemAction> getItemActions() {
        if (itemActions == null) {
            itemActions = new ArrayList<AbstractItemAction>();
        }
        return this.itemActions;
    }

}