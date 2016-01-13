/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userControl;

import myjavadesc.events.EventModeTypeChanged;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EtchedBorder;
import myjavadesc.events.IChModeType;

/**
 *
 * @author viky
 */
public class ModeChanger extends JPanel
{
    public EventModeTypeChanged EMT;
    
    private JToggleButton Mode;
    private boolean status=false;
    private JToggleButton SendScreen;
    public JToggleButton Record;
    private JToggleButton board;
    private JToggleButton viewers;
  
    private Dimension panelSize= new Dimension(110,220);
    private int beginMoveY;
   
    private Rectangle openRect;
    private Rectangle closeRect;
    class MyMouselistener extends MouseAdapter
    {      
        @Override
        public void mousePressed(MouseEvent me)
        {
           // System.out.println("   mousePressed ");
            
            ModeChanger.this.beginMoveY=me.getY();   
            
        }
    
    }
    class MyMouseMotionListener extends MouseMotionAdapter
    {
        

        @Override
        public void mouseDragged(MouseEvent me)
        {
           
          int y =me.getYOnScreen()-ModeChanger.this.closeRect.y;
          int Y=ModeChanger.this.closeRect.y+y-ModeChanger.this.beginMoveY;
          if(Y<0 ||(Y+ModeChanger.this.closeRect.height)>ModeChanger.this.getParent().getHeight())
                return;                
         // System.out.println( " y" +y);
          ModeChanger.this.closeRect.y=Y;
          ModeChanger.this.openRect.y=Y;
          ModeChanger.this.setPosition();        
             
        }
        
        @Override
        public void mouseMoved(MouseEvent me)
        {
             ModeChanger.this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.MOVE_CURSOR));
        
        }
        
        
    }
    
    public ModeChanger(Dimension d)
    {
      this.openRect = new Rectangle(panelSize);
      this.closeRect= new Rectangle(panelSize);      
      setStartPoints(d) ;
      this.setPreferredSize(this.closeRect.getSize());
      this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
      this.setBounds(this.closeRect);       
      this.addMouseMotionListener(new ModeChanger.MyMouseMotionListener());
      this.addMouseListener(new ModeChanger.MyMouselistener());
      this.Mode=new JToggleButton();//ImageIconURL.get("resources/open.png")
      this.Mode.setPreferredSize(new Dimension(20,100));
      this.Mode.setIcon(ImageIconURL.get("resources/left_28.png"));
      this.Mode.setSelectedIcon(ImageIconURL.get("resources/right_28.png"));
      
      this.setToggleButtonPaintOff(Mode);
      this.Mode.addActionListener(new  ActionListener()              
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {  
                ModeChanger.this.status=!ModeChanger.this.status;
                if(ModeChanger.this.status)
                {
                ModeChanger.this.Mode.setToolTipText("Открыть");
                }
                else
                {
                ModeChanger.this.Mode.setToolTipText("Закрыть");
                }
                ModeChanger.this.setPosition(); 
            }

        } );
      
      
      this.SendScreen=new JToggleButton(ImageIconURL.get("resources/monitor_45.png"));
      this.SendScreen.setIcon(ImageIconURL.get("resources/video_start_45.png"));
      this.SendScreen.setSelectedIcon(ImageIconURL.get("resources/video_stop_45.png"));     
      this.setToggleButtonPaintOff(this.SendScreen);
      this.SendScreen.setToolTipText("Просмотр подключений");
      this.Record=new JToggleButton(ImageIconURL.get("resources/micStart.png")); 
      this.Record.setSelectedIcon(ImageIconURL.get("resources/micStop.png"));
      this.setToggleButtonPaintOff(this.Record);      
      this.Record.setToolTipText("Включение записи звука");
      this.Record.setEnabled(false);
      this.viewers= new JToggleButton(ImageIconURL.get("resources/monitor_45.png"));
      this.viewers.setToolTipText("Включение трансляции рабочего стола");
      this.setToggleButtonPaintOff(this.viewers);
      this.viewers.addActionListener(new ActionListener()
      {
          @Override
          public void actionPerformed(ActionEvent e)
          {
             IChModeType MT= (IChModeType)ModeChanger.this.EMT.getListener();
             MT.setModeType(ModeType.viewer);
             ModeChanger.this.closeMode();          
            
          }
      });
      this.board= new JToggleButton(ImageIconURL.get("resources/board45.png"));
      this.setToggleButtonPaintOff( this.board);
      this.board.setToolTipText("Доска");
      
      this.board.addActionListener(new ActionListener()
      {  @Override
          public void actionPerformed(ActionEvent e)
          {
             IChModeType MT= (IChModeType)ModeChanger.this.EMT.getListener();
             MT.setModeType(ModeType.board);
             ModeChanger.this.closeMode();
          }
      });
      
      this.EMT= new EventModeTypeChanged();
      
      ButtonGroup bg = new ButtonGroup();
      bg.add(viewers);
      bg.add(board);
      
      
      JPanel p_modes=new JPanel(); 
      p_modes.setPreferredSize(new Dimension(50,200));
      p_modes.setLayout(new GridLayout(4,0));
      
      p_modes.add(viewers);
      p_modes.add(board);
      p_modes.add(SendScreen);
      p_modes.add(Record);
      
      this.setLayout(new FlowLayout(FlowLayout.LEFT));       
      this.add(Mode);
     JPanel Struts = new JPanel();
     
     Struts.setPreferredSize(new Dimension(10,100));
     Struts.setOpaque(false);
      this.add(Struts);
      this.add(p_modes);
     
    }
    private void setToggleButtonPaintOff(JToggleButton TB)
    {
        TB.setFocusPainted(false);
        TB.setBorderPainted(false);
        TB.setContentAreaFilled(false);    
        TB.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
    }
    
   
    private void closeMode()
    {
        this.status=false;
        this.setBounds(closeRect); 
        this.Mode.setSelected(false);
    }
    
    //changing the location depending on the size of the window
    public void setNewLocation(Dimension d)
    {
        setStartPoints(d) ;       
        this.setPosition();
        this.repaint();
    }
    
    //open or close panel depending on the status properties
    private void setPosition()
    {
         
        if(this.status)
        {
           this.setBounds(this.openRect);           
        }
        else
        {
            this.setBounds(this.closeRect);           
        }
    }
    
    // define start points openrect and closeRect
    private void setStartPoints(Dimension d) 
    {
        this.openRect.x=d.width-110;
        this.openRect.y=d.height/2-100;
        this.closeRect.x=d.width-50;
        this.closeRect.y=d.height/2-100;    
    }
            
    public void setActionListenerSendScreen(ActionListener AL)
    {
    this.SendScreen.addActionListener(AL);
    }
    
    public void setActionListenerRecord(ActionListener AL)
    {
        this.Record.addActionListener(AL);
    }
}
