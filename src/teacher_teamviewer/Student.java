/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teacher_teamviewer;


import java.io.DataInputStream;
import java.net.InetAddress;
import java.net.Socket;
import masterPanel.ReportException;
import teacher_teamviewer.event_package.IUnpack;


/**
 *
 * @author viky
 */
public class Student extends Object implements Comparable
{
    
    public StudentPane SP;
    public CanvasScreen CS;
    public TCP_Client_RecieverPrScr RecieverPrScr;
    private InetAddress IP; 
  
    IUnpack UR= new IUnpack()
    {

        @Override
        public void unpackImg(DataInputStream dis, byte type)
        {
            switch(type)
            {
                case TypeView.PREVIEW:
                     Student.this.SP.UnPackPreview(dis);
                     break;
                     
                case TypeView.FULL:
                    Student.this.SP.UnPackImage(dis);
                    Student.this.CS.getImage(Student.this.SP.BI);
                    Student.this.CS.repaint();
                break;
            }
        }

    };
    
    public Student(InetAddress ip)
    {
        this.IP=ip;
        this.SP= new StudentPane();
        this.CS = new CanvasScreen(this.SP.BI);
    }

   
   @Override
    public boolean equals(Object ob)
    {
        if(ob==null)
            return false;
       
        return( this.IP.equals(((Student)ob).IP)  );
      
    }
    
    
    //создание потока для работы с изображением
    public  void createRecieverPrScr(Socket client)
    {       
        this.RecieverPrScr= new TCP_Client_RecieverPrScr(client); 
        this.RecieverPrScr.ER.addEventUnpack(UR);  
    }
    
    //создание потока для работы с упралением
    public void createSenderMessage(Socket client)
    {
        this.CS.SenderCommand.setSocket( client);    
    }
     
 
    void setRegimeView(byte regimeView)
    {
        this.CS.regimeCurrent=regimeView;
    }

    @Override
    public int compareTo(Object o)
    { 
      
       int r=0;
        try
        { 
           byte[] ip1= this.getIP().getAddress();
           byte[] ip2= ((Student)o).getIP().getAddress();
            for(int i=0; i<4;i++)
            {  
             
                if((ip1[i]-ip2[i])!=0)
                {
                    r=(ip1[i]-ip2[i]);
                    return r;
                }
            }
        }
        catch(Exception ex)
        {
            ReportException.write("KeyIP.compareTo()"+ex.getMessage());
        }
        return r;
    }
    
    
    public Student getStudentByIP(String ip)
    {
      
        if(this.IP.getHostAddress().equals(ip))
            return this;
        else
            return null;    
    }
    

    /**
     * @return the IP
     */
    public InetAddress getIP()
    {
        return IP;
    }
    
}

