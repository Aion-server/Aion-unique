/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.aionemu.packetsamurai.gui;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;


import com.aionemu.packetsamurai.protocol.protocoltree.PacketFamilly;
import com.aionemu.packetsamurai.protocol.protocoltree.PacketFormat;
import com.aionemu.packetsamurai.protocol.protocoltree.ProtocolNode;
import com.aionemu.packetsamurai.protocol.protocoltree.PacketFamilly.packetDirection;
import com.aionemu.packetsamurai.session.DataPacket;
import com.aionemu.packetsamurai.session.GameSessionViewer;

import javolution.util.FastMap;


/**
 * A filter dialog is attached with the SessionViewer, this allow this filter implementation to work with any protocols.
 * 
 * @author Ulysses R. Ribeiro
 */
@SuppressWarnings("serial")
public class FilterDlg extends JDialog
{
    private Map<String, Boolean> _serverFormatsFilter = new FastMap<String, Boolean>();
    private Map<String, Boolean> _clientFormatsFilter = new FastMap<String, Boolean>();
    private ViewPane _viewPane;

    private JTabbedPane _tabPane  = new JTabbedPane();
    private JButton _buttonOk = new JButton("Ok");
    private JButton _buttonApply = new JButton("Apply");

    public FilterDlg(Frame owner, ViewPane vp)
    {
        super(owner);
        this.setSize(275, 450);
        this.setLocationRelativeTo(owner);
        this.setTitle("Filter");
        
        
        _viewPane = vp;
        this.init();
    }

    public void init()
    {
        FilterActionListener fal = new FilterActionListener();

        PacketFamilly family = this.getSessionViewer().getSession().getProtocol().getClientPacketsFamilly();
        if (family != null)
            getAllFormatsName(_clientFormatsFilter, family);

        JPanel clientPanel = new JPanel();
        clientPanel.setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
        cons.gridy = 0;
        cons.anchor = GridBagConstraints.WEST;
        for (String format : _clientFormatsFilter.keySet())
        {
            JCheckBox jcb = new JCheckBox(format);
            jcb.addActionListener(fal);
            clientPanel.add(jcb, cons);
            cons.gridy++;
        }

        family = this.getSessionViewer().getSession().getProtocol().getServerPacketsFamilly();
        if (family != null)
            getAllFormatsName(_serverFormatsFilter, family);

        JPanel serverPanel = new JPanel();
        serverPanel.setLayout(new GridBagLayout());
        cons.gridy = 0;
        for (String format : _serverFormatsFilter.keySet())
        {
            JCheckBox jcb = new JCheckBox(format);
            jcb.addActionListener(fal);
            serverPanel.add(jcb, cons);
            cons.gridy++;
        }

        JScrollPane serverScrollPane = new JScrollPane(serverPanel);
        JScrollPane clientScrollPane = new JScrollPane(clientPanel);
        _tabPane.add("Client",  clientScrollPane);
        _tabPane.add("Server",  serverScrollPane);

        Box hbox = Box.createHorizontalBox();
        _buttonOk.setActionCommand("ok");
        _buttonOk.addActionListener(fal);
        _buttonApply.setActionCommand("apply");
        _buttonApply.addActionListener(fal);

        hbox.add(_buttonOk);
        hbox.add(_buttonApply);

        Box vbox = Box.createVerticalBox();
        vbox.add(_tabPane);
        vbox.add(hbox);

        this.add(vbox);
    }

    public boolean isFilterEnabledFor(DataPacket dp)
    {
        PacketFormat format = dp.getPacketFormat();
        // we can only filter packets with formats :/
        if (format == null)
            return false;
        
        if (dp.getDirection() == packetDirection.serverPacket)
        {
            if (this.getServerFilter().containsKey(format.getOpcodeStr()+" "+format.getName()))
            {
                return this.getServerFilter().get(format.getOpcodeStr()+" "+format.getName());
            }
        }
        else
        {
            if (this.getClientFilter().containsKey(format.getOpcodeStr()+" "+format.getName()))
            {
                return this.getClientFilter().get(format.getOpcodeStr()+" "+format.getName());
            }
        }
        return false;
    }

    protected Map<String, Boolean> getClientFilter()
    {
        return _clientFormatsFilter;
    }

    protected Map<String, Boolean> getServerFilter()
    {
        return _serverFormatsFilter;
    }

    private void getAllFormatsName(Map<String, Boolean> filter, PacketFamilly packetFamilly)
    {
        for (ProtocolNode node : packetFamilly.getNodes().values())
        {
            if (node instanceof PacketFamilly)
            {
                getAllFormatsName(filter, (PacketFamilly)node);
            }
            else
            {
                filter.put(((PacketFormat)node).getOpcodeStr()+" "+((PacketFormat)node).getName(), Boolean.FALSE);
            }
        }
    }

    public GameSessionViewer getSessionViewer()
    {
        return this.getViewPane().getGameSessionViewer();
    }

    public ViewPane getViewPane()
    {
        return _viewPane;
    }

    public class FilterActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent e)
        {
            if (e.getActionCommand().equals("ok"))
            {
                FilterDlg.this.setVisible(false);
            }
            else if (e.getActionCommand().equals("apply"))
            {
                FilterDlg.this.getViewPane().displaySession();
            }
            else
            {
                JCheckBox jcb = ((JCheckBox)e.getSource());
                String name = jcb.getText();
                boolean selected = jcb.isSelected();
                // is it client?
                if (FilterDlg.this._tabPane.getSelectedIndex() == 0)
                {
                    FilterDlg.this.getClientFilter().put(name, selected);
                }
                else
                {
                    FilterDlg.this.getServerFilter().put(name, selected);
                }
            }
        }
    }
}
