/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teacher_teamviewer;

import teacher_teamviewer.event_package.EventUnpack;
import teacher_teamviewer.event_package.EventRemoveStudent;
import teacher_teamviewer.event_package.IUnpack;
import teacher_teamviewer.event_package.IEventRemoveStudent;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.zip.GZIPInputStream;
import masterPanel.ReportException;


/**
 *
 * @author viky
 */
public class TCP_Client_RecieverPrScr extends Thread
{
    private Socket client;
    public EventUnpack ER;
    public EventRemoveStudent ERS;
    
    private String FULL="FULL";
    private String PREVIEW ="PREVIEW";
    
    private long time;
    public String msg;
    

    public TCP_Client_RecieverPrScr(Socket client )
    {
        
        this.ER= new EventUnpack();
        this.ERS = new  EventRemoveStudent();        
        this.client=client;
        this.setTime(1000);        
        this.setDaemon(true);       
        this.msg=PREVIEW;
        
    }
        
    
    
    public void setTime(long t)
    {
        this.time=t;
    }
   
    @Override
    public void run()
    {  
        
        try
        {
            
            // обмен данными
            while(true)
            {    
               
                byte[] a = msg.getBytes("UTF8");

                this.client.getOutputStream().write(a, 0, a.length);
                ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
                int size = 0;
                byte[] s = new byte[5];
                int lengthByteArr = this.client.getInputStream().read(s, 0, s.length);
                byte type=100;
                if (lengthByteArr == 5)
                {
                    ByteArrayInputStream BAISlengthByteArr = new ByteArrayInputStream(s);
                    DataInputStream dis = new DataInputStream(BAISlengthByteArr);
                    lengthByteArr = dis.readInt();
                    type=dis.readByte();
                }
                else
                {
                    System.out.println("Error  lengthByteArr");
                }
                System.out.println(" lengthByteArr"+lengthByteArr);
                System.out.println(" type"+type);
                byte[] dataBuffer = new byte[32768];
                do
                {
                    int cnt = this.client.getInputStream().read(dataBuffer, 0, dataBuffer.length);
                    if (cnt == -1)
                    {
                        throw new IOException("Recived - 1 bytes");
                    }

                    BAOS.write(dataBuffer, 0, cnt);
                    size = size + cnt;
                } 
                while (lengthByteArr - size - 5 > 0);


                byte [] body;
                byte[] bodyZip = BAOS.toByteArray();
                body= unzip(bodyZip);
                
                System.out.println("    Get "+body.length);
                ByteArrayInputStream BAIS = new ByteArrayInputStream(body);
                DataInputStream DIS = new DataInputStream(BAIS);

               IUnpack RPS= (IUnpack) this.ER.getListener();            
                RPS.unpackImg(DIS,type );  


                DIS.close();
                Thread.sleep(this.time);

            }
        }
        catch (IOException ex)
        {  
            try
            {
                ReportException.write(this.getClass().getName()+"\t2\t"+ex.getMessage());
                String ip=this.client.getInetAddress().toString().substring(1);
                System.out.println("1 " + ex.getMessage() + "Begin remove_"+ ip );
                IEventRemoveStudent ers = (IEventRemoveStudent) this.ERS.getListener();
                ers.removeStudent(ip);
                this.client.close();
                
            }
            catch (IOException ex1)
            {
                ReportException.write("TCP_Client_RecieverPrScr.run()  IOException\t"+ex.getMessage());
                System.out.println("Can't close client");   
            }
            
        }
        
        catch (InterruptedException ex)
        {
            Logger.getLogger(TCP_Client_RecieverPrScr.class.getName()).log(Level.SEVERE, null, ex);
            ReportException.write("TCP_Client_RecieverPrScr.run()  InterruptedException\t"+ex.getMessage());
        }
        
        finally
        {
            try
            {
                this.client.close();
            }
            catch (IOException ex)
            {
              System.out.println("3 " + ex.getMessage());
            }
            this.client=null;
            
        }
    }
    
    private boolean isConnect()
    {
        return  this.client.isConnected();
    }
    
     
    private byte [] unzip(byte [] tmp)
    {
        byte[] buffer = new byte[8192]; 
        ByteArrayOutputStream Baos = new ByteArrayOutputStream();
        ByteArrayInputStream BAIS = new ByteArrayInputStream(tmp);
       
        try
        {
            GZIPInputStream gzipis = new GZIPInputStream(BAIS);
            int length = 0;
            while ((length = gzipis.read(buffer)) > 0)
            {
                Baos.write(buffer, 0, length);
            }
            BAIS.close();
            
            
        } 
        catch (FileNotFoundException ex)
        {
            System.out.println(ex.getMessage());
            ReportException.write("TCP_Client_RecieverPrScr.unzip()  FileNotFoundException\t"+ex.getMessage());
        } 
        catch (IOException ex)
        {
            System.out.println(ex.getMessage());
             ReportException.write("TCP_Client_RecieverPrScr.unzip()  IOException\t"+ex.getMessage());
        }
         byte [] body =Baos.toByteArray();
         System.out.println("   size"+body.length);
    return  body;
    
    }
    


}

