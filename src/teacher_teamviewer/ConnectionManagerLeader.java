/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teacher_teamviewer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import masterPanel.ReportException;
import masterPanel.SettingsConfig;
import teacher_teamviewer.event_package.EventAddStudent;

/**
 *
 * @author viky
 */
public class ConnectionManagerLeader extends Thread
{

    //контейнер для всех запросов на подключение, полученных по UDP

    public ArrayList <Student> Applicants = new ArrayList<> ();
    
    public EventAddStudent EL;
   //котейнер для ожидающих подкдлючение
    //объект добавляется, если его нет в перечне уже подлючившихся и ожидающих подкдлючение
    private ArrayList<String> WaitingConnection = new ArrayList<> ();
    private int timeout=5000;

    public ConnectionManagerLeader()
    {
        this.EL= new EventAddStudent();
    }

    @Override
    public void run()
    {
        while (true)
        {
           
            try
            {
                final String IP = this.getItem();  
                InetAddress ip= InetAddress.getByName(IP);
                Socket client = new Socket();
                {
                    try
                    {
                      //  System.out.println("Start  TCP");
                        InetSocketAddress clientSocketAdress = new InetSocketAddress(ip, SettingsConfig.PORT_TCP_IMG);
                        System.out.println("IP " + clientSocketAdress.toString());
                        client.connect(clientSocketAdress,timeout);
                      // System.out.println("  TCP_image  OK ");
                    } 
                    catch (Exception exc)
                    {
                        //выход по исключению
                        System.out.println("Ошибка подключения по адресу "+IP+"\t"+ exc.getMessage());
                        ReportException.write("Ошибка подключения по адресу "+IP+"\t"+ exc.getMessage());
                        client.close();
                                        
                    }
                }
                
                Socket clientCC = new Socket();
                {
                    try
                    {
                      //  System.out.println("Start  TCP");
                        InetSocketAddress clientSocketAdress = new InetSocketAddress(ip, SettingsConfig.PORT_TCP_COMMAND);
                       // System.out.println("IP " + clientSocketAdress.toString());
                        clientCC.connect(clientSocketAdress,timeout);
                       // System.out.println("  TCP_command  OK");
                    } 
                    catch (Exception exc)
                    {
                        //выход по исключению
                        System.out.println("Ошибка подключения по адресу "+IP+"\t"+ exc.getMessage());
                        ReportException.write("Ошибка подключения по адресу "+IP+"\t"+ exc.getMessage());
                        clientCC.close();
                                              
                    }
                }
                
                //нет подключения
                if (client.isConnected() && clientCC.isConnected())
                {  
                    final Student S= new Student(ip);
                    S.createRecieverPrScr(client);
                    S.CS.SenderCommand.setSocket(clientCC);
                    S.SP.setLabelIp(IP); 
                    Applicants.add(S);
                }
                else
                {
                    System.out.println("    IP "+IP);
                }
                
            } 
            
            catch (Exception ex)
            {
                ReportException.write(this.getName() + " run() \t\t" + ex.getMessage());
            }
        }

    }

    //
    public synchronized void addItem(String ip)
    {
        //этот студент уже есть в очереди
        if(this.WaitingConnection.contains(ip))
        {
            return;
        }
        // иначе добавляем в очередь
       
        this.WaitingConnection.add(ip);
        this.notify();
        
    }

    // первый в очереди на подключение
    public synchronized String getItem()
    {
        while (this.WaitingConnection.isEmpty())
        {
            try
            {
                this.wait();
            } catch (InterruptedException ex)
            {
                ReportException.write(this.getName() + " getItem() \t\t" + ex.getMessage());
                Logger.getLogger(ConnectionManagerLeader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return this.WaitingConnection.remove(0);
    }
    
    

}
