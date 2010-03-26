package com.aionemu.gameserver.itemengine.actions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * @author Nemiroff
 *         Date: 17.12.2009
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestStartAction")
public class QuestStartAction extends AbstractItemAction {

    @XmlAttribute
	protected int questid;

    @Override
	public void act(Player player, Item parentItem, Item targetItem)
	{
		
	}
}
