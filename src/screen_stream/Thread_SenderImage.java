/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package screen_stream;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import masterPanel.RecordInfo;
import masterPanel.ReportException;

/**
 *
 * @author Студент
 */
public class Thread_SenderImage extends Thread
{
   // private Socket client;
    private ScreenTiles ST;
   
    private final InetAddress IP_UDP;
    private final int port;
    
    
    
    private boolean Status=false;
    
    public Thread_SenderImage(InetAddress ip, int port,RecordInfo r) 
    {
       
        this.IP_UDP = ip;
        this.port = port;
        this.ST = new  ScreenTiles(r);
        this.setDaemon(true);
       System.out.println(" Thread_SenderImage "+  port);
    }
            
    
    @Override
    public void run()
    {
     
       try
       {
           while(true)
           {
               
               System.out.println(" Thread_SenderImage   run");
               getStatus();
            
              {
                //отправляем команду на обработку и получаем массив(запакованный) для отправки
                byte[] ByteSream=  ST.PrScrToBytes();
                if(ByteSream!=null)
                {
                    System.out.println("Send "+ ByteSream.length); 
                    Send(ByteSream) ;       
                }
                else 
                { 
                    System.out.println("Send Null"); 
                }
                Thread.sleep(ST.time);
              }
            }
       
       } 
       catch (InterruptedException ex)
       { 
            Logger.getLogger(Thread_SenderImage.class.getName()).log(Level.SEVERE, null, ex);
        } 
       
       
    }
    
    public  void Send(byte[] ByteSream)
    {
        try
        {
            if (ByteSream.length == 0)
            {
                return;
            }
           System.out.println(" LENGTH SEND "+ByteSream.length);   
            
            DatagramSocket DS = new DatagramSocket();
            DatagramPacket DP = new DatagramPacket(
                    ByteSream, ByteSream.length, this.IP_UDP, this.port);
            DS.send(DP);
            DS.close();            

        }
        catch (Exception se)
        {
            ReportException.write("Sender_UDP_Image.Send(..)" + se.getMessage());
            System.out.println("Sender_UDP_Image.Send(..)" + se.getMessage());

        }
    }
    
    
    public synchronized void setStatus(boolean flag)
    {
        this.Status=flag;
        if(Status)
        {   this.notify();
            System.out.println(" Thread_SenderImage   start");
        }    
           
    }
    
     public synchronized void getStatus()
     {
         if(!Status)
             try 
            {
                System.out.println(" Thread_SenderImage   stop");
                this.wait();
        } catch (InterruptedException ex) {
            Logger.getLogger(Thread_SenderImage.class.getName()).log(Level.SEVERE, null, ex);
        }
     
     }
    
    
}
