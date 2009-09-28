package com.aionemu.packetsamurai.gui.logrepo;

import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class LogRepoTab extends JTabbedPane
{
    private MainRepoTab _mainRepoTab;
    private LogFilesTab _logFilesTab;
    private RepoUsersTab _repoUsersTab;

    public LogRepoTab()
    {
        _mainRepoTab = new MainRepoTab();
        _logFilesTab = new LogFilesTab();
        _repoUsersTab = new RepoUsersTab();
        
        this.add("Main",_mainRepoTab);
        this.add("Logs",_logFilesTab);
        this.add("Users",_repoUsersTab);
    }
    
    public LogFilesTab getLogFilesTab()
    {
        return _logFilesTab;
    }
    
    public RepoUsersTab getRepoUserTab()
    {
        return _repoUsersTab;
    }
}