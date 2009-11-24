package com.aionemu.packetsamurai.parser.valuereader;
/*
 * This file is part of aion-unique <aion-unique.com>.
 *
 *     Aion-unique is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Aion-unique is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.aionemu.packetsamurai.PacketSamurai;
import com.aionemu.packetsamurai.gui.Main;
import com.aionemu.packetsamurai.parser.datatree.StringValuePart;
import com.aionemu.packetsamurai.parser.datatree.ValuePart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Nemiroff
 */

public class TextReader implements Reader {
    public <T extends Enum<T>> T getEnum(ValuePart part) {
        return null;
    }

    public boolean loadReaderFromXML(Node n) {
        return true;
    }

    public String read(ValuePart part) {
        if (part instanceof StringValuePart) {
            return ((StringValuePart) part).getStringValue();
        }
        PacketSamurai.getUserInterface().log("Text ValueReader set on a non String part: " + part.getModelPart().getName());
        return "";
    }

    public JComponent readToComponent(ValuePart part) {
        JButton view = new JButton("View");
        view.addActionListener(new ButtonActionListener(this.read(part)));
        view.setActionCommand("clicked");
        return view;
    }


    public boolean saveReaderToXML(Element element, Document doc) {
        return true;
    }

    public boolean supportsEnum() {
        return false;
    }

    class ButtonActionListener implements ActionListener {
        private String _xml;

        public ButtonActionListener(String html) {
            _xml = html;
        }

        public void actionPerformed(ActionEvent e) {
            JDialog dlg = new JDialog(((Main) PacketSamurai.getUserInterface()).getMainFrame(), "Text");
            dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dlg.setSize(350, 400);
            dlg.setLocationRelativeTo(((Main) PacketSamurai.getUserInterface()).getMainFrame());

            JEditorPane sourceDisplay = new JEditorPane();
            sourceDisplay.setEditable(false);
            sourceDisplay.setContentType("text/plain");
            sourceDisplay.setText(_xml);

            dlg.add(new JScrollPane(sourceDisplay));
            dlg.setVisible(true);
        }
    }
}
