/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userControl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author viky
 */
public class SettingTools extends JPanel
{
     private Dimension btnSize= new Dimension(40,30);  
     public LinePreview currLine;
     public JPanel currFill;
     public JButton colorLine;
     public JButton colorFill;
     public DashLineComboBox styleLine;
     public WidthLineComboBox thicnessLine;
     
     
     private Object[][] widthLine={{1,"resources/lineCB1.png","resources/lineCB1_s.png"},
     {2,"resources/lineCB2.png", "resources/lineCB2_s.png"},{4,"resources/lineCB4.png","resources/lineCB4_s.png"},
     {6,"resources/lineCB6.png","resources/lineCB6_s.png"},{10,"resources/lineCB10.png","resources/lineCB10_s.png"}
     };
     
     private Object[][] dashLine={{0,"resources/lineCB1.png","resources/lineCB1_s.png"},
     {1,"resources/lineCB_d2.png","resources/lineCB_d2.png"},{2,"resources/lineCB_d3.png","resources/lineCB_d3.png"},
     {3,"resources/lineCB_d4.png","resources/lineCB_d4.png"},{4,"resources/lineCB_d5.png","resources/lineCB_d5.png"}
     };
     
     public SettingTools( float w, int t, int w_p)
     {
        this.setLayout(new FlowLayout());       
       // this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
        this.currLine= new LinePreview();
        this.currLine.setPreferredSize(new Dimension((w_p-5)/2, 20));
        
        
        this.currFill= new JPanel();
        this.currFill.setPreferredSize(new Dimension((w_p-5)/2, 20));
        this.currFill.setToolTipText("Цвет заливки");
        
        this.add(this.currLine);
        this.add(this.currFill);
        this.colorLine= new JButton(ImageIconURL.get("resources/colorPen30.png"));
        this.colorLine.setPressedIcon(ImageIconURL.get("resources/colorPen30_press.png"));
        this.colorLine.setPreferredSize(btnSize);
        this.setButtonPaintOff(this.colorLine);
        this.add(this.colorLine);
        
        this.colorFill= new JButton(ImageIconURL.get("resources/colorBrush30.png"));
        this.colorFill.setPressedIcon(ImageIconURL.get("resources/colorBrush30_press.png"));
        this.colorFill.setPreferredSize(btnSize);
        this.setButtonPaintOff(this.colorFill);
        this.add(this.colorFill);
        
        this.add( new JLabel("Толщина линии"));
        
        this.thicnessLine= new WidthLineComboBox();
        
        this.thicnessLine.setEditable(true);
        this.thicnessLine.addItems(widthLine);
        this.add(this.thicnessLine);
        this.thicnessLine.setSelectedItem(getSelectedThickness((int) w));
        this.thicnessLine.setEnabled(false);
        
        this.add( new JLabel("Тип линии  "));
        this.styleLine=  new DashLineComboBox();
        this.styleLine.setEditable(true);
        this.styleLine.addItems(dashLine);
        this.styleLine.setSelectedItem(getSelectedDash(t));
        this.add(this.styleLine);
        this.styleLine.setEnabled(false);
        
     
     }
     /**
      * выставляем толщину линии
      * @param w толщина линии
      * @return выбранный объект 
      */
     private Object getSelectedThickness(int w)
     {
         for(Object[] obj:this.widthLine)
         {
             if((int)obj[0]==w)
                 return obj;
         
         }
        return this.widthLine[0];
     }
     
     /**
      * выставляем тип линии
      * @param t номер в массиве
      * @return выбранный элемент
      */
     
     private Object getSelectedDash(int t)
     {
         for(Object[] obj:this.dashLine)
         {
             if((int)obj[0]==t)
                 return obj;
         
         }
        return this.dashLine[0];
     }
     
     
      /**
     * Отключение прорисовки рамки, фона 
     * @param TB 
     */
     private void setButtonPaintOff(JButton B)
    {
        B.setFocusPainted(false);
        B.setBorderPainted(false);
        B.setContentAreaFilled(false);   
        B.setEnabled(false);
    }
     
     
     public void setEnabledAll()
     {
     this.colorLine.setEnabled(true);
     this.colorFill.setEnabled(true);
     this.thicnessLine.setEnabled(true);
     this.styleLine.setEnabled(true);
     
     }
    
}

