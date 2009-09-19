/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.dataholders.loadingutils;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.aionemu.gameserver.dataholders.StaticData;

/**
 * This class is responsible for loading xml files. It uses JAXB to do the job.<br>
 * In addition, it uses @{link {@link XmlMerger} to create input file from all xml files.
 * 
 * @author Luno
 * 
 */
public class XmlDataLoader
{
	private static final Logger	log				= Logger.getLogger(XmlDataLoader.class);

	/** File containing xml schema declaration */
	private final static String	XML_SCHEMA_FILE	= "./data/static_data/static_data.xsd";

	private static final String	CACHE_DIRECTORY	= "./cache/";
	private static final String	CACHE_XML_FILE	= "./cache/static_data.xml";
	private static final String	MAIN_XML_FILE	= "./data/static_data/static_data.xml";

	/**
	 */
	public XmlDataLoader()
	{
	}

	/**
	 * Creates {@link StaticData} object based on xml files, starting from static_data.xml
	 * 
	 * @return StaticData object, containing all game data defined in xml files
	 */
	public StaticData loadStaticData()
	{
		makeCacheDirectory();

		File cachedXml = new File(CACHE_XML_FILE);
		File cleanMainXml = new File(MAIN_XML_FILE);

		mergeXmlFiles(cachedXml, cleanMainXml);

		try
		{
			JAXBContext jc = JAXBContext.newInstance(StaticData.class);
			Unmarshaller un = jc.createUnmarshaller();
			un.setSchema(getSchema());

			return (StaticData) un.unmarshal(new File(CACHE_XML_FILE));
		}
		catch(Exception e)
		{
			log.fatal("Error while loading static data", e);
			throw new Error("Error while loading static data", e);
		}
	}

	/**
	 * Creates and returns {@link Schema} object representing xml schema of xml files
	 * 
	 * @return a Schema object.
	 */
	private Schema getSchema()
	{
		Schema schema = null;
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		try
		{
			schema = sf.newSchema(new File(XML_SCHEMA_FILE));
		}
		catch(SAXException saxe)
		{
			log.fatal("Error while getting schema", saxe);
			throw new Error("Error while getting schema", saxe);
		}

		return schema;
	}

	/** Creates directory for cache files if it doesn't already exist */
	private void makeCacheDirectory()
	{
		File cacheDir = new File(CACHE_DIRECTORY);
		if(!cacheDir.exists())
			cacheDir.mkdir();
	}

	/**
	 * Merges xml files(if are newer than cache file) and puts output to cache file. 
	 * @see XmlMerger
	 * 
	 * @param cachedXml
	 * @param cleanMainXml
	 * @throws Error is thrown if some problem occured. 
	 */
	private void mergeXmlFiles(File cachedXml, File cleanMainXml) throws Error
	{
		XmlMerger merger = new XmlMerger(cleanMainXml, cachedXml);
		try
		{
			merger.process();
		}
		catch(Exception e)
		{
			log.error("Error while merging xml files,e");
			throw new Error("Error while merging xml files", e);
		}
	}
}
