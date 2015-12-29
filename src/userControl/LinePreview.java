/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userControl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import myjavadesc.shapes.DashArrays;

/**
 *
 * @author viky
 */
public class LinePreview extends JPanel
{
    private int type=0;
    private float width=1;
    private Color lineColor= Color.BLACK;
    
    public LinePreview ()
    {
        this.setToolTipText("Предварительный просмотр линии");
    
    }
    
    
    public void setProperty(Color c)
    {
        this.lineColor=c;
        this.repaint();
    
    }
    public void setProperty(float w)
    {
        this.width=w;
        this.repaint();
    
    }
    public void setProperty(int t)
    {
        this.type=t;
        this.repaint();
    
    }
    public void setProperties(Color c, float w, int t)
    {
        this.lineColor=c;
        this.width=w;
        this.type=t;
    
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d=(Graphics2D)g.create();
        g2d.setStroke(DashArrays.getStrokeLine(width, type));
        g2d.setColor(lineColor);
        g2d.drawLine(5, this.getHeight()/2, this.getWidth()-5, this.getHeight()/2);
        g2d.dispose();
    }
    
}
