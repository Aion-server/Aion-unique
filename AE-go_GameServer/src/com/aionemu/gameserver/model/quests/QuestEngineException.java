package com.aionemu.gameserver.model.quests;

@SuppressWarnings("serial")
public class QuestEngineException extends Exception
{

       public QuestEngineException(Throwable cause)
       {
               super(cause);
       }

       public QuestEngineException(String message, Throwable cause)
       {
               super(message, cause);
       }

       public QuestEngineException(String message)
       {
               super(message);
       }

       public QuestEngineException()
       {
       }

}