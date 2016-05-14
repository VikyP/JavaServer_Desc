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
import masterPanel.SettingsConfig;


/**
 *
 * @author viky
 */
public class UDP_Listener extends Thread
{
   
    public EventAddStudent EL;
    
    public UDP_Listener( )
    {        
        this.EL= new EventAddStudent();
        this.setDaemon(true);
        System.out.println("    port " + SettingsConfig.PORT_UDP);       
    }
    

    @Override
    public void run()
    {
        try
        {
            DatagramSocket  DS  = new DatagramSocket (SettingsConfig.PORT_UDP);
            byte[] b= new byte [8196] ;
            DatagramPacket DP= new DatagramPacket (b, 0, b.length);
            while(true)
            {
               // System.out.println("Start UDP");
                DS.receive(DP);
                String msg= new String (DP.getData(), 0, DP.getLength(),"UTF8");
             
                final String [] studentInfo=msg.split(";");
                if(studentInfo[0].equals("NEW"))
                {                  
                    javax.swing.SwingUtilities.invokeLater(new Runnable(){@Override public void run()
                     {
                         IEventAddStudent newS= (IEventAddStudent)EL.getListener();
                         newS.addNewStudent(studentInfo[1]);                          
                     }
                     });
                }
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