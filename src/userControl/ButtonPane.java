/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userControl;


import java.awt.BorderLayout;
import java.awt.Dimension;

import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;

import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;


/**
 *
 * @author 06585
 */
public class ButtonPane extends JPanel
{
   // флаг состояния
    public boolean state;
    private JToggleButton B;
    
    private Dimension open;
    private Dimension close;
   
    
    Icon openIcon=ImageIconURL.get("resources/closeGreen.png");
    Icon closeIcon=ImageIconURL.get("resources/openGreen.png");
    
    /**
     * 
     * @param btn_text надпись кнопки разворачивания
     * @param w ширина панели
     * @param openY высота в открытом состоянии
     * @param closeY высота в закрытом состоянии
     */
    public ButtonPane(String btn_text)
    {      
        this.setLayout(new BorderLayout());
        this.state=false;
        this.B= new JToggleButton (btn_text);
        this.B.setPreferredSize(new Dimension(SizeSketch.ROW_WIDTH,SizeSketch.CONTROL_HEIGHT));
        this.B.setHorizontalAlignment(SwingConstants.LEFT);
        this.B.setMargin( new Insets(2, 2, 2, 14));
        this.B.setIcon(closeIcon);        
        this.B.setFocusPainted(false);//нет рамки фокуса
        
        //размеры кнопки разворачивания панели
              
        this.B.addActionListener(
        new  ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String tmp=ButtonPane.this.B.getText();
              if(ButtonPane.this.state)
                {
                  ButtonPane.this.setPreferredSize(ButtonPane.this.close);  
                  ButtonPane.this.B.setText("1");
                  ButtonPane.this.B.setText(tmp);
                  ButtonPane.this.B.setIcon(closeIcon);
                }
               else
                {
                  ButtonPane.this.setPreferredSize(ButtonPane.this.open);
                  ButtonPane.this.B.setText("2");
                  ButtonPane.this.B.setText(tmp);
                  ButtonPane.this.B.setIcon(openIcon);
                }
               ButtonPane.this.state=!ButtonPane.this.state;
             }
             
        });        
        this.add(this.B, BorderLayout.NORTH);   
        this.setBorder(BorderFactory.createLoweredBevelBorder());
    }
    
    
     /**
     * задаем уже сформированную панель
     * @param p 
     * @param rowsClose 
     */
    public void setPanel (JPanel p, int rowsClose)
    {  
        this.open=p.getPreferredSize();
        this.close=new Dimension(SizeSketch.ROW_WIDTH, SizeSketch.ROW_HEIGHT*rowsClose);
        this.add(p,BorderLayout.CENTER);
        setOpen ();
    }
    
    /**
     * Задать открытое состояние
     */
    public void setOpen ()
    {
      this.setPreferredSize(ButtonPane.this.open);
      this.B.setIcon(openIcon);
      this.state=true;
      this.B.setSelected(state);     
       
    }
    
    /**
     * Название кнопки разворачивания
     * @param text Текст на кнопке после иконки
     */
    public void setBtnText(String text)
    {
        this.B.setText(text);
    }
  
}

