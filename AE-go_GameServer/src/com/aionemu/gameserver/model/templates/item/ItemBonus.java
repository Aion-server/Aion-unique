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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * @author ATracer
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Bonus")
public class ItemBonus {

    @XmlAttribute
    protected Integer magicalskillboost;
    @XmlAttribute
    protected Integer magicalattack;
    @XmlAttribute
    protected Integer elementaldefendwater;
    @XmlAttribute
    protected Integer arstagger;
    @XmlAttribute
    protected Integer aropenareial;
    @XmlAttribute
    protected Integer parry;
    @XmlAttribute
    protected Integer block;
    @XmlAttribute
    protected Integer arfear;
    @XmlAttribute
    protected Integer elementaldefendair;
    @XmlAttribute
    protected Integer arperification;
    @XmlAttribute
    protected Integer archarm;
    @XmlAttribute
    protected Integer arstumble;
    @XmlAttribute
    protected Integer critical;
    @XmlAttribute
    protected Integer pvpdefendratio;
    @XmlAttribute
    protected String boostcastingtime;
    @XmlAttribute
    protected Integer arstun;
    @XmlAttribute
    protected Integer magicalresist;
    @XmlAttribute
    protected Integer arspin;
    @XmlAttribute
    protected Integer arroot;
    @XmlAttribute
    protected Integer arpoison;
    @XmlAttribute
    protected Integer arconfuse;
    @XmlAttribute
    protected Integer arslow;
    @XmlAttribute
    protected Integer magicalhitaccuracy;
    @XmlAttribute
    protected Integer maxmp;
    @XmlAttribute
    protected Integer concentration;
    @XmlAttribute
    protected Integer arbleed;
    @XmlAttribute
    protected String speed;
    @XmlAttribute
    protected Integer magicalcritical;
    @XmlAttribute
    protected Integer phyattack;
    @XmlAttribute
    protected Integer arsnare;
    @XmlAttribute
    protected Integer arblind;
    @XmlAttribute
    protected Integer hitaccuracy;
    @XmlAttribute
    protected Integer dodge;
    @XmlAttribute
    protected String boosthate;
    @XmlAttribute
    protected Integer pvpattackratio;
    @XmlAttribute
    protected Integer physicaldefend;
    @XmlAttribute
    protected Integer elementaldefendearth;
    @XmlAttribute
    protected Integer arsleep;
    @XmlAttribute
    protected String flyspeed;
    @XmlAttribute
    protected Integer elementaldefendfire;
    @XmlAttribute
    protected Integer ardisease;
    @XmlAttribute
    protected String attackdelay;
    @XmlAttribute
    protected Integer arcurse;
    @XmlAttribute
    protected Integer arsilence;
    @XmlAttribute
    protected Integer arparalyze;
    @XmlAttribute
    protected Integer maxhp;
    @XmlAttribute
    protected Integer maxfp;
    @XmlAttribute(name = "magical_resist")
    protected Integer magicalResist;

    /**
     * Gets the value of the magicalskillboost property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMagicalskillboost() {
        return magicalskillboost;
    }

    /**
     * Sets the value of the magicalskillboost property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMagicalskillboost(Integer value) {
        this.magicalskillboost = value;
    }

    /**
     * Gets the value of the magicalattack property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMagicalattack() {
        return magicalattack;
    }

    /**
     * Sets the value of the magicalattack property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMagicalattack(Integer value) {
        this.magicalattack = value;
    }

    /**
     * Gets the value of the elementaldefendwater property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getElementaldefendwater() {
        return elementaldefendwater;
    }

    /**
     * Sets the value of the elementaldefendwater property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setElementaldefendwater(Integer value) {
        this.elementaldefendwater = value;
    }

    /**
     * Gets the value of the arstagger property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getArstagger() {
        return arstagger;
    }

    /**
     * Sets the value of the arstagger property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setArstagger(Integer value) {
        this.arstagger = value;
    }

    /**
     * Gets the value of the aropenareial property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAropenareial() {
        return aropenareial;
    }

    /**
     * Sets the value of the aropenareial property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAropenareial(Integer value) {
        this.aropenareial = value;
    }

    /**
     * Gets the value of the parry property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getParry() {
        return parry;
    }

    /**
     * Sets the value of the parry property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setParry(Integer value) {
        this.parry = value;
    }

    /**
     * Gets the value of the block property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBlock() {
        return block;
    }

    /**
     * Sets the value of the block property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBlock(Integer value) {
        this.block = value;
    }

    /**
     * Gets the value of the arfear property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getArfear() {
        return arfear;
    }

    /**
     * Sets the value of the arfear property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setArfear(Integer value) {
        this.arfear = value;
    }

    /**
     * Gets the value of the elementaldefendair property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getElementaldefendair() {
        return elementaldefendair;
    }

    /**
     * Sets the value of the elementaldefendair property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setElementaldefendair(Integer value) {
        this.elementaldefendair = value;
    }

    /**
     * Gets the value of the arperification property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getArperification() {
        return arperification;
    }

    /**
     * Sets the value of the arperification property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setArperification(Integer value) {
        this.arperification = value;
    }

    /**
     * Gets the value of the archarm property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getArcharm() {
        return archarm;
    }

    /**
     * Sets the value of the archarm property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setArcharm(Integer value) {
        this.archarm = value;
    }

    /**
     * Gets the value of the arstumble property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getArstumble() {
        return arstumble;
    }

    /**
     * Sets the value of the arstumble property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setArstumble(Integer value) {
        this.arstumble = value;
    }

    /**
     * Gets the value of the critical property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCritical() {
        return critical;
    }

    /**
     * Sets the value of the critical property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCritical(Integer value) {
        this.critical = value;
    }

    /**
     * Gets the value of the pvpdefendratio property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPvpdefendratio() {
        return pvpdefendratio;
    }

    /**
     * Sets the value of the pvpdefendratio property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPvpdefendratio(Integer value) {
        this.pvpdefendratio = value;
    }

    /**
     * Gets the value of the boostcastingtime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBoostcastingtime() {
        return boostcastingtime;
    }

    /**
     * Sets the value of the boostcastingtime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBoostcastingtime(String value) {
        this.boostcastingtime = value;
    }

    /**
     * Gets the value of the arstun property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getArstun() {
        return arstun;
    }

    /**
     * Sets the value of the arstun property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setArstun(Integer value) {
        this.arstun = value;
    }

    /**
     * Gets the value of the magicalresist property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMagicalresist() {
        return magicalresist;
    }

    /**
     * Sets the value of the magicalresist property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMagicalresist(Integer value) {
        this.magicalresist = value;
    }

    /**
     * Gets the value of the arspin property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getArspin() {
        return arspin;
    }

    /**
     * Sets the value of the arspin property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setArspin(Integer value) {
        this.arspin = value;
    }

    /**
     * Gets the value of the arroot property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getArroot() {
        return arroot;
    }

    /**
     * Sets the value of the arroot property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setArroot(Integer value) {
        this.arroot = value;
    }

    /**
     * Gets the value of the arpoison property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getArpoison() {
        return arpoison;
    }

    /**
     * Sets the value of the arpoison property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setArpoison(Integer value) {
        this.arpoison = value;
    }

    /**
     * Gets the value of the arconfuse property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getArconfuse() {
        return arconfuse;
    }

    /**
     * Sets the value of the arconfuse property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setArconfuse(Integer value) {
        this.arconfuse = value;
    }

    /**
     * Gets the value of the arslow property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getArslow() {
        return arslow;
    }

    /**
     * Sets the value of the arslow property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setArslow(Integer value) {
        this.arslow = value;
    }

    /**
     * Gets the value of the magicalhitaccuracy property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMagicalhitaccuracy() {
        return magicalhitaccuracy;
    }

    /**
     * Sets the value of the magicalhitaccuracy property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMagicalhitaccuracy(Integer value) {
        this.magicalhitaccuracy = value;
    }

    /**
     * Gets the value of the maxmp property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxmp() {
        return maxmp;
    }

    /**
     * Sets the value of the maxmp property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxmp(Integer value) {
        this.maxmp = value;
    }

    /**
     * Gets the value of the concentration property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getConcentration() {
        return concentration;
    }

    /**
     * Sets the value of the concentration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setConcentration(Integer value) {
        this.concentration = value;
    }

    /**
     * Gets the value of the arbleed property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getArbleed() {
        return arbleed;
    }

    /**
     * Sets the value of the arbleed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setArbleed(Integer value) {
        this.arbleed = value;
    }

    /**
     * Gets the value of the speed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpeed() {
        return speed;
    }

    /**
     * Sets the value of the speed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpeed(String value) {
        this.speed = value;
    }

    /**
     * Gets the value of the magicalcritical property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMagicalcritical() {
        return magicalcritical;
    }

    /**
     * Sets the value of the magicalcritical property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMagicalcritical(Integer value) {
        this.magicalcritical = value;
    }

    /**
     * Gets the value of the phyattack property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPhyattack() {
        return phyattack;
    }

    /**
     * Sets the value of the phyattack property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPhyattack(Integer value) {
        this.phyattack = value;
    }

    /**
     * Gets the value of the arsnare property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getArsnare() {
        return arsnare;
    }

    /**
     * Sets the value of the arsnare property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setArsnare(Integer value) {
        this.arsnare = value;
    }

    /**
     * Gets the value of the arblind property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getArblind() {
        return arblind;
    }

    /**
     * Sets the value of the arblind property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setArblind(Integer value) {
        this.arblind = value;
    }

    /**
     * Gets the value of the hitaccuracy property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getHitaccuracy() {
        return hitaccuracy;
    }

    /**
     * Sets the value of the hitaccuracy property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setHitaccuracy(Integer value) {
        this.hitaccuracy = value;
    }

    /**
     * Gets the value of the dodge property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDodge() {
        return dodge;
    }

    /**
     * Sets the value of the dodge property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDodge(Integer value) {
        this.dodge = value;
    }

    /**
     * Gets the value of the boosthate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBoosthate() {
        return boosthate;
    }

    /**
     * Sets the value of the boosthate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBoosthate(String value) {
        this.boosthate = value;
    }

    /**
     * Gets the value of the pvpattackratio property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPvpattackratio() {
        return pvpattackratio;
    }

    /**
     * Sets the value of the pvpattackratio property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPvpattackratio(Integer value) {
        this.pvpattackratio = value;
    }

    /**
     * Gets the value of the physicaldefend property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPhysicaldefend() {
        return physicaldefend;
    }

    /**
     * Sets the value of the physicaldefend property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPhysicaldefend(Integer value) {
        this.physicaldefend = value;
    }

    /**
     * Gets the value of the elementaldefendearth property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getElementaldefendearth() {
        return elementaldefendearth;
    }

    /**
     * Sets the value of the elementaldefendearth property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setElementaldefendearth(Integer value) {
        this.elementaldefendearth = value;
    }

    /**
     * Gets the value of the arsleep property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getArsleep() {
        return arsleep;
    }

    /**
     * Sets the value of the arsleep property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setArsleep(Integer value) {
        this.arsleep = value;
    }

    /**
     * Gets the value of the flyspeed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlyspeed() {
        return flyspeed;
    }

    /**
     * Sets the value of the flyspeed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlyspeed(String value) {
        this.flyspeed = value;
    }

    /**
     * Gets the value of the elementaldefendfire property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getElementaldefendfire() {
        return elementaldefendfire;
    }

    /**
     * Sets the value of the elementaldefendfire property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setElementaldefendfire(Integer value) {
        this.elementaldefendfire = value;
    }

    /**
     * Gets the value of the ardisease property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getArdisease() {
        return ardisease;
    }

    /**
     * Sets the value of the ardisease property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setArdisease(Integer value) {
        this.ardisease = value;
    }

    /**
     * Gets the value of the attackdelay property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttackdelay() {
        return attackdelay;
    }

    /**
     * Sets the value of the attackdelay property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttackdelay(String value) {
        this.attackdelay = value;
    }

    /**
     * Gets the value of the arcurse property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getArcurse() {
        return arcurse;
    }

    /**
     * Sets the value of the arcurse property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setArcurse(Integer value) {
        this.arcurse = value;
    }

    /**
     * Gets the value of the arsilence property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getArsilence() {
        return arsilence;
    }

    /**
     * Sets the value of the arsilence property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setArsilence(Integer value) {
        this.arsilence = value;
    }

    /**
     * Gets the value of the arparalyze property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getArparalyze() {
        return arparalyze;
    }

    /**
     * Sets the value of the arparalyze property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setArparalyze(Integer value) {
        this.arparalyze = value;
    }

    /**
     * Gets the value of the maxhp property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxhp() {
        return maxhp;
    }

    /**
     * Sets the value of the maxhp property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxhp(Integer value) {
        this.maxhp = value;
    }

    /**
     * Gets the value of the maxfp property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxfp() {
        return maxfp;
    }

    /**
     * Sets the value of the maxfp property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxfp(Integer value) {
        this.maxfp = value;
    }

    /**
     * Gets the value of the magicalResist property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMagicalResist() {
        return magicalResist;
    }

    /**
     * Sets the value of the magicalResist property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMagicalResist(Integer value) {
        this.magicalResist = value;
    }

}
