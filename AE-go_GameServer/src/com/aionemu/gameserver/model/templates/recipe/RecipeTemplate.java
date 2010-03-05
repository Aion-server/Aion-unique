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
package com.aionemu.gameserver.model.templates.recipe;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.skillengine.model.learn.SkillRace;


/**
 * @author ATracer
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RecipeTemplate")
public class RecipeTemplate {

    protected List<Component> component;
    protected List<ComboProduct> comboproduct;
    @XmlAttribute
    protected int tasktype;
    @XmlAttribute
    protected int componentquantity;
    @XmlAttribute
    protected int quantity;
    @XmlAttribute
    protected int productid;
    @XmlAttribute
    protected int autolearn;
    @XmlAttribute
    protected int dp;
    @XmlAttribute
    protected int skillpoint;
    @XmlAttribute
    protected SkillRace race;
    @XmlAttribute
    protected int skillid;
    @XmlAttribute
    protected int itemid;
    @XmlAttribute
    protected int nameid;
    @XmlAttribute
    protected int id;

    /**
     * Gets the value of the component property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the component property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getComponent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Component }
     * 
     * 
     */
    public List<Component> getComponent() {
        if (component == null) {
            component = new ArrayList<Component>();
        }
        return this.component;
    }

    /**
     * Gets the value of the tasktype property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTasktype() {
        return tasktype;
    }

    /**
     * Gets the value of the componentquantity property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getComponentquantity() {
        return componentquantity;
    }

    /**
     * Gets the value of the quantity property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Gets the value of the productid property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getProductid() {
        return productid;
    }

    /**
     * Gets the value of the autolearn property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAutolearn() {
        return autolearn;
    }

    /**
     * Gets the value of the dp property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDp() {
        return dp;
    }

    /**
     * Gets the value of the skillpoint property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSkillpoint() {
        return skillpoint;
    }

    /**
     * Gets the value of the race property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public SkillRace getRace() {
        return race;
    }

    /**
     * Gets the value of the skillid property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSkillid() {
        return skillid;
    }

    /**
     * Gets the value of the itemid property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getItemid() {
        return itemid;
    }

    /**
	 * @return the nameid
	 */
	public int getNameid()
	{
		return nameid;
	}

	/**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getId() {
        return id;
    }

}
