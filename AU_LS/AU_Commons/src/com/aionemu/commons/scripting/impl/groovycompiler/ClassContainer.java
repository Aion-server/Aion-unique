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

package com.aionemu.commons.scripting.impl.groovycompiler;

import java.util.Arrays;

/**
 * @author SoulKeeper
 */
public class ClassContainer {

	private byte[] bytecode;

	private Class<?> definedClass;

	public ClassContainer(byte[] bytecode) {
		this.bytecode = bytecode;
	}

	public byte[] getBytecode() {
		return bytecode;
	}

	public void setBytecode(byte[] bytecode) {
		this.bytecode = bytecode;
	}

	public Class<?> getDefinedClass() {
		return definedClass;
	}

	public void setDefinedClass(Class<?> definedClass) {
		this.definedClass = definedClass;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ClassContainer)) return false;

		ClassContainer that = (ClassContainer) o;

		return Arrays.equals(bytecode, that.bytecode);

	}

	@Override
	public int hashCode() {
		return bytecode != null ? Arrays.hashCode(bytecode) : 0;
	}
}
