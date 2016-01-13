/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teacher_teamviewer;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;
import masterPanel.ReportException;

import static teacher_teamviewer.RegimeView.EXIT;
import static teacher_teamviewer.RegimeView.INSCRIBED;

import teacher_teamviewer.robotDevice.ActionType;
import teacher_teamviewer.robotDevice.MessageAction;

/**
 *
 * @author viky
 */
public class CanvasScreen extends JPanel
{
    private BufferedImage bi;
    public RegimeView regimeCurrent;
    private Dimension imgD;
   
    public boolean isControl;
    private int scale_point=1;
    private Point p= new Point(0,0);
    public TCP_RemoteAccessThr SenderCommand;
    
    class MyMouseMotionListener extends MouseMotionAdapter
    {

        @Override
        public void mouseDragged(MouseEvent me)
        {
            super.mouseDragged(me);
            super.mouseMoved(me); //To change body of generated methods, choose Tools | Templates.
            Rectangle Screen =new Rectangle(CanvasScreen.this.p.x,CanvasScreen.this.p.y,CanvasScreen.this.imgD.width,CanvasScreen.this.imgD.height);
            if(CanvasScreen.this.isControl && Screen.contains(me.getPoint()))
            { 
                Point M= CanvasScreen.this.getUserPoint(me.getPoint());
                //System.out.println("X ="+M.x + "   Y="+M.y);         
                CanvasScreen.this.SenderCommand.Send(new MessageAction(ActionType.Mouse_Move, M.x,M.y));
                
            }
            
           
        }
        
        
        @Override
        public  void mouseMoved(MouseEvent e)
        {
            
            super.mouseMoved(e); //To change body of generated methods, choose Tools | Templates.
            Rectangle Screen =new Rectangle(CanvasScreen.this.p.x,CanvasScreen.this.p.y,CanvasScreen.this.imgD.width,CanvasScreen.this.imgD.height);
            if(CanvasScreen.this.isControl && Screen.contains(e.getPoint()))
            { 
                Point M= CanvasScreen.this.getUserPoint(e.getPoint());                
                //System.out.println("X ="+M.x + "   Y="+M.y);
                CanvasScreen.this.SenderCommand.Send(new MessageAction(ActionType.Mouse_Move, M.x,M.y));
                
            }
        }

    }
   
    class MyMouseAdapter extends MouseAdapter    
    {

        @Override
        public void mousePressed(MouseEvent e)
        {
            super.mousePressed(e);
            System.out.println(e.getButton());
            if(CanvasScreen.this.isControl )
            { 
             CanvasScreen.this.SenderCommand.Send(new MessageAction(ActionType.Mouse_Press, e.getButton()));
                  
            }
        
        }
        
        @Override
        public void mouseReleased(MouseEvent e)
        {
            super.mouseReleased(e);
            System.out.println(e.getButton());
            if(CanvasScreen.this.isControl )
            { 
                CanvasScreen.this.SenderCommand.Send(new MessageAction(ActionType.Mouse_Release, e.getButton()));
            }
        
        }
        
                
        @Override
        public synchronized void mouseExited(MouseEvent e)
        {
            super.mouseExited(e); //To change body of generated methods, choose Tools | Templates.
            
        }

        @Override
        public synchronized void mouseEntered(MouseEvent e)
        {
            super.mouseEntered(e); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public synchronized void mouseDragged(MouseEvent e)
        {
            super.mouseDragged(e); //To change body of generated methods, choose Tools | Templates.
            // System.out.println( " Dragged X =" +e.getX()+ "  Y = "+ e.getY());
        }
        
        
    
    }
    
    private class MyDispatcher implements KeyEventDispatcher
    {
       
        @Override
        public synchronized boolean dispatchKeyEvent(KeyEvent e)
        { 
            if(!CanvasScreen.this.isControl)
                 return false;
            
            switch(e.getID())
            {
                case KeyEvent.KEY_PRESSED:
                    CanvasScreen.this.SenderCommand.Send(new MessageAction(ActionType.Key_Press, e.getKeyCode()));
                    break;
                case KeyEvent.KEY_RELEASED:
                    CanvasScreen.this.SenderCommand.Send(new MessageAction(ActionType.Key_Release, e.getKeyCode()));
                    break;
            }
           
           return false;
        }
    }
   
    public class TCP_RemoteAccessThr
{
    
    private Socket client;
    
    
    public void setSocket(Socket client)
    {
         this.client = client;
       
    }
    
    public void Send( MessageAction MA)
    {
        ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
        DataOutputStream DOS = new DataOutputStream(BAOS);
        try
        {   
            {
                BAOS.reset();
                DOS.flush();
                MA.write(DOS); 
                byte [] a =BAOS.toByteArray();
                this.client.getOutputStream().write(a, 0, a.length);

                byte [] ansver = new byte[4];
                int cnt=this.client.getInputStream().read(ansver, 0,ansver.length );
                if(ansver[0]!=1)
                {
                    System.out.println( " ERROR  !!!!!!!!!");
                }
                    
            }
            
        }
        catch (IOException ex)
        {
          System.out.println("1 " + ex.getMessage() + "Begin remove"+ this.client.getInetAddress() );
          ReportException.write(this.getClass().getName()+" \t1 \t"+ex.getMessage());
        } 
        
        finally
        {
            
            try
            {
                BAOS.close();
                DOS.close();
            } catch (IOException ex)
            {
                Logger.getLogger(CanvasScreen.class.getName()).log(Level.SEVERE, null, ex);
                ReportException.write(this.getClass().getName()+"\t2\t"+ex.getMessage());
            }
            
        }
        
    }

}

    
    public CanvasScreen(BufferedImage BI)
    {
        this.isControl=false;
        
        this.imgD=new Dimension (BI.getWidth(),BI.getHeight());
        this.setPreferredSize(imgD);
        this.bi=BI;
        this.regimeCurrent=RegimeView.INSCRIBED;
        MyMouseAdapter MMA= new  MyMouseAdapter();
        MyMouseMotionListener MMML = new MyMouseMotionListener();
        
        this.SenderCommand= new TCP_RemoteAccessThr();
        // подключение и обработка событий мыши ()
        this.addMouseListener(MMA);
        this.addMouseMotionListener(MMML);
        
        
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new CanvasScreen.MyDispatcher());
       
        this.addComponentListener(new ComponentAdapter()
       {
           
           @Override
           public void componentResized(ComponentEvent e)
           {
               super.componentResized(e); 
                
           }

       
       } );
        
        
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        
        if(this.regimeCurrent==INSCRIBED)
        {
           g.setColor(new Color(150,150,180));
           g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
        if(this.regimeCurrent!=EXIT)
        {
            Graphics2D g2D = (Graphics2D) g.create();
           // g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2D.drawImage(this.bi, this.p.x, this.p.y,this.imgD.width, this.imgD.height, this);
            g2D.dispose();
        }
       
    }

    void getImage(BufferedImage BI)
    {
        try
        {
        this.bi=BI;
        switch(this.regimeCurrent)
        {
            case FULLSCREEN:
             
            break;
                
            case USERSIZE:
               
                this.imgD = new Dimension(this.bi.getWidth(),this.bi.getHeight());
                this.setPreferredSize(this.imgD);
                this.scale_point=1;
                this.p.x=0;
                this.p.y=0;
            break;
                
            case INSCRIBED:
                UpdateSize();
              //  this.setPreferredSize(this.imgD);
                break;
        }
        
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());   
            ReportException.write(this.getClass().getName()+"\t2\t"+ex.getMessage());
        }
    }
    
    private void UpdateSize()
    {
        int w= this.getParent().getWidth();
        int h =this.getParent().getHeight(); 
        
       // System.out.println("scaleH  =" + w + "    ="+h);   
        this.setPreferredSize(new Dimension(w,h));
        
        int scaleW=(w*1000)/this.bi.getWidth();
       // System.out.println("scaleW  = " + scaleW);   
                 
        int scaleH=(h*1000)/this.bi.getHeight();
         
        if(scaleW<scaleH)
        {  
            this.scale_point=w*1000/this.bi.getWidth();
            this.imgD=new Dimension(w,this.scale_point*this.bi.getHeight()/1000);
            this.p.x=0;
            this.p.y=(h-this.imgD.height)/2;
        }
        else
        {
          //  System.out.println(scaleH*this.bi.getHeight());
            this.scale_point=h*1000/this.bi.getHeight();
            this.imgD=new Dimension(this.bi.getWidth()*this.scale_point/1000,h);
            this.p.x= (w-this.imgD.width)/2;
            this.p.y=0;
        }
    
    
    }
    
    public void UserSize()
    {
        this.setPreferredSize( new Dimension(this.bi.getWidth(),this.bi.getHeight()));
    }
    
    // размеры окна
    public void FrameSize()
    {
        this.setPreferredSize( new Dimension(this.getParent().getWidth(),this.getParent().getHeight()));
    }
    
    //определение координат курсора мыши 
    //масштабируемого отображения с учетом расположения изображения по центу(учет полей)
    //возвращает откорректированнные координаты для отправки команды
    private Point getUserPoint( Point p_m)
    {
        Point tmp= new Point();
        if(this.regimeCurrent==INSCRIBED)            
        {
            tmp.x=(p_m.x-this.p.x)*1000/this.scale_point;
            tmp.y=(p_m.y-this.p.y)*1000/this.scale_point;
        
            return tmp;
        }
        else
            return p_m;
    
    }
    
    
}
