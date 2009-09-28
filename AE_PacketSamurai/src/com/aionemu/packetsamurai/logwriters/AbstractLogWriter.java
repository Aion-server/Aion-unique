package com.aionemu.packetsamurai.logwriters;

import java.io.IOException;
import java.io.RandomAccessFile;


import com.aionemu.packetsamurai.PacketSamurai;
import com.aionemu.packetsamurai.session.DataPacket;
import com.aionemu.packetsamurai.session.Session;


/**
 * 
 * @author Gilles Duboscq
 * @author Ulysses R. Ribeiro
 */
public abstract class AbstractLogWriter
{
    private String _directory;
    private String _fileName;
    protected Session _session;
    private RandomAccessFile _raFile;
    
    public AbstractLogWriter(Session session) throws IOException
    {
        this(session.getSessionName(),session);
    }
    
    public AbstractLogWriter(String filename, Session session) throws IOException
    {
        this(PacketSamurai.getConfigProperty("lastLogDir",".\\logs"), filename, session);
    }
    
    public AbstractLogWriter(String dir, String filename, Session session) throws IOException
    {
        setDirectory(dir);
        setFileName(filename);
        _session = session;
        if(!getDirectory().endsWith("/") && !getDirectory().endsWith("\\") )
        {
            setDirectory(getDirectory()+"/");
        }
        _raFile = new RandomAccessFile(getDirectory()+getFileName()+"."+getFileExtension(),"rw");
        writeHeader();
    }
    
    public void writeLog() throws IOException
    {
        writePackets();
        this.close();
    }
    
    public void close() throws IOException
    {
        _raFile.close();
    }
    
    protected void setRandomAccessFile(RandomAccessFile raFile)
    {
        _raFile = raFile;
    }
    
    public RandomAccessFile getRandomAccessFile()
    {
        return _raFile;
    }
    
    protected Session getSession()
    {
        return _session;
    }
    
    protected abstract String getFileExtension();

    protected abstract void writeHeader() throws IOException;
    
    protected abstract void writePackets() throws IOException;

    protected abstract void writePacket(DataPacket packet) throws IOException;

    /**
     * @param fileName the fileName to set
     */
    protected void setFileName(String fileName)
    {
        _fileName = fileName;
    }

    /**
     * @return the fileName
     */
    public String getFileName()
    {
        return _fileName;
    }

    /**
     * @param directory the directory to set
     */
    protected void setDirectory(String directory)
    {
        _directory = directory;
    }

    /**
     * @return the directory
     */
    public String getDirectory()
    {
        return _directory;
    }
}