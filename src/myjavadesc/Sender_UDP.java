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
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import masterPanel.ReportException;

/**
 *
 * @author viky
 */
public class Sender_UDP
{
    private final InetAddress IP_UDP;
    private final int port;
    DatagramSocket DS;
    public Sender_UDP(InetAddress ip, int port)
    {      
        this.IP_UDP = ip;
        this.port = port;
        try
        {
            this.DS = new DatagramSocket();
        } 
        catch (SocketException ex)
        {
            Logger.getLogger(Sender_UDP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void Send(byte[] ByteSream)
    {
        try
        {
            if (ByteSream.length == 0)
            {
                return;
            }
          ///////// System.out.println(" LENGTH SEND "+ByteSream.length+ Calendar.getInstance().getTime().toString());   
            
            
            DatagramPacket DP = new DatagramPacket(
                    ByteSream, ByteSream.length, this.IP_UDP, this.port);
            DS.send(DP);
                        

        }
        catch (Exception se)
        {
            ReportException.write("Sender_UDP.Send(..)" + se.getMessage());
            System.out.println("Sender_UDP.Send(..)" + se.getMessage());

        }
    }

}
