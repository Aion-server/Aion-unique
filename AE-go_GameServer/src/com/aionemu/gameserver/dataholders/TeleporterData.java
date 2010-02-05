package com.aionemu.gameserver.dataholders;

import gnu.trove.TIntObjectHashMap;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.NpcTemplate;
import com.aionemu.gameserver.model.templates.teleport.TeleporterTemplate;

/**
 * This is a container holding and serving all {@link NpcTemplate} instances.<br>
 * Briefly: Every {@link Npc} instance represents some class of NPCs among which each have the same id, name, items,
 * statistics. Data for such NPC class is defined in {@link NpcTemplate} and is uniquely identified by npc id.
 * 
 * @author orz
 * 
 */
@XmlRootElement(name = "npc_teleporter")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeleporterData
{
	@XmlElement(name = "teleporter_template")
	private List<TeleporterTemplate> tlist;
	
	/** A map containing all trade list templates */
	private TIntObjectHashMap<TeleporterTemplate>	npctlistData	= new TIntObjectHashMap<TeleporterTemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(TeleporterTemplate npc: tlist)
		{
			npctlistData.put(npc.getNpcId(), npc);
		}
	}
	
	public int size()
	{
		return npctlistData.size();
	}


	/**
	 * Returns an {@link NpcTemplate} object with given id.
	 * 
	 * @param id
	 *            id of NPC
	 * @return NpcTemplate object containing data about NPC with that id.
	 */
	public TeleporterTemplate getTeleporterTemplate(int id)
	{
		return npctlistData.get(id);
	}

}
