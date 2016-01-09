/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teacher_teamviewer;


import java.io.DataInputStream;
import java.net.Socket;
import masterPanel.ReportException;
import teacher_teamviewer.event_package.IUnpack;


/**
 *
 * @author viky
 */
public class Student extends Object implements Comparable
{
    public static final String FULL="FULL";
    public static final String PREVIEW="PREVIEW";
    
    public StudentPane SP;
    public CanvasScreen CS;
    public boolean  isConnect;
    public  ConfigInfo config;   
    private String IP;
   
    public TCP_Client_RecieverPrScr RecieverPrScr;
  
    IUnpack UR= new IUnpack()
    {

        @Override
        public void unpackImg(DataInputStream dis, byte type)
        {
            if(type==(byte)1)
            {
                Student.this.SP.UnPackImage(dis);
                Student.this.CS.getImage(Student.this.SP.BI);
                Student.this.CS.repaint();
            }
            if(type==(byte)0)
            {
               
                Student.this.SP.UnPackPreview(dis);
            }
            
        }

    };
    
    public Student( String ip, ConfigInfo c)
    {
        this.IP=ip; 
        this.config=c;
        this.SP= new StudentPane(ip);
        this.CS = new CanvasScreen(this.SP.BI);
        this.isConnect=false;
      
    }

    @Override
    public String toString()
    {
       return this.IP+":"+this.config.PORT_TCP_IMG;      
    }
    
   @Override
    public boolean equals(Object ob)
    {
        if(ob==null)
            return false;
       
        return( this.IP.equals(((Student)ob).IP) && this.config.PORT_TCP_IMG==((Student)ob).config.PORT_TCP_IMG );
      
    }
    
    
    //создание потока для работы с изображением
    public  void createRecieverPrScr(Socket client)
    {
       
        this.RecieverPrScr= new TCP_Client_RecieverPrScr(client); 
        this.RecieverPrScr.ER.addEventUnpack(UR);        
        System.out.println("After");
    }
     //создание потока для работы с упралением
    public void createSenderMessage(Socket client)
    {
        this.CS.SenderCommand.setSocket( client);
    
    }
    
    public Student getStudentByIP(String ip)
    {
        if(this.IP.equals(ip))
            return this;
        else
            return null;    
    }

    public void setIP(String ip)
    {
        this.IP=ip;
    }
    
   

    public String getIP()
    {  
        return this.IP;
    }
    
    @Override
    public int compareTo(Object o)
    {
        System.out.println(" compare " + this.IP + "  "+ ((Student)o).IP);   
        try
        { 
            
        String [] ip1=this.IP.split("\\."); 
           
        String [] ip2 =((Student)o).IP.split("\\.");
       
            for(int i=0; i<4;i++)
            {
                int tmp=Integer.parseInt(ip1[i])-Integer.parseInt(ip2[i]);
                if(tmp!=0)
                {
                   
                    return tmp;
                }
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
            ReportException.write("Student.compareTo()"+ex.getMessage());
        }
        return 0;
    }

    void setRegimeView(RegimeView regimeView)
    {
        this.CS.regimeCurrent=regimeView;
    }
    
}

