/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userControl;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import static javax.swing.GroupLayout.Alignment.BASELINE;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author viky
 */
public class SettingTools 
{
     private Dimension btnSize= new Dimension(40,30);  
     public LinePreview currLine;
     public JPanel currFill;
     public JButton colorLine;
     public JButton colorFill;
     public DashLineComboBox styleLine;
     public WidthLineComboBox thicnessLine;
     public StartLineComboBox startLineCB;
     public EndLineComboBox endLineCB;
     
     // панель всегда доступна
    public JPanel settingPanel;
    //панель может быть скрыта
    public JPanel settingPanelHide;
     
     private Object[][] widthLine={{1,"resources/lineCB1.png","resources/lineCB1_s.png"},
     {2,"resources/lineCB2.png", "resources/lineCB2_s.png"},{4,"resources/lineCB4.png","resources/lineCB4_s.png"},
     {6,"resources/lineCB6.png","resources/lineCB6_s.png"},{10,"resources/lineCB10.png","resources/lineCB10_s.png"}
     };
     
     private Object[][] dashLine={{0,"resources/lineCB1.png","resources/lineCB1_s.png"},
     {1,"resources/lineCB_d2.png","resources/lineCB_d2.png"},{2,"resources/lineCB_d3.png","resources/lineCB_d3.png"},
     {3,"resources/lineCB_d4.png","resources/lineCB_d4.png"},{4,"resources/lineCB_d5.png","resources/lineCB_d5.png"}
     };
     
     private Object[][] startLine={{0,"resources/startLine.png","resources/startLine.png"},
     {1,"resources/startLineArrow.png","resources/startLineArrow.png"},{2,"resources/startLineCircle.png","resources/startLineCircle.png"},
     {3,"resources/startLineRect.png","resources/startLineRect.png"}
     };
     
     private Object[][] endLine={{0,"resources/startLine.png","resources/startLine.png"},
     {1,"resources/endLineArrow.png","resources/endLineArrow.png"},{2,"resources/endLineCircle.png","resources/endLineCircle.png"},
     {3,"resources/endLineRect.png","resources/endLineRect.png"}
     };
     
     
     public SettingTools( float w, int t)
     {
         
        this.settingPanel= new JPanel();
        GroupLayout layout = new GroupLayout(this.settingPanel);
        this.settingPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
       // this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
        this.currLine= new LinePreview();
        this.currLine.setPreferredSize(new Dimension(SizeSketch.LABEL_WIDTH, SizeSketch.LABEL_HEIGHT));        
        this.currLine.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.currFill= new JPanel();
        this.currFill.setPreferredSize(new Dimension(SizeSketch.LABEL_WIDTH, SizeSketch.LABEL_HEIGHT));
        this.currFill.setToolTipText("Цвет заливки");
        this.currFill.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
       
        
        this.colorLine= new JButton(ImageIconURL.get("resources/colorPen30.png"));
        this.colorLine.setPressedIcon(ImageIconURL.get("resources/colorPen30_press.png"));
        this.colorLine.setPreferredSize(btnSize);
        this.setButtonPaintOff(this.colorLine);
      
       
        
        this.colorFill= new JButton(ImageIconURL.get("resources/colorBrush30.png"));
        this.colorFill.setPressedIcon(ImageIconURL.get("resources/colorBrush30_press.png"));
        this.colorFill.setPreferredSize(btnSize);
       
        this.setButtonPaintOff(this.colorFill);
        
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addComponent(this.currLine)
                .addComponent(this.currFill)                
                );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(this.currLine)
                .addComponent(this.currFill)
               
        );        
        
        JLabel labThincness= new JLabel("Толщина линии");
        labThincness.setPreferredSize(new Dimension(SizeSketch.COMBOBOX_WIDTH, SizeSketch.CONTROL_HEIGHT));
       
        this.thicnessLine= new WidthLineComboBox();        
        this.thicnessLine.setEditable(true);
        this.thicnessLine.addItems(widthLine);
        this.thicnessLine.setSelectedItem(getSelectedThickness((int) w));
        this.thicnessLine.setEnabled(false);
      //  this.thicnessLine.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        JLabel labStyle= new JLabel("Тип линии");
        labStyle.setPreferredSize(new Dimension(SizeSketch.COMBOBOX_WIDTH, SizeSketch.CONTROL_HEIGHT));
       
        this.styleLine=  new DashLineComboBox();
        this.styleLine.setEditable(true);
        this.styleLine.addItems(dashLine);
        this.styleLine.setSelectedItem(getSelectedDash(t));       
        this.styleLine.setEnabled(false);
        JLabel lineStart= new JLabel("Начало линии");
        JLabel lineEnd= new JLabel("Конец линии");
        this.startLineCB=  new StartLineComboBox();
        this.startLineCB.setEditable(true);
        this.startLineCB.addItems(startLine);
        this.startLineCB.setSelectedItem(getSelectedDash(t));
        
       
        this.startLineCB.setEnabled(false);
        
        this.endLineCB=  new EndLineComboBox();
        this.endLineCB.setEditable(true);
        this.endLineCB.addItems(endLine);
        this.endLineCB.setSelectedItem(getSelectedDash(t));
        
        
        this.endLineCB.setEnabled(false);
        
        this.settingPanelHide= new JPanel();
        layout = new GroupLayout(this.settingPanelHide);
        this.settingPanelHide.setLayout(layout);
        layout.setAutoCreateGaps(false);
        layout.setAutoCreateContainerGaps(true);
        layout.linkSize(SwingConstants.HORIZONTAL, this.thicnessLine, this.styleLine,this.startLineCB,this.endLineCB);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
               .addGroup(layout.createSequentialGroup() 
                    .addComponent(this.colorLine)
                    .addComponent(this.colorFill))
                .addComponent(labThincness)
                .addComponent(this.thicnessLine)
                .addComponent(labStyle)
                .addComponent(this.styleLine)
                .addComponent(lineStart)
                .addComponent(this.startLineCB)
                .addComponent(lineEnd)
                .addComponent(this.endLineCB)
               
                );
        
        layout.setVerticalGroup( layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE) 
                    .addComponent(this.colorLine)
                    .addComponent(this.colorFill))
                .addComponent(labThincness)
                .addComponent(this.thicnessLine)
                .addComponent(labStyle)
                .addComponent(this.styleLine)
                .addComponent(lineStart)
                .addComponent(this.startLineCB)
                .addComponent(lineEnd)
                .addComponent(this.endLineCB)
        );
        
     
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
     this.startLineCB.setEnabled(true);
     this.endLineCB.setEnabled(true);
     
     }
    
}

