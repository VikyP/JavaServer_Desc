/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package screen_stream;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import masterPanel.RecordInfo;
import masterPanel.ReportException;
import masterPanel.SettingsConfig;

/**
 *
 * @author Студент
 */
public class Thread_SenderImage extends Thread
{

    private ScreenTiles ST;
    private boolean Status = false;
    private DatagramSocket DS;

    public Thread_SenderImage(RecordInfo r)
    {
        this.ST = new ScreenTiles(r);
        this.setDaemon(true);
        try
        {
            this.DS = new DatagramSocket();
        } catch (SocketException ex)
        {
            Logger.getLogger(Thread_SenderImage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run()
    {

        try
        {

            while (true)
            {

                //  System.out.println(" Thread_SenderImage   run");
                getStatus();

                {
                    //отправляем команду на обработку и получаем массив(запакованный) для отправки
                    byte[] ByteSream = ST.PrScrToBytes();
                    if (ByteSream != null)
                    {
                        System.out.println("Send Screeen " + ByteSream.length);
                        Send(ByteSream);
                    } else
                    {
                        System.out.println("Send Null");
                    }

                    Thread.sleep(ST.time);
                }
            }

        } catch (InterruptedException ex)
        {

            Logger.getLogger(Thread_SenderImage.class.getName()).log(Level.SEVERE, null, ex);
        } finally
        {
            this.DS.close();

        }

    }

    public void Send(byte[] ByteSream)
    {
        try
        {
            if (ByteSream.length == 0)
            {
                return;
            }
            DatagramPacket DP = new DatagramPacket(
                    ByteSream, ByteSream.length, SettingsConfig.IP_UDP, SettingsConfig.PORT_TCP_ScStr);
            DS.send(DP);
        } catch (Exception se)
        {
            ReportException.write("Sender_UDP_Image.Send(..)" + se.getMessage() + ByteSream.length);
        }
    }

    public synchronized void setStatus(boolean flag)
    {
        this.Status = flag;
        if (Status)
        {
            this.notify();
        }

    }

    public synchronized void getStatus()
    {
        if (!Status)
        {
            try
            {
                this.wait();
            } catch (InterruptedException ex)
            {
                Logger.getLogger(Thread_SenderImage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
