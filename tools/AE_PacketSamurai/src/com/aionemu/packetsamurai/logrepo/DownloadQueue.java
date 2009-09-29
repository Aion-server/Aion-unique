package com.aionemu.packetsamurai.logrepo;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javolution.util.FastList;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public class DownloadQueue
{
    private ThreadPoolExecutor _threadPool;
    
    public DownloadQueue(int size)
    {
        _threadPool = new ThreadPoolExecutor(size,size,15L,TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>());
    }
    
    public void addNewDownLoadTask(LogFile log)
    {
        _threadPool.execute(new DownloadTask(log));
    }
    
    public List<LogFile> getWaitingLogs()
    {
        List<LogFile> logs = new FastList<LogFile>();
        for (Runnable r :_threadPool.getQueue())
        {
            logs.add(((DownloadTask)r).getLog());
        }
        return logs;
    }
    
    private class DownloadTask implements Runnable
    {
        private LogFile _log;

        public DownloadTask(LogFile log)
        {
            _log = log;
        }
        
        public LogFile getLog()
        {
            return _log;
        }

        public void run()
        {
            RemoteLogRepositoryBackend.getInstance().downLoadFile(_log);
        }
        
    }
}