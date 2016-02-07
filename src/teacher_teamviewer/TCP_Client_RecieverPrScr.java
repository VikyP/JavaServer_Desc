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
import screen_stream.Thread_SenderImage;


/**
 *
 * @author viky
 */
public class TCP_Client_RecieverPrScr extends Thread
{
    private Socket client;
    public EventUnpack ER;
    public EventRemoveStudent ERS;
    
    private long time;
    public byte messageTypeView;
    private boolean Status=false;
    

    public TCP_Client_RecieverPrScr(Socket client )
    {  
        this.ER= new EventUnpack();
        this.ERS = new  EventRemoveStudent();        
        this.client=client;
        this.setTime(1000);        
        this.setDaemon(true);       
        this.messageTypeView=Student.PREVIEW;
    }
    
    public synchronized void setStatus(boolean flag)
    {
        this.Status=flag;
        if(Status)
        {   
            this.notify();
        }    
           
    }
    
     public synchronized void getStatus()
     {
         if(!Status)
             try 
            {               
                this.wait();
            }
             catch (InterruptedException ex)
        {
            Logger.getLogger(Thread_SenderImage.class.getName()).log(Level.SEVERE, null, ex);
        }
     
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
                getStatus();
                this.client.getOutputStream().write(this.messageTypeView);
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
                    ReportException.write("lengthByteArr ="+lengthByteArr);
                }
                //если пакет получен 
                if(type!=Student.NULL && lengthByteArr>0)
                {
                    byte[] dataBuffer = new byte[32768];
                    do
                    {
                        int cnt = this.client.getInputStream().read(dataBuffer, 0, dataBuffer.length);
                        if (cnt == -1)
                        {
                            ReportException.write("( cnt==-1) lengthByteArr ="+lengthByteArr);
                            ReportException.write("type ="+type);
                            throw new IOException("Recived - 1 bytes");
                        }

                        BAOS.write(dataBuffer, 0, cnt);
                        size = size + cnt;
                    } 
                    while (lengthByteArr - size  > 0);

                    byte [] body;
                    byte[] bodyZip = BAOS.toByteArray();
                    body= unzip(bodyZip);
                    ByteArrayInputStream BAIS = new ByteArrayInputStream(body);
                    DataInputStream DIS = new DataInputStream(BAIS);

                    IUnpack RPS= (IUnpack) this.ER.getListener();            
                    RPS.unpackImg(DIS,type ); 

                    DIS.close();
                }
                Thread.sleep(this.time);
            }
        }
        catch (IOException ex)
        {
            ReportException.write(this.getClass().getName()+"\t2\t"+ex.getMessage());
            String ip=this.client.getInetAddress().toString().substring(1);
            IEventRemoveStudent ers = (IEventRemoveStudent) this.ERS.getListener();
            ers.removeStudent(ip);
            ReportException.write(" Remove student IP "+ip);
        }
        
        catch (InterruptedException ex)
        {  
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
              ReportException.write(" client.close() "+ex.getMessage());
            }
            this.client=null;
            
        }
    }
        
     /**
      * распаковка данных от студента
      * @param tmp запакованный пакет
      * @return пакет для формирования картинки
      */
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
    return  body;
    
    }
    
}

