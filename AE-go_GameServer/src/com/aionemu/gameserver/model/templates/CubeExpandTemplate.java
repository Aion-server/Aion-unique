package com.aionemu.gameserver.model.templates;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author dragoon112
 *
 */
@XmlRootElement(name="cube_npc")
@XmlAccessorType(XmlAccessType.NONE)
public class CubeExpandTemplate
{
	/**
	 * NPC ID
	 */
	@XmlAttribute(name = "id", required = true)
	private int		Id;
	
	/**
	 * NPC name
	 */
	@XmlAttribute(name = "name", required = true)
	private String	name	= "";
	
	/**
	 * These are the minimum upgrade level and the maximum.
	 */
	@XmlAttribute(name = "min_level", required = true)
	private int	 min_level = 0;
	
	@XmlAttribute(name = "max_level", required = true)
	private int	 max_level = 0;
	
	/**
	 * Prices
	 */
	@XmlAttribute(name = "price01")
	private int price01 = 0;
	@XmlAttribute(name = "price02")
	private int price02 = 0;
	@XmlAttribute(name = "price03")
	private int price03 = 0;
	@XmlAttribute(name = "price04")
	private int price04 = 0;
	@XmlAttribute(name = "price05")
	private int price05 = 0;
	@XmlAttribute(name = "price06")
	private int price06 = 0;
	@XmlAttribute(name = "price07")
	private int price07 = 0;
	@XmlAttribute(name = "price08")
	private int price08 = 0;
	@XmlAttribute(name = "price09")
	private int price09 = 0;
	
	public String getName()
	{
		return name;
	}

	public int getNpcId()
	{
		return Id;
	}
	
	public int getMinLevel()
	{
		return min_level;
	}
	public int getMaxLevel()
	{
		return max_level;
	}

	public int getPrice1()
	{
		return price01;
	}
	public int getPrice2()
	{
		return price02;
	}
	public int getPrice3()
	{
		return price03;
	}
	public int getPrice4()
	{
		return price04;
	}
	public int getPrice5()
	{
		return price05;
	}
	public int getPrice6()
	{
		return price06;
	}
	public int getPrice7()
	{
		return price07;
	}
	public int getPrice8()
	{
		return price08;
	}
	public int getPrice9()
	{
		return price09;
	}
}
