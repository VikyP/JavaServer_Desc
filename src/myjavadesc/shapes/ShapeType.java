/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myjavadesc.shapes;

/**
 *
 * @author 06585
 */
public class ShapeType
{
    public static final byte None=-1;
    public static final byte Line=0;
    public static final byte LineHorizontal=1;
    public static final byte LineVertical=2;
    public static final byte PenLine=3;
    public static final byte Ellipse=4;
    public static final byte FillEllipse=5;
    public static final byte Rectangle=6;  
    public static final byte FillRectangle=7;  
    public static final byte Table=8;
    
    
    
    public static class LineCl implements IShapeModel
    {
       
        @Override
        public byte getType()
        {
            return ShapeType.Line;
        }

        @Override
        public String getName()
        {
            return "Line";
        }

        @Override
        public String getToolTipText()
        {
            return "Линия";
        }
    }
    
    public static class LineHorizontalCl implements IShapeModel
    {
     
        @Override
        public byte getType()
        {
            return ShapeType.LineHorizontal;
        }
        
        @Override
        public String getName()
        {
            return "LineHorizontal";
        }

        @Override
        public String getToolTipText()
        {
            return "Горизонтальная линия ";
        }
    }
    
    public static class LineVerticalCl implements IShapeModel
    {
       
        @Override
        public byte getType()
        {
            return ShapeType.LineVertical;
        }

        @Override
        public String getName()
        {
            return "LineVertical";
        }

        @Override
        public String getToolTipText()
        {
            return "Вертикальная линия";
        }
    }
    
    public static class PenLineCl implements IShapeModel
    {
       
        @Override
        public byte getType()
        {
            return ShapeType.PenLine;
        }

        @Override
        public String getName()
        {
            return "PenLine";
        }

        @Override
        public String getToolTipText()
        {
            return "Карандаш";
        }
    }
    
    public static class EllipseCl implements IShapeModel
    {
       
        @Override
        public byte getType()
        {
            return ShapeType.Ellipse;
        }

        @Override
        public String getName()
        {
            return "Ellipse";
        }

        @Override
        public String getToolTipText()
        {
            return "Эллипс";
        }
        
    }
    
    public static class FillEllipseCl implements IShapeModel
    {
       
        @Override
        public byte getType()
        {
            return ShapeType.FillEllipse;
        }

        @Override
        public String getName()
        {
            return "FillEllipse";
        }

        @Override
        public String getToolTipText()
        {
           return "Эллипс с заливкой";
        }
        
    }
    public static class RectangleCl implements IShapeModel
    {
       
        @Override
        public byte getType()
        {
            return ShapeType.Rectangle;
        }

        @Override
        public String getName()
        {
            return "Rectangle";
        }

        @Override
        public String getToolTipText()
        {
            return "Прямоугольник";
        }
    }
    
    public static class FillRectangleCl implements IShapeModel
    {
        
        @Override
        public byte getType()
        {
            return ShapeType.FillRectangle;
        }

        @Override
        public String getName()
        {
            return "FillRectangle";
        }

        @Override
        public String getToolTipText()
        {
            return "Прямоугольник с заливкой";
        }
        
    }
    
    public static class TableCl implements IShapeModel
    {
        
        @Override
        public byte getType()
        {
            return ShapeType.Table;
        }

        @Override
        public String getName()
        {
            return "Table";
        }

        @Override
        public String getToolTipText()
        {
            return "Таблица";
        }
        
    }
    
   
    
    
    public static String toStr(int Type)
    {
        switch( Type)
        {
            case ShapeType.Line:
                case ShapeType.LineHorizontal:
                    case ShapeType.LineVertical:
                return "Линия";
                        
            case ShapeType.PenLine:
                return "Карандаш";
                
            case ShapeType.Ellipse:
                case ShapeType.FillEllipse:
                return "Эллипс";
                    
            case ShapeType.Rectangle:
                case ShapeType.FillRectangle:
                    
                return "Прямоугольник";
                case ShapeType.Table:
                    return " Таблица";
        
        }
    return "Shape ?2";
    
    }
    
}