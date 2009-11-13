/*
 * This file is part of aion-emu <aion-emu.com>.
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

/**
 * @author xavier
 *
 */
public class StatModifier
{
	private StatEnum modifiedStat;
	private String modifier;
	private int modifierValue;
	private boolean isBonus;
	private boolean isPercent;
	private int oldValue;
	private boolean isEnded;

	public StatModifier (StatEnum modifiedStat, String modifier, int oldValue, boolean isBonus) {
		this.modifiedStat = modifiedStat;
		this.modifier = modifier;
		this.isBonus = isBonus;
		this.oldValue = oldValue;
		this.isPercent = modifier.contains("%");
		this.isEnded = false;
		this.modifierValue = processModifier();
	}
	
	public boolean isBonus () {
		return isBonus;
	}
	
	public boolean isPercent () {
		return isPercent;
	}
	
	public boolean isEnded () {
		return isEnded;
	}
	
	private int processModifier () {
		int value;
		if (modifiedStat.isReplace()) {
			if (!isBonus) {
				return Integer.parseInt(modifier);
			}
		}
		
		if(isPercent)
		{
			int percentValue = Integer.parseInt(modifier.substring(0, modifier.indexOf("%")));
			value = Math.round(percentValue * oldValue / 100f);
		}
		else
		{
			value = Integer.parseInt(modifier);
		}
		value = modifiedStat.getSign()*value;
		
		return value;
	}
	
	public int getModifier () {
		return modifierValue;
	}
	
	public int endModifier (int currentStat) {
		this.isEnded = true;
		if (modifiedStat.isReplace()) {
			if (!isBonus) {
				return oldValue;
			}
		}
		return currentStat-modifierValue;
	}
	
	public void setOldValue (int oldValue) {
		this.oldValue = oldValue;
		this.modifierValue = processModifier();
	}
	
	public StatEnum getModifiedStat () {
		return modifiedStat;
	}
}
