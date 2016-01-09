/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userControl;

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
     public StartLineComboBox startLineCB;
     public EndLineComboBox endLineCB;
     public int rowsClose=2;
     
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
         int rows=0;
        this.setLayout(new FlowLayout());  
       // this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
        this.currLine= new LinePreview();
        this.currLine.setPreferredSize(new Dimension(SizeSketch.LABEL_WIDTH, SizeSketch.CONTROL_HEIGHT));        
        
        this.currFill= new JPanel();
        this.currFill.setPreferredSize(new Dimension(SizeSketch.LABEL_WIDTH, SizeSketch.CONTROL_HEIGHT));
        this.currFill.setToolTipText("Цвет заливки");
        
        this.add(this.currLine);
        this.add(this.currFill);
        rows++;
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
        rows++;
        this.add( new JLabel("Толщина линии"));
        rows++;
        this.thicnessLine= new WidthLineComboBox();        
        this.thicnessLine.setEditable(true);
        this.thicnessLine.addItems(widthLine);
        this.add(this.thicnessLine); 
        rows++;
        this.thicnessLine.setSelectedItem(getSelectedThickness((int) w));
        this.thicnessLine.setEnabled(false);
        
        this.add( new JLabel("Тип линии  "));
        rows++;
        this.styleLine=  new DashLineComboBox();
        this.styleLine.setEditable(true);
        this.styleLine.addItems(dashLine);
        this.styleLine.setSelectedItem(getSelectedDash(t));
        this.add(this.styleLine);
        rows++;
        this.styleLine.setEnabled(false);
        
        this.add( new JLabel("Начало и конец линии"));
        rows++;
        this.startLineCB=  new StartLineComboBox();
        this.startLineCB.setEditable(true);
        this.startLineCB.addItems(startLine);
        this.startLineCB.setSelectedItem(getSelectedDash(t));
        this.add(this.startLineCB);
       
        this.startLineCB.setEnabled(false);
        
        this.endLineCB=  new EndLineComboBox();
        this.endLineCB.setEditable(true);
        this.endLineCB.addItems(endLine);
        this.endLineCB.setSelectedItem(getSelectedDash(t));
        this.add(this.endLineCB);
        rows++;
        this.endLineCB.setEnabled(false);
        
        this.setPreferredSize(new Dimension(SizeSketch.ROW_WIDTH, SizeSketch.ROW_HEIGHT*rows+btnSize.height));
        
     
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

