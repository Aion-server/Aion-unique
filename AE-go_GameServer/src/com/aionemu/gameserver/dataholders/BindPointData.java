package com.aionemu.gameserver.dataholders;

import gnu.trove.TIntObjectHashMap;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.BindPointTemplate;

/**
 * 
 * @author avol
 * 
 */
@XmlRootElement(name = "bind_points")
@XmlAccessorType(XmlAccessType.FIELD)
public class BindPointData
{
	@XmlElement(name = "bind_point")
	private List<BindPointTemplate> bplist;
	
	/** A map containing all bind point location templates */
	private TIntObjectHashMap<BindPointTemplate>	bindplistData	= new TIntObjectHashMap<BindPointTemplate>();
	private TIntObjectHashMap<BindPointTemplate>	bindplistData2	= new TIntObjectHashMap<BindPointTemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(BindPointTemplate bind: bplist)
		{
			bindplistData.put(bind.getNpcId(), bind);
			bindplistData2.put(bind.getBindId(), bind);
		}
	}

	public int size()
	{
		return bindplistData.size();
	}

	public BindPointTemplate getBindPointTemplate(int npcId)
	{
		return bindplistData.get(npcId);
	}

	public BindPointTemplate getBindPointTemplate2(int bindPointId)
	{
		return bindplistData2.get(bindPointId);
	}
}
