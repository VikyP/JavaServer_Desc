/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myjavadesc.shapes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 06585
 */
public class SEllipse extends SContour implements IShapeAction 
{
    public SEllipse(Point Begin, Point End, Color c, float s, byte t)
    {
        super(Begin, End, c, s,t);
        this.Type=ShapeType.Ellipse;
    }
    
    public SEllipse(Point Begin, Point End, Color c, Color f, float s, byte t) 
    {
        super(Begin, End, c, s, t);
        this.Filling=f;
        this.Type=ShapeType.FillEllipse;
    }
    
    public SEllipse (DataInputStream DIS, byte type)
    {
        super( DIS, type);
        
        try
        {
            if(this.Type==ShapeType.FillEllipse)
                this.Filling= new Color(DIS.readInt());
            else
                this.Filling=null;
        } catch (IOException ex)
        {
            this.Type=-1;
            Logger.getLogger(SEllipse.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void draw(Graphics g) 
    {
        Rectangle R=null;
        if(this.isEditable)
            R=this.RectEditable;
        else
            R=this.SRect;
        if(this.Filling!=null)
        {
            this.ColorLine=this.Filling;
            this.setProperties(g,this.Filling).fillOval(R.x, R.y, R.width, R.height);
        }
        else
        this.setProperties(g).drawOval(R.x, R.y, R.width, R.height);
       
    }
   
    @Override
    public Point getBegin()
    {
        return new Point(SRect.x,SRect.y);
    }
    
    @Override
    public Point getEnd()
    {
        return new Point(SRect.x+SRect.width,SRect.y+SRect.height);
    }
     @Override
    public Rectangle getRectangle()
    {
        return this.SRect;
    }
    @Override
    public int getType()
    {
       return this.Type;
    }
    @Override
    public Rectangle resize_moveRightBottom(int deltaWidth, int deltaHeight)
    {
        this.SetRE_ResizeMoveRightBottom(deltaWidth, deltaHeight);
        return this.RectEditable;
    }

    @Override
    public Rectangle resize_moveLeftTop(int deltaWidth, int deltaHeight)
    {
       this.SetRE_ResizeMoveLeftTop(deltaWidth, deltaHeight);
       return this.RectEditable;
    }

    @Override
    public Rectangle resize_moveRightTop(int deltaWidth, int deltaHeight)
    {
       this.SetRE_ResizeMoveRightTop(deltaWidth, deltaHeight);
       return this.RectEditable;
    }

    @Override
    public Rectangle resize_moveLeftBottom(int deltaWidth, int deltaHeight)
    {
        this.SetRE_ResizeMoveLeftBottom(deltaWidth, deltaHeight);
        return this.RectEditable;
    }

    
 @Override
    public Rectangle move(int xStep, int yStep) 
    {
       this.RectEditable = new Rectangle(this.SRect.x + xStep, this.SRect.y + yStep, this.SRect.width,this.SRect.height);
       return this.RectEditable;
    }

    @Override
    public void BinaryWrite(DataOutputStream DOS) 
    {
       super.BinaryWrite(DOS);
       try
        {           
            if(this.Type==ShapeType.FillEllipse)
                DOS.writeInt(this.Filling.getRGB());
        }
        catch (IOException ex) 
        {
            Logger.getLogger(SEllipse.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

   
   @Override
    public void setEditable(boolean flag)
    {
      if(!flag && this.RectEditable!=null)
        {
            this.SRect=this.RectEditable;
        }        
       this.isEditable=flag;
    }

    @Override
    public IShapeAction copyShape(int x, int y)
    {
        Point begin= new Point(this.getBegin().x+x,this.getBegin().y+y);
        Point end= new Point(this.getEnd().x+x,this.getEnd().y+y);
       switch(this.Type)
       {
           case ShapeType.Ellipse:
               return new SEllipse(begin, end, this.ColorLine, this.thicknessLine, this.typeLine);
              
           case ShapeType.FillEllipse:
               return new SEllipse(begin,end, this.ColorLine,this.Filling, this.thicknessLine, this.typeLine);
       }
       
       return null;
    }

   
}
