/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userControl;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
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
    
    public DrawTools()
    {
        this.setLayout(new FlowLayout());
        this.setPreferredSize(new Dimension(145,100));
        this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
        this.EvShapeType=new EventShapeTypeChanged();
        
        ButtonGroup bg = new ButtonGroup();
        for(final IShapeModel type:ShapeType.Shapes)
        {
            JToggleButton tb= new JToggleButton();
            tb.setIcon(ImageIconURL.get("resources/shapes/"+type.getName()+".png"));
            tb.setSelectedIcon(ImageIconURL.get("resources/shapes/"+type.getName()+"_selected.png"));
            tb.setPreferredSize(btnSize);
            this.setButtonPaintOff(tb);
            bg.add(tb);
            tb.setToolTipText(type.getToolTipText());
            tb.addActionListener(new  ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
              IChShapeType IShapeType= (IChShapeType)EvShapeType.getListener();
              IShapeType.setShapeType(type.getType());  
            }
        });
            this.add(tb);        
        }
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
