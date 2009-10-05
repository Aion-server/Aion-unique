/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.quests.qparser;

import org.w3c.dom.Node;

import com.aionemu.gameserver.model.quests.ConditionFactory;
import com.aionemu.gameserver.model.quests.OperationFactory;
import com.aionemu.gameserver.model.quests.QuestCondition;
import com.aionemu.gameserver.model.quests.QuestEngineException;
import com.aionemu.gameserver.model.quests.QuestOperation;
import com.aionemu.gameserver.model.quests.QuestStep;
import com.aionemu.gameserver.model.quests.qholder.QuestHolder;
import com.aionemu.gameserver.model.quests.types.ConditionSet;
import com.aionemu.gameserver.model.quests.types.ConditionUnionType;

/**
 * @author Felixx
 */
public class QuestParser extends AbstractDirParser
{
	private static QuestParser _instance;

	private QuestParser()
	{
		super("./data/static_data/quests/", "template.xml");
		_holder = QuestHolder.getInstance();
		super.parse();
	}

	public static QuestParser getInstance()
	{
		if (_instance == null)
		{
			_instance = new QuestParser();
		}
		return _instance;
	}

	@Override
	protected void readData(Node node)
	{
		try
		{
			if ("quest".equalsIgnoreCase(node.getNodeName()))
			{
				long questId = Long.parseLong(node.getAttributes().getNamedItem("id").getNodeValue());
				String questName = node.getAttributes().getNamedItem("name").getNodeValue();
				QuestHolder.getInstance().newQuest(questId, questName);
				// quest inner nodes
				for (Node questNode = node.getFirstChild(); questNode != null; questNode = questNode.getNextSibling())
				{
					QuestStep prevStep = null;
					if ("condition".equalsIgnoreCase(questNode.getNodeName()))
					{
						try
						{
							ConditionUnionType condUnion = ConditionUnionType.valueOf(questNode.getAttributes().getNamedItem("operate").getNodeValue().toUpperCase());
							ConditionSet questCond = new ConditionSet(condUnion);
							// parse quest conditions
							for (Node condNode = questNode.getFirstChild(); condNode != null; condNode = condNode.getNextSibling())
							{
								String condName = condNode.getNodeName();
								try
								{
									QuestCondition cond = ConditionFactory.newCondition(condName, condNode.getAttributes());
									questCond.add(cond);
								}
								catch (Exception ex)
								{
									_log.info("[QuestsParser] Error on Quest " + questName + ":" + questId + " Condition " + condName + " loading failure: " + ex.getMessage());
								}
							}
							QuestHolder.getInstance().getQuest(questId).setQuestConditions(questCond);
						}
						catch (QuestEngineException ex)
						{
							_log.info("[QuestsParser] Error on Quest " + questName + ":" + questId + " QuestConditions loading failure: " + ex.getMessage());
						}
						catch (Exception ex)
						{
							_log.info("[QuestsParser] Error on Quest " + questName + ":" + questId + " QuestConditions loading failure: " + ex.getMessage());
						}
					}
					else if ("quest_step".equalsIgnoreCase(questNode.getNodeName()))
					{
						long num = Long.parseLong(questNode.getAttributes().getNamedItem("num").getNodeValue());
						QuestStep questStep = new QuestStep(QuestHolder.getInstance().getQuest(questId), num);
						// add cur-quest-step to prev-step sa next
						if (prevStep != null)
						{
							prevStep.setNextStep(questStep);
						}
						prevStep = questStep;
						for (Node stepNode = questNode.getFirstChild(); stepNode != null; stepNode = stepNode.getNextSibling())
						{
							if ("condition".equalsIgnoreCase(stepNode.getNodeName()))
							{
								try
								{
									ConditionUnionType condUnion = ConditionUnionType.valueOf(stepNode.getAttributes().getNamedItem("operate").getNodeValue().toUpperCase());
									ConditionSet questStepCond = new ConditionSet(condUnion);
									// parse quest-step conditions
									for (Node condNode = stepNode.getFirstChild(); condNode != null; condNode = condNode.getNextSibling())
									{
										String condName = condNode.getNodeName();
										try
										{
											QuestCondition cond = ConditionFactory.newCondition(condName, condNode.getAttributes());
											questStepCond.add(cond);
										}
										catch (Exception ex)
										{
											_log.info("[QuestsParser] Error on Quest " + questName + ":" + questId + ", step: " + num + " Condition " + condName + " loading failure: " + ex.getMessage());
										}
									}
									questStep.addCondition(questStepCond);
								}
								catch (Exception ex)
								{
									_log.info("[QuestsParser] Error on Quest " + questName + ":" + questId + " QuestConditions loading failure: " + ex.getMessage());
								}
							}
							else if ("operation".equalsIgnoreCase(stepNode.getNodeName()))
							{
								try
								{
									String opType = stepNode.getAttributes().getNamedItem("type").getNodeName();
									try
									{
										QuestOperation oper = OperationFactory.newOperation(opType, stepNode);
										questStep.addOperation(oper);
									}
									catch (QuestEngineException ex)
									{
										_log.info("[QuestsParser] Error on Quest " + questName + ":" + questId + ", step: " + num + " Operation " + opType + " loading failure: " + ex.getMessage());
									}
								}
								catch (Exception ex)
								{
									_log.info("[QuestsParser] Error on Quest " + questName + ":" + questId + ", step: " + num + " Operation loading failure: " + ex.getMessage());
								}
							}
						}
					}
				}
			}
		}
		catch (QuestEngineException ex)
		{
			_log.info("[QuestsParser] Error on quest: " + ex.getMessage());
		}
	}
}
