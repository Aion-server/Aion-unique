/**
 * 
 */
package com.aionemu.packetsamurai.logreaders;

import java.io.IOException;


import com.aionemu.packetsamurai.FileCaptor;
import com.aionemu.packetsamurai.session.Session;


/**
 * @author Ulysses R. Ribeiro
 *
 */
public class PCapReader extends AbstractLogReader
{
    private FileCaptor _fCaptor;
    
    public PCapReader(String filename) throws IOException
    {
        super(filename);
        _fCaptor = new FileCaptor(filename);
    }
    
    @Override
    protected String getAditionalName()
    {
        return "pcap";
    }

    @Override
    public boolean parseHeader()
    {
        return true;
        // reading those logs is delegated to the Captor
        
    }

    @Override
    public boolean parsePackets() throws IOException
    {
//      reading those logs is delegated to the Captor
        _fCaptor.proccesCaptureFile();
        for (Session s : _fCaptor.getFileTCPSessions())
        {
            _sessions.add(s);
            s.saveSession(); //XXX Temp Hack this should be triggered by some button in the UI or something
        }
        return false;
    }

    @Override
    protected void closeFile() throws IOException
    {
        // FileCaptor will take care of that
    }

    @Override
    protected String getFileExtension()
    {
        return "pcap";
    }

    @Override
    public boolean supportsHeaderReading()
    {
        return false;
    }

}
