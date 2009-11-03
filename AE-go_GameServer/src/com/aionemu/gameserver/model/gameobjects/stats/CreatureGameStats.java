/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.gameobjects.stats;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.SkillElement;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * @author xavier
 *
 */
public class CreatureGameStats<T extends Creature>
{
	protected static final Logger log = Logger.getLogger(CreatureGameStats.class);
	
	private static final int ATTACK_MAX_COUNTER = Integer.MAX_VALUE;
	
	private int attackCounter = 0;
	private int power = 0;
	private int health = 0;
	private int agility = 0;
	private int accuracy = 0;
	private int knowledge = 0;
	private int will = 0;
	protected int mainHandAttack = 0;
	protected int mainHandCritRate = 0;
	private int offHandAttack = 0;
	private int offHandCritRate = 0;
	private int water = 0;
	private int wind = 0;
	private int earth = 0;
	private int fire = 0;
	private int flyTime = 0;
	protected int mainHandAccuracy = 0;
	protected int offHandAccuracy = 0;
	protected int magicAccuracy = 0;
	protected int magicResistance = 0;
	protected int physicalDefense = 0;
	protected int evasion = 0;
	protected int block = 0;
	protected int parry = 0;
	private int magicBoost = 0;
	private int attackSpeed = 0;
	private int attackRange = 0;
	private boolean initialized = false;
	private T owner = null;
	
	protected CreatureGameStats (T owner, int power, int health, int agility, int accuracy, int knowledge, int will, int mainHandAttack, int mainHandCritRate, int offHandAttack, int offHandCritRate, int attackSpeed, int attackRange)
	{
		this.owner = owner;
		this.initialized = true;
		setPower(power);
		setHealth(health);
		setAgility(agility);
		setAccuracy(accuracy);
		setKnowledge(knowledge); 
		setWill(will);
		setMainHandAttack(mainHandAttack);
		setMainHandCritRate(mainHandCritRate);
		setOffHandAttack(offHandAttack);
		setOffHandCritRate(offHandCritRate);
		setAttackSpeed(attackSpeed);
		setAttackRange(attackRange);
	}

	@Override
	public String toString () {
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		Class<?> clazz = CreatureGameStats.class;
		for (Field fi : clazz.getDeclaredFields()) {
			sb.append(fi.getName()); sb.append(':');
			try { sb.append(fi.getInt(this)); }
			catch(Exception e) { try { sb.append(fi.getBoolean(this)); }
			catch(Exception f) { try { sb.append(fi.getFloat(this)); }
			catch(Exception g) { try { sb.append(fi.getDouble(this)); }
			catch(Exception h) { try { sb.append(fi.get(this).toString()); }
			catch(Exception i) { sb.append('?'); } } } } }
			sb.append(':');
		}
		sb.append('}');
		return sb.toString();
	}
	
	public boolean isInitialized () {
		return initialized;
	}
	
	public void setInitialized (boolean initialized) {
		this.initialized = initialized;
	}
	
	/**
	 * @return the atcount
	 */
	public int getAttackCounter()
	{
		return attackCounter;
	}

	/**
	 * @param atcount the atcount to set
	 */
	public void setAttackCounter(int attackCounter)
	{
		if (attackCounter<=0) {
			this.attackCounter = 1;
		} else {
			this.attackCounter = attackCounter;
		}
	}
	
	public void increaseAttackCounter()
	{
		if(attackCounter == ATTACK_MAX_COUNTER)
		{
			this.attackCounter = 1;
		}
		else
		{
			this.attackCounter++;
		}
	}
	
	/**
	 * @return the power
	 */
	public int getPower()
	{
		return power;
	}
	/**
	 * @param power the power to set
	 */
	public void setPower(int power)
	{
		this.power = power;
	}
	/**
	 * @return the health
	 */
	public int getHealth()
	{
		return health;
	}
	/**
	 * @param health the health to set
	 */
	public void setHealth(int health)
	{
		this.health = health;
		this.physicalDefense = (int)Math.round(health / 3.1);
	}
	/**
	 * @return the agility
	 */
	public int getAgility()
	{
		return agility;
	}
	/**
	 * @param agility the agility to set
	 */
	public void setAgility(int agility)
	{
		this.agility = agility;
		this.parry = (int)Math.round(agility / 3.1);
		this.evasion = (int)Math.round(agility / 3.1);
		this.block = (int)Math.round(agility / 3.1);
	}
	/**
	 * @return the accuracy
	 */
	public int getAccuracy()
	{
		return accuracy;
	}
	/**
	 * @param accuracy the accuracy to set
	 */
	public void setAccuracy(int accuracy)
	{
		this.accuracy = accuracy;
		this.mainHandAccuracy = (int) Math.round(accuracy * 1.25);
		this.offHandAccuracy = (int) Math.round(accuracy * 0.75);
	}
	/**
	 * @return the knowledge
	 */
	public int getKnowledge()
	{
		return knowledge;
	}
	/**
	 * @param knowledge the knowledge to set
	 */
	public void setKnowledge(int knowledge)
	{
		this.knowledge = knowledge;
		this.magicResistance = (int)Math.round(knowledge / 3.1);
	}
	/**
	 * @return the will
	 */
	public int getWill()
	{
		return will;
	}
	/**
	 * @param will the will to set
	 */
	public void setWill(int will)
	{
		this.will = will;
		this.magicAccuracy = (int) Math.round(will * 0.75);
	}
	/**
	 * @return the mainHandAttack
	 */
	public int getMainHandAttack()
	{
		return mainHandAttack;
	}
	/**
	 * @param mainHandAttack the mainHandAttack to set
	 */
	public void setMainHandAttack(int mainHandAttack)
	{
		this.mainHandAttack = mainHandAttack;
	}
	/**
	 * @return the mainHandCritRate
	 */
	public int getMainHandCritRate()
	{
		return mainHandCritRate;
	}
	/**
	 * @param mainHandCritRate the mainHandCritRate to set
	 */
	public void setMainHandCritRate(int mainHandCritRate)
	{
		this.mainHandCritRate = mainHandCritRate;
	}
	/**
	 * @return the otherHandAttack
	 */
	public int getOffHandAttack()
	{
		return offHandAttack;
	}
	/**
	 * @param otherHandAttack the otherHandAttack to set
	 */
	public void setOffHandAttack(int offHandAttack)
	{
		this.offHandAttack = offHandAttack;
	}
	/**
	 * @return the otherHandCritRate
	 */
	public int getOffHandCritRate()
	{
		return offHandCritRate;
	}
	/**
	 * @param otherHandCritRate the otherHandCritRate to set
	 */
	public void setOffHandCritRate(int offHandCritRate)
	{
		this.offHandCritRate = offHandCritRate;
	}
	
	/**
	 * @return the attackSpeed
	 */
	public int getAttackSpeed()
	{
		return attackSpeed;
	}

	/**
	 * @param attackSpeed the attackSpeed to set
	 */
	public void setAttackSpeed(int attackSpeed)
	{
		this.attackSpeed = attackSpeed;
	}

	/**
	 * @return the attackRange
	 */
	public int getAttackRange()
	{
		return attackRange;
	}
	
	/**
	 * @param attackRange the attackRange to set
	 */
	public void setAttackRange(int attackRange)
	{
		this.attackRange = attackRange;
	}

	/**
	 * @return the water
	 */
	public int getWater()
	{
		return water;
	}
	/**
	 * @param water the water to set
	 */
	public void setWater(int water)
	{
		this.water = water;
	}
	/**
	 * @return the wind
	 */
	public int getWind()
	{
		return wind;
	}
	/**
	 * @param wind the wind to set
	 */
	public void setWind(int wind)
	{
		this.wind = wind;
	}
	/**
	 * @return the earth
	 */
	public int getEarth()
	{
		return earth;
	}
	/**
	 * @param earth the earth to set
	 */
	public void setEarth(int earth)
	{
		this.earth = earth;
	}
	/**
	 * @return the fire
	 */
	public int getFire()
	{
		return fire;
	}
	/**
	 * @param fire the fire to set
	 */
	public void setFire(int fire)
	{
		this.fire = fire;
	}
	/**
	 * @return the flyTime
	 */
	public int getFlyTime()
	{
		return flyTime;
	}
	/**
	 * @param flyTime the flyTime to set
	 */
	public void setFlyTime(int flyTime)
	{
		this.flyTime = flyTime;
	}
	/**
	 * @return the mainHandAccuracy
	 */
	public int getMainHandAccuracy()
	{
		return mainHandAccuracy;
	}
	/**
	 * @return the otherHandAccuracy
	 */
	public int getOffHandAccuracy()
	{
		return offHandAccuracy;
	}
	/**
	 * @return the magicAccuracy
	 */
	public int getMagicAccuracy()
	{
		return magicAccuracy;
	}
	/**
	 * @return the magicResistance
	 */
	public int getMagicResistance()
	{
		return magicResistance;
	}
	/**
	 * @return the physicalDefense
	 */
	public int getPhysicalDefense()
	{
		return physicalDefense;
	}
	/**
	 * @return the evasion
	 */
	public int getEvasion()
	{
		return evasion;
	}
	/**
	 * @return the block
	 */
	public int getBlock()
	{
		return block;
	}
	/**
	 * @return the parry
	 */
	public int getParry()
	{
		return parry;
	}
	/**
	 * @return the magicBoost
	 */
	public int getMagicBoost()
	{
		return magicBoost;
	}
	/**
	 * @return the owner
	 */
	public Creature getOwner () {
		return owner;
	}
	/**
	 * @param Creature the owner
	 */
	public void setOwner (T owner) {
		this.owner = owner;
	}
	
	public int getMagicalDefenseFor (SkillElement element) {
		switch (element) {
			case EARTH:
				return earth; 
			case FIRE:
				return fire;
			case WATER:
				return water;
			case WIND:
				return wind;
			default:
				return 0;
		}
	}
	
	public void setMagicalDefenseFor (SkillElement element, int value) {
		switch (element) {
			case EARTH:
				this.earth = value; break; 
			case FIRE:
				this.fire = value; break;
			case WATER:
				this.water = value; break;
			case WIND:
				this.wind = value; break;
			default:
				break;
		}
	}
}
