package com.aionemu.gameserver.utils.chathandlers;

import java.lang.reflect.Modifier;

import org.apache.log4j.Logger;

import com.aionemu.commons.scripting.classlistener.ClassListener;
import com.aionemu.commons.scripting.classlistener.DefaultClassListener;
import com.aionemu.commons.utils.ClassUtils;
import com.google.inject.Injector;

/**
 * Created on: 12.09.2009 14:13:24
*
* @author Aquanox
*/
class ChatHandlersLoader
		extends DefaultClassListener
		implements ClassListener
{
	private static final Logger logger = Logger.getLogger(ChatHandlersLoader.class);

	private final Injector injector;
	
	private final AdminCommandChatHandler adminCCH;

	public ChatHandlersLoader(Injector injector, AdminCommandChatHandler handler)
	{
		this.injector = injector;
		this.adminCCH = handler;
	}

	@Override
	public void postLoad(Class<?>[] classes)
	{
		for (Class<?> c : classes)
		{
			if (logger.isDebugEnabled())
				logger.debug("Load class " + c.getName());

			if (!isValidClass(c))
				continue;

			if (ClassUtils.isSubclass(c, AdminCommand.class))
			{
				adminCCH.registerAdminCommand((AdminCommand) injector.getInstance(c));
			}
		}

		// call onClassLoad()
		super.postLoad(classes);

		logger.info("Loaded " + adminCCH.getSize() + " admin command handlers.");
	}

	@Override
	public void preUnload(Class<?>[] classes)
	{
		if (logger.isDebugEnabled())
			for (Class<?> c : classes)// debug messages
				logger.debug("Unload class " + c.getName());

		// call onClassUnload()
		super.preUnload(classes);

		adminCCH.clearHandlers();// unload all admin handlers.
	}

	public boolean isValidClass(Class<?> clazz)
	{
		final int modifiers = clazz.getModifiers();

		if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers))
			return false;

		if (!Modifier.isPublic(modifiers))
			return false;

		return true;
	}
}
