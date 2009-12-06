/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.questEngine.types;

/**
 * @author Blackmouse
 */
public enum ConditionOperation
{
       EQUAL, // equal
       GREATER_EQUAL, // greater or equal
       GREATER, // greater then
       NOT_EQUAL, // not equal
       LESSER_EQUAL, // lesser or equal
       LESSER,
       IN,
       NOT_IN; // lesser then
}
