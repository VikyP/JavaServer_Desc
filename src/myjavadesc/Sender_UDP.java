/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myjavadesc;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import masterPanel.ReportException;

/**
 *
 * @author viky
 */
public class Sender_UDP
{
    private final InetAddress IP_UDP;
    private final int port;

    public Sender_UDP(InetAddress ip, int port)
    {      
        this.IP_UDP = ip;
        this.port = port;
    }

    public synchronized void Send(byte[] ByteSream)
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
            ReportException.write("Sender_UDP.Send(..)" + se.getMessage());
            System.out.println("Sender_UDP.Send(..)" + se.getMessage());

        }
    }

}
