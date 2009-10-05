package com.aionemu.gameserver.model.quests.qparser;

import java.io.File;

public abstract class AbstractDirParser extends AbstractParser
{
       private String _root;
       private String _ignoringFile;

       protected AbstractDirParser(String root, String ignoringFile)
       {
               _root = root;
               _ignoringFile = ignoringFile;
       }

       @Override
       protected final void parse()
       {
               parse(_root);
               _holder.log();
       }

       protected final void parse(String root)
       {
               File file = null;
               try
               {
                       File dir = new File(root);

                       if (!dir.exists())
                       {
                               _log.info("[" + getClass().getSimpleName() + "] Dir " + dir.getAbsolutePath() + " not exists");
                               return;
                       }

                       File[] files = dir.listFiles();
                       for (File f : files)
                       {
                               if (f.isDirectory())
                               {
                                       parse(f.getAbsolutePath());
                               }
                               else if (f.getName().endsWith(".xml") && !f.getName().equals(_ignoringFile)) 
                               {
                                       file = f;
                                       parseDocument(f);
                               }
                       }
               }
               catch (Exception e)
               {
                       _log.info("[" + getClass().getSimpleName() + "] parse(): error " + e + " in file " + file.getName());
                       e.printStackTrace();
               }
       }
}