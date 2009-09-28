/**
 * 
 */
package com.aionemu.packetsamurai.session;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ulysses R. Ribeiro
 *
 */
public class GameSessionViewer
{
	private List<DataPacket> _logPackets;
	private Session _session;
	
	public GameSessionViewer(Session s)
	{
		_session = s;
		_logPackets = new ArrayList<DataPacket>(s.getPackets().size());
		_logPackets.addAll(s.getPackets());
	}
	
	public List<DataPacket> getAllPackets()
	{
		return _logPackets;
	}
	
	public DataPacket getPacket(int index)
	{
		return _logPackets.get(index);
	}
	
	public Session getSession()
	{
		return _session;
	}
}
