/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userControl;


import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

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
    private JPanel P;
    
    private Dimension BSize;    
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
    public ButtonPane(String btn_text, int w, int openY, int closeY)
    {
        int W=w+10;
        this.setLayout(new BorderLayout());
        
        this.close = new Dimension(W,closeY);
        this.open= new Dimension(W,openY); 
        
        //размеры кнопки разворачивания панели
        this.BSize = new Dimension(W,20);
        
        this.state=false;
        this.B= new JToggleButton (btn_text);
        this.B.setHorizontalAlignment(SwingConstants.LEFT);
        this.B.setMargin( new Insets(2, 2, 2, 14));
        this.B.setIcon(closeIcon);        
        this.B.setFocusPainted(false);//нет рамки фокуса
        this.B.setPreferredSize(this.BSize);        
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
        
        
        this.P= new JPanel( new FlowLayout());        
        this.P.setPreferredSize(new Dimension(W,this.close.height-this.BSize.height));       
        this.setBorder(BorderFactory.createLoweredBevelBorder());
        this.add(this.P,BorderLayout.CENTER);
        this.setPreferredSize(this.close);
    }
    
    /**
     * добавление компонента на панель
     * @param C компонента
     */
    public void addRow (Component C)
    {
        this.P.add(C);
    }
     /**
     * задаем уже сформированную панель
     * @param p 
     */
    public void setPanel (JPanel p)
    {       
       // p.setPreferredSize(new Dimension(this.BSize.width,this.close.height-this.BSize.height));   
     //
        
        this.add(p,BorderLayout.CENTER);
        // this.P.add(p);
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

