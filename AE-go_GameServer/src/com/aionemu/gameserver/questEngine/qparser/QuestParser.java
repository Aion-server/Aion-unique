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
package com.aionemu.gameserver.questEngine.qparser;

import org.w3c.dom.Node;

import com.aionemu.gameserver.questEngine.Quest;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.QuestEngineException;
import com.aionemu.gameserver.questEngine.conditions.ConditionFactory;
import com.aionemu.gameserver.questEngine.conditions.QuestCondition;
import com.aionemu.gameserver.questEngine.events.EventFactory;
import com.aionemu.gameserver.questEngine.events.QuestEvent;
import com.aionemu.gameserver.questEngine.operations.OperationFactory;
import com.aionemu.gameserver.questEngine.operations.QuestOperation;
import com.aionemu.gameserver.questEngine.types.ConditionSet;
import com.aionemu.gameserver.questEngine.types.ConditionUnionType;

/**
 * @author Felixx
 */
public class QuestParser extends AbstractDirParser
{
	private static QuestParser _instance;

	private QuestParser()
	{
		super("./data/static_data/quests/", "template.xml");
		_holder = QuestEngine.getInstance();
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
			if ("quests".equalsIgnoreCase(node.getNodeName()))
			{
				for (Node questsNode = node.getFirstChild(); questsNode != null; questsNode = questsNode.getNextSibling())
				{
					if ("quest".equalsIgnoreCase(questsNode.getNodeName()))
					{
						int questId = Integer.parseInt(questsNode.getAttributes().getNamedItem("id").getNodeValue());
						int startNpcId = Integer.parseInt(questsNode.getAttributes().getNamedItem("start_npc_id").getNodeValue());
						int endNpcId = Integer.parseInt(questsNode.getAttributes().getNamedItem("end_npc_id").getNodeValue());
						Quest quest = QuestEngine.getInstance().newQuest(questId, startNpcId, endNpcId);
						// quest inner nodes
						for (Node questNode = questsNode.getFirstChild(); questNode != null; questNode = questNode.getNextSibling())
						{
							if ("condition".equalsIgnoreCase(questNode.getNodeName()))
							{
								try
								{
									ConditionUnionType condUnion = ConditionUnionType.valueOf(questNode.getAttributes().getNamedItem("operate").getNodeValue().toUpperCase());
									ConditionSet questCond = new ConditionSet(condUnion);
									// parse quest conditions
									for (Node condNode = questNode.getFirstChild(); condNode != null; condNode = condNode.getNextSibling())
									{
										if (condNode.getAttributes() == null)
											continue;
										String condName = condNode.getNodeName();
										try
										{
											QuestCondition cond = ConditionFactory.newCondition(condName, condNode.getAttributes(), quest);
											questCond.add(cond);
										}
										catch (Exception ex)
										{
											_log.info("[QuestsParser] Error on Quest :" + questId + " Condition " + condName + " loading failure: " + ex.getMessage());
										}
									}
									quest.setQuestConditions(questCond);
								}
								catch (Exception ex)
								{
									_log.info("[QuestsParser] Error on Quest :" + questId + " QuestConditions loading failure: " + ex.getMessage());
								}
							}
							else if ("quest_event".equalsIgnoreCase(questNode.getNodeName()))
							{
								String eventType = questNode.getAttributes().getNamedItem("type").getNodeValue();
								QuestEvent event = EventFactory.newEvent(eventType, quest, questNode.getAttributes());
								for (Node eventNode = questNode.getFirstChild(); eventNode != null; eventNode = eventNode.getNextSibling())
								{
									if ("condition".equalsIgnoreCase(eventNode.getNodeName()))
									{
										try
										{
											ConditionUnionType condUnion = ConditionUnionType.valueOf(eventNode.getAttributes().getNamedItem("operate").getNodeValue().toUpperCase());
											ConditionSet eventCond = new ConditionSet(condUnion);
											// parse quest conditions
											for (Node condNode = eventNode.getFirstChild(); condNode != null; condNode = condNode.getNextSibling())
											{
												String condName = condNode.getNodeName();
												if (condNode.getAttributes() == null)
													continue;
												try
												{
													QuestCondition cond = ConditionFactory.newCondition(condName, condNode.getAttributes(), quest);
													eventCond.add(cond);
												}
												catch (Exception ex)
												{
													_log.info("[QuestsParser] Error on Quest :" + questId + " Condition " + condName + " loading failure: " + ex.getMessage());
												}
											}
											event.setConditions(eventCond);
										}
										catch (Exception ex)
										{
											_log.info("[QuestsParser] Error on Quest :" + questId + " QuestConditions loading failure: " + ex.getMessage());
										}
									}
									else if ("operations".equalsIgnoreCase(eventNode.getNodeName()))
									{
										for (Node operationNode = eventNode.getFirstChild(); operationNode != null; operationNode = operationNode.getNextSibling())
										{
											if (operationNode.getAttributes() == null)
												continue;
											String operationName = operationNode.getNodeName();
											try
											{
												QuestOperation oper = OperationFactory.newOperation(operationName, operationNode.getAttributes(), quest);
												event.addOperation(oper);
											}
											catch (Exception ex)
											{
												_log.info("[QuestsParser] Error on Quest :" + questId + " Operation " + operationName + " loading failure: " + ex.getMessage());
											}
										}
									}
								}
								quest.addEvent(event);
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
