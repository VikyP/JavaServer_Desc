/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myjavadesc;

import myjavadesc.shapes.DashArrays;
import java.awt.AlphaComposite;
import java.awt.Cursor;
import java.awt.Color;
import java.awt.Dimension;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JEditorPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import masterPanel.RecordInfo;
import masterPanel.ReportException;
import masterPanel.SettingsConfig;

import myjavadesc.events.EventAddShape;

import myjavadesc.shapes.AnchorsManipulations;
import myjavadesc.shapes.EditAction;
import myjavadesc.shapes.IShapeAction;
import myjavadesc.shapes.SEllipse;
import myjavadesc.shapes.SLine;
import myjavadesc.shapes.SPenLine;
import myjavadesc.shapes.SRectangle;
import myjavadesc.shapes.ShapeType;

import userControl.EditGraphTools;

/**
 *
 * @author viky
 */
public class MyJavaCanvas extends JEditorPane {

    //данные для заголовка
    //состояние (вкл/выкл) записи звука
   // public boolean isRecord = false;

   // private String group = "";
    
    public RecordInfo recordHead;
    //состояние (вкл/выкл) трансляции экрана преподавателя
    public boolean isShareScreen = false;

    //номер доски(страницы) текущей даты
    //еслитекущая страница относится не к сегодняшней дате
    // номер<0
    public int numberPage = -1;

    //список фигур доски
    public ArrayList<IShapeAction> shapes = new ArrayList<IShapeAction>();
    // якоря выбранной фигуры
    private AnchorsManipulations EditAnchors;
    //событие изменился тип фигуры для рисования
    public EventAddShape EASh = new EventAddShape();
    // тип фигуры, который будет нарисован
    public int shapeType = ShapeType.None;
    // количество строк на доске
    private int rowsCount = 30;
    // Цвет линии
    public Color LineColor;
    // Цвет заливки
    public Color FillColor;
    //толщина линии
    public float stroke;

    public int typeLine = 0;
    //флаг (рисовать или нет фигуру при движении мышки)
    //Fonts  Courier, Andale Mono, Monaco, Profont, Consolas, Deja Vu Sans Mono
    private Font F = new Font(Font.MONOSPACED, Font.PLAIN, 16);

    //флаг рисование в данный момент
    private boolean isDraw = false;
    //флаг (режим редактирования)
    private boolean isEdit = false;
    //флаг манипуляциис фигурой в данный момент
    private boolean isManipulation = false;
    // начальная точка фигуры
    private Point begP;
    // текущая позиция мышки
    private Point curP;

    private int RedLineX = 0;
    private int height = 600;
    private int width = 750;

    private byte indexRow = 1;

    private SPenLine freeLine;

    private int currentEditAction = EditAction.empty;

    /**
     * возвращает редактируемую фигуру
     *
     * @return текущая редактируемая фигура
     */
    private IShapeAction getCurrentShapeEditable() {
        if (this.editTools == null || this.editTools.shapesList == null) {
            return null;
        }
        int index = this.editTools.shapesList.getSelectedIndex();
        if (index < 0) {
            return null;
        }
        return this.shapes.get(index);
    }

    // позиция курсора
    private int MyCaretPos;
    //панель редактирования графики
    public EditGraphTools editTools;

    /**
     * частота отправки текста
     */
    public int timeT = 200;
    /**
     * частота отправки графики
     */
    public int timeG = 200;
    private Sender_UDP sender;

    /**
     * Поток, отправляющий данные доски - текст
     */
    private class ThreadSendeкText extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    MyJavaCanvas.this.sender.Send(MyJavaCanvas.this.textToBytes());

                    Thread.sleep(timeT);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MyJavaCanvas.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    private ThreadSendeкText TST;

    /**
     * Поток, отправляющий данные доски - графика
     */
    private class ThreadSendeкGraph extends Thread {

        @Override
        public void run() {
            while (true) {
                try {

                    synchronized (MyJavaCanvas.this.shapes) {
                        MyJavaCanvas.this.sender.Send(MyJavaCanvas.this.graphToBytes());
                    }
                    Thread.sleep(timeG);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MyJavaCanvas.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    private ThreadSendeкGraph TSG;

    /**
     * @return the rowsCount
     */
    public int getRowsCount() {
        return rowsCount;
    }

    /**
     * @param rowsCount the rowsCount to set
     */
    public final void setRowsCount(int rowsCount) {
        this.rowsCount = rowsCount;
    }

    private class KeyListenerEdit implements KeyListener {

        @Override
        public void keyReleased(KeyEvent e) {
            MyJavaCanvas.this.deleteSelected();
        }

        @Override
        public void keyTyped(KeyEvent ke) {
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void keyPressed(KeyEvent ke) {
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private class KeyListenerText implements KeyListener {

        @Override
        public void keyReleased(KeyEvent e) {
            cutRows();
        }

        @Override
        public void keyTyped(KeyEvent ke) {
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void keyPressed(KeyEvent ke) {
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    class MyMouselistener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent me) {

            if (me.getButton() == MouseEvent.BUTTON3 && me.isControlDown()) {

                int index = 0;
                for (IShapeAction sh : MyJavaCanvas.this.shapes) {
                    if (sh.getRectangle().contains(me.getPoint())) {
                        //если индекс и выбренная фигура не совпадают
                        if (index != MyJavaCanvas.this.editTools.shapesList.getSelectedIndex()) {
                            MyJavaCanvas.this.editTools.shapesList.setSelectedIndex(index);  // назначаем выбранной указанную фигуру
                        } else {
                            MyJavaCanvas.this.editTools.shapesList.clearSelection();// снимаем редактирование   
                        }
                        return;
                    }
                    index++;
                }

            }

            if (me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() == 1) {
                MyJavaCanvas.this.endEdit();
            }
        }

        @Override
        public void mousePressed(MouseEvent me) {

            if (me.getButton() != MouseEvent.BUTTON3 && !me.isControlDown()
                    && MyJavaCanvas.this.getCursor().getType() != java.awt.Cursor.E_RESIZE_CURSOR) {

                MyJavaCanvas.this.endEdit();
                return;
            }

            if (MyJavaCanvas.this.isEdit) {
                int index = MyJavaCanvas.this.editTools.shapesList.getSelectedIndex();
                if (index >= 0) {
                    System.out.println("    isEdit");
                    MyJavaCanvas.this.isManipulation = true;
                    MyJavaCanvas.this.begP = new Point(me.getX(), me.getY());
                    MyJavaCanvas.this.shapes.get(index).setEditable(true);
                }
            } else {
                MyJavaCanvas.this.begP = new Point(me.getX(), me.getY());
                MyJavaCanvas.this.curP = MyJavaCanvas.this.begP;
                //выход, если действие не назначено
                if (MyJavaCanvas.this.shapeType == ShapeType.None) {
                    System.out.println("ShapeType.None");
                    return;
                }

                if (me.getButton() == MouseEvent.BUTTON3) {
                    System.out.println("    isDraw");
                    MyJavaCanvas.this.isDraw = true;
                    MyJavaCanvas.this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    MyCaretPos = MyJavaCanvas.this.getCaretPosition();
                }

            }
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            //<editor-fold defaultstate="collapsed" desc="изменение размеров холста ( если не использовать красную линию) ">

            //
           /*  if (me.getButton() != MouseEvent.BUTTON3)
             {
               
             if(me.getButton() == MouseEvent.BUTTON1)
             {
             int stepX = me.getX() - MyJavaCanvas.this.begP.x;
             Dimension newSize = new Dimension(MyJavaCanvas.this.getPreferredSize().width + stepX, MyJavaCanvas.this.getHeight());
             MyJavaCanvas.this.setSize(newSize);
             MyJavaCanvas.this.setPreferredSize(newSize);
             MyJavaCanvas.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));    
                
             }
             return;
             }*///</editor-fold>   
            int index = MyJavaCanvas.this.editTools.shapesList.getSelectedIndex();

            if (MyJavaCanvas.this.isEdit) {
                MyJavaCanvas.this.shapes.get(index).setEditable(false);
                MyJavaCanvas.this.isManipulation = false;
                return;
            }

            //нет новой фигуры
            if (!MyJavaCanvas.this.isDraw) {
                return;//выход
            }
            MyJavaCanvas.this.isDraw = false;
            MyJavaCanvas.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            int x1 = (MyJavaCanvas.this.begP.x < MyJavaCanvas.this.curP.x) ? MyJavaCanvas.this.begP.x : MyJavaCanvas.this.curP.x;
            int y1 = (MyJavaCanvas.this.begP.y < MyJavaCanvas.this.curP.y) ? MyJavaCanvas.this.begP.y : MyJavaCanvas.this.curP.y;
            int x2 = (MyJavaCanvas.this.begP.x > MyJavaCanvas.this.curP.x) ? MyJavaCanvas.this.begP.x : MyJavaCanvas.this.curP.x;
            int y2 = (MyJavaCanvas.this.begP.y > MyJavaCanvas.this.curP.y) ? MyJavaCanvas.this.begP.y : MyJavaCanvas.this.curP.y;

            switch (MyJavaCanvas.this.shapeType) {
                case ShapeType.Line:
                case ShapeType.LineHorizontal:
                case ShapeType.LineVertical:
                    MyJavaCanvas.this.shapes.add(new SLine(MyJavaCanvas.this.begP, MyJavaCanvas.this.curP, MyJavaCanvas.this.LineColor, MyJavaCanvas.this.stroke, MyJavaCanvas.this.typeLine));
                    break;
                case ShapeType.PenLine:
                    if (MyJavaCanvas.this.freeLine != null) {
                        MyJavaCanvas.this.shapes.add(new SPenLine(MyJavaCanvas.this.freeLine.Poins(), MyJavaCanvas.this.LineColor, MyJavaCanvas.this.stroke, MyJavaCanvas.this.typeLine));
                        MyJavaCanvas.this.freeLine = null;
                    }
                    break;
                case ShapeType.Ellipse:
                    MyJavaCanvas.this.shapes.add(new SEllipse(new Point(x1, y1), new Point(x2, y2), MyJavaCanvas.this.LineColor, MyJavaCanvas.this.stroke, MyJavaCanvas.this.typeLine));
                    break;
                case ShapeType.FillEllipse:
                    MyJavaCanvas.this.shapes.add(new SEllipse(new Point(x1, y1), new Point(x2, y2), MyJavaCanvas.this.LineColor, MyJavaCanvas.this.FillColor, MyJavaCanvas.this.stroke, MyJavaCanvas.this.typeLine));
                    break;
                case ShapeType.Rectangle:
                    MyJavaCanvas.this.shapes.add(new SRectangle(new Point(x1, y1), new Point(x2, y2), MyJavaCanvas.this.LineColor, MyJavaCanvas.this.stroke, MyJavaCanvas.this.typeLine));
                    break;
                case ShapeType.FillRectangle:
                    MyJavaCanvas.this.shapes.add(new SRectangle(new Point(x1, y1), new Point(x2, y2), MyJavaCanvas.this.LineColor, MyJavaCanvas.this.FillColor, MyJavaCanvas.this.stroke, MyJavaCanvas.this.typeLine));
                    break;
            }
            addShape(MyJavaCanvas.this.shapeType);

            //выполнить перерисовку как можно скорее
            MyJavaCanvas.this.repaint();
            //MyJavaCanvas.this.invalidate()// Компонент поменял размеры - требует перерисовки

        }

    }

    class MyMouseMotionListener extends MouseMotionAdapter {

        @Override
        public void mouseDragged(MouseEvent me) {
            //<editor-fold defaultstate="collapsed" desc="Изменение размеров"> 
            if (MyJavaCanvas.this.getCursor().getType() == java.awt.Cursor.E_RESIZE_CURSOR) {
                int stepX = me.getX() - MyJavaCanvas.this.begP.x;
                Dimension newSize = new Dimension(MyJavaCanvas.this.getPreferredSize().width + stepX, MyJavaCanvas.this.getHeight());

                MyJavaCanvas.this.RedLineX = me.getX();

            }
            //</editor-fold>   
            //<editor-fold defaultstate="collapsed" desc="Редактирование ">

            if (MyJavaCanvas.this.isEdit && MyJavaCanvas.this.isManipulation) {

                int index = MyJavaCanvas.this.editTools.shapesList.getSelectedIndex();

                if (index < 0) {
                    return;
                }

                IShapeAction shapeEditable = MyJavaCanvas.this.shapes.get(index);
                switch (MyJavaCanvas.this.currentEditAction) {
                    case EditAction.sizeBottom:
                        EditAnchors = new AnchorsManipulations(
                                shapeEditable.getType(),
                                shapeEditable.resize_moveRightBottom(0, (me.getY() - MyJavaCanvas.this.begP.y)),
                                shapeEditable.getBegin(), shapeEditable.getEnd());
                        break;
                    case EditAction.sizeRight:
                        EditAnchors = new AnchorsManipulations(
                                shapeEditable.getType(),
                                shapeEditable.resize_moveRightBottom(me.getX() - MyJavaCanvas.this.begP.x, 0),
                                shapeEditable.getBegin(), shapeEditable.getEnd());
                        break;
                    case EditAction.sizeRightBottom:
                        EditAnchors = new AnchorsManipulations(
                                shapeEditable.getType(),
                                shapeEditable.resize_moveRightBottom(me.getX() - MyJavaCanvas.this.begP.x, me.getY() - MyJavaCanvas.this.begP.y),
                                shapeEditable.getBegin(), shapeEditable.getEnd());
                        break;
                    case EditAction.sizeLeftTop:
                        EditAnchors = new AnchorsManipulations(
                                shapeEditable.getType(),
                                shapeEditable.resize_moveLeftTop(me.getX() - MyJavaCanvas.this.begP.x, me.getY() - MyJavaCanvas.this.begP.y),
                                shapeEditable.getBegin(), shapeEditable.getEnd());
                        break;
                    case EditAction.sizeTop:
                        EditAnchors = new AnchorsManipulations(
                                shapeEditable.getType(),
                                shapeEditable.resize_moveLeftTop(0, me.getY() - MyJavaCanvas.this.begP.y),
                                shapeEditable.getBegin(), shapeEditable.getEnd());
                        break;
                    case EditAction.sizeLeft:
                        EditAnchors = new AnchorsManipulations(
                                shapeEditable.getType(),
                                shapeEditable.resize_moveLeftTop(me.getX() - MyJavaCanvas.this.begP.x, 0),
                                shapeEditable.getBegin(), shapeEditable.getEnd());
                        break;
                    case EditAction.sizeLeftBottom:
                        EditAnchors = new AnchorsManipulations(
                                shapeEditable.getType(),
                                shapeEditable.resize_moveLeftBottom(me.getX() - MyJavaCanvas.this.begP.x, me.getY() - MyJavaCanvas.this.begP.y),
                                shapeEditable.getBegin(), shapeEditable.getEnd());
                        break;
                    case EditAction.sizeRightTop:
                        EditAnchors = new AnchorsManipulations(
                                shapeEditable.getType(),
                                shapeEditable.resize_moveRightTop(me.getX() - MyJavaCanvas.this.begP.x, me.getY() - MyJavaCanvas.this.begP.y),
                                shapeEditable.getBegin(), shapeEditable.getEnd());
                        break;
                    case EditAction.sizeLineMove:
                    case EditAction.move:
                        Rectangle r = shapeEditable.move(me.getPoint().x - MyJavaCanvas.this.begP.x, me.getPoint().y - MyJavaCanvas.this.begP.y);
                        EditAnchors = new AnchorsManipulations(shapeEditable.getType(), r,
                                new Point(shapeEditable.getBegin().x + (me.getPoint().x - MyJavaCanvas.this.begP.x), shapeEditable.getBegin().y + (me.getPoint().y - MyJavaCanvas.this.begP.y)),
                                new Point(shapeEditable.getEnd().x + (me.getPoint().x - MyJavaCanvas.this.begP.x), shapeEditable.getEnd().y + (me.getPoint().y - MyJavaCanvas.this.begP.y)));
                        break;

                    /// for line
                    /// 
                    case EditAction.sizeLineBeginPoint:
                        System.out.println("   sizeLineBeginPoint ");
                        EditAnchors = new AnchorsManipulations(shapeEditable.getType(),
                                shapeEditable.resize_moveRightBottom(me.getPoint().x - MyJavaCanvas.this.begP.x, me.getPoint().y - MyJavaCanvas.this.begP.y),
                                new Point(shapeEditable.getBegin().x + (me.getPoint().x - MyJavaCanvas.this.begP.x),
                                        shapeEditable.getBegin().y + (me.getPoint().y - shapeEditable.getBegin().y)), shapeEditable.getEnd());
                        break;
                    case EditAction.sizeLineEndPoint:
                        System.out.println("   sizeLineEndPoint ");
                        EditAnchors = new AnchorsManipulations(shapeEditable.getType(),
                                shapeEditable.resize_moveLeftTop(me.getPoint().x - MyJavaCanvas.this.begP.x, me.getPoint().y - MyJavaCanvas.this.begP.y),
                                shapeEditable.getBegin(),
                                new Point(shapeEditable.getEnd().x + (me.getPoint().x - MyJavaCanvas.this.begP.x),
                                        shapeEditable.getEnd().y + (me.getPoint().y - MyJavaCanvas.this.begP.y)));
                        break;

                    // ни один из якорей не задействован                        
                    default:
                        //выход без перерисовки
                        return;

                }

            }

            //</editor-fold>  
            //<editor-fold defaultstate="collapsed" desc="Рисование ">
            if (MyJavaCanvas.this.isDraw) {

                MyJavaCanvas.this.select(MyCaretPos, MyCaretPos);
                switch (MyJavaCanvas.this.shapeType) {
                    case ShapeType.LineHorizontal:
                        MyJavaCanvas.this.curP = new Point(me.getX(), MyJavaCanvas.this.curP.y);
                        break;
                    case ShapeType.LineVertical:
                        MyJavaCanvas.this.curP = new Point(MyJavaCanvas.this.curP.x, me.getY());
                        break;
                    case ShapeType.PenLine:
                        if (MyJavaCanvas.this.freeLine == null) {
                            MyJavaCanvas.this.freeLine = new SPenLine(MyJavaCanvas.this.begP, MyJavaCanvas.this.LineColor, MyJavaCanvas.this.stroke, MyJavaCanvas.this.typeLine);
                        } else {
                            MyJavaCanvas.this.freeLine.AddPoint(new Point(me.getX(), me.getY()));
                        }

                    default:
                        MyJavaCanvas.this.curP = new Point(me.getX(), me.getY());
                        break;

                }
            }
            //</editor-fold>  
            MyJavaCanvas.this.repaint();
        }

        @Override
        public void mouseMoved(MouseEvent me) {

            if (MyJavaCanvas.this.isEdit && MyJavaCanvas.this.EditAnchors != null) {
                // назначение курсора по якорям
                Cursor c = null;
                c = MyJavaCanvas.this.EditAnchors.setCursorFromAnchor(me.getPoint());

                if (c != null) {
                    MyJavaCanvas.this.setCursor(c);
                } else {
                    MyJavaCanvas.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
                // назначение действия редактирования
                MyJavaCanvas.this.currentEditAction = MyJavaCanvas.this.EditAnchors.getcurrentEditAction();
                return;
            }

        }
    }

    /**
     * удаление всей графики с текущей доски
     */
    ActionListener clearboard = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            MyJavaCanvas.this.clearBoard();
        }
    };

    /**
     * удаление выбранного графического объекта *
     */
    ActionListener deleteselect = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            MyJavaCanvas.this.deleteSelected();
        }
    };

    /**
     * копирование и вставка выбранного графического объекта *
     */
    ActionListener addselect = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            MyJavaCanvas.this.addSelect();
        }
    };

    /**
     * изменился выбранный объект в перечне графических фигур
     */
    ListSelectionListener selectShape = new ListSelectionListener() {
        private int index = -1;

        @Override
        public void valueChanged(ListSelectionEvent e) {

            int i = MyJavaCanvas.this.editTools.shapesList.getSelectedIndex();
            //активация или деактивация кнопок 
            if (i < 0) {
                editTools.isSelectedOne(false);
            } else {
                editTools.isSelectedOne(true);
            }

            // проверка  изменился ли индекс
            if (this.index >= 0 && this.index < MyJavaCanvas.this.shapes.size() && this.index != i) {
                //если изменился, отменяем редактирование предыдущей фигуры                
                IShapeAction oldSelect = MyJavaCanvas.this.shapes.get(index);
                oldSelect.setEditable(false);
            }

            this.index = i;

            if (index < 0) {
                MyJavaCanvas.this.endEdit();
                return;
            }
            // Оперделяем фигуру для редактирования
            IShapeAction select = MyJavaCanvas.this.getCurrentShapeEditable();
            // назначаем якоря
            MyJavaCanvas.this.EditAnchors = new AnchorsManipulations(select.getType(), select.getRectangle(), select.getBegin(), select.getEnd());
            MyJavaCanvas.this.isEdit = true;
            MyJavaCanvas.this.repaint();
        }

    };
    BufferedImage b;

    public MyJavaCanvas() {
        setOpaque(false);
        /*   try
         {
         //HTMLDocument hd = new HTMLDocument();
         // this.setContentType("text/html");
         // this.setDocument(hd);
            
         b = ImageIO.read(new File("i_.jpeg"));
         } catch (IOException ex)
         {
         Logger.getLogger(MyJavaCanvas.class.getName()).log(Level.SEVERE, null, ex);
         }*/

        this.addMouseListener(new MyJavaCanvas.MyMouselistener());
        this.addMouseMotionListener(new MyJavaCanvas.MyMouseMotionListener());
        this.addKeyListener(new KeyListenerText());

        //this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
        // this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        this.rowsCount = 30;
        this.setRowsCount(rowsCount);
        this.setPreferredSize(new Dimension(this.width, this.height));
        this.setMaximumSize(new Dimension(this.width, this.height));
        this.setMinimumSize(new Dimension(100, 200));

        Insets myMargin = new Insets(13, 35, 10, 20);
        this.setMargin(myMargin);

        this.TST = new ThreadSendeкText();
        this.TST.setDaemon(true);

        this.TSG = new ThreadSendeкGraph();
        this.TSG.setDaemon(true);

        this.editTools = new EditGraphTools();
        this.editTools.ActionListenerClear(clearboard);
        this.editTools.ActionListenerButtonDelete(deleteselect);
        this.editTools.ActionListenerButtonAdd(addselect);
        this.editTools.shapesList.addListSelectionListener(selectShape);
        this.editTools.shapesList.addKeyListener(new KeyListenerEdit());
        this.editTools.isSelectedOne(false);

        this.addComponentListener(new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent e) {

            }

            @Override
            public void componentMoved(ComponentEvent e) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void componentShown(ComponentEvent e) {
                System.out.println("    **** canvas show ");
                // TextTimer.schedule(TextTask, 1000, 50);
                //  GraphTimer.schedule(GraphTask, 2500, 200);
                MyJavaCanvas.this.timeG = 200;
                MyJavaCanvas.this.timeT = 50;
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                System.out.println("    **** canvas Hidden ");
                MyJavaCanvas.this.timeG = 5000;
                MyJavaCanvas.this.timeT = 2000;
            }
        });

        this.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent ce) {
                int pos = MyJavaCanvas.this.getCaretPosition();
                Element map = MyJavaCanvas.this.getDocument().getDefaultRootElement();
                int row = map.getElementIndex(pos);
                Element lineElem = map.getElement(row);
                int col = pos - lineElem.getStartOffset();
                MyJavaCanvas.this.indexRow = (byte) row;
                System.out.println("   row " + MyJavaCanvas.this.indexRow);
                System.out.println("   col " + col);
            }

        });

    }

    /**
     * считывание данных из файла.xml
     *
     * @param SC объект с данными из файла
     */
    public void Init(SettingsConfig SC) {
        this.sender = new Sender_UDP(SC.IP_UDP, SC.PORT_UDP_BOARD);

        this.setDrawColors(SC.LineColor, SC.FillColor);
        this.setColors(SC.Background, SC.Foreground);
        this.stroke = SC.thicknessLine;
        this.typeLine = SC.typeLine;
        this.setFont(F);
        this.RedLineX = this.width;
        this.TST.start();
        this.TSG.start();

    }

    /**
     * Добавление выбранной фигуры
     */
    private void addSelect() {
        int index = this.editTools.shapesList.getSelectedIndex();
        IShapeAction newShape = this.shapes.get(index).copyShape(5, 5);
        this.shapes.add(index + 1, newShape);
        this.editTools.dlm.add(index + 1, ShapeType.toStr(newShape.getType()));
        this.editTools.shapesList.setSelectedIndex(index + 1);

    }

    /**
     * Удаление выбранной фигуры
     */
    private void deleteSelected() {

        int index = this.editTools.shapesList.getSelectedIndex();

        if (index < 0) {
            this.endEdit();
            return;
        }

        this.shapes.remove(index);
        this.editTools.dlm.remove(index);

        if (this.editTools.dlm.getSize() == 0) {
            this.endEdit();
            return;
        } else {
            // если список не пуст назначаем выбранным объектом следующий(если раньше был первый)или предыдущий (если не первый)
            if (this.editTools.dlm.getSize() == 1) {
                this.editTools.shapesList.setSelectedIndex(0);
            } else {
                this.editTools.shapesList.setSelectedIndex((index == 0) ? index++ : index--);
            }
        }
        this.repaint();

    }

    /**
     * Определение высоты строки в зависимости от размера шрифта
     *
     * @return высоту строки
     */
    private int getHeightLine()
    {
        FontMetrics metrics = this.getGraphics().getFontMetrics(this.getFont());
        return metrics.getHeight();
    }

    /**
     * Добавление фигуры в список
     *
     * @param name название фигуры
     */
    private void addShape(int type) {
        System.out.println(" type   " + type);
        this.editTools.dlm.addElement(ShapeType.toStr(type));
    }

    /**
     * установка цвета текста и фона
     *
     * @param b цвет фона
     * @param f цвет текста
     */
    public void setColors(Color b, Color f) {
        this.setBackground(b);
        this.setForeground(f);
        this.setCaretColor(f);
    }

    /**
     * установка цветов линии и заливки
     *
     * @param l цвет линии
     * @param f цвет заливки
     */
    public void setDrawColors(Color l, Color f) {
        this.LineColor = l;
        this.FillColor = f;
    }

    /**
     * обрезка контента, который не помещается в 30 строк
     */
    private void cutRows() {
        try {
            if (this.getText().length() == 0) {
                return;
            }

            boolean isCut = false;
            int lastIndex = this.getText().length() - 1;
            Rectangle R = this.modelToView(lastIndex);
// обрезка текста
            while (lastIndex > 0 && R.getY() > (this.rowsCount + 10) * this.getHeightLine() + 5) {
                lastIndex--;
                R = this.modelToView(lastIndex);
                isCut = true;
            }

            if (!isCut) {
                return;
            }

            this.setText(this.getText().substring(0, lastIndex++));

        } catch (BadLocationException ex) {
            ReportException.write(this.getClass().getName() + "\t4\t" + ex.getMessage());
            Logger.getLogger(MyJavaCanvas.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * прорисовка номеров строк
     *
     * @param g Graphics доски
     */
    private void drawLinesNumber(Graphics2D g2D) {
        int H = this.getHeightLine();

        g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g2D.setFont(this.F);
        g2D.setColor(this.getForeground());
        AlphaComposite A1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        g2D.setComposite(A1);
        for (int i = 0; i < getRowsCount(); i++) {
            g2D.drawString((i + 1) + "", 5, (i + 1) * H + 10);
        }
        AlphaComposite A2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
        g2D.setComposite(A2);

    }

    /**
     * прорисовка текущей фигуры в режиме рисования
     *
     * @param g Graphics доски
     */
    private void draw_IsDraw(Graphics g) {
        if (!this.isDraw) {
            return;
        }
        g.setColor(this.LineColor);

        Graphics2D g2D = setProperties(g);

        int x1 = (this.begP.x < this.curP.x) ? this.begP.x : this.curP.x;

        int y1 = (this.begP.y < this.curP.y) ? this.begP.y : this.curP.y;

        int x2 = (this.begP.x > this.curP.x) ? this.begP.x : this.curP.x;
        int y2 = (this.begP.y > this.curP.y) ? this.begP.y : this.curP.y;

        switch (MyJavaCanvas.this.shapeType) {
            case ShapeType.Line:
            case ShapeType.LineHorizontal:
            case ShapeType.LineVertical:
                g2D.drawLine(this.begP.x, this.begP.y, this.curP.x, this.curP.y);
                break;
            case ShapeType.PenLine:
                this.freeLine.draw(g);
                break;
            case ShapeType.Ellipse:
                g2D.drawOval(x1, y1, x2 - x1, y2 - y1);
                break;
            case ShapeType.FillEllipse:
                g2D.setColor(this.FillColor);
                g2D.fillOval(x1, y1, x2 - x1, y2 - y1);
                break;
            case ShapeType.Rectangle:
                g2D.drawRect(x1, y1, x2 - x1, y2 - y1);
                break;
            case ShapeType.FillRectangle:
                g2D.setColor(this.FillColor);
                g2D.fillRect(x1, y1, x2 - x1, y2 - y1);
                break;
        }

    }

    /**
     * происовка красной линиии(ограничения видимости доски для студента)
     *
     * @param g
     */
    private void drawRedLine(Graphics g) {
        g.setColor(Color.RED);
        g.drawLine(this.RedLineX, 0, this.RedLineX, MyJavaCanvas.this.getHeight());

    }

    @Override
    public void paintComponent(Graphics g) {
        // Рисование фона      
        // super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        if (b != null) {
            int x = (getWidth() - b.getWidth()) / 2;
            int y = (getHeight() - b.getHeight()) / 2;
            g2d.drawImage(b, 0, 0, this);
        } else {
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        for (IShapeAction R : this.shapes) {
            R.draw(g);
        }

        // drawRedLine(g);
        draw_IsDraw(g);
        if (this.EditAnchors != null) {
            this.EditAnchors.DrawAnchors(g);
        }

        drawLinesNumber(g2d);
        getUI().paint(g2d, this);
        g2d.dispose();
    }

    /**
     * очистка доски от графических объектов
     */
    public void clearBoard() {
        endEdit();
        this.shapes.clear();
        this.editTools.dlm.removeAllElements();
        this.repaint();
    }

    /**
     * выход из режима редактирования
     */
    private void endEdit() {
        this.isEdit = false;
        this.EditAnchors = null;
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        IShapeAction ShA = this.getCurrentShapeEditable();
        if (ShA != null) {
            ShA.setEditable(false);
        }
        this.editTools.shapesList.clearSelection();
        this.repaint();
    }

    protected Graphics2D setProperties(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setColor(this.LineColor);
        g2D.setStroke(DashArrays.getStrokeLine(stroke, this.typeLine));
        return g2D;
    }

    /**
     * запись текста в массив байт для отправки по UDP
     *
     *
     * @return массив байт с текстом
     */
    private byte[] textToBytes() {
        try {
            ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
            DataOutputStream DOS = new DataOutputStream(BAOS);
            writeHead(DOS, TypeInfo.TEXT);
            synchronized (this.getText()) {
                DOS.writeUTF(this.getText());
            }
            return BAOS.toByteArray();
        } catch (UnsupportedEncodingException ex) {
            ReportException.write(this.getClass().getName() + "\t2\t" + ex.getMessage());
            Logger.getLogger(MyJavaCanvas.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            ReportException.write(this.getClass().getName() + "\t3\t" + ex.getMessage());
            Logger.getLogger(MyJavaCanvas.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    /**
     * Запись графического содержимого в массив байт для отправки по UDP
     *
     * @return массив байт с графикой
     */
    private byte[] graphToBytes() {
        ByteArrayOutputStream BAOS = null;
        DataOutputStream DOS = null;
        try {
            BAOS = new ByteArrayOutputStream();
            DOS = new DataOutputStream(BAOS);
            writeHead(DOS, TypeInfo.GRAPH);
            //////////////////////////////////////////// DOS.writeByte(this.RedLineX/5);
            this.writeBytesGraph(DOS);
            byte[] b = BAOS.toByteArray();
            DOS.close();
            BAOS.close();
            return b;

        } catch (IOException ex) {
            Logger.getLogger(MyJavaCanvas.class.getName()).log(Level.SEVERE, null, ex);
            ReportException.write("MyJavaCanvas.graphToBytes(..)" + ex.getMessage());
            return null;
        }

    }
    

    /**
     * Запись заголовка 
     * 
     * Protocol
     * byte[0]- isRecord (0 - false /  1 - true)
     * byte[1]- nameGroup.length (length of string nameGroup)
     * new byte[length] - название группы
     * byte [length+2] - номер доски
     * byte[length+3]- тип данных (TypeInfo.TEXT=1/TypeInfo.GRAPH=2 )
     * if (type == TypeInfo.TEXT) 
     *      byte[length+4] - номер строчки 
     * @param DOS поток для записи заголовка
     */
    
    private void writeHead(DataOutputStream DOS, int type) {
        try 
        {
            //первый байт указывает состояние микрофона (0(выкл)||1(вкл))
            /*
            DOS.writeByte((this.isRecord) ? (byte) 1 : (byte) 0);
            DOS.writeByte(this.group.length());
            DOS.writeChars(this.group);
          */
            
            DOS.write(this.recordHead.getHeadDesc());
            DOS.write(this.numberPage);
            DOS.writeByte((byte) type);
            if (type == TypeInfo.TEXT) {
                DOS.writeByte(this.indexRow);
            }

        } catch (IOException ex) {
            ReportException.write(this.getClass().getName() + "\t1\t" + ex.getMessage());
            Logger.getLogger(MyJavaCanvas.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    

    //<editor-fold defaultstate="collapsed" desc="Read Write Save board ">
    /**
     * запись в поток текста
     */
    private void writeBytesText(DataOutputStream DOS) {
        try {
            byte[] text = this.getText().getBytes();
            DOS.writeInt(text.length);
            DOS.write(text, 0, text.length);
        } catch (IOException ex) {
            Logger.getLogger(MyJavaCanvas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * запись в поток графики
     */
    private void writeBytesGraph(DataOutputStream DOS) {
        try {

            synchronized (this.shapes) {
                //размер массива
                DOS.writeInt(shapes.size());
                // запись всех фигур
                for (IShapeAction shape : shapes) {
                    switch (shape.getType()) {
                        case ShapeType.Line:
                            ((SLine) shape).BinaryWrite(DOS);
                            break;
                        case ShapeType.PenLine:
                            ((SPenLine) shape).BinaryWrite(DOS);
                            break;
                        case ShapeType.Ellipse:
                        case ShapeType.FillEllipse:
                            ((SEllipse) shape).BinaryWrite(DOS);
                            break;
                        case ShapeType.Rectangle:
                        case ShapeType.FillRectangle:
                            ((SRectangle) shape).BinaryWrite(DOS);
                            break;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MyJavaCanvas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Сохранение контента доски
     *
     * @param f файл для сохранения доски
     */
    public void save(File f) {
        FileOutputStream FOS = null;
        DataOutputStream DOS = null;
        try {
            FOS = new FileOutputStream(f);
            DOS = new DataOutputStream(FOS);
            this.writeBytesText(DOS);
            this.writeBytesGraph(DOS);

        } catch (FileNotFoundException ex) {
            ReportException.write(" Ошибка записи файла 1  " + f.getName());
        } finally {
            try {
                DOS.close();
                FOS.close();
            } catch (IOException ex) {
                ReportException.write(" Ошибка записи файла 3  " + f.getName());
            }

        }

    }

    /**
     * чтение текста из потока
     */
    private void readBitesText(DataInputStream DIS) {
        try {
            this.setText("");
            if (DIS.available() == 0) {
                return;
            }
            int length = DIS.readInt();
            System.out.println(length);
            byte[] text = new byte[length];
            int cnt = DIS.read(text, 0, length);
            System.out.println(cnt == length);
            String str = new String(text);
            this.setText(str);

        } catch (IOException ex) {
            ReportException.write(" Ошибка чтения файла (текст) ");
        }

    }

    /**
     * чтение графики из потока
     */
    private void readBitesGraph(DataInputStream DIS) {
        try {
            shapes.clear();
            this.editTools.dlm.removeAllElements();
            if (DIS.available() == 0) {
                return;
            }

            int length = DIS.readInt();
            int type = -1;

            for (int i = 0; i < length; i++) {

                try {
                    IShapeAction sa = null;
                    type = DIS.readByte();
                    System.out.println("Type " + type);
                    switch (type) {
                        case ShapeType.Line:
                            sa = new SLine(DIS, type);
                            break;
                        case ShapeType.PenLine:
                            sa = new SPenLine(DIS, type);
                            break;
                        case ShapeType.Ellipse:
                        case ShapeType.FillEllipse:
                            sa = new SEllipse(DIS, type);
                            break;
                        case ShapeType.Rectangle:
                        case ShapeType.FillRectangle:
                            sa = new SRectangle(DIS, type);
                            break;
                    }
                    if (sa != null && sa.getType() > 0) {
                        this.shapes.add(sa);
                        this.editTools.dlm.addElement(ShapeType.toStr(type));
                    }

                } catch (IOException ex) {
                    ReportException.write(" Ошибка чтения файла (графика) ");
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(MyJavaCanvas.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * чтение доски из указанного файла
     */
    public void readBoard(File f)
    {
        try {
            String gr=f.getPath().substring(f.getPath().indexOf("_"), f.getPath().indexOf("\\"));
            //this.group = gr;
            this.recordHead.setNameGroup(gr);
        }
        catch (Exception exc)
        {
            System.out.println("     GROUP ????");
          //  this.group = "000000";
            this.recordHead.setNameGroup("???");
        }
        this.setText("");
        this.clearBoard();
        this.height =( this.rowsCount+1) * getHeightLine();
        if (this.getHeight() != this.height)
        {
            this.setPreferredSize(new Dimension(this.width, this.height));
            this.setMaximumSize(new Dimension(this.width, this.height));            
        }
        FileInputStream FIS;
        DataInputStream DIS;
        try {
            FIS = new FileInputStream(f);
            DIS = new DataInputStream(FIS);
            if (DIS.available() == 0) {
                return;
            }

            readBitesText(DIS);
            readBitesGraph(DIS);
            this.repaint();
        } catch (FileNotFoundException ex) {
            ReportException.write(" Ошибка чтения файла (графика) " + f.getName());
        } catch (IOException ex) {
            Logger.getLogger(MyJavaCanvas.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    //</editor-fold>

}
