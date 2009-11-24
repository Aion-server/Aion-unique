package com.aionemu.packetsamurai.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileFilter;

import javolution.util.FastList;

import com.aionemu.packetsamurai.Captor;
import com.aionemu.packetsamurai.IUserInterface;
import com.aionemu.packetsamurai.PacketSamurai;
import com.aionemu.packetsamurai.gui.logrepo.LogRepoTab;
import com.aionemu.packetsamurai.gui.protocoleditor.ProtocolEditor;
import com.aionemu.packetsamurai.logreaders.AbstractLogReader;
import com.aionemu.packetsamurai.session.DataPacket;
import com.aionemu.packetsamurai.session.GameSessionTable;
import com.aionemu.packetsamurai.session.Session;
import com.aionemu.packetsamurai.utils.NpcTitleExporter;

/**
 * @author Ulysses R. Ribeiro
 */
public class Main implements IUserInterface {
	protected JFrame _frame;

	private JMenuBar _menuBar = new JMenuBar();

	private JMenu _fileMenu = new JMenu("File");
	private JMenu _editMenu = new JMenu("Edit");
	private JMenu _utilsMenu = new JMenu("Utils");
	private JMenu _toolsMenu = new JMenu("Tools");
	private JMenu _helpMenu = new JMenu("Help");

	private JMenuItem _itemClose;
	private JMenuItem _itemCloseAll;

	private JMenuItem _itemSearch;
	private JMenuItem _itemSearchNext;
	private JMenuItem _itemGoto;
	private JMenuItem _itemFilter;

	private ProtocolEditor _pEditor;

	private ActionListener _menuListener = new MenuActionListener();

	private JTabbedPane _tabPane = new JTabbedPane();

	private ConsoleTab _consoleTab;

	private ViewTab _viewerTab;

	private JDialog _selectInterfaceWindow;

	private SearchDlg _searchDlg;

	private JList _interfaceList;

	private LogRepoTab _logRepo;

	public Main() {

	}

	public void init() {
		_frame = new JFrame("Packet Samurai [aion-unique edition]");
		_frame.setLayout(new BorderLayout());
		_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Menu Bar Itens
		// File Menu
		JMenuItem itemOpen = new JMenuItem("Open");
		itemOpen.setActionCommand("Open");
		itemOpen.addActionListener(_menuListener);

		_itemClose = new JMenuItem("Close");
		_itemClose.setEnabled(false);
		_itemClose.setActionCommand("Close");
		_itemClose.addActionListener(_menuListener);

		_itemCloseAll = new JMenuItem("Close All");
		_itemCloseAll.setEnabled(false);
		_itemCloseAll.setActionCommand("CloseAll");
		_itemCloseAll.addActionListener(_menuListener);

		JMenuItem itemExit = new JMenuItem("Exit");
		itemExit.setActionCommand("Exit");
		itemExit.addActionListener(_menuListener);

		_fileMenu.add(itemOpen);
		_fileMenu.add(_itemClose);
		_fileMenu.add(_itemCloseAll);
		_fileMenu.add(itemExit);

		// Edit
		_itemSearch = new JMenuItem("Search");
		_itemSearch.setEnabled(false);
		_itemSearch.setActionCommand("Search");
		_itemSearch.setMnemonic(KeyEvent.VK_F);
		_itemSearch.addActionListener(_menuListener);
		_editMenu.add(_itemSearch);

		_itemSearchNext = new JMenuItem("Search Next");
		_itemSearchNext.setEnabled(false);
		_itemSearchNext.setActionCommand("SearchNext");
		_itemSearchNext.addActionListener(_menuListener);
		_editMenu.add(_itemSearchNext);

		_itemGoto = new JMenuItem("Go to...");
		_itemGoto.setEnabled(false);
		_itemGoto.setActionCommand("GoTo");
		_itemGoto.addActionListener(_menuListener);
		_editMenu.add(_itemGoto);

		_itemFilter = new JMenuItem("Filter");
		_itemFilter.setEnabled(false);
		_itemFilter.setActionCommand("Filter");
		_itemFilter.addActionListener(_menuListener);
		_editMenu.add(_itemFilter);

		// Utils
		JMenuItem exportTitles = new JMenuItem("Export Npc Titles");
		exportTitles.setActionCommand("ExportNpcTitles");
		exportTitles.addActionListener(_menuListener);
		_utilsMenu.add(exportTitles);

		// Tools
		JMenuItem itemSelectInterface = new JMenuItem("Select Interface");
		itemSelectInterface.setActionCommand("SelectInterface");
		itemSelectInterface.addActionListener(_menuListener);
		_toolsMenu.add(itemSelectInterface);

		JMenuItem itemEditProtocol = new JMenuItem("Edit Protocol");
		itemEditProtocol.setActionCommand("EditProtocol");
		itemEditProtocol.addActionListener(_menuListener);
		_toolsMenu.add(itemEditProtocol);

		JMenuItem itemSetActiveProtocols = new JMenuItem("Set Active Protocols");
		itemSetActiveProtocols.setActionCommand("SetActiveProtocols");
		itemSetActiveProtocols.addActionListener(_menuListener);
		_toolsMenu.add(itemSetActiveProtocols);

		// Help
		JMenuItem itemAbout = new JMenuItem("About");
		itemAbout.setActionCommand("About");
		itemAbout.addActionListener(_menuListener);
		_helpMenu.add(itemAbout);

		_menuBar.add(_fileMenu);
		_menuBar.add(_editMenu);
		_menuBar.add(_utilsMenu);
		_menuBar.add(_toolsMenu);
		_menuBar.add(_helpMenu);
		_frame.setJMenuBar(_menuBar);

		// Console Tab
		_consoleTab = new ConsoleTab();

		// Viewer Tab
		_viewerTab = new ViewTab();

		_logRepo = new LogRepoTab();

		// hotkeys
		_tabPane.registerKeyboardAction(_menuListener, "Search", KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		_tabPane.registerKeyboardAction(_menuListener, "SearchNext", KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		_tabPane.registerKeyboardAction(_menuListener, "EditProtocol", KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		_tabPane.registerKeyboardAction(_menuListener, "Filter", KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		_tabPane.add("Console", _consoleTab);
		_tabPane.add("Viewer", _viewerTab);
		_tabPane.add("Log Repository", _logRepo);

		// build the frame
		_frame.add(_tabPane, BorderLayout.CENTER);

		// add the window listeners
		addListeners();

		_frame.setMinimumSize(new Dimension(600, 400));
		_frame.setExtendedState(JFrame.MAXIMIZED_HORIZ);
		_frame.setVisible(true);
	}

	public JTabbedPane getTabPane() {
		return _tabPane;
	}

	public LogRepoTab getLogRepoTab() {
		return _logRepo;
	}

	public void log(String text) {
		_consoleTab.addText(text);
		System.err.println(text);
	}

	private void addListeners() {
		// Window Closing
		/*
		 * _frame.addWindowListener(new WindowAdapter() { public void
		 * windowClosing(WindowEvent event) { close(); } });
		 */
	}

	public void showInterfaceSelector(String[] interfaceNames) {
		_selectInterfaceWindow = new JDialog(_frame);
		_selectInterfaceWindow.setTitle("Double-Click to Select the Interface");
		_selectInterfaceWindow.setLocationRelativeTo(_frame);
		_interfaceList = new JList(interfaceNames);
		_interfaceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(_interfaceList);

		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = _interfaceList.locationToIndex(e.getPoint());
					if (Captor.getInstance().getCurrentDeviceId() == index) {
						_selectInterfaceWindow.dispose();
						return;
					}
					Captor.getInstance().openDevice(index);
					PacketSamurai.setConfigProperty("NetworkInterface", Integer.toString(index));
					_selectInterfaceWindow.dispose();
				}
			}
		};

		_interfaceList.addMouseListener(mouseListener);

		_selectInterfaceWindow.add(scrollPane);
		_selectInterfaceWindow.setSize(400, 350);
		_selectInterfaceWindow.setVisible(true);
	}

	public void showAboutDialog() {
		JOptionPane.showMessageDialog(this.getMainFrame(), "Packet Samurai v??\n\nGilles Duboscq\nUlysses R. Ribeiro", "About Packet Samurai", JOptionPane.PLAIN_MESSAGE);
	}

	public void close() {
		PacketSamurai.saveConfig();
	}

	public void toggleSearchDialog() {
		if (getViewerTab().getComponentCount() > 0) {
			if (_searchDlg == null) {
				_searchDlg = new SearchDlg(_frame);
				_searchDlg.setVisible(true);
			} else {
				// toggle display
				_searchDlg.setVisible(!_searchDlg.isVisible());
			}
		}
	}

	public void toggleFilterDialog() {
		if (getViewerTab().getComponentCount() > 0) {
			FilterDlg filterDlg = getViewerTab().getCurrentViewPane().getFilterDialog();

			// toggle display
			filterDlg.setVisible(!filterDlg.isVisible());
		}
	}

	public void searchNext() {
		if (getViewerTab().getComponentCount() > 0) {
			if (_searchDlg != null) {
				ViewPane pane = ((Main) PacketSamurai.getUserInterface()).getViewerTab().getCurrentViewPane();
				if (pane != null) {
					int index = _searchDlg.search(pane.getSelectedPacketindex() + 1);
					if (index >= 0) {
						JTable pt = ((Main) PacketSamurai.getUserInterface()).getViewerTab().getCurrentViewPane().getPacketTable();
						pt.setAutoscrolls(true);
						pt.getSelectionModel().setSelectionInterval(index, index);
						pt.scrollRectToVisible(pt.getCellRect(index, 0, true));
						_searchDlg.setCurrentSearchIndex(index + 1);
					}
				}
			} else {
				toggleSearchDialog();
			}
		}
	}

	public void toggleProtocolEditor() {
		if (_pEditor == null) {
			ProtocolEditor pe = new ProtocolEditor(_frame);
			_pEditor = pe;
			_pEditor.setLocationRelativeTo(_frame); // will make PE spawn
			// centered relatively to
			// the main frame
		}

		// toggle display
		_pEditor.setVisible(!_pEditor.isVisible());
	}

	private void exportNpcTitles() {
		if (getViewerTab().getComponentCount() > 0) {

			ViewPane pane = ((Main) PacketSamurai.getUserInterface()).getViewerTab().getCurrentViewPane();
			if (pane != null) {
				String sessionName = pane.getGameSessionViewer().getSession().getSessionName();
				FastList<DataPacket> packets = pane.getGameSessionViewer().getSession().getPackets();
				new NpcTitleExporter(packets, sessionName).parse();
				
			}
		}
	}

	// MenuActions
	public class MenuActionListener implements ActionListener {

		public void actionPerformed(ActionEvent ev) {
			String actionCmd = ev.getActionCommand();
			if (actionCmd.equals("Open")) {
				final JFileChooser chooser = new JFileChooser(PacketSamurai.getConfigProperty("lastLogDir", ".\\logs\\"));
				chooser.setFileFilter(new FileFilter() {

					@Override
					public boolean accept(File f) {
						return f.isDirectory() || f.isFile() && (f.getName().endsWith(".pcap") || f.getName().endsWith(".cap") || f.getName().endsWith(".bin") || f.getName().endsWith(".psl"));
					}

					@Override
					public String getDescription() {
						return "All supported formats (.cap .pcap .bin .psl)";
					}

				});
				final int returnVal = chooser.showOpenDialog(_frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					Thread t = new Thread() {
						public void run() {
							((Main) PacketSamurai.getUserInterface()).openSession(chooser.getSelectedFile());
						}
					};
					t.start();
				}
			} else if (actionCmd.equals("Close")) {
				Main.this.closeSessionTab(Main.this.getViewerTab().getCurrentViewPane());
			} else if (actionCmd.equals("CloseAll")) {
				getViewerTab().removeAll();
				Main.this.toggleSessionItems(false);
			} else if (actionCmd.equals("Search")) {
				toggleSearchDialog();
			} else if (actionCmd.equals("SearchNext")) {
				searchNext();
			} else if (actionCmd.equals("GoTo")) {
				String ret = JOptionPane.showInputDialog(null, "Enter the packet number", "Go to packet...", JOptionPane.INFORMATION_MESSAGE);
				if (ret != null) {
					try {
						int pn = Integer.parseInt(ret);

						if (pn >= 0 && pn < Main.this.getViewerTab().getCurrentViewPane().getPacketTable().getRowCount()) {
							Main.this.getViewerTab().getCurrentViewPane().setSelectedPacket(pn, pn);
						} else {
							JOptionPane.showMessageDialog(null, "Invalid value for packet number.", "ERROR", JOptionPane.ERROR_MESSAGE);
						}
					} catch (NumberFormatException e) {
						JOptionPane.showMessageDialog(null, "Invalid value for packet number.", "ERROR", JOptionPane.ERROR_MESSAGE);
					}
				}
			} else if (actionCmd.equals("Filter")) {
				toggleFilterDialog();
			} else if (actionCmd.equals("SelectInterface")) {
				// must run asynchronously from Main UI
				new Thread(new Runnable() {

					public void run() {
						Captor.getInstance().selectNetWorkInterface();

					}
				}).start();
			} else if (actionCmd.equals("SetActiveProtocols")) {
				new Thread(new Runnable() {

					public void run() {
						Captor.showSetActiveProtocols();
					}
				}).start();
			} else if (actionCmd.equals("Exit")) {
				System.exit(0);
			} else if (actionCmd.equals("About")) {
				showAboutDialog();
			} else if (actionCmd.equals("EditProtocol")) {
				toggleProtocolEditor();
			} else if (actionCmd.equals("ExportNpcTitles")){
				exportNpcTitles();
			}

		}
	}

	public ProtocolEditor getProtocolEditor() {
		return _pEditor;
	}

	public ViewTab getViewerTab() {
		return _viewerTab;
	}

	public void openSession(File file) {
		Set<Session> sessions = null;
		try {
			sessions = Main.getSessionsFromFile(file);
		} catch (FileNotFoundException fnfe) {
			PacketSamurai.getUserInterface().log("ERROR: Opening (" + file.getAbsolutePath() + "), file was not found.");
		} catch (IOException ioe) {
			ioe.printStackTrace();
			PacketSamurai.getUserInterface().log("ERROR: Opening (" + file.getAbsolutePath() + "), I/O error.");
		} catch (BufferUnderflowException e) {
			PacketSamurai.getUserInterface().log("ERROR: Opening (" + file.getAbsolutePath() + "), file format error.");
			e.printStackTrace();
		}

		if (sessions != null) {
			PacketSamurai.setConfigProperty("lastLogDir", file.getParent());
			for (Session s : sessions) {
				this.showSession(s, false);
			}
		}
	}

	/**
	 * 
	 * @param file
	 *            The file to be opened
	 * @return A set containing all session(s) stored on the file, or null if
	 *         there is no support for the file extension.
	 * @throws IOException
	 *             if there was an I/O error.
	 * @throws BufferUnderflowException
	 *             if there was insufficient data or a file format error.
	 */
	public static Set<Session> getSessionsFromFile(File file) throws IOException {
		Set<Session> sessions = null;

		String filename = file.getAbsolutePath();

		// for now its hardcoded, maybe registering later
		AbstractLogReader reader = AbstractLogReader.getLogReaderForFile(filename);
		if (reader == null)
			return null;
		reader.parse();
		sessions = reader.getSessions();

		return sessions;
	}

	public JFrame getMainFrame() {
		return _frame;
	}

	public void showSessions() {
		for (Session s : GameSessionTable.getInstance().getSessions()) {
			if (!s.isShown())
				this.showSession(s, true);
		}
	}

	public void showSession(Session s, boolean notify) {
		this.getViewerTab().showSession(s, notify);
		this.toggleSessionItems(true);
	}

	public void toggleSessionItems(boolean enabled) {
		_itemClose.setEnabled(enabled);
		_itemCloseAll.setEnabled(enabled);
		_itemSearch.setEnabled(enabled);
		_itemSearchNext.setEnabled(enabled);
		_itemGoto.setEnabled(enabled);
		_itemFilter.setEnabled(enabled);
	}

	public void closeSessionTab(ViewPane vp) {
		Session s = vp.getGameSessionViewer().getSession();
		if (s != null) {
			s.setShown(false);
		}
		getViewerTab().remove(vp);
		if (getViewerTab().getComponentCount() == 0) {
			this.toggleSessionItems(false);
		}
	}
}
