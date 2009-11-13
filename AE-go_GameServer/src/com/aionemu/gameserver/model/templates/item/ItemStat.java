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
package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.stats.StatEnum;

/**
 * @author ATracer
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemStat")
public class ItemStat
{
	
	private StatEnum statEnum;
	
	@XmlAttribute
    protected String name;
    @XmlAttribute
    protected String value;
    @XmlAttribute
    protected Boolean bonus;
    
    /**
     *  Called by unmarshaller
     *  
     * @param u
     * @param parent
     */
    void afterUnmarshal(Unmarshaller u, Object parent)
	{
		statEnum = StatEnum.find(name);
	}

    /**
	 * @return the statEnum
	 */
	public StatEnum getStatEnum()
	{
		return statEnum;
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
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the value of the bonus property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBonus() {
        return bonus;
    }
}
