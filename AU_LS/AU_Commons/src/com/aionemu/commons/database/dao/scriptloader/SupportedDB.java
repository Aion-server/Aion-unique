/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.commons.database.dao.scriptloader;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * @author SoulKeeper
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SupportedDB
{

	/**
	 * Database name, for instance, MySQL Server Community Edition v 5.1. will be "MySQL"
	 * 
	 * @return databae name
	 */
	String name();

	/**
	 * Database major version, for instance, MySQL Server Community Edition v 5.1. will be "5"
	 * 
	 * @return major version
	 */
	int majorVersion();

	/**
	 * Database minor version, for instance, MySQL Server Community Edition v 5.1. will be "1"
	 * 
	 * @return minor version
	 */
	int minorVersion() default 0;

	/**
	 * Comparator that should be used to compare database versions.
	 * 
	 * @return comparator
	 */
	ComparatorType majorVersionComparator() default ComparatorType.GREATER_OR_EQUAL;

	/**
	 * Comparator that should be usedto compare database versions
	 * 
	 * @return comparator
	 */
	ComparatorType minorVersionComparator() default ComparatorType.GREATER_OR_EQUAL;
}
