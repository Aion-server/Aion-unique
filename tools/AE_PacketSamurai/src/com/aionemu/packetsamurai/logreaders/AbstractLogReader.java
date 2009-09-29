package com.aionemu.packetsamurai.logreaders;

import java.io.IOException;
import java.util.Set;


import com.aionemu.packetsamurai.PacketSamurai;
import com.aionemu.packetsamurai.protocol.Protocol;
import com.aionemu.packetsamurai.protocol.ProtocolManager;
import com.aionemu.packetsamurai.session.Session;

import javolution.util.FastSet;

/**
 * 
 * @author Ulysses R. Ribeiro
 * @author Gilles Duboscq
 *
 */
public abstract class AbstractLogReader
{
    protected Set<Session> _sessions;
    protected String _fileName;

    protected AbstractLogReader(String filename) throws IOException
    {
        _fileName = filename;
        _sessions = new FastSet<Session>();
    }
    
    public void parse() throws IOException
    {
        long time = System.currentTimeMillis();
        this.parseHeader();
        this.parsePackets();
        closeFile();
        if(PacketSamurai.VERBOSITY.ordinal() >= PacketSamurai.VerboseLevel.VERBOSE.ordinal())
            System.out.println("Log parsed in "+(System.currentTimeMillis() - time)+"ms");
    }

    public Set<Session> getSessions()
    {
        return _sessions;
    }
    
    /**
     * if this is true, then you can get a partial Session just after loading the headers and getSessions will return a set containing only 1 session
     * @return
     */
    public abstract boolean supportsHeaderReading();
    
    public abstract boolean parsePackets() throws IOException;
    
    public abstract boolean parseHeader() throws IOException;
    
    protected abstract void closeFile() throws IOException;

    protected abstract String getAditionalName();
    
    protected abstract String getFileExtension();
    
    public boolean hasContinuation()
    {
        return false;
    }
    
    /**
     * Attempts to retrieve the protocol by the specified port, using the following methodology:<BR>
     * <li>If there no protocol loaded for the specified port the user is prompted to choose one from all the loaded ones.</li>
     * <li>If there only one protocol loaded for the specified port its intantly returned.</li>
     * <li>If there more then one protocol loaded for the specified port the user is prompted to choose between them.</li>
     * @param port
     * @return The protocol as specified above
     */
    public static Protocol getLogProtocolByPort(int port)
    {
        Protocol p = null;
        Set<Protocol> protos = ProtocolManager.getInstance().getProtocolForPort(port);
        if (protos == null || protos.isEmpty())
        {
            p = ProtocolManager.promptUserToChoose("Choose the protocol for this log.");
        }
        else if (protos.size() == 1)
        {
            p = protos.iterator().next();
        }
        else
        {
            p = ProtocolManager.promptUserToChoose(protos, "Choose the protocol for this log.");
        }
        return p;
    }
    
    public static AbstractLogReader getLogReaderForFile(String filename)
    {
        //maybe we'd better register the readers and select wth the getExtension
        AbstractLogReader reader = null;
        try
        {
            if (filename.endsWith("pcap") || filename.endsWith("cap"))
            {
                reader = new PCapReader(filename);
            }
            else if(filename.endsWith("psl")){
            	reader = new PSLReader(filename);
            }
            else
            {
                PacketSamurai.getUserInterface().log("ERROR: Failed to open file, unsupported extension.");
                return null;
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return reader;
    }
}
