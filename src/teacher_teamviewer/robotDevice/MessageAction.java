/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teacher_teamviewer.robotDevice;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import masterPanel.ReportException;

/**
 *
 * @author Viky
 */


public class MessageAction
{
    
    private byte action=-1;
    
    private int KM_code=-1;//key code or mouse button 
    private int X=-1 ;
    private int Y=-1;
    
   
    public   MessageAction ()
    {
        
    }
    public   MessageAction (byte act, int kc)
    {
     this.action=act;
     this.KM_code=kc;
    }
    
    public   MessageAction (byte at, int x, int y )
    {
     this.action=at;     
     this.X=x;
     this.Y=y;
    }
    
    
    public MessageAction(DataInputStream DIS)
    {
        try
        {
          
            this.action=DIS.readByte();
            if(this.action!=ActionType.Mouse_Move)
            {
              this.KM_code= DIS.readInt();
            }
            if(this.action==ActionType.Mouse_Move)
            {
                this.X=DIS.readInt();
                this.Y= DIS.readInt();            
            }
            
        } 
        catch (IOException ex)
        {
           ReportException.write("MessageAction  ( DIS) "+ex.getMessage());
        }
    
    
    }
    
    public void write ( DataOutputStream DOS)
    { 
        try
        { 
            DOS.writeByte(this.action);
            if(this.action!=ActionType.Mouse_Move)
            {
             DOS.writeInt(this.KM_code);
            }
            if(this.action==ActionType.Mouse_Move)
            {
                DOS.writeInt(this.X);
                DOS.writeInt(this.Y);
            }
          
        } catch (IOException ex)
        {
            ReportException.write("MessageAction  write "+ex.getMessage());
        }
      
    }
    
}
