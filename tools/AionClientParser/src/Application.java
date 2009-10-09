/*
 * This file is part of aion-unique <aion-unique.com>.
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.aionunique.clientparser.gen.items.ClientItem;
import com.aionunique.clientparser.gen.items.ClientItems;
import com.aionunique.clientparser.gen.npcs.Npc;
import com.aionunique.clientparser.gen.npcs.NpcClients;
import com.aionunique.clientparser.gen.npctemplates.NpcTemplate;
import com.aionunique.clientparser.gen.npctemplates.NpcTemplates;
import com.aionunique.clientparser.gen.npctemplates.ObjectFactory;
import com.aionunique.clientparser.gen.npctemplates.Stats;
import com.aionunique.clientparser.gen.npctemplates.TemplateEquipment;
import com.aionunique.clientparser.gen.strings.ClientString;
import com.aionunique.clientparser.gen.strings.Strings;

/**
 * 
 * @author ATracer
 *
 * java -jar ..\lib\jaxb-xjc.jar -p com.aionunique.clientparser.gen.npcs client_npcs.xsd
 * java -jar ..\lib\jaxb-xjc.jar -p com.aionunique.clientparser.gen.strings client_strings.xsd
 * java -jar ..\lib\jaxb-xjc.jar -p com.aionunique.clientparser.gen.items client_items.xsd
 * java -jar ..\lib\jaxb-xjc.jar -p com.aionunique.clientparser.gen.npctemplates npc_templates.xsd
 */
public class Application 
{
	private static List<Npc> npcList;
	private static List<ClientItem> itemList;
	private static List<ClientString> stringList;
	
	private static Map<String, Integer> itemNameIdMap = new HashMap<String, Integer>();
	private static Map<String, ClientString> stringNameMap = new HashMap<String, ClientString>();
	
	public static void main(String[] args) throws JAXBException 
	{
		long startTime = System.currentTimeMillis();
		
		parseNpcs();
		parseItems();
		parseStrings();
		
		createItemMap();
		createStringMap();
		
		createTempalteXml();
		
		long processTime = (System.currentTimeMillis() - startTime) / 1000;
		System.out.println("Processed in " + processTime +  " seconds");
	}

	private static void parseNpcs() throws JAXBException 
	{
		JAXBContext jc = JAXBContext.newInstance("com.aionunique.clientparser.gen.npcs");
		Unmarshaller unmarshaller = jc.createUnmarshaller();

		NpcClients collection= (NpcClients)
		unmarshaller.unmarshal(new File( "src/client_npcs.xml"));
		npcList = collection.getNpcClient();
		
		System.out.println("Size of npcs: " + collection.getNpcClient().size());
	}

	private static void parseItems() throws JAXBException 
	{
		JAXBContext jc = JAXBContext.newInstance("com.aionunique.clientparser.gen.items");
		Unmarshaller unmarshaller = jc.createUnmarshaller();

		ClientItems collection= (ClientItems)
		unmarshaller.unmarshal(new File( "src/client_items.xml"));
		itemList = collection.getClientItem();
		
		System.out.println("Size of items: " + collection.getClientItem().size());
	}

	private static void parseStrings() throws JAXBException 
	{
		JAXBContext jc = JAXBContext.newInstance("com.aionunique.clientparser.gen.strings");
		Unmarshaller unmarshaller = jc.createUnmarshaller();

		Strings collection= (Strings)
		unmarshaller.unmarshal(new File( "src/client_strings.xml"));
		stringList = collection.getString();
		
		System.out.println("Size of strings: " + collection.getString().size());
	}
	
	private static void createItemMap()
	{
		for(ClientItem item : itemList)
		{
			itemNameIdMap.put(item.getName(), item.getId());
		}
	}
	
	private static void createStringMap()
	{
		for(ClientString item : stringList)
		{
			stringNameMap.put(item.getName(), item);
		}
	}
	
	private static void createTempalteXml()
	{
		ObjectFactory objFactory = new ObjectFactory();

		NpcTemplates collection= 
			   (NpcTemplates) objFactory.createNpcTemplates();
		List<NpcTemplate> npcTemplateList =  collection.getNpcTemplate();
		
		for(Npc npc : npcList)
		{
			NpcTemplate newTemplate = new NpcTemplate();
			newTemplate.setNpcId(npc.getId());
			
			ClientString clString = stringNameMap.get(npc.getName());
			if(clString != null)
			{
				newTemplate.setNameId(clString.getId());
			}
			
			newTemplate.setLevel(npc.getHpGaugeLevel());
			
			Stats stats = new Stats();
			stats.setRunSpeed(npc.getMoveSpeedNormalRun());
			stats.setRunSpeedFight(npc.getMoveSpeedCombatRun());
			stats.setWalkSpeed(npc.getMoveSpeedNormalWalk());
			
			newTemplate.setStats(stats);
			
			//TODO
			TemplateEquipment equipment = new TemplateEquipment();
			
			newTemplate.setEquipment(equipment);
			npcTemplateList.add(newTemplate);
		}

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance("com.aionunique.clientparser.gen.npctemplates");
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					   new Boolean(true));
			marshaller.marshal(collection,
					   new FileOutputStream("npc_templates.xml"));
		
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
