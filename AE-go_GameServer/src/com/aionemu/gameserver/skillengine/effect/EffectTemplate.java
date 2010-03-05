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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.SkillElement;
import com.aionemu.gameserver.skillengine.change.Change;
import com.aionemu.gameserver.skillengine.effect.modifier.ActionModifier;
import com.aionemu.gameserver.skillengine.effect.modifier.ActionModifiers;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;

/**
 * @author ATracer
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Effect")
public abstract class EffectTemplate 
{
	protected ActionModifiers modifiers;
    protected List<Change> change;
    @XmlAttribute
    protected int effectid;
	@XmlAttribute(required = true)
	protected int duration;
	@XmlAttribute(name = "e")
	protected int position;
	@XmlAttribute(name = "basiclvl")
	protected int basicLvl;
	@XmlAttribute(name = "element")
	protected SkillElement element = SkillElement.NONE;
	@XmlElement(name = "subeffect")
	protected SubEffect subEffect;
	
	/**
	 * @return the duration
	 */
	public int getDuration()
	{
		return duration;
	}
	

	/**
	 * @return the modifiers
	 */
	public ActionModifiers getModifiers()
	{
		return modifiers;
	}


	/**
	 * @return the change
	 */
	public List<Change> getChange()
	{
		return change;
	}

	/**
	 * @return the effectid
	 */
	public int getEffectid()
	{
		return effectid;
	}

	/**
	 * @return the position
	 */
	public int getPosition()
	{
		return position;
	}

	/**
	 * @return the basicLvl
	 */
	public int getBasicLvl()
	{
		return basicLvl;
	}

	/**
	 * @return the element
	 */
	public SkillElement getElement()
	{
		return element;
	}

	/**
	 * @param value
	 * @return
	 */
	protected int applyActionModifiers(Effect effect, int value)
	{	
		if(modifiers == null)
			return value;
		
		/**
		 * Only one of modifiers will be applied now
		 */
		for(ActionModifier modifier : modifiers.getActionModifiers())
		{
			if(modifier.check(effect))
				return modifier.analyze(effect, value);
		}
		
		return value;
	}

	/**
	 *  Calculate effect result
	 *  
	 * @param effect
	 */
	public abstract void calculate(Effect effect);
	/**
	 *  Apply effect to effected 
	 *  
	 * @param effect
	 */
	public abstract void applyEffect(Effect effect);
	/**
	 *  Start effect on effected
	 *  
	 * @param effect
	 */
	public void startEffect(Effect effect){};
	
	/**
	 * 
	 * @param effect
	 */
	public void startSubEffect(Effect effect)
	{
		if(subEffect == null)
			return;
		
		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(subEffect.getSkillId());
		int duration = template.getEffectsDuration();
		Effect newEffect = new Effect(effect.getEffector(), effect.getEffected(), template, template.getLvl(), duration);
		newEffect.initialize();
		effect.setSpellStatus(newEffect.getSpellStatus());
		newEffect.applyEffect();
	}
	/**
	 *  Do periodic effect on effected
	 *  
	 * @param effect
	 */
	public void onPeriodicAction(Effect effect){};
	/**
	 *  End effect on effected
	 *  
	 * @param effect
	 */
	public void endEffect(Effect effect){};
}
