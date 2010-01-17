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
package com.aionemu.gameserver.model.templates.goods;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GoodsList")
public class GoodsList {

	protected List<GoodsList.Item> item;
	@XmlAttribute
	protected int id;

	protected List<Integer> itemIdList;

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		itemIdList = new ArrayList<Integer>();
		
		if(item == null)
			return;
		
		for(Item it : item)
		{
			itemIdList.add(it.getId());
		}
		item = null;
	}	

	/**
	 * Gets the value of the id property.
	 */
	public int getId() {
		return id;
	}


	/**
	 * @return the itemIdList
	 */
	public List<Integer> getItemIdList()
	{
		return itemIdList;
	}




	/**
	 * <p>Java class for anonymous complex type.
	 * 
	 * <p>The following schema fragment specifies the expected content contained within this class.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}int" />
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "")
	public static class Item {

		@XmlAttribute
		protected int id;

		/**
		 * Gets the value of the id property.
		 *     
		 */
		public int getId() {
			return id;
		}

	}

}
