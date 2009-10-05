package com.aionemu.gameserver.model.quests.qparser;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.aionemu.gameserver.model.quests.qholder.AbstractHolder;

public abstract class AbstractParser
{
       protected final Logger _log = Logger.getLogger(getClass().getName());
       protected AbstractHolder _holder;

       protected final void parseDocument(File f) throws Exception
       {
               DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
               factory.setValidating(false);
               factory.setIgnoringComments(true);
               Document doc = factory.newDocumentBuilder().parse(f);

               for (Node start0 = doc.getFirstChild(); start0 != null; start0 = start0.getNextSibling())
               {
                       readData(start0);
               }
       }

       protected abstract void readData(Node node);

       protected abstract void parse();

       public void reload()
       {
               _log.info("[" + getClass().getSimpleName() + "] reload start...");
               _holder.clear();
               parse();
               //_holder.log();
       }
}