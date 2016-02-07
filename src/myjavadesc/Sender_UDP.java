/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myjavadesc;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import masterPanel.ReportException;
import masterPanel.SettingsConfig;

/**
 *
 * @author viky
 */
public class Sender_UDP
{    
    DatagramSocket DS;
    public Sender_UDP()
    {   
        try 
        {
            this.DS = new DatagramSocket();
        } 
        catch (SocketException ex)
        {
            Logger.getLogger(Sender_UDP.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public synchronized boolean Send(byte[] ByteSream)
    {
        try
        {
            if (ByteSream.length == 0)
            {
                return true;
            }
          ///////// System.out.println(" LENGTH SEND "+ByteSream.length+ Calendar.getInstance().getTime().toString()); 
           
            DatagramPacket DP = new DatagramPacket(
                    ByteSream, ByteSream.length, SettingsConfig.IP_UDP, SettingsConfig.PORT_UDP_BOARD);
            DS.send(DP);
            return true;

        }
        catch (Exception se)
        {            
            ReportException.write("Sender_UDP.Send(..)" + se.getMessage());
        }
        
        return false;
    }

}
