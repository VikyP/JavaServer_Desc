/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myjavadesc.shapes;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;


/**
 *
 * @author Viky_Pa
 */
public class AnchorsManipulations
{
    
    private int currentEditAction=-1;
    private Rectangle currentShapeRect;
    public ArrayList<Rectangle> AnchorsDraw = new ArrayList <Rectangle>();
        private int size = 5;

        public AnchorsManipulations( int type, Rectangle FRec, Point Begin, Point End )
        {
            Point CenterLine;
            this.currentShapeRect=FRec;
            switch(type)
            {
                /*
                case "Filling":
                    Point P = new Point(FRec.x + 5, FRec.y + 5);
                    AnchorsDraw.add(new Rectangle(new Point(P.x - size, P.y - size), new Dimension(size * 2, size * 2)));
                    break;*/
                case  ShapeType.Line:                     
                case  ShapeType.LineVertical:                    
                case  ShapeType.LineHorizontal:
                
                    CenterLine = new Point(FRec.x + FRec.width/2, FRec.y + FRec.height/2);
                    AnchorsDraw.add(new Rectangle(new Point(Begin.x - size, Begin.y - size), new Dimension(size * 2, size * 2)));
                    AnchorsDraw.add(new Rectangle(new Point(CenterLine.x - size, CenterLine.y - size), new Dimension(size * 2, size * 2)));
                    AnchorsDraw.add(new Rectangle(new Point(End.x - size, End.y - size), new Dimension(size * 2, size * 2)));
                    break;

                case ShapeType.PenLine:                   
                    AnchorsDraw.add(new Rectangle(new Point(Begin.x - size, Begin.y - size), new Dimension(size * 2, size * 2)));
                    AnchorsDraw.add(new Rectangle(new Point(End.x - size, End.y - size), new Dimension(size * 2, size * 2)));
                    break;
                default:
                    AnchorsDraw.add( new Rectangle(new Point(FRec.x - size, FRec.y - size), new Dimension(size * 2, size * 2)));
                    AnchorsDraw.add( new Rectangle(new Point((FRec.x+FRec.width/2) - size, FRec.y - size), new Dimension(size * 2, size * 2)));
                    AnchorsDraw.add( new Rectangle(new Point((FRec.x+FRec.width)- size, FRec.y - size), new Dimension(size * 2, size * 2)));

                    AnchorsDraw.add( new Rectangle(new Point(FRec.x - size, (FRec.y + FRec.height/2 )- size), new Dimension(size * 2, size * 2)));
                    AnchorsDraw.add( new Rectangle(new Point((FRec.x+FRec.width) -size, (FRec.y + FRec.height / 2) - size), new Dimension(size * 2, size * 2)));
               
                    AnchorsDraw.add( new Rectangle(new Point(FRec.x - size, FRec.y+FRec.height - size), new Dimension(size * 2, size * 2)));
                    AnchorsDraw.add(new Rectangle(new Point((FRec.x + FRec.width / 2) - size, FRec.y+FRec.height - size), new Dimension(size * 2, size * 2)));
                    AnchorsDraw.add( new Rectangle(new Point((FRec.x+FRec.width) -size, FRec.y+FRec.height - size), new Dimension(size * 2, size * 2)));
                    break;
            }

            
        }

        public void DrawAnchors(Graphics Gr)
        {
            switch (AnchorsDraw.size())
            {
                case 1:
                   // Pen P = new Pen(Color.Gold,2.5f);
                   // P.DashStyle= DashStyle.Dot;
                   // Gr.DrawEllipse(P, new Rectangle(new Point(AnchorsDraw[0].Left - (size*2 ), AnchorsDraw[0].Top - (size*2 )), new Size(size * 6, size * 6)));
                  //  Gr.FillEllipse(Brushes.GreenYellow, AnchorsDraw[0]);
                    break;
                case 2:
                    Gr.setColor(Color.YELLOW);
                    Gr.fillOval( AnchorsDraw.get(0).x, AnchorsDraw.get(0).y,  AnchorsDraw.get(0).width, AnchorsDraw.get(0).height );
                    Gr.fillOval(AnchorsDraw.get(1).x, AnchorsDraw.get(1).y,  AnchorsDraw.get(1).width, AnchorsDraw.get(1).height);
                    break;

                default:
                    Gr.setColor(Color.YELLOW);
                    for(Rectangle r:AnchorsDraw)
                    {
                        Gr.fillRect( r.x,r.y, r.width, r.height );
                    }                    
                    break;
               
            }
            Graphics2D g2d=(Graphics2D) Gr.create();
            
             g2d.setColor(Color.BLUE);
             g2d.setStroke(DashArrays.getStrokeLine(1, 0));
             g2d.drawRect(this.currentShapeRect.x, this.currentShapeRect.y, this.currentShapeRect.width,this.currentShapeRect.height);
             g2d.dispose();
            
        }
        
        /**
         * Определяем вид курсора
         * @param mep координаты мышки
         * @return курсор
         */
        public Cursor setCursorFromAnchor( Point mep)//mouse event point
        {
            Cursor c= null;
            currentEditAction=EditAction.empty;
            switch (this.AnchorsDraw.size())
            {
                case 1:
                    break;
                case 2:
                    c = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
                    break;
                default:
                    for (int i = 0; i < this.AnchorsDraw.size(); i++)// проверка якорей на  наведение
                    {
                        if (this.AnchorsDraw.get(i).contains(mep))//если курсор наведен на один из якорей
                        {
                            if (this.AnchorsDraw.size()==8)//назначение действия и отрисовка манипуляций для эллипса и прямоугольника
                            {
                                int index=i+2;
                                switch (index)
                                {
                                    //левый правый угол
                                    case EditAction.sizeLeftTop: 
                                        c = Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR); 
                                        currentEditAction = EditAction.sizeLeftTop;
                                        break;
                                    //центральный верхний
                                    case EditAction.sizeTop:
                                        c = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
                                        currentEditAction = EditAction.sizeTop; 
                                        break;
                                    //правый верхний угол
                                    case EditAction.sizeRightTop: 
                                        c = Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR); 
                                        currentEditAction = EditAction.sizeRightTop;
                                        break;
                                    //центрльный левый
                                    case EditAction.sizeLeft:
                                        c = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
                                        currentEditAction = (int)EditAction.sizeLeft;
                                        break;
                                    // центральный правый
                                    case EditAction.sizeRight: 
                                        c = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
                                        currentEditAction = (int)EditAction.sizeRight;
                                        break;
                                    //левый нижний
                                    case EditAction.sizeLeftBottom:
                                        c = Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
                                        currentEditAction = (int)EditAction.sizeLeftBottom;
                                        break;
                                    //центральный нижний
                                    case EditAction.sizeBottom: 
                                        c = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
                                        currentEditAction = (int)EditAction.sizeBottom;
                                        break;
                                    //правый нижний
                                    case EditAction.sizeRightBottom: 
                                        c = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
                                        currentEditAction = (int)EditAction.sizeRightBottom; 
                                        break;
                                }
                                
                            }
                            //назначение действия и отрисовка манипуляций для линии
                            else
                            {
                                switch (i + 10)
                                {
                                    case EditAction.sizeLineBeginPoint:
                                        c = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
                                        currentEditAction = EditAction.sizeLineBeginPoint; 
                                        break;
                                    case EditAction.sizeLineEndPoint:
                                         c = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
                                        currentEditAction = EditAction.sizeLineEndPoint;
                                        break;
                                    case (int)EditAction.sizeLineMove:
                                         c = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
                                        currentEditAction = EditAction.sizeLineMove;
                                        break;
                                }

                            }
                        }
                    }
                    break;
                    
                    
            }
            if(c==null && this.AnchorsDraw.size()==8 && this.currentShapeRect.contains(mep))
            {
               c = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
               currentEditAction = EditAction.move;
            }
           return c;
        
        
        }
        
        public int getcurrentEditAction()
        {
            return currentEditAction;
        }
        


    }
    

