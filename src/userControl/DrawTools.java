/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userControl;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EtchedBorder;
import myjavadesc.events.EventShapeTypeChanged;
import myjavadesc.events.IChShapeType;
import myjavadesc.shapes.IShapeModel;
import myjavadesc.shapes.ShapeType;

/**
 *
 * @author Viky_Pa
 * 
 * панель переключения текущего графического объекта
 * cобъект создается в MasterBoard
 * размещается на ToolsPanel
 * 
 */
public class DrawTools extends JPanel
{
    //событие изменения текущего графического объекта
    public EventShapeTypeChanged EvShapeType;
    // размер кнопки
    private Dimension btnSize= new Dimension(25,25);    
    private final  byte countMAX=15;
    
    private JComboBox colCB;
    private JComboBox rowsCB;
    private Dimension btnSizeTable= new Dimension(35,35);
    public DrawTools(  )
    {
        this.setLayout(new FlowLayout());
        this.setPreferredSize(new Dimension(SizeSketch.ROW_WIDTH,btnSize.height*6));
        this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
        this.EvShapeType=new EventShapeTypeChanged();
        
        
        
        ButtonGroup bg = new ButtonGroup();
        for(final IShapeModel type:ShapeType.Shapes)
        {
            JToggleButton tb= new JToggleButton();
            tb.setIcon(ImageIconURL.get("resources/shapes/"+type.getName()+".png"));
            tb.setSelectedIcon(ImageIconURL.get("resources/shapes/"+type.getName()+"_selected.png"));
            if(type.getType()==ShapeType.Table)
                tb.setPreferredSize(btnSizeTable);
                else
                tb.setPreferredSize(btnSize);
            this.setButtonPaintOff(tb);
            bg.add(tb);
            tb.setToolTipText(type.getToolTipText());
            tb.addActionListener( 
                    new  ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
              IChShapeType IShapeType= (IChShapeType)EvShapeType.getListener();
              IShapeType.setShapeType(type.getType());  
            }}
        );
            this.add(tb);        
        }
        
        
        JPanel p= new JPanel();
      //  p.setLayout();
        p.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        p.setPreferredSize(new Dimension(80,btnSize.height*3));
        p.setSize(80,btnSize.height*3);
        
        
       JLabel rows= new JLabel (ImageIconURL.get("resources/row.png"));
       p.add(rows);
       
        this.rowsCB= new JComboBox();
         for(byte i=1; i<countMAX; i++)
            rowsCB.addItem(i);
        
         p.add(rowsCB);
         JLabel column= new JLabel (ImageIconURL.get("resources/column.png"));
        p.add(column);
        this.colCB= new JComboBox();
         for(byte i=1; i<countMAX; i++)
            colCB.addItem(i);
        p.add(colCB);
        this.add(p);
    } 
    
    public void setActionRowCount(ActionListener AL)
    {
    rowsCB.addActionListener(AL);
    }
    public void setActionColumnCount(ActionListener AL)
    {
    colCB.addActionListener(AL);
    }
  
    
    /**
     * Отключение прорисовки рамки, фона 
     * @param TB 
     */
     private void setButtonPaintOff(JToggleButton TB)
    {
        TB.setFocusPainted(false);
        TB.setBorderPainted(false);
        TB.setContentAreaFilled(false);    
    }
     
     
}
