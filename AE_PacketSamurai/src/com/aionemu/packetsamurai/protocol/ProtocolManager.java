package com.aionemu.packetsamurai.protocol;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;
import javolution.util.FastSet;


import com.aionemu.packetsamurai.PacketSamurai;
import com.aionemu.packetsamurai.gui.ChoiceDialog;

public class ProtocolManager
{
    private Map<Integer, Set<Protocol>> _protocolsByPort;
    private Map<String, Protocol> _protocolsByName;
    private String _protocolsDir;
    
    private static class SingletonHolder
	{
		private final static ProtocolManager singleton = new ProtocolManager(PacketSamurai.getConfigProperty("protocols","./protocols"));
	}
	
	public static ProtocolManager getInstance()
	{
		return SingletonHolder.singleton;
	}
    
    private ProtocolManager(String protocolsDir)
    {
        _protocolsDir = protocolsDir;
        _protocolsByPort = new FastMap<Integer, Set<Protocol>>();
        _protocolsByName = new FastMap<String, Protocol>();
        loadProtocols();
    }
    
    public Set<Protocol> getProtocolForPort(int port)
    {
        if(!_protocolsByPort.containsKey(port))
            PacketSamurai.getUserInterface().log("ProtocolManager : Warning : can not find protocol for port "+port);
        return _protocolsByPort.get(port);
    }
    
    public Protocol getProtocolByName(String name)
    {
        if(!_protocolsByName.containsKey(name))
            PacketSamurai.getUserInterface().log("ProtocolManager : Warning : can not find protocol for name "+name);
        return _protocolsByName.get(name);
    }
    
    public Map<Integer, Set<Protocol>> getProtocolsByPort()
    {
        return _protocolsByPort;
    }
    
    public Set<Integer> getAvailablePorts()
    {
        return _protocolsByPort.keySet();
    }
    
    public String getProtocolsDirectory()
    {
        return _protocolsDir;
    }
    
    public Collection<Protocol> getProtocols()
    {
        return _protocolsByName.values();
    }

    public void loadProtocols()
    {
        _protocolsByPort.clear();
        _protocolsByName.clear();
        File dir = new File(_protocolsDir);
        if(!dir.isDirectory())
        {
            PacketSamurai.getUserInterface().log("Invalid Protocols directory ("+_protocolsDir+")");
            return;
        }
        File[] files = dir.listFiles(new FilenameFilter(){

            public boolean accept(File dir, String name)
            {
                if(name.endsWith(".xml"))
                    return true;
                return false;
            }
        
        });
        for (File f : files)
        {
            Protocol p = new Protocol(_protocolsDir+"/"+f.getName());
            if (p.isLoaded())
            {
                PacketSamurai.getUserInterface().log("Loaded protocol ["+p.getName()+"] from ("+f.getName()+")");
                Set<Protocol> protocols = _protocolsByPort.get(p.getPort());
                if(protocols == null)
                {
                    protocols = new FastSet<Protocol>();
                    _protocolsByPort.put(p.getPort(), protocols);
                }
                protocols.add(p);
                
                if(_protocolsByName.put(p.getName(),p) != null)
                {
                    PacketSamurai.getUserInterface().log("ProtocolManager : Warning : there is more than one protocol for the name "+p.getName()+" only the last one will be available through getProtocolByName.");
                }
            }
        }
    }
    
    public static Protocol promptUserToChoose(String msg)
    {
        return ProtocolManager.promptUserToChoose(ProtocolManager.getInstance().getProtocols(), msg);
    }
    
    public static Protocol promptUserToChoose(Collection<Protocol> protos, String msg)
    {
        Protocol[] prots = protos.toArray(new Protocol[protos.size()]);
        if (!protos.isEmpty())
        {
            String[][] choices = new String[1][prots.length];
            for (int i = 0; i < prots.length; i++)
            {
                choices[0][i] = prots[i].getName();
            }
            int[] ret = ChoiceDialog.choiceDialog(msg, new String[]{"Protocol"}, choices);
            if (ret != null)
            {
                return prots[ret[0]];
            }
        }
        return null;
    }
}