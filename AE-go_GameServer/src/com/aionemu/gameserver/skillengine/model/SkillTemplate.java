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
package com.aionemu.gameserver.skillengine.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.skillengine.action.Actions;
import com.aionemu.gameserver.skillengine.condition.Conditions;
import com.aionemu.gameserver.skillengine.effect.Effects;


/**
 * @author ATracer
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "skillTemplate", propOrder = {
    "conditions",
    "effects",
    "actions"
})
public class SkillTemplate {

    protected Conditions conditions;
    protected Effects effects;
    protected Actions actions;
    @XmlAttribute(name = "skill_id", required = true)
    protected int skillId;
    @XmlAttribute(required = true)
    protected String name;
    @XmlAttribute(required = true)
    protected SkillType type;
    @XmlAttribute(required = true)
    protected int level;
    @XmlAttribute(required = true)
    protected int duration;
    @XmlAttribute
    protected Integer cooldown;

    /**
     * Gets the value of the conditions property.
     * 
     * @return
     *     possible object is
     *     {@link Conditions }
     *     
     */
    public Conditions getConditions() {
        return conditions;
    }

    /**
     * Sets the value of the conditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Conditions }
     *     
     */
    public void setConditions(Conditions value) {
        this.conditions = value;
    }

    /**
     * Gets the value of the effects property.
     * 
     * @return
     *     possible object is
     *     {@link Effects }
     *     
     */
    public Effects getEffects() {
        return effects;
    }

    /**
     * Sets the value of the effects property.
     * 
     * @param value
     *     allowed object is
     *     {@link Effects }
     *     
     */
    public void setEffects(Effects value) {
        this.effects = value;
    }

    /**
     * Gets the value of the actions property.
     * 
     * @return
     *     possible object is
     *     {@link Actions }
     *     
     */
    public Actions getActions() {
        return actions;
    }

    /**
     * Sets the value of the actions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Actions }
     *     
     */
    public void setActions(Actions value) {
        this.actions = value;
    }

    /**
     * Gets the value of the skillId property.
     * 
     */
    public int getSkillId() {
        return skillId;
    }

    /**
     * Sets the value of the skillId property.
     * 
     */
    public void setSkillId(int value) {
        this.skillId = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link SkillType }
     *     
     */
    public SkillType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link SkillType }
     *     
     */
    public void setType(SkillType value) {
        this.type = value;
    }

    /**
     * Gets the value of the level property.
     * 
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the value of the level property.
     * 
     */
    public void setLevel(int value) {
        this.level = value;
    }

    /**
     * Gets the value of the duration property.
     * 
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Sets the value of the duration property.
     * 
     */
    public void setDuration(int value) {
        this.duration = value;
    }

    /**
     * Gets the value of the cooldown property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getCooldown() {
        if (cooldown == null) {
            return  0;
        } else {
            return cooldown;
        }
    }

    /**
     * Sets the value of the cooldown property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCooldown(Integer value) {
        this.cooldown = value;
    }

}
