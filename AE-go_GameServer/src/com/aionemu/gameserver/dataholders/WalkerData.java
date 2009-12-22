package com.aionemu.gameserver.dataholders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.walker.WalkerTemplate;

/**
 * @author KKnD
 */
@XmlRootElement(name = "npc_walker")
@XmlAccessorType(XmlAccessType.FIELD)
public class WalkerData
{
	@XmlElement(name = "walker_template")
	private List<WalkerTemplate> walkerlist;
	
	private Map<Integer, WalkerTemplate>	walkerlistData	= new HashMap<Integer, WalkerTemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(WalkerTemplate route: walkerlist)
		{
			walkerlistData.put(route.getRouteId(), route);
		}
	}
	
	public int size()
	{
		return walkerlistData.size();
	}

	public WalkerTemplate getWalkerTemplate(int id)
	{
		return walkerlistData.get(id);
	}

}
