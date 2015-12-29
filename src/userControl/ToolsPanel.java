/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userControl;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Viky Панель инструментов содержит : -панель работы с группой
 * ButtonPane Group -панель работы с архивом группы ButtonPane Hystory -панель
 * настроек рисования ButtonPane Setting -панель переключения графических
 * объектов JPanel GraphObjects -панель редактора графики GraphEdit
 */
public class ToolsPanel extends JPanel
{

    private ButtonPane Hystory;
    private ButtonPane Group;
    private ButtonPane Setting;
    
    
    // private ButtonPane GraphFill;
    private JPanel GraphObjects;
    private JPanel GraphEdit;
    
    public int widthControl=0 ;
    public int width = 160;    
    private int delta =30;

    public ToolsPanel()
    {
       
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
      //  this.setPreferredSize(new Dimension(width , 300));
        this.widthControl=width-delta;
        //<editor-fold defaultstate="collapsed" desc="Группа ">
        this.Group = new ButtonPane("Группа", widthControl, 110, 50);
        this.Group.setOpen();
        this.add(this.Group);
        //</editor-fold>         
        //<editor-fold defaultstate="collapsed" desc="История ">
        this.Hystory = new ButtonPane("История", widthControl, 190, 75);
        this.Hystory.setOpen();
        this.add(this.Hystory);
        //</editor-fold>  
        
        //<editor-fold defaultstate="collapsed" desc="Настройки">
        this.Setting = new ButtonPane("Настройки", widthControl, 180, 50);
        this.Setting.setOpen();
        this.add(this.Setting);
        //</editor-fold> 
        //<editor-fold defaultstate="collapsed" desc="Графика">
        this.GraphObjects = new JPanel();
        
      //</editor-fold>   
       
        //<editor-fold defaultstate="collapsed" desc="Редактор">
        this.GraphEdit = new JPanel();
        //</editor-fold>  
        
        
    }

    public void setEditTools(JPanel p)
    {
        this.GraphEdit = p;
        this.add(this.GraphEdit);
    }


    public void setToolsGroups(Component cb, JPanel p)
    {       
        cb.setPreferredSize(new Dimension(this.widthControl, 20));
        this.Group.addRow(cb);
        JLabel l = new JLabel("Новая группа");
        l.setPreferredSize(new Dimension(this.widthControl, 20));
        this.Group.addRow(l);
        p.setPreferredSize(new Dimension(this.widthControl, 40));
        this.Group.addRow(p);
    }

    public void setDaysList(JPanel p)
    {
      
        this.Hystory.setPanel(p);
    }
    
    public void setSettingTools(JPanel p)
    {   
         this.Setting.setPanel(p);
    }


    public void setDrawTools(JPanel p)
    {
       p.setPreferredSize(new Dimension(this.width-20,100));
        this.GraphObjects = p;
        this.add(this.GraphObjects);
    }

    

}
