package com.aionemu.packetsamurai.session;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;


import com.aionemu.packetsamurai.Captor;
import com.aionemu.packetsamurai.PacketSamurai;
import com.aionemu.packetsamurai.gui.Main;
import com.aionemu.packetsamurai.protocol.Protocol;

/**
 * @author Ulysses R. Ribeiro
 *
 */
public class GameSessionTable 
{
	private Map<Long, PSLogSession> _gameSessionTable;
	
	private static class SingletonHolder
	{
		private final static GameSessionTable singleton = new GameSessionTable();
	}
	
	public static GameSessionTable getInstance()
	{
		return SingletonHolder.singleton;
	}
	
	private GameSessionTable()
	{
		_gameSessionTable = new FastMap<Long, PSLogSession>();
	}
	
	public TCPSession newGameSession(long sessionId, int port, InetAddress serverAddr, InetAddress clientAddr) throws IOException
	{
	    Protocol protocol = Captor.getActiveProtocolForPort(port);
	    if (protocol != null)
	    {
	        PSLogSession gameSession = new PSLogSession(sessionId, protocol, "live", true, serverAddr, clientAddr);
	        _gameSessionTable.put(sessionId, gameSession);
	        if (PacketSamurai.getUserInterface() instanceof Main)
	        {
	            ((Main) PacketSamurai.getUserInterface()).showSession(gameSession, true);
	        }
	        PacketSamurai.getUserInterface().log("Started to log new Session (ID: "+gameSession.getSessionId()+")");
	        return gameSession;
	    }
        throw new IllegalStateException("ERROR: Received packet on port ("+port+") but there is no active protocol for this port.");
	}
	
	public void removeGameSession(long sessionId)
	{
		_gameSessionTable.remove(sessionId);
	}
	
	public boolean sessionExists(long sessionId)
	{
		return _gameSessionTable.containsKey(sessionId);
	}
	
	public TCPSession getSession(long sessionId)
	{
		return _gameSessionTable.get(sessionId);
	}
	
	public List<TCPSession> getSessions()
	{
		FastList<TCPSession> sessions = new FastList<TCPSession>();
		sessions.addAll(_gameSessionTable.values());
		return sessions;
	}
}
