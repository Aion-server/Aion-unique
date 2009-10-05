package com.aionemu.gameserver.model.quests.qparser;

import java.io.File;

public abstract class AbstractFileParser extends AbstractParser
{
       private String _file;

       protected AbstractFileParser(String file)
       {
               _file = file;
       }

       @Override
       protected final void parse()
       {
               try
               {
                       File file = new File(_file);

                       if (!file.exists())
                       {
                               _log.info("[" + getClass().getSimpleName() + "] file " + file.getAbsolutePath() + " not exists");
                               return;
                       }
                       parseDocument(file);
               }
               catch (Exception e)
               {
                       _log.info("[" + getClass().getSimpleName() + "] parse(): error " + e);
                       e.printStackTrace();
               }

               if (_holder != null)
               {
                       _holder.log();
               }
       }
}