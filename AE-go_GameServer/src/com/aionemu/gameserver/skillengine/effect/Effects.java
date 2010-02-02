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
package com.aionemu.gameserver.skillengine.effect;

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
@XmlType(name = "Effects", propOrder = {
    "effects"
})
public class Effects 
{
	
	@XmlElements({
        @XmlElement(name = "root", type = RootEffect.class),
        @XmlElement(name = "buf", type = BufEffect.class),
        @XmlElement(name = "dot", type = DamageOverTimeEffect.class),
        @XmlElement(name = "hot", type = HealOverTimeEffect.class),
        @XmlElement(name = "transform", type = TransformEffect.class),
        @XmlElement(name = "poison", type = PoisonEffect.class),
        @XmlElement(name = "stun", type = StunEffect.class)
    })
    protected List<EffectTemplate> effects;

    /**
     * Gets the value of the effects property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the effect property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEffects().add(newItem);
     * </pre>
     * 
     */
    public List<EffectTemplate> getEffects() 
    {
        if (effects == null) {
            effects = new ArrayList<EffectTemplate>();
        }
        return this.effects;
    }
}
