/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.utils.idfactory;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.google.inject.BindingAnnotation;

/**
 * This annotation is used to mark references to IDFactory, which should be injected by Injector.<br>
 * Dependency injector will recognize variable marked with this annotation and will inject proper IDFactory.<br>
 * <br>
 * 
 * IDFactory marked with this annotation should be use to obtain objIds for instances of all classes subclassing
 * {@link AionObject}
 * 
 * @author Luno
 * 
 */
@BindingAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target( { FIELD, PARAMETER, LOCAL_VARIABLE, METHOD })
public @interface IDFactoryAionObject
{

}
