package com.aionemu.commons;

//~--- non-JDK imports --------------------------------------------------------

import com.aionemu.commons.services.LoggingService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Author: SoulKeeper
 */
public class Server
{
    private static Server                  server;
    private ClassPathXmlApplicationContext applicationContext;

    public void start() {
        LoggingService.init();
        applicationContext = new ClassPathXmlApplicationContext("/META-INF/server.xml");
    }

    public void stop() {
        applicationContext.close();
    }

    public static void main(String[] args) {
        server = new Server();
        server.start();
        server.stop();

        synchronized (Server.class) {
            try {
                Server.class.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();    // To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
