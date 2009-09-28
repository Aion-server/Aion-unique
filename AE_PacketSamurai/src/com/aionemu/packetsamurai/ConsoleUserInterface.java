/**
 * 
 */
package com.aionemu.packetsamurai;

import java.awt.SplashScreen;

/**
 * @author Ulysses R. Ribeiro
 *
 */
public class ConsoleUserInterface implements IUserInterface
{

    public ConsoleUserInterface()
    {
        SplashScreen.getSplashScreen().close();
    }
    
    public void log(String text)
    {
        System.out.println(text);
    }
    
    public void close()
    {
        PacketSamurai.saveConfig();
    }
}
