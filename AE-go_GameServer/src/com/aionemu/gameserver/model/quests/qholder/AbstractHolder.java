package com.aionemu.gameserver.model.quests.qholder;

import org.apache.log4j.Logger;

public abstract class AbstractHolder
{
       protected Logger _log = Logger.getLogger(getClass().getSimpleName());

       public void log()
       {
               _log.info(String.format("[%s] load %d %s count.", getClass().getSimpleName(), size(), getClass().getSimpleName().replace("Holder", "").toLowerCase()));
       }

       public abstract int size();

       public abstract void clear();
}