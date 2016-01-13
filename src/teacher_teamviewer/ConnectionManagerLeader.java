/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teacher_teamviewer;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import masterPanel.ReportException;

/**
 *
 * @author viky
 */
public class ConnectionManagerLeader extends Thread
{

    //контейнер для всех запросов на подключение, полученных по UDP

    public ArrayList <Student> Applicants = new ArrayList<Student> ();
    

   //котейнер для ожидающих подкдлючение
    //объект добавляется, если его нет в перечне уже подлючившихся и ожидающих подкдлючение
    private ArrayList<Student> WaitingConnection = new ArrayList<Student> ();

    public ConnectionManagerLeader()
    {

    }

    @Override
    public void run()
    {
        while (true)
        {
           
            try
            {
                Student S = this.getItem();
                
                Socket client = new Socket();
                {
                    try
                    {
                      //  System.out.println("Start  TCP");
                        InetSocketAddress clientSocketAdress = new InetSocketAddress(S.getIP(), S.config.PORT_TCP_IMG);
                       // System.out.println("IP " + clientSocketAdress.toString());
                        client.connect(clientSocketAdress, 5000);
                      // System.out.println("  TCP_image  OK ");
                    } 
                    catch (Exception exc)
                    {
                        //выход по исключению
                        System.out.println("Ошибка подключения по адресу "+S.getIP()+"\t"+ exc.getMessage());
                        ReportException.write("Ошибка подключения по адресу "+S.getIP()+"\t"+ exc.getMessage());
                        client.close();
                                        
                    }
                }
                
                Socket clientCC = new Socket();
                {
                    try
                    {
                      //  System.out.println("Start  TCP");
                        InetSocketAddress clientSocketAdress = new InetSocketAddress(S.getIP(), S.config.PORT_TCP_COMMAND);
                       // System.out.println("IP " + clientSocketAdress.toString());
                        clientCC.connect(clientSocketAdress, 5000);
                       // System.out.println("  TCP_command  OK");
                    } 
                    catch (Exception exc)
                    {
                        //выход по исключению
                        System.out.println("Ошибка подключения по адресу "+S.getIP()+"\t"+ exc.getMessage());
                        ReportException.write("Ошибка подключения по адресу "+S.getIP()+"\t"+ exc.getMessage());
                        clientCC.close();
                                              
                    }
                }
                
                //нет подключения
                if (client.isConnected() && clientCC.isConnected())
                {
                    
                   
                    S.createRecieverPrScr(client);
                    S.CS.SenderCommand.setSocket(clientCC);
                    Applicants.add(S);
                  //  System.out.println("  TCP  OK " +Applicants.size());
                }
                else
                {
                 S = null;
                }
               
            } 
            
            catch (Exception ex)
            {
                ReportException.write(this.getName() + " run() \t\t" + ex.getMessage());
                Logger.getLogger(ConnectionManagerLeader.class.getName()).log(Level.SEVERE, null, ex);
            }
            

        }

    }

    //
    public synchronized void addItem(Student s)
    {
        //этот студент уже есть в очереди
        if(this.WaitingConnection.contains(s))
        {
            return;
        }
        // иначе добавляем в очередь
       
        this.WaitingConnection.add(s);
      //  System.out.println(" add ok" );
        this.notify();
        
    }

    // первый в очереди на подключение
    public synchronized Student getItem()
    {
        while (this.WaitingConnection.isEmpty())
        {
          //  System.out.println("  ConnectionManagerLeader  ");
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
