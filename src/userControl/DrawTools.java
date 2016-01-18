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
import myjavadesc.shapes.ShapeType.TableCl;

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
    private int countRowComponents=4;
    public DrawTools(  )
    {
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        this.EvShapeType=new EventShapeTypeChanged();
        ButtonGroup bg = new ButtonGroup();
        for(int j=0;j<ShapeType.Shapes.length/countRowComponents;j++)
        {
            
            JPanel row= new JPanel(new FlowLayout(FlowLayout.CENTER));
            row.setPreferredSize(new Dimension(SizeSketch.ROW_WIDTH,btnSize.height));
            for (int i = 0; i <countRowComponents; i++)
            {
            final IShapeModel shape=ShapeType.Shapes[i+j*countRowComponents];
            JToggleButton tb= new JToggleButton();
            tb.setIcon(ImageIconURL.get("resources/shapes/"+shape.getName()+".png"));
            tb.setSelectedIcon(ImageIconURL.get("resources/shapes/"+shape.getName()+"_selected.png"));
            tb.setPreferredSize(btnSize);
            this.setButtonPaintOff(tb);
            bg.add(tb);
            tb.setToolTipText(shape.getToolTipText());
            tb.addActionListener
            ( 
                    new  ActionListener()
                    {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                  IChShapeType IShapeType= (IChShapeType)EvShapeType.getListener();
                  
                  IShapeType.setShapeType(shape.getType());  
                }}
             );
             row.add(tb);
            }
            this.add(row); 
        }
        
        JToggleButton tb= new JToggleButton();
        final TableCl table =new TableCl();
        tb.setIcon(ImageIconURL.get("resources/shapes/"+table.getName()+".png"));
        tb.setSelectedIcon(ImageIconURL.get("resources/shapes/"+table.getName()+"_selected.png"));
        tb.setPreferredSize(btnSizeTable);
        this.setButtonPaintOff(tb);
        bg.add(tb);
        tb.setToolTipText(table.getToolTipText());
        
        JPanel row= new JPanel(new FlowLayout(FlowLayout.CENTER));
        row.setPreferredSize(new Dimension(SizeSketch.ROW_WIDTH,btnSizeTable.height));
        tb.addActionListener
        ( 
            new  ActionListener()
            {
               @Override
               public void actionPerformed(ActionEvent e)
               {
                 IChShapeType IShapeType= (IChShapeType)EvShapeType.getListener();
                 IShapeType.setShapeType(table.getType());  
               }
         });
        row.add(tb);
        this.add(row);    
        
        JPanel p= new JPanel();
        p.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        
       JLabel rows= new JLabel (ImageIconURL.get("resources/row.png"));
       p.add(rows);
       
        this.rowsCB= new JComboBox();
         for(byte i=1; i<countMAX; i++)
            rowsCB.addItem(i);
        rowsCB.setToolTipText("Количество строк");
         p.add(rowsCB);
         JLabel column= new JLabel (ImageIconURL.get("resources/column.png"));
        p.add(column);
        this.colCB= new JComboBox();
         for(byte i=1; i<countMAX; i++)
            colCB.addItem(i);
         
          colCB.setToolTipText("Количество столбцов");
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
