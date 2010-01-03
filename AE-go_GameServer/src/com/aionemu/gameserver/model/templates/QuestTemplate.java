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
package com.aionemu.gameserver.model.templates;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Gender;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.templates.quest.CollectItems;
import com.aionemu.gameserver.model.templates.quest.QuestWorkItems;
import com.aionemu.gameserver.model.templates.quest.Rewards;
import com.aionemu.gameserver.questEngine.conditions.QuestConditions;
import com.aionemu.gameserver.questEngine.events.OnEnterZoneEvent;
import com.aionemu.gameserver.questEngine.events.OnItemUseEvent;
import com.aionemu.gameserver.questEngine.events.OnKillEvent;
import com.aionemu.gameserver.questEngine.events.OnLvlUpEvent;
import com.aionemu.gameserver.questEngine.events.OnMovieEndEvent;
import com.aionemu.gameserver.questEngine.events.OnTalkEvent;


/**
 * @author MrPoke
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Quest", propOrder = {
    "conditions",
    "collectItems",
    "rewards",
    "onKillEvent",
    "onTalkEvent",
    "onLvlUpEvent",
    "onItemUseEvent",
    "onEnterZoneEvent",
    "onMovieEndEvent",
    "finishedQuestConds",
    "classPermitted",
    "genderPermitted",
    "questWorkItems"
})
public class QuestTemplate {

    protected QuestConditions conditions;
    @XmlElement(name = "collect_items")
    protected CollectItems collectItems;
    protected List<Rewards> rewards;
    @XmlElement(name = "on_kill_event")
    protected List<OnKillEvent> onKillEvent;
    @XmlElement(name = "on_talk_event")
    protected List<OnTalkEvent> onTalkEvent;
    @XmlElement(name = "on_lvl_up_event")
    protected List<OnLvlUpEvent> onLvlUpEvent;
    @XmlElement(name = "on_item_use_event")
    protected List<OnItemUseEvent> onItemUseEvent;
    @XmlElement(name = "on_enter_zone_event")
    protected List<OnEnterZoneEvent> onEnterZoneEvent;
    @XmlElement(name = "on_movie_end_event")
    protected List<OnMovieEndEvent> onMovieEndEvent;
    @XmlList
    @XmlElement(name = "finished_quest_conds", type = Integer.class)
    protected List<Integer> finishedQuestConds;
    @XmlList
    @XmlElement(name = "class_permitted")
    protected List<PlayerClass> classPermitted;
    @XmlElement(name = "gender_permitted")
    protected Gender genderPermitted;
    @XmlElement(name = "quest_work_items")
    protected QuestWorkItems questWorkItems;
    @XmlAttribute(required = true)
    protected int id;
    @XmlAttribute(name = "start_npc_id")
    protected Integer startNpcId;
    @XmlAttribute(name = "end_npc_id")
    protected Integer endNpcId;
    @XmlAttribute
    protected String name;
    @XmlAttribute(name = "minlevel_permitted")
    protected Integer minlevelPermitted;
    @XmlAttribute(name = "max_repeat_count")
    protected Integer maxRepeatCount;
    @XmlAttribute(name = "cannot_share")
    protected Boolean cannotShare;
    @XmlAttribute(name = "cannot_giveup")
    protected Boolean cannotGiveup;
    @XmlAttribute(name = "f_mission")
    protected Integer fMission;
    @XmlAttribute(name = "race_permitted")
    protected Race racePermitted;

    
    /**
     * Gets the value of the conditions property.
     * 
     * @return
     *     possible object is
     *     {@link QuestConditions }
     *     
     */
    public QuestConditions getConditions() {
        return conditions;
    }

    /**
     * Gets the value of the collectItems property.
     * 
     * @return
     *     possible object is
     *     {@link CollectItems }
     *     
     */
    public CollectItems getCollectItems() {
        return collectItems;
    }

    /**
     * Gets the value of the rewards property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rewards property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRewards().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Rewards }
     * 
     * 
     */
    public List<Rewards> getRewards() {
        if (rewards == null) {
            rewards = new ArrayList<Rewards>();
        }
        return this.rewards;
    }
    /**
     * Gets the value of the onKillEvent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the onKillEvent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOnKillEvent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OnKillEvent }
     * 
     * 
     */
    public List<OnKillEvent> getOnKillEvent() {
        if (onKillEvent == null) {
            onKillEvent = new ArrayList<OnKillEvent>();
        }
        return this.onKillEvent;
    }

    /**
     * Gets the value of the onTalkEvent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the onTalkEvent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOnTalkEvent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OnTalkEvent }
     * 
     * 
     */
    public List<OnTalkEvent> getOnTalkEvent() {
        if (onTalkEvent == null) {
            onTalkEvent = new ArrayList<OnTalkEvent>();
        }
        return this.onTalkEvent;
    }
    /**
     * Gets the value of the onLvlUpEvent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the onLvlUpEvent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOnLvlUpEvent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OnLvlUpEvent }
     * 
     * 
     */
    public List<OnLvlUpEvent> getOnLvlUpEvent() {
        if (onLvlUpEvent == null) {
            onLvlUpEvent = new ArrayList<OnLvlUpEvent>();
        }
        return this.onLvlUpEvent;
    }

    /**
     * Gets the value of the onItemUseEvent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the onItemUseEvent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOnItemUseEvent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OnItemUseEvent }
     * 
     * 
     */
    public List<OnItemUseEvent> getOnItemUseEvent() {
        if (onItemUseEvent == null) {
            onItemUseEvent = new ArrayList<OnItemUseEvent>();
        }
        return this.onItemUseEvent;
    }

    /**
     * Gets the value of the onEnterZoneEvent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the onEnterZoneEvent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOnEnterZoneEvent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OnEnterZoneEvent }
     * 
     * 
     */
    public List<OnEnterZoneEvent> getOnEnterZoneEvent() {
        if (onEnterZoneEvent == null) {
            onEnterZoneEvent = new ArrayList<OnEnterZoneEvent>();
        }
        return this.onEnterZoneEvent;
    }

    /**
     * Gets the value of the onMovieEndEvent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the onMovieEndEvent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOnMovieEndEvent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OnMovieEndEvent }
     * 
     * 
     */
    public List<OnMovieEndEvent> getOnMovieEndEvent() {
        if (onMovieEndEvent == null) {
            onMovieEndEvent = new ArrayList<OnMovieEndEvent>();
        }
        return this.onMovieEndEvent;
    }

    /**
     * Gets the value of the finishedQuestConds property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the finishedQuestConds property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFinishedQuestConds().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getFinishedQuestConds() {
        if (finishedQuestConds == null) {
            finishedQuestConds = new ArrayList<Integer>();
        }
        return this.finishedQuestConds;
    }

    /**
     * Gets the value of the classPermitted property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the classPermitted property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClassPermitted().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PlayerClass }
     * 
     * 
     */
    public List<PlayerClass> getClassPermitted() {
        if (classPermitted == null) {
            classPermitted = new ArrayList<PlayerClass>();
        }
        return this.classPermitted;
    }

    /**
     * Gets the value of the genderPermitted property.
     * 
     * @return
     *     possible object is
     *     {@link Gender }
     *     
     */
    public Gender getGenderPermitted() {
        return genderPermitted;
    }

    /**
     * Gets the value of the questWorkItems property.
     * 
     * @return
     *     possible object is
     *     {@link QuestWorkItems }
     *     
     */
    public QuestWorkItems getQuestWorkItems() {
        return questWorkItems;
    }

    /**
     * Gets the value of the id property.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the value of the startNpcId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getStartNpcId() {
        return startNpcId;
    }

    /**
     * Gets the value of the endNpcId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getEndNpcId() {
        return endNpcId;
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
     * Gets the value of the minlevelPermitted property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMinlevelPermitted() {
        return minlevelPermitted;
    }

    /**
     * Gets the value of the maxRepeatCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxRepeatCount() {
        return maxRepeatCount;
    }

    /**
     * Gets the value of the cannotShare property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isCannotShare() {
        if (cannotShare == null) {
            return false;
        } else {
            return cannotShare;
        }
    }

    /**
     * Gets the value of the cannotGiveup property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isCannotGiveup() {
        if (cannotGiveup == null) {
            return false;
        } else {
            return cannotGiveup;
        }
    }

    /**
     * Gets the value of the fMission property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFMission() {
        return fMission;
    }

    /**
     * Gets the value of the racePermitted property.
     * 
     * @return
     *     possible object is
     *     {@link Race }
     *     
     */
    public Race getRacePermitted() {
        return racePermitted;
    }
}