package com.aionemu.gameserver.dataholders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.aionemu.gameserver.model.templates.TelelocationTemplate;

/**
 * 
 * @author orz
 * 
 */
@XmlRootElement(name = "teleport_location")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeleLocationData
{
	@XmlElement(name = "teleloc_template")
	private List<TelelocationTemplate> tlist;
	
	/** A map containing all teleport location templates */
	private Map<Integer, TelelocationTemplate>	loctlistData	= new HashMap<Integer, TelelocationTemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(TelelocationTemplate loc: tlist)
		{
			loctlistData.put(loc.getLocId(), loc);
		}
	}
	
	public int size()
	{
		return loctlistData.size();
	}


	public TelelocationTemplate getTelelocationTemplate(int id)
	{
		return loctlistData.get(id);
	}

}
