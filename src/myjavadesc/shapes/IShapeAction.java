/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myjavadesc.shapes;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author 06585
 */
public interface IShapeAction 
{
    public void draw(Graphics g);
    
    
    /**
     * редактируемый прямоугольник фигуры
     * @param xStep смещение по х
     * @param yStep смещение по у
     * @return 
     */
    public  Rectangle move(int xStep, int yStep);
    public IShapeAction copyShape(int x, int y);
    public Point getBegin();
    public Point getEnd();
    public Rectangle getRectangle();
    public int getType();
    public void setEditable(boolean flag);
    
    
    public  Rectangle resize_moveRightBottom(int deltaWidth, int deltaHeight);
    public  Rectangle resize_moveLeftTop(int deltaWidth, int deltaHeight);
    public  Rectangle resize_moveRightTop(int deltaWidth, int deltaHeight);
    public  Rectangle resize_moveLeftBottom(int deltaWidth, int deltaHeight);
    
}
