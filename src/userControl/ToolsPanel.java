/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userControl;

import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
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

    public ButtonPane Hystory;
    public ButtonPane Group;
    public ButtonPane Setting;
    
    
    // private ButtonPane GraphFill;
    private JPanel GraphObjects;
    private JPanel GraphEdit;
    

    public ToolsPanel()
    {  
       this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        //<editor-fold defaultstate="collapsed" desc="Группа ">
        this.Group = new ButtonPane("Группа");
        this.add(this.Group);
        //</editor-fold>         
        //<editor-fold defaultstate="collapsed" desc="История ">
        this.Hystory = new ButtonPane("История");
        this.add(this.Hystory);
        //</editor-fold> 
        //<editor-fold defaultstate="collapsed" desc="Настройки">
        this.Setting = new ButtonPane("Настройки");
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
    
    public void setDrawTools(JPanel p)
    {      
        this.GraphObjects = p;
        this.add(this.GraphObjects);
    }

    

}
