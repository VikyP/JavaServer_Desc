/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teacher_teamviewer.robotDevice;

import teacher_teamviewer.robotDevice.ActionType;
import java.awt.AWTException;
import java.awt.Robot;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Студент
 */


public class MessageAction
{
    
    private byte action=-1;
    private byte size=-1;
    
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
     this.size=(byte)Byte.SIZE/Byte.SIZE+(byte)Byte.SIZE/Byte.SIZE+(byte)Integer.SIZE/Byte.SIZE;
     //this.size=(byte)Byte.BYTES+(byte)Byte.BYTES+(byte)Integer.BYTES;
    // size + actiontype + key_code
    }
    
    public   MessageAction (byte at, int x, int y )
    {
     this.action=at;     
     this.X=x;
     this.Y=y;
     this.size=(byte)Byte.SIZE/Byte.SIZE +    (byte)Byte.SIZE/Byte.SIZE +   (byte)Byte.SIZE/Byte.SIZE +   (byte)Integer.SIZE/Byte.SIZE +   (byte)Integer.SIZE/Byte.SIZE;
   //  this.size=(byte)Byte.BYTES +    (byte)Byte.BYTES +   (byte)Byte.BYTES +   (byte)Integer.BYTES +   (byte)Integer.BYTES;
    // size + actiontype + mouse_button+ x+y;
    }
    
    
    public MessageAction(DataInputStream DIS)
    {
        try
        {
            this.size =-1;
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
            Logger.getLogger(MessageAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    
    }
    
    public void write ( DataOutputStream DOS)
    { 
        try
        { 
            DOS.writeByte(this.size);//1
            DOS.writeByte(this.action);//1
            if(this.action!=ActionType.Mouse_Move)
            {
             DOS.writeInt(this.KM_code);//4
            }
            if(this.action==ActionType.Mouse_Move)
            {
                DOS.writeInt(this.X);//4
                DOS.writeInt(this.Y);//4
            }
          
        } catch (IOException ex)
        {
            Logger.getLogger(MessageAction.class.getName()).log(Level.SEVERE, null, ex);
        }
      
    }
    
}
