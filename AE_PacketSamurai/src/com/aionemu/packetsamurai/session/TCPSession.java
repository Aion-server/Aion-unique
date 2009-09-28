package com.aionemu.packetsamurai.session;


import com.aionemu.packetsamurai.PacketSamurai;
import com.aionemu.packetsamurai.protocol.Protocol;
import com.aionemu.packetsamurai.session.packetbuffer.DefaultPacketBuffer;
import com.aionemu.packetsamurai.session.packetbuffer.PacketBuffer;

import jpcap.packet.TCPPacket;

/**
 * @author Ulysses R. Ribeiro
 *
 */
public class TCPSession extends Session
{
    private PacketBuffer _csPacketBuffer;
    private PacketBuffer _scPacketBuffer;

    private TCPPacketBuffer _scTCPPacketBuffer = new TCPPacketBuffer();
    private TCPPacketBuffer _csTCPPacketBuffer = new TCPPacketBuffer();
    
    public TCPSession(long sessionId, Protocol protocol, String prefix, boolean crypted)
    {
        this(sessionId, protocol, prefix, crypted, true);
    }
    
    public TCPSession(long sessionId, Protocol protocol, String prefix, boolean crypted, boolean decrypt)
    {
        super(sessionId, protocol, prefix, crypted, decrypt);
        this.initBuffers();
    }
    
    private void initBuffers()
    {
        String pBufType = this.getProtocol().getPacketBufferType();
        Class<?> clazz = null;
        try
        {
            clazz = Class.forName("com.aionemu.packetsamurai.session.packetbuffer."+pBufType);
            if(clazz == null)
            {
                PacketSamurai.getUserInterface().log("Wrong PacketBuffer type in "+this.getProtocol().getFileName()+" defaulting to DefaultPacketBuffer");
                _csPacketBuffer = new DefaultPacketBuffer();
                _scPacketBuffer = new DefaultPacketBuffer();
                return;
            }
            _csPacketBuffer = (PacketBuffer) clazz.newInstance();
            _scPacketBuffer = (PacketBuffer) clazz.newInstance();
        }
        catch (Exception e)
        {
            PacketSamurai.getUserInterface().log("Wrong PacketBuffer type in "+this.getProtocol().getFileName()+" defaulting to DefaultPacketBuffer");
            _csPacketBuffer = new DefaultPacketBuffer();
            _scPacketBuffer = new DefaultPacketBuffer();
        }
        _csPacketBuffer.setProtocol(this.getProtocol());
        _scPacketBuffer.setProtocol(this.getProtocol());
    }
    
    public void receivePacket(TCPPacket p, boolean fromServer, long time)
    {
        int size;
        if (fromServer)
        {
            // sequence packets from server
            if (p.data.length > 0)
            {
                _scTCPPacketBuffer.add(p);
            }

            // ack on the other side
            _csTCPPacketBuffer.ack(p);
            // process pos ack
            for (TCPPacket packet : _csTCPPacketBuffer.getSequencedPackets())
            {
                _csPacketBuffer.addData(packet.data);
                while ((size = _csPacketBuffer.nextAvaliablePacket()) > 0)
                {
                    byte[] header = new byte[2];
                    byte[] packetData = new byte[size];
                    _csPacketBuffer.getNextPacket(header, packetData);
                    //System.out.println("TCPSession : receivePacket : New Full Packet (ClientPacket) size="+size+" time="+time);
                    this.addPacket(packetData, !fromServer,time);
                }
            }
            _csTCPPacketBuffer.flush();
        }
        else //from client
        {
            // sequence packets from server
            if (p.data.length > 0)
            {
                _csTCPPacketBuffer.add(p);
            }

            // ack on the other side
            _scTCPPacketBuffer.ack(p);
            // process pos ack
            for (TCPPacket packet : _scTCPPacketBuffer.getSequencedPackets())
            {
                
                if (packet.data.length > 0)
                {
                    _scPacketBuffer.addData(packet.data);
                    while ((size = _scPacketBuffer.nextAvaliablePacket()) > 0)
                    {
                        byte[] header = new byte[2];
                        byte[] packetData = new byte[size];
                        _scPacketBuffer.getNextPacket(header, packetData);
                        //System.out.println("TCPSession : receivePacket : New Full Packet (ServerPacket) size="+size+" time="+time);
                        this.addPacket(packetData, !fromServer,time);
                    }
                }
                
                // Connection end?
                if (packet.fin || packet.rst)
                {
                    PacketSamurai.getUserInterface().log("[S -> C] TCP Sequencing Buffer Report: Pending: "+_scTCPPacketBuffer.getPendingSequencePackets());
                    PacketSamurai.getUserInterface().log("[S -> C] TCP Data Buffer Report: Pending: "+_scPacketBuffer.nextAvaliablePacket());
                    PacketSamurai.getUserInterface().log("[C -> S] TCP Sequencing Buffer Report: Pending: "+_csTCPPacketBuffer.getPendingSequencePackets());
                    PacketSamurai.getUserInterface().log("[C -> S] TCP Data Buffer Report: Pending: "+_csPacketBuffer.nextAvaliablePacket());
                    PacketSamurai.getUserInterface().log("Connection ended. (FIN: "+packet.fin+" - RST: "+packet.rst+")");
                    this.saveSession();
                    this.close();
                    return;
                }
            }
            _scTCPPacketBuffer.flush();
            
        }
    }

    public void receivePacket(TCPPacket p, boolean fromServer)
    {
        this.receivePacket(p, fromServer, (p.sec * 1000) + (p.usec/1000));
    }
    
    public void close()
    {
        GameSessionTable.getInstance().removeGameSession(this.getSessionId());
    }
}
