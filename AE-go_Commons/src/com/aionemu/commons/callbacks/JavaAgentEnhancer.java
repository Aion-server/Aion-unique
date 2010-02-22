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
package com.aionemu.commons.callbacks;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;

import org.apache.log4j.Logger;

import com.aionemu.commons.utils.ExitCode;

/**
 * This class is used as javaagent to do on-class-load transformations with objects whose methods are marked by
 * {@link com.aionemu.commons.callbacks.Enhancable} annotation.<br>
 * Code is inserted dynamicly before method call and after method call.<br>
 * For implementation docs please reffer to: http://www.csg.is.titech.ac.jp/~chiba/javassist/tutorial/tutorial2.html<br>
 * <br>
 * Usage: java -javaagent:lib/ae_commons.jar
 * 
 * @author SoulKeeper
 */
public class JavaAgentEnhancer implements ClassFileTransformer
{
	/**
	 * Logger
	 */
	private static final Logger	log							= Logger.getLogger(JavaAgentEnhancer.class);

	/**
	 * Field name for callbacks map
	 */
	public static final String	FIELD_NAME_CALLBACKS		= "$$$callbacks";

	/**
	 * Field name for synchronizer
	 */
	public static final String	FIELD_NAME_CALLBACKS_LOCK	= "$$$callbackLock";

	/**
	 * Premain method that registers this class as ClassFileTransformer
	 * 
	 * @param args
	 *            arguments passed to javaagent, ignored
	 * @param instrumentation
	 *            Instrumentation object
	 */
	public static void premain(String args, Instrumentation instrumentation)
	{
		instrumentation.addTransformer(new JavaAgentEnhancer(), true);
	}

	/**
	 * This method analyzes class and adds callback support if needed.
	 * 
	 * @param loader
	 *            ClassLoader of class
	 * @param className
	 *            class name
	 * @param classBeingRedefined
	 *            not used
	 * @param protectionDomain
	 *            not used
	 * @param classfileBuffer
	 *            basic class data
	 */
	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
		ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
	{
		try
		{
			// no need to scan whole jvm boot classpath
			if(loader == null)
			{
				return null;
			}

			// classes from jvm ext dir, no need to modify
			if(loader.getClass().getName().equals("sun.misc.Launcher$ExtClassLoader"))
			{
				return null;
			}

			// actual class transformation
			return transformClass(loader, classfileBuffer);
		}
		catch(Exception e)
		{

			Error e1 = new Error("Can't transform class " + className, e);
			log.fatal(e1);

			// if it is a class from core (not a script) - terminate server
			// noinspection ConstantConditions
			if(loader.getClass().getName().equals("sun.misc.Launcher$AppClassLoader"))
			{
				Runtime.getRuntime().halt(ExitCode.CODE_ERROR);
			}

			throw e1;
		}
	}

	/**
	 * Does actual transformation
	 * 
	 * @param loader
	 *            class loader
	 * @param clazzBytes
	 *            class bytecode
	 * @return transformed class bytecode
	 * @throws Exception
	 *             is something went wrong
	 */
	protected byte[] transformClass(ClassLoader loader, byte[] clazzBytes) throws Exception
	{
		ClassPool cp = new ClassPool();
		cp.appendClassPath(new LoaderClassPath(loader));
		CtClass clazz = cp.makeClass(new ByteArrayInputStream(clazzBytes));

		Set<CtMethod> methdosToEnhance = new HashSet<CtMethod>();

		for(CtMethod method : clazz.getMethods())
		{
			if(!isEnhancable(method))
			{
				continue;
			}

			methdosToEnhance.add(method);
		}

		if(!methdosToEnhance.isEmpty())
		{
			CtClass eo = cp.get(EnhancedObject.class.getName());
			for(CtClass i : clazz.getInterfaces())
			{
				if(i.equals(eo))
				{
					throw new RuntimeException("Class already implements EnhancedObject interface, WTF???");
				}
			}

			writeEnhancedObjectImpl(clazz);

			for(CtMethod method : methdosToEnhance)
			{
				enhanceMethod(method);
			}

			return clazz.toBytecode();
		}
		else
		{
			return null;
		}
	}

	/**
	 * Resposible for method enhancing, writing service calls to method.
	 * 
	 * @param method
	 *            Method that has to be edited
	 * @throws CannotCompileException
	 *             if something went wrong
	 * @throws NotFoundException
	 *             if something went wrong
	 */
	protected void enhanceMethod(CtMethod method) throws CannotCompileException, NotFoundException
	{
		ClassPool cp = method.getDeclaringClass().getClassPool();

		method.addLocalVariable("___cbr", cp.get(CallbackResult.class.getName()));

		Annotation enhancable = null;
		for(Object o : method.getMethodInfo().getAttributes())
		{
			if(o instanceof AnnotationsAttribute)
			{
				AnnotationsAttribute attribute = (AnnotationsAttribute) o;
				enhancable = attribute.getAnnotation(Enhancable.class.getName());
				break;
			}
		}
		// noinspection ConstantConditions
		String listenerClassName = enhancable.getMemberValue("callback").toString();
		listenerClassName = listenerClassName.substring(1, listenerClassName.length() - 7);
		int paramLength = method.getParameterTypes().length;

		method.insertBefore(writeBeforeMethod(method, paramLength, listenerClassName));
		method.insertAfter(writeAfterMethod(method, paramLength, listenerClassName));
	}

	/**
	 * Code that is added in the begining of the method
	 * 
	 * @param method
	 *            method that should be edited
	 * @param paramLength
	 *            Lenght of methods parameters
	 * @param listenerClassName
	 *            Listener class that is used for method
	 * @return code that will be inserted before method
	 * @throws NotFoundException
	 *             if something went wrong
	 */
	protected String writeBeforeMethod(CtMethod method, int paramLength, String listenerClassName)
		throws NotFoundException
	{
		StringBuilder sb = new StringBuilder();
		sb.append('{');

		sb.append(" ___cbr = ");
		sb.append(CallbackHelper.class.getName()).append(".beforeCall((");
		sb.append(EnhancedObject.class.getName());
		sb.append(")this, Class.forName(\"");
		sb.append(listenerClassName).append("\", true, getClass().getClassLoader()), ");
		if(paramLength > 0)
		{
			sb.append("new Object[]{");
			for(int i = 1; i <= paramLength; i++)
			{
				sb.append("($w)$").append(i);

				if(i < paramLength)
				{
					sb.append(',');
				}
			}
			sb.append("}");
		}
		else
		{
			sb.append("null");
		}
		sb.append(");");

		sb.append("if(___cbr.isBlockingCaller()){");

		// Fake return due to javassist bug
		// $r is not available in "insertBefore"
		CtClass returnType = method.getReturnType();
		if(returnType.equals(CtClass.voidType))
		{
			sb.append("return");
		}
		else if(returnType.equals(CtClass.booleanType))
		{
			sb.append("return false");
		}
		else if(returnType.equals(CtClass.charType))
		{
			sb.append("return 'a'");
		}
		else if(returnType.equals(CtClass.byteType) || returnType.equals(CtClass.shortType)
			|| returnType.equals(CtClass.intType) || returnType.equals(CtClass.floatType)
			|| returnType.equals(CtClass.longType) || returnType.equals(CtClass.longType))
		{
			sb.append("return 0");
		}
		sb.append(";}}");

		return sb.toString();
	}

	/**
	 * Writes code that will be inserted after method
	 * 
	 * @param method
	 *            method to edit
	 * @param paramLength
	 *            lenght of method paramenters
	 * @param listenerClassName
	 *            method listener
	 * @return actual code that should be inserted
	 * @throws NotFoundException
	 *             if something went wrong
	 */
	protected String writeAfterMethod(CtMethod method, int paramLength, String listenerClassName)
		throws NotFoundException
	{
		StringBuilder sb = new StringBuilder();
		sb.append('{');

		// workaround for javassist bug, $r is not available in "insertBefore"
		if(!method.getReturnType().equals(CtClass.voidType))
		{
			sb.append("if(___cbr.isBlockingCaller()){");
			sb.append("$_ = ($r)($w)___cbr.getResult();");
			sb.append("}");
		}

		sb.append("___cbr = ").append(CallbackHelper.class.getName()).append(".afterCall((");
		sb.append(EnhancedObject.class.getName()).append(")this, Class.forName(\"");
		sb.append(listenerClassName).append("\", true, getClass().getClassLoader()), ");
		if(paramLength > 0)
		{
			sb.append("new Object[]{");
			for(int i = 1; i <= paramLength; i++)
			{
				sb.append("($w)$").append(i);

				if(i < paramLength)
				{
					sb.append(',');
				}
			}
			sb.append("}");
		}
		else
		{
			sb.append("null");
		}
		sb.append(", ($w)$_);");
		sb.append("if(___cbr.isBlockingCaller()){");
		if(method.getReturnType().equals(CtClass.voidType))
		{
			sb.append("return;");
		}
		else
		{
			sb.append("return ($r)($w)___cbr.getResult();");
		}
		sb.append("}");
		sb.append("else {return $_;}");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Implements {@link EnhancedObject on class}
	 * 
	 * @param clazz
	 *            class to edit
	 * @throws NotFoundException
	 *             if something went wrong
	 * @throws CannotCompileException
	 *             if something went wrong
	 */
	protected void writeEnhancedObjectImpl(CtClass clazz) throws NotFoundException, CannotCompileException
	{
		ClassPool cp = clazz.getClassPool();
		clazz.addInterface(cp.get(EnhancedObject.class.getName()));
		writeEnhancedOBjectFields(clazz);
		writeEnhancedObjectMethods(clazz);
	}

	/**
	 * Implements {@link EnhancedObject} fields
	 * 
	 * @param clazz
	 *            Class to add fields
	 * @throws CannotCompileException
	 *             if something went wrong
	 * @throws NotFoundException
	 *             if something went wrong
	 */
	private void writeEnhancedOBjectFields(CtClass clazz) throws CannotCompileException, NotFoundException
	{
		ClassPool cp = clazz.getClassPool();
		CtField cbField = new CtField(cp.get(List.class.getName()), FIELD_NAME_CALLBACKS, clazz);
		cbField.setModifiers(Modifier.PRIVATE);
		clazz.addField(cbField, CtField.Initializer.byExpr("new " + ConcurrentHashMap.class.getName() + "();"));

		CtField cblField = new CtField(cp.get(ReentrantLock.class.getName()), FIELD_NAME_CALLBACKS_LOCK, clazz);
		cblField.setModifiers(Modifier.PRIVATE);
		clazz.addField(cblField, CtField.Initializer.byExpr("new " + ReentrantLock.class.getName() + "();"));
	}

	/**
	 * Implements {@link EnhancedObject methods}
	 * 
	 * @param clazz
	 *            Class to add methods
	 * @throws NotFoundException
	 *             if something went wrong
	 * @throws CannotCompileException
	 *             if something went wrong
	 */
	private void writeEnhancedObjectMethods(CtClass clazz) throws NotFoundException, CannotCompileException
	{

		ClassPool cp = clazz.getClassPool();

		CtClass callbackClass = cp.get(Callback.class.getName());
		CtClass mapClass = cp.get(Map.class.getName());
		CtClass reentrantReadWriteLockClass = cp.get(ReentrantLock.class.getName());

		CtMethod method = new CtMethod(CtClass.voidType, "addCallback", new CtClass[] { callbackClass }, clazz);
		method.setModifiers(Modifier.PUBLIC);
		method.setBody("com.aionemu.commons.callbacks.CallbackHelper.addCallback($1, this);");
		clazz.addMethod(method);

		method = new CtMethod(CtClass.voidType, "removeCallback", new CtClass[] { callbackClass }, clazz);
		method.setModifiers(Modifier.PUBLIC);
		method.setBody("com.aionemu.commons.callbacks.CallbackHelper.removeCallback($1, this);");
		clazz.addMethod(method);

		method = new CtMethod(mapClass, "getCallbacks", new CtClass[] {}, clazz);
		method.setModifiers(Modifier.PUBLIC);
		method.setBody("return " + FIELD_NAME_CALLBACKS + ";");
		clazz.addMethod(method);

		method = new CtMethod(reentrantReadWriteLockClass, "getCallbackLock", new CtClass[] {}, clazz);
		method.setModifiers(Modifier.PUBLIC);
		method.setBody("return " + FIELD_NAME_CALLBACKS_LOCK + ";");
		clazz.addMethod(method);
	}

	/**
	 * Checks if annotation is present on method
	 * 
	 * @param method
	 *            Method to check
	 * @param annotation
	 *            Annotation to look for
	 * @return result
	 */
	protected boolean isAnnotationPresent(CtMethod method, Class<? extends java.lang.annotation.Annotation> annotation)
	{
		for(Object o : method.getMethodInfo().getAttributes())
		{
			if(o instanceof AnnotationsAttribute)
			{
				AnnotationsAttribute attribute = (AnnotationsAttribute) o;
				if(attribute.getAnnotation(annotation.getName()) != null)
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Checks if method is enhancable. It should be marked with {@link com.aionemu.commons.callbacks.Enhancable}
	 * annotation, be not native and not abstract
	 * 
	 * @param method
	 *            method to check
	 * @return check result
	 */
	protected boolean isEnhancable(CtMethod method)
	{
		int modifiers = method.getModifiers();
		return !(Modifier.isAbstract(modifiers) || Modifier.isNative(modifiers))
			&& isAnnotationPresent(method, Enhancable.class);
	}
}