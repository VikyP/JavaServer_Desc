/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myjavadesc.shapes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 *
 * @author 06585
 */
abstract class MyShape
{

    //тип фигуры
    protected byte Type;
    //толщина линии
    protected float thicknessLine = 0;
    //тип линии
    protected byte typeLine = 0;
    //цвет линии
    protected Color ColorLine;
    //прямоугольник границ фигуры
    protected Rectangle SRect;
    //прямоугольник границ фигуры при редактиовании
    protected Rectangle RectEditable = null;
    //флаг редактирования
    protected boolean isEditable = false;

    public MyShape()
    {
    }

    public MyShape(DataInputStream DIS, byte type)
    {
        try
        {
            this.Type = type;
        }
        catch (Exception exc)
        {
            this.Type = ShapeType.None;
        }

    }

    public MyShape(Point Begin, Point End, Color c, float s, byte t)
    {
        this.SRect = new Rectangle(
                (Begin.x < End.x) ? Begin.x : End.x,
                (Begin.y < End.y) ? Begin.y : End.y,
                Math.abs(End.x - Begin.x),
                Math.abs(End.y - Begin.y));
        this.ColorLine = c;
        this.thicknessLine = s;
        this.typeLine = t;
    }

    //выставлении параметров 
    protected Graphics2D setProperties(Graphics g)
    {
        Graphics2D g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setColor(this.ColorLine);
        g2D.setStroke(DashArrays.getStrokeLine(this.thicknessLine, this.typeLine));
        return g2D;
    }
   //выставлении параметров 
    protected Graphics2D setProperties(Graphics g, Color f)
    {
        Graphics2D g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setColor(f);
        g2D.setStroke(DashArrays.getStrokeLine(this.thicknessLine, this.typeLine));
        return g2D;
    }

    public void BinaryWrite(DataOutputStream DOS)
    {
    }

    @Override
    public String toString()
    {
        switch (this.Type)
        {
            case ShapeType.Line:
                return "Line";
            case ShapeType.PenLine:
                return "Pen";
            case ShapeType.Ellipse:
            case ShapeType.FillEllipse:
                return "Ellipse";
            case ShapeType.Rectangle:
            case ShapeType.FillRectangle:
                return "Rectangle";

        }
        return "Shape ?1";

    }
}
