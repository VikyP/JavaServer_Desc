/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userControl;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
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
    private int countRowComponents=4;
    
    public static final IShapeModel[] Shapes={ new ShapeType.LineCl(), new ShapeType.LineHorizontalCl(), new ShapeType.LineVerticalCl(),new ShapeType.PenLineCl(), 
                                               new ShapeType.EllipseCl(),new ShapeType.FillEllipseCl(),new ShapeType.RectangleCl(),new ShapeType.FillRectangleCl(),
                                               new ShapeType.TableCl()
    };
    private JToggleButton [] buttons= new JToggleButton [Shapes.length];
    public DrawTools(  )
    {
        
        this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        this.EvShapeType=new EventShapeTypeChanged();
        ButtonGroup bg = new ButtonGroup();
        for(int i=0;i<Shapes.length;i++)
        {
            final IShapeModel shape=Shapes[i];
            buttons[i]= new JToggleButton();
            buttons[i].setIcon(ImageIconURL.get("resources/shapes/"+shape.getName()+".png"));
            buttons[i].setSelectedIcon(ImageIconURL.get("resources/shapes/"+shape.getName()+"_selected.png"));
            buttons[i].setPreferredSize(btnSize);
            this.setButtonPaintOff(buttons[i]);
            bg.add(buttons[i]);
            buttons[i].setToolTipText(shape.getToolTipText());
            buttons[i].addActionListener
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
           
          
        }
        buttons[ShapeType.Table].setPreferredSize(btnSizeTable);
        
        JLabel rows= new JLabel (ImageIconURL.get("resources/row.png"));
       rows.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
       
        this.rowsCB= new JComboBox();
         for(byte i=1; i<countMAX; i++)
            rowsCB.addItem(i);
        rowsCB.setToolTipText("Количество строк");
        rowsCB.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        JLabel column= new JLabel (ImageIconURL.get("resources/column.png"));
        column.setBorder(BorderFactory.createEmptyBorder(0, 0, 0,0));
        this.colCB= new JComboBox();
         for(byte i=1; i<countMAX; i++)
            colCB.addItem(i);
         
        colCB.setToolTipText("Количество столбцов");
        colCB.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER) 
                .addGroup(layout.createSequentialGroup() 
                    .addComponent(this.buttons[ShapeType.Line])
                    .addComponent(this.buttons[ShapeType.LineVertical])
                    .addComponent(this.buttons[ShapeType.LineHorizontal])
                    .addComponent(this.buttons[ShapeType.PenLine])    
                )
                .addGroup(layout.createSequentialGroup() 
                    .addComponent(this.buttons[ShapeType.Ellipse])
                    .addComponent(this.buttons[ShapeType.FillEllipse])
                    .addComponent(this.buttons[ShapeType.Rectangle])
                    .addComponent(this.buttons[ShapeType.FillRectangle])    
                )
                
                .addGroup(layout.createSequentialGroup() 
                .addComponent(buttons[ShapeType.Table])
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(rows)
                    .addComponent(column)   
                 )
                 .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(rowsCB)
                    .addComponent(colCB)
                 ))
                );
        layout.setVerticalGroup(layout.createSequentialGroup()                
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER) 
                   .addComponent(this.buttons[ShapeType.Line])
                    .addComponent(this.buttons[ShapeType.LineVertical])
                    .addComponent(this.buttons[ShapeType.LineHorizontal])
                    .addComponent(this.buttons[ShapeType.PenLine])   
                )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER) 
                   .addComponent(this.buttons[ShapeType.Ellipse])
                    .addComponent(this.buttons[ShapeType.FillEllipse])
                    .addComponent(this.buttons[ShapeType.Rectangle])
                    .addComponent(this.buttons[ShapeType.FillRectangle])    
                )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING) 
                .addComponent(buttons[ShapeType.Table])
                .addGroup(layout.createSequentialGroup() 
                    
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(rows)
                        .addComponent(rowsCB)
                        )
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(column)
                        .addComponent(colCB)
                        )
                )
                )
        );
        
        
        
       
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
        TB.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
    }
     
     
}
