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
import javax.xml.bind.annotation.XmlAttribute;
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
		@XmlElement(name = "stun", type = StunEffect.class),
		@XmlElement(name = "sleep", type = SleepEffect.class),
		@XmlElement(name = "bleed", type = BleedEffect.class),
		@XmlElement(name = "hide", type = HideEffect.class),
		@XmlElement(name = "search", type = SearchEffect.class),
		@XmlElement(name = "statup", type = StatupEffect.class),
		@XmlElement(name = "statdown", type = StatdownEffect.class),
		@XmlElement(name = "statboost", type = StatboostEffect.class),
		@XmlElement(name = "weaponstatboost", type = WeaponStatboostEffect.class),
		@XmlElement(name = "wpnmastery", type = WeaponMasteryEffect.class),
		@XmlElement(name = "snare", type = SnareEffect.class),
		@XmlElement(name = "slow", type = SlowEffect.class),
		@XmlElement(name = "stumble", type = StumbleEffect.class),
		@XmlElement(name = "spin", type = SpinEffect.class),
		@XmlElement(name = "stagger", type = StaggerEffect.class),
		@XmlElement(name = "openaerial", type = OpenAerialEffect.class),
		@XmlElement(name = "closeaerial", type = CloseAerialEffect.class),
		@XmlElement(name = "shield", type = ShieldEffect.class),
		@XmlElement(name = "bind", type = BindEffect.class),
		@XmlElement(name = "dispel", type = DispelEffect.class),
		@XmlElement(name = "skillatk", type = SkillAttackEffect.class),
		@XmlElement(name = "spellatk", type = SpellAttackEffect.class),
		@XmlElement(name = "dash", type = DashEffect.class),
		@XmlElement(name = "backdash", type = BackDashEffect.class),
		@XmlElement(name = "delaydamage", type = DelayDamageEffect.class),
		@XmlElement(name = "return", type = ReturnEffect.class),
		@XmlElement(name = "heal", type = HealEffect.class),
		@XmlElement(name = "healmp", type = HealMpEffect.class),
		@XmlElement(name = "healdp", type = HealDpEffect.class),
		@XmlElement(name = "healfp", type = HealFpEffect.class),
		@XmlElement(name = "itemheal", type = ItemHealEffect.class),
		@XmlElement(name = "itemhealmp", type = ItemHealMpEffect.class),
		@XmlElement(name = "itemhealdp", type = ItemHealDpEffect.class),
		@XmlElement(name = "itemhealfp", type = ItemHealFpEffect.class),
		@XmlElement(name = "carvesignet", type = CarveSignetEffect.class),
		@XmlElement(name = "signet", type = SignetEffect.class),
		@XmlElement(name = "signetburst", type = SignetBurstEffect.class),
		@XmlElement(name = "silence", type = SilenceEffect.class),
		@XmlElement(name = "curse", type = CurseEffect.class),
		@XmlElement(name = "blind", type = BlindEffect.class),
		@XmlElement(name = "boosthate", type = BoostHateEffect.class),
		@XmlElement(name = "hostileup", type = HostileUpEffect.class),
		@XmlElement(name = "paralyze", type = ParalyzeEffect.class),
		@XmlElement(name = "confuse", type = ConfuseEffect.class),
		@XmlElement(name = "dispeldebuffphysical", type = DispelDebuffPhysicalEffect.class),
		@XmlElement(name = "dispeldebuff", type = DispelDebuffEffect.class),
		@XmlElement(name = "alwaysdodge", type = AlwaysDodgeEffect.class),
		@XmlElement(name = "alwaysparry", type = AlwaysParryEffect.class),
		@XmlElement(name = "alwaysresist", type = AlwaysResistEffect.class),
		@XmlElement(name = "alwaysblock", type = AlwaysBlockEffect.class)
	})
	protected List<EffectTemplate> effects;
	
	@XmlAttribute
	protected boolean food;

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

	/**
	 * @return the food
	 */
	public boolean isFood()
	{
		return food;
	}

	/**
	 *
	 * @return
	 */
	public int getEffectsDuration()
	{
		int duration = 0;
		for(EffectTemplate template : getEffects())
		{
			duration = duration > template.getDuration() ? duration : template.getDuration();
		}
		return duration;
	}
}
