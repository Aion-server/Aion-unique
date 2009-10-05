package com.aionemu.gameserver.model.quests.types;

public enum ConditionOperation
{

       EQUAL("eq"), // equal
       GREATER_EQUAL("ge"), // greater or equal
       GREATER("gt"), // greater then
       NOT_EQUAL("ne"), // not equal
       LESSER_EQUAL("le"), // lesser or equal
       LESSER("lt"); // lesser then
       @SuppressWarnings("unused")
       private final String name;

       private ConditionOperation(String name)
       {
               this.name = name;
       }
}