package com.aionemu.packetsamurai.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.aionemu.packetsamurai.PacketSamurai;
import com.aionemu.packetsamurai.parser.datatree.ValuePart;
import com.aionemu.packetsamurai.session.DataPacket;

/**
 * @author ATracer
 */
public class NpcTitleExporter 
{
	private List<DataPacket> packets;
	private String sessionName;
	private SortedMap<String, String> npcIdTitleMap = new TreeMap<String, String>();

	public NpcTitleExporter(List<DataPacket> packets, String sessionName) 
	{
		super();
		this.packets = packets;
		this.sessionName = sessionName;
	}

	public void parse()
	{
		String fileName = "npctitles_" + sessionName + ".txt";

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName));

			for(DataPacket packet : packets)
			{
				String name = packet.getName();
				if("SM_NPC_INFO".equals(name))
				{
					String npcId = "";
					String titleId = "";

					List<ValuePart> valuePartList = packet.getValuePartList();

					for(ValuePart valuePart : valuePartList)
					{
						String partName = valuePart.getModelPart().getName();
						if("npcId".equals(partName)){
							npcId = valuePart.readValue();
						}else if("titleId".equals(partName)){
							titleId = valuePart.readValue();
						}	
					}
					if(!"0".equals(titleId))
					{
						npcIdTitleMap.put(npcId, titleId);
					}   		
				}

			}

			out.write("NPC titles for session: " + sessionName);
			out.write("\n");
			out.write("\n");

			for(Entry<String, String> entry : npcIdTitleMap.entrySet())
			{
				StringBuilder sb = new StringBuilder();
				sb.append("<title npcid=\"");
				sb.append(entry.getKey());
				sb.append("\" titleid=\"");
				sb.append(entry.getValue());
				sb.append("\" />\n");
				out.write(sb.toString());
			}

			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PacketSamurai.getUserInterface().log("The npctitles data has been written");
	}
}
