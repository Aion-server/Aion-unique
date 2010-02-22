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
package com.aionemu.commons.scripting.classlistener;

/**
 * This interface implements listener that is called post class load/before class unload.<br>
 * Default implementation is: {@link DefaultClassListener}
 * 
 * @author SoulKeeper
 */
public interface ClassListener
{
	/**
	 * This method is invoked after classes were loaded. As areguments are passes all loaded classes
	 * 
	 * @param classes
	 */
	public void postLoad(Class<?>[] classes);

	/**
	 * This method is invoked before class unloading. As argument are passes all loaded classes
	 * 
	 * @param classes
	 */
	public void preUnload(Class<?>[] classes);
}
