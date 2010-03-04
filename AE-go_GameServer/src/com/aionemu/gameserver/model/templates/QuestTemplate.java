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
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.model.templates.quest.QuestWorkItems;
import com.aionemu.gameserver.model.templates.quest.Rewards;

/**
 * @author MrPoke
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Quest", propOrder = { "collectItems", "rewards", "finishedQuestConds", "classPermitted",
	"genderPermitted", "questWorkItems", "fighterSelectableReward", "knightSelectableReward", "rangerSelectableReward",
	"assassinSelectableReward", "wizardSelectableReward", "elementalistSelectableReward", "priestSelectableReward",
	"chanterSelectableReward" })
public class QuestTemplate
{

	@XmlElement(name = "collect_items")
	protected CollectItems		collectItems;
	protected List<Rewards>		rewards;
	@XmlList
	@XmlElement(name = "finished_quest_conds", type = Integer.class)
	protected List<Integer>		finishedQuestConds;
	@XmlList
	@XmlElement(name = "class_permitted")
	protected List<PlayerClass>	classPermitted;
	@XmlElement(name = "gender_permitted")
	protected Gender			genderPermitted;
	@XmlElement(name = "quest_work_items")
	protected QuestWorkItems	questWorkItems;
	@XmlElement(name = "fighter_selectable_reward")
	protected List<QuestItems>	fighterSelectableReward;
	@XmlElement(name = "knight_selectable_reward")
	protected List<QuestItems>	knightSelectableReward;
	@XmlElement(name = "ranger_selectable_reward")
	protected List<QuestItems>	rangerSelectableReward;
	@XmlElement(name = "assassin_selectable_reward")
	protected List<QuestItems>	assassinSelectableReward;
	@XmlElement(name = "wizard_selectable_reward")
	protected List<QuestItems>	wizardSelectableReward;
	@XmlElement(name = "elementalist_selectable_reward")
	protected List<QuestItems>	elementalistSelectableReward;
	@XmlElement(name = "priest_selectable_reward")
	protected List<QuestItems>	priestSelectableReward;
	@XmlElement(name = "chanter_selectable_reward")
	protected List<QuestItems>	chanterSelectableReward;
	@XmlAttribute(required = true)
	protected int				id;
	@XmlAttribute
	protected String			name;
	@XmlAttribute
	protected Integer			nameId;
	@XmlAttribute(name = "minlevel_permitted")
	protected Integer			minlevelPermitted;
	@XmlAttribute(name = "max_repeat_count")
	protected Integer			maxRepeatCount;
	@XmlAttribute(name = "cannot_share")
	protected Boolean			cannotShare;
	@XmlAttribute(name = "cannot_giveup")
	protected Boolean			cannotGiveup;
	@XmlAttribute(name = "use_class_reward")
	protected Boolean			useClassReward;
	@XmlAttribute(name = "race_permitted")
	protected Race				racePermitted;
	@XmlAttribute
	protected Integer			combineskill;
	@XmlAttribute(name = "combine_skillpoint")
	protected Integer			combineSkillpoint;

	/**
	 * Gets the value of the collectItems property.
	 * 
	 * @return possible object is {@link CollectItems }
	 * 
	 */
	public CollectItems getCollectItems()
	{
		return collectItems;
	}

	/**
	 * Gets the value of the rewards property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the rewards property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getRewards().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Rewards }
	 * 
	 * 
	 */
	public List<Rewards> getRewards()
	{
		if(rewards == null)
		{
			rewards = new ArrayList<Rewards>();
		}
		return this.rewards;
	}

	/**
	 * Gets the value of the finishedQuestConds property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the finishedQuestConds property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getFinishedQuestConds().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Integer }
	 * 
	 * 
	 */
	public List<Integer> getFinishedQuestConds()
	{
		if(finishedQuestConds == null)
		{
			finishedQuestConds = new ArrayList<Integer>();
		}
		return this.finishedQuestConds;
	}

	/**
	 * Gets the value of the classPermitted property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the classPermitted property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getClassPermitted().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link PlayerClass }
	 * 
	 * 
	 */
	public List<PlayerClass> getClassPermitted()
	{
		if(classPermitted == null)
		{
			classPermitted = new ArrayList<PlayerClass>();
		}
		return this.classPermitted;
	}

	/**
	 * Gets the value of the genderPermitted property.
	 * 
	 * @return possible object is {@link Gender }
	 * 
	 */
	public Gender getGenderPermitted()
	{
		return genderPermitted;
	}

	/**
	 * Gets the value of the questWorkItems property.
	 * 
	 * @return possible object is {@link QuestWorkItems }
	 * 
	 */
	public QuestWorkItems getQuestWorkItems()
	{
		return questWorkItems;
	}

	/**
	 * Gets the value of the fighterSelectableReward property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the fighterSelectableReward property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getFighterSelectableReward().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link QuestItems }
	 * 
	 * 
	 */
	public List<QuestItems> getFighterSelectableReward()
	{
		if(fighterSelectableReward == null)
		{
			fighterSelectableReward = new ArrayList<QuestItems>();
		}
		return this.fighterSelectableReward;
	}

	/**
	 * Gets the value of the knightSelectableReward property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the knightSelectableReward property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getKnightSelectableReward().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link QuestItems }
	 * 
	 * 
	 */
	public List<QuestItems> getKnightSelectableReward()
	{
		if(knightSelectableReward == null)
		{
			knightSelectableReward = new ArrayList<QuestItems>();
		}
		return this.knightSelectableReward;
	}

	/**
	 * Gets the value of the rangerSelectableReward property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the rangerSelectableReward property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getRangerSelectableReward().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link QuestItems }
	 * 
	 * 
	 */
	public List<QuestItems> getRangerSelectableReward()
	{
		if(rangerSelectableReward == null)
		{
			rangerSelectableReward = new ArrayList<QuestItems>();
		}
		return this.rangerSelectableReward;
	}

	/**
	 * Gets the value of the assassinSelectableReward property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the assassinSelectableReward property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getAssassinSelectableReward().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link QuestItems }
	 * 
	 * 
	 */
	public List<QuestItems> getAssassinSelectableReward()
	{
		if(assassinSelectableReward == null)
		{
			assassinSelectableReward = new ArrayList<QuestItems>();
		}
		return this.assassinSelectableReward;
	}

	/**
	 * Gets the value of the wizardSelectableReward property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the wizardSelectableReward property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getWizardSelectableReward().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link QuestItems }
	 * 
	 * 
	 */
	public List<QuestItems> getWizardSelectableReward()
	{
		if(wizardSelectableReward == null)
		{
			wizardSelectableReward = new ArrayList<QuestItems>();
		}
		return this.wizardSelectableReward;
	}

	/**
	 * Gets the value of the elementalistSelectableReward property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the elementalistSelectableReward property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getElementalistSelectableReward().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link QuestItems }
	 * 
	 * 
	 */
	public List<QuestItems> getElementalistSelectableReward()
	{
		if(elementalistSelectableReward == null)
		{
			elementalistSelectableReward = new ArrayList<QuestItems>();
		}
		return this.elementalistSelectableReward;
	}

	/**
	 * Gets the value of the priestSelectableReward property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the priestSelectableReward property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getPriestSelectableReward().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link QuestItems }
	 * 
	 * 
	 */
	public List<QuestItems> getPriestSelectableReward()
	{
		if(priestSelectableReward == null)
		{
			priestSelectableReward = new ArrayList<QuestItems>();
		}
		return this.priestSelectableReward;
	}

	/**
	 * Gets the value of the chanterSelectableReward property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the chanterSelectableReward property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getChanterSelectableReward().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link QuestItems }
	 * 
	 * 
	 */
	public List<QuestItems> getChanterSelectableReward()
	{
		if(chanterSelectableReward == null)
		{
			chanterSelectableReward = new ArrayList<QuestItems>();
		}
		return this.chanterSelectableReward;
	}

	/**
	 * Gets the value of the id property.
	 * 
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * Gets the value of the name property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the value of the nameId property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getNameId()
	{
		return nameId;
	}

	/**
	 * Gets the value of the minlevelPermitted property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getMinlevelPermitted()
	{
		return minlevelPermitted;
	}

	/**
	 * Gets the value of the maxRepeatCount property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getMaxRepeatCount()
	{
		return maxRepeatCount;
	}

	/**
	 * Gets the value of the cannotShare property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public boolean isCannotShare()
	{
		if(cannotShare == null)
		{
			return false;
		}
		else
		{
			return cannotShare;
		}
	}

	/**
	 * Gets the value of the cannotGiveup property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public boolean isCannotGiveup()
	{
		if(cannotGiveup == null)
		{
			return false;
		}
		else
		{
			return cannotGiveup;
		}
	}

	/**
	 * Gets the value of the useClassReward property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public boolean isUseClassReward()
	{
		if(useClassReward == null)
		{
			return false;
		}
		else
		{
			return useClassReward;
		}
	}

	/**
	 * Gets the value of the racePermitted property.
	 * 
	 * @return possible object is {@link Race }
	 * 
	 */
	public Race getRacePermitted()
	{
		return racePermitted;
	}

	/**
	 * Gets the value of the combineskill property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getCombineSkill()
	{
		return combineskill;
	}

	/**
	 * Gets the value of the combineSkillpoint property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getCombineSkillPoint()
	{
		return combineSkillpoint;
	}
}