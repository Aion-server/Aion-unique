/**
 * 
 */
package com.aionemu.packetsamurai.logrepo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.methods.multipart.FilePart;

/**
 * @author Ulysses R. Ribeiro
 *
 */
public class LogFilePart extends FilePart
{
    private LogFile _logFile;

    public LogFilePart(String name, LogFile logFile) throws FileNotFoundException
    {
        super(logFile.getFile().getName(), logFile.getFile());
        _logFile = logFile;
    }

    @Override
    protected void sendData(OutputStream out) throws IOException
    {
        try
        {
            if (lengthOfData() > 0)
            {
                RemoteLogRepositoryBackend.getInstance().getUploadListener().updateTransferProgressText(_logFile, "Uploading");

                byte[] buffer = new byte[4096];
                InputStream instream = this.getSource().createInputStream();
                try
                {
                    int len;
                    int progress, writed = 0;

                    while ((len = instream.read(buffer)) >= 0)
                    {
                        out.write(buffer, 0, len);
                        writed += len;
                        progress = (int) ((((double) writed)/lengthOfData())*100);
                        RemoteLogRepositoryBackend.getInstance().getUploadListener().updateTransferProgress(_logFile, progress);
                    }
                }
                finally
                {
                    instream.close();
                    RemoteLogRepositoryBackend.getInstance().getUploadListener().transferFinished(_logFile, true);
                }
            }
            else
            {
                RemoteLogRepositoryBackend.getInstance().getUploadListener().transferFinished(_logFile, false);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
