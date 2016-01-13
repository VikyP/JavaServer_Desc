/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teacher_teamviewer;

import java.io.IOException;
import teacher_teamviewer.event_package.EventAddStudent;
import teacher_teamviewer.event_package.IEventAddStudent;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import masterPanel.ReportException;


/**
 *
 * @author viky
 */
public class UDP_Listener extends Thread
{
   
    public EventAddStudent EL;
    private Student newStudent;
    private ConfigInfo config;
    
    public UDP_Listener( ConfigInfo c)
    {
        
        this.EL= new EventAddStudent();
        this.setDaemon(true);
        this.config=c;       
       // System.out.println("    port " + c.PORT_UDP);       
    }
    

    @Override
    public void run()
    {
        try
        {
            DatagramSocket  DS  = new DatagramSocket (this.config.PORT_UDP);
            byte[] b= new byte [8196] ;
            DatagramPacket DP= new DatagramPacket (b, 0, b.length);
            while(true)
            {
               // System.out.println("Start UDP");
                DS.receive(DP);
                String msg= new String (DP.getData(), 0, DP.getLength(),"UTF8");
               
                String [] studentInfo=msg.split(";");
                /*
                for (int i = 0; i < studentInfo.length; i++)
                {
                    System.out.println(studentInfo[i]);   
                }
                */
                
                if(studentInfo[0].equals("NEW"))
                {
                    newStudent =new Student(studentInfo[1], this.config);
                    javax.swing.SwingUtilities.invokeLater(new Runnable(){@Override public void run()
                     {
                         IEventAddStudent newS= (IEventAddStudent)UDP_Listener.this.EL.getListener();
                         newS.addNewStudent(UDP_Listener.this.newStudent); 
                     }
                     });
                   
                   //IEventAddStudent newS= (IEventAddStudent) this.EL.getListener();
                   // newS.addNewStudent(newStudent); 
                  
                    
                  //  System.out.println( " Received from :" +DP.getAddress().toString()+ " : " + msg);  
                }
             //   System.out.println("Stop UDP");
              
             Thread.sleep(1000);
            }
            
        }
        catch(IOException | InterruptedException se)
        {
             ReportException.write("UDP_Listener.run()"+se.getMessage());
            System.out.println( " UDP_Listener SocketException #1 : udp" + se.getMessage());
                    
        }
       
    }
    
    
    
}

/*
javax.swing.SwingUtilities.invokeLater(new Runnable(){@Override public void run()
                    {
                        IEventAddStudent newS= (IEventAddStudent) FindNewStudent.this.EL.getListener();
                        newS.addNewStudent(newStudent); 
                    }
                    });
*/