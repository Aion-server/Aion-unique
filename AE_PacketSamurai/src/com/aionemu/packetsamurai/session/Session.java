package com.aionemu.packetsamurai.session;

import java.io.IOException;
import java.net.Inet4Address;


import com.aionemu.packetsamurai.PacketSamurai;
import com.aionemu.packetsamurai.crypt.NullCrypter;
import com.aionemu.packetsamurai.crypt.ProtocolCrypter;
import com.aionemu.packetsamurai.logwriters.PSLogWriter;
import com.aionemu.packetsamurai.protocol.Protocol;
import com.aionemu.packetsamurai.protocol.protocoltree.PacketFamilly.packetDirection;

import javolution.util.FastList;
/**
 * @author Ulysses R. Ribeiro
 * @author Gilles Duboscq
 *
 */
public class Session 
{
	private long _sessionId;
	
    private ProtocolCrypter _crypt;
	
	private Protocol _protocol;
	
	private String _sessionName;
	
	private FastList<DataPacket> _packets;
	
	private Runnable _newPacketNotification;

	private boolean _shown;

    private Inet4Address _clientIP;

    private Inet4Address _serverIP;
    
    private String _serverType;
    
    private long _analyserBits;

    private String _comments;
    
    private boolean _decrypt;

    private boolean _parse;
    
    public Session(long sessionId, Protocol protocol, String prefix, boolean crypted, boolean decrypt)
    {
        _sessionName = prefix+sessionId;
        _packets = new FastList<DataPacket>();
        _sessionId = sessionId;
        _protocol = protocol;
        _decrypt = decrypt || !crypted;
        _parse = PacketSamurai.PARSER_ACTIVE;
        init(crypted);
    }
	
	public Session(long sessionId, Protocol protocol, String prefix, boolean crypted)
	{
        this(sessionId, protocol, prefix, crypted, true);
	}
    
    private void init(boolean crypted)
    {
        if (crypted && _decrypt)
        {
            try
            {
                Class<?> clazz = Class.forName("com.aionemu.packetsamurai.crypt."+_protocol.getEncryption()+"Crypter");
                _crypt = (ProtocolCrypter) clazz.newInstance();
            }
            catch (SecurityException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (ClassCastException e)
            {
                e.printStackTrace();
            }
            catch (ClassNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IllegalArgumentException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (InstantiationException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else
        {
            _crypt = new NullCrypter();
        }
        _crypt.setProtocol(_protocol);
    }

    /**
     * <li>Decrypts the raw data received</li>
     * <li>Adds a DataPacket to the packetList, instantiated from the decrypted data</li>
     * <li>Runs the packet notification, if any</li>
     * 
     * @param data the raw data WITHOUT header
     * @param fromServer should be true if packet came from server, false otherwise
     * @param time (miliseconds)
     */
	public void addPacket(byte[] data, boolean fromServer, long time)
	{
        packetDirection direction = (fromServer ? packetDirection.serverPacket : packetDirection.clientPacket);
        
        if (_decrypt)
        {
            _crypt.decrypt(data, direction);
        }
        
		_packets.add(new DataPacket(data, direction, time, _protocol,_parse));
        
        if (_newPacketNotification != null)
        {
            _newPacketNotification.run();
        }
	}
	
    public long getSessionId()
    {
        return _sessionId;
    }
    
    public String getSessionName()
    {
        return _sessionName;
    }
    
	public FastList<DataPacket> getPackets()
	{
		return _packets;
	}
	
	public void setPackets(FastList<DataPacket> packets){
		_packets=packets;
	}
	
	public void setNewPacketNotification(Runnable r)
	{
		_newPacketNotification = r;
	}

	public void setShown(boolean b)
	{
		_shown = b;
	}
	
	public boolean isShown()
	{
		return _shown;
	}

    public void setClientIp(Inet4Address ip)
    {
        _clientIP = ip;
    }
    
    public Inet4Address getClientIp()
    {
        return _clientIP;
    }

    public void setServerIp(Inet4Address ip)
    {
        _serverIP = ip;        
    }
    
    public Inet4Address getServerIp()
    {
        return _serverIP;
    }
    
    public Protocol getProtocol()
    {
        return _protocol;
    }
    
    public String getServerType()
    {
        return _serverType;
    }
    
    public void setServerType(String type)
    {
        _serverType = type;
    }
    
    public long getAnalyserBitSet()
    {
        return _analyserBits;
    }
    
    public void setAnalyserBitSet(long bits)
    {
        _analyserBits = bits;
    }
    
    
    public String getComments()
    {
        return _comments;
    }
    
    public void setComments(String com)
    {
        _comments = com;
    }

    public void saveSession()
    {
        try
        {
            PSLogWriter writer = new PSLogWriter(this);
            writer.writeLog();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void unloadPackets()
    {
        _packets.clear();        
    }
    
    public boolean isDecrypted()
    {
        return _decrypt;
    }
}

