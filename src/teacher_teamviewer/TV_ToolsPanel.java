/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teacher_teamviewer;

import java.awt.BorderLayout;
import java.awt.Color;
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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EtchedBorder;

import userControl.ImageIconURL;

/**
 *
 * @author viky
 */
public class TV_ToolsPanel extends JPanel
{
    public EventModeTypeChanged EMT;
    public JPanel Tools = new JPanel();
   
    private boolean status=false;
    
   
    
    
    private JToggleButton Mode;
    public JToggleButton FullScreen;
    public JToggleButton b_scale;
    public JToggleButton b_control;
    public JButton b_exit;
    
  
    private Dimension panelSize= new Dimension(120,240);
   
    private Rectangle openRect;
    private Rectangle closeRect;
   
    private JLabel studentName;
    private int beginMoveY;
    
    class MyMouselistener extends MouseAdapter
    {      
        @Override
        public void mousePressed(MouseEvent me)
        {
          TV_ToolsPanel.this.beginMoveY=me.getY(); 
        }
    
    }
    class MyMouseMotionListener extends MouseMotionAdapter
    {
        

        @Override
        public void mouseDragged(MouseEvent me)
        {
          
          int y =me.getYOnScreen()-TV_ToolsPanel.this.closeRect.y;
          int Y=TV_ToolsPanel.this.closeRect.y+y-TV_ToolsPanel.this.beginMoveY;
          if(Y<0 ||(Y+TV_ToolsPanel.this.closeRect.height)>TV_ToolsPanel.this.getParent().getHeight())
                return;                
          System.out.println( " y" +y);
          TV_ToolsPanel.this.closeRect.y=Y;
          TV_ToolsPanel.this.openRect.y=Y;
          TV_ToolsPanel.this.setPosition();        
             
       }
    } 
    public TV_ToolsPanel(Dimension d )
    {
      this.Tools = new JPanel();
      this.openRect = new Rectangle(panelSize);
      this.closeRect= new Rectangle(panelSize);
      setStartPoints(d) ;
      this.setPreferredSize(this.closeRect.getSize());
      this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
      this.setBounds(this.closeRect);
      
      
      this.Mode=new JToggleButton();
      this.Mode.setPreferredSize(new Dimension(20,100));
      this.Mode.setIcon(ImageIconURL.get("resources/left_28.png"));
      this.Mode.setSelectedIcon(ImageIconURL.get("resources/right_28.png"));
      this.setToggleButtonPaintOff(Mode);
      this.Mode.addActionListener(new  ActionListener()              
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {  
                TV_ToolsPanel.this.status=!TV_ToolsPanel.this.status;
                TV_ToolsPanel.this.setPosition(); 
                TV_ToolsPanel.this.Tools.requestFocus();
            }

        } );
      
      
      createButtons();
      this.Tools.setLayout(new GridLayout(this.Tools.getComponentCount(),1));      
     // this.setLayout(new FlowLayout(FlowLayout.RIGHT));
      this.setLayout(new BorderLayout());
      this.setBounds(this.closeRect); 
      //this.setOpaque(false);//variant
      
     JPanel p= new JPanel(new FlowLayout(FlowLayout.LEFT));     
     p.add(this.Tools);
     p.add(Mode);
     
     studentName=new JLabel("******");
     studentName.setHorizontalAlignment(0);
     studentName.setBackground(Color.red);
    
     
     this.add(p,BorderLayout.CENTER);
     this.add(studentName,BorderLayout.SOUTH);
     this.addMouseMotionListener(new MyMouseMotionListener());
     this.addMouseListener( new MyMouselistener());
     
     
    }
    
    private void setToggleButtonPaintOff(JToggleButton TB)
    {
        TB.setFocusPainted(false);
        TB.setBorderPainted(false);
        TB.setContentAreaFilled(false);    
    }
    
    private void buttonPaintOff(JButton B)
    {
        B.setFocusPainted(false);
        B.setBorderPainted(false);
        B.setContentAreaFilled(false);    
    }
    
     private void createButtons()
    {
    
      this.FullScreen = new JToggleButton();
      /*
      this.FullScreen.setSelectedIcon(ImageIconURL.get("resources/fullscreeen_off_40.png"));
      this.FullScreen.setIcon(ImageIconURL.get("resources/fullscreeen_on_40.png"));
      setToggleButtonPaintOff(this.FullScreen);*/
      
      this.b_scale= new JToggleButton();
      this.b_scale.setIcon(ImageIconURL.get("resources/monitor_sc1.png"));
      this.b_scale.setSelectedIcon(ImageIconURL.get("resources/monitor_sc4.png"));
      setToggleButtonPaintOff(this.b_scale);
      
      this.b_control=new JToggleButton();
      this.b_control.setIcon(ImageIconURL.get("resources/controller_45_on.png"));
      this.b_control.setSelectedIcon(ImageIconURL.get("resources/controller_45_off.png"));
      setToggleButtonPaintOff(this.b_control);
      
      this.b_exit=new JButton(ImageIconURL.get("resources/exit_45.png"));
      buttonPaintOff(this.b_exit);
        //Dimension btnD=new Dimension (50,50);
     
       // FullScreen.setPreferredSize(btnD);
       // FullScreen.setToolTipText(" Полный экран ");
       // this.Tools.add(FullScreen);
        
        
      //  b_scale.setPreferredSize(btnD);
        b_scale.setToolTipText(" Экран вписан в окно приложения");
       
        this.Tools.add(b_scale);
       
        b_control.setToolTipText(" Включить управление ");
      //  b_control.setPreferredSize(btnD);
        this.Tools.add(b_control);
        
        
        b_exit.setToolTipText(" Вернуться к списку подключений");
      //  b_exit.setPreferredSize(btnD);
        this.Tools.add(b_exit);
    }
    
    public void setStudentName( String S)
    {
    this.studentName.setText(S);
    }
   
    public void closeMode()
    {
        this.status=false;
        this.setBounds(closeRect); 
       
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
        this.studentName.setVisible(!status);
    }
    
    // define start points openrect and closeRect
    private void setStartPoints(Dimension d) 
    {
        this.openRect.x=1-80;
        this.openRect.y=d.height/2-100;
        this.closeRect.x=0;
        this.closeRect.y=d.height/2-100;    
    }
      
   
    
    
    
    
}

