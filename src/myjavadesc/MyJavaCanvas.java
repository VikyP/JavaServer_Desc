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
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
import myjavadesc.shapes.EndLineType;
import myjavadesc.shapes.IShapeAction;
import myjavadesc.shapes.SEllipse;
import myjavadesc.shapes.SLine;
import myjavadesc.shapes.SPenLine;
import myjavadesc.shapes.SRectangle;
import myjavadesc.shapes.STable;
import myjavadesc.shapes.ShapeType;

import userControl.EditGraphTools;

/**
 *
 * @author viky
 */
public class MyJavaCanvas extends JEditorPane
{

    //данные для заголовка
    //состояние (вкл/выкл) записи звука
    public RecordInfo recordHead;

    //состояние (вкл/выкл) трансляции экрана преподавателя
    public boolean isShareScreen = false;
    public boolean isStartSending = false;

    //номер доски(страницы) текущей даты
    //еслитекущая страница относится не к сегодняшней дате
    // номер<0
    public byte numberPage = -1;

    //список фигур доски
    public ArrayList<IShapeAction> shapes = new ArrayList<IShapeAction>();

    // якоря выбранной фигуры
    private AnchorsManipulations EditAnchors;

    //событие изменился тип фигуры для рисования
    public EventAddShape EASh = new EventAddShape();

    // тип фигуры, который будет нарисован
    public byte shapeType = ShapeType.None;

    // количество строк на доске
    private byte rowsCount = 30;
    private byte rowHeigth = 0;
    private byte rowDescent = 0;

    // количество символов в строке
    private byte colsCount = 80;
    private byte colWidth = 0;
    
    // Цвет линии
    public Color LineColor;

    // Цвет заливки
    public Color FillColor;

    //толщина линии
    public float stroke;

    //тип линии
    public byte typeLine = 0;

    public byte startLineType = 0;
    public byte endLineType = 0;

    //строки таблицы
    public byte rows = 1;
    //колонки таблицы
    public byte columns = 1;

    //флаг (рисовать или нет фигуру при движении мышки)
    //Fonts  Courier, Andale Mono, Monaco, Profont, Consolas, Deja Vu Sans Mono
    private Font F = new Font(Font.MONOSPACED, Font.PLAIN, 16);
    private int fontHeight = 0;

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

    //предварительная высота, пересчитывается в зависимости от размера шрифта
    private int height = 600;

    // ширина доски
    private int width = 750;
    // номер текущей строчки, в которой находится курсор
    // передается на доску студента и подсвечивается
    private byte indexRow = 1;

    private SPenLine freeLine;

    private int currentEditAction = EditAction.empty;

    /**
     * возвращает редактируемую фигуру
     *
     * @return текущая редактируемая фигура
     */
    private IShapeAction getCurrentShapeEditable()
    {
        if (this.editTools == null || this.editTools.shapesList == null)
        {
            return null;
        }
        int index = this.editTools.shapesList.getSelectedIndex();
        if (index < 0)
        {
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
    private int timeT = 200;
    /**
     * частота отправки графики
     */
    private int timeG = 200;
    private Sender_UDP sender;

    /**
     * Поток, отправляющий данные доски - текст
     */
    private class ThreadSendeкText extends Thread
    {

        int count = 0;
        final int frequency = 3;
        String oldText = "";

        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    if (!oldText.equals(MyJavaCanvas.this.getText()))
                    {
                        count = 0;
                    }
                    if (count % frequency == 0)
                    {
                        if (!MyJavaCanvas.this.sender.Send(MyJavaCanvas.this.textToBytes()))
                        {
                           // JOptionPane.showMessageDialog(MyJavaCanvas.this, " Откройте следующую доску ", "Ошибка передачи данных", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    count++;

                    Thread.sleep(timeT);
                } catch (InterruptedException ex)
                {
                    Logger.getLogger(MyJavaCanvas.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    private ThreadSendeкText TST;

    /**
     * Поток, отправляющий данные доски - графика
     */
    private class ThreadSendeкGraph extends Thread
    {

        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    //  System.out.println("    Graph ");

                    synchronized (MyJavaCanvas.this.shapes)
                    {
                        if (!MyJavaCanvas.this.sender.Send(MyJavaCanvas.this.graphToBytes()))
                        {
                            JOptionPane.showMessageDialog(MyJavaCanvas.this, " Откройте следующую доску ", "Ошибка передачи данных", JOptionPane.WARNING_MESSAGE);
                        }

                    }
                    Thread.sleep(timeG);
                } catch (InterruptedException ex)
                {
                    Logger.getLogger(MyJavaCanvas.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    private ThreadSendeкGraph TSG;

    /**
     * @return the rowsCount
     */
    public int getRowsCount()
    {
        return rowsCount;
    }

    /**
     * @param rowsCount the rowsCount to set
     */
    public final void setRowsCount(byte rowsCount)
    {
        this.rowsCount = rowsCount;

    }

    private class KeyListenerEdit implements KeyListener
    {

        @Override
        public void keyReleased(KeyEvent e)
        {
            MyJavaCanvas.this.deleteSelected();
        }

        @Override
        public void keyTyped(KeyEvent ke)
        {
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void keyPressed(KeyEvent ke)
        {
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private class KeyListenerText implements KeyListener
    {

        @Override
        public void keyReleased(KeyEvent e)
        {
            cutRows();
        }

        @Override
        public void keyTyped(KeyEvent ke)
        {
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void keyPressed(KeyEvent ke)
        {
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    class MyMouselistener extends MouseAdapter
    {

        @Override
        public void mouseClicked(MouseEvent me)
        {

            if (me.getButton() == MouseEvent.BUTTON3 && me.isControlDown())
            {

                int index = 0;
                for (IShapeAction sh : MyJavaCanvas.this.shapes)
                {
                    if (sh.getRectangle().contains(me.getPoint()))
                    {
                        //если индекс и выбренная фигура не совпадают
                        if (index != MyJavaCanvas.this.editTools.shapesList.getSelectedIndex())
                        {
                            MyJavaCanvas.this.editTools.shapesList.setSelectedIndex(index);  // назначаем выбранной указанную фигуру
                        } else
                        {
                            MyJavaCanvas.this.editTools.shapesList.clearSelection();// снимаем редактирование   
                        }
                        return;
                    }
                    index++;
                }

            }

            if (me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() == 1)
            {
                MyJavaCanvas.this.endEdit();
            }
        }

        @Override
        public void mousePressed(MouseEvent me)
        {

            if (me.getButton() != MouseEvent.BUTTON3 && !me.isControlDown()
                    && MyJavaCanvas.this.getCursor().getType() != java.awt.Cursor.E_RESIZE_CURSOR)
            {

                MyJavaCanvas.this.endEdit();
                return;
            }

            if (MyJavaCanvas.this.isEdit)
            {
                int index = MyJavaCanvas.this.editTools.shapesList.getSelectedIndex();
                if (index >= 0)
                {
                    // System.out.println("    isEdit");
                    MyJavaCanvas.this.isManipulation = true;
                    MyJavaCanvas.this.begP = new Point(me.getX(), me.getY());
                    MyJavaCanvas.this.shapes.get(index).setEditable(true);
                }
            } else
            {
                MyJavaCanvas.this.begP = new Point(me.getX(), me.getY());
                MyJavaCanvas.this.curP = MyJavaCanvas.this.begP;
                //выход, если действие не назначено
                if (MyJavaCanvas.this.shapeType == ShapeType.None)
                {
                    // System.out.println("ShapeType.None");
                    return;
                }

                if (me.getButton() == MouseEvent.BUTTON3)
                {
                    //  System.out.println("    isDraw");
                    MyJavaCanvas.this.isDraw = true;
                    MyJavaCanvas.this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    MyCaretPos = MyJavaCanvas.this.getCaretPosition();
                }

            }
        }

        @Override
        public void mouseReleased(MouseEvent me)
        {
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

            if (MyJavaCanvas.this.isEdit)
            {
                MyJavaCanvas.this.shapes.get(index).setEditable(false);
                MyJavaCanvas.this.isManipulation = false;
                return;
            }

            //нет новой фигуры
            if (!MyJavaCanvas.this.isDraw)
            {
                return;//выход
            }
            MyJavaCanvas.this.isDraw = false;
            MyJavaCanvas.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            int x1 = (MyJavaCanvas.this.begP.x < MyJavaCanvas.this.curP.x) ? MyJavaCanvas.this.begP.x : MyJavaCanvas.this.curP.x;
            int y1 = (MyJavaCanvas.this.begP.y < MyJavaCanvas.this.curP.y) ? MyJavaCanvas.this.begP.y : MyJavaCanvas.this.curP.y;
            int x2 = (MyJavaCanvas.this.begP.x > MyJavaCanvas.this.curP.x) ? MyJavaCanvas.this.begP.x : MyJavaCanvas.this.curP.x;
            int y2 = (MyJavaCanvas.this.begP.y > MyJavaCanvas.this.curP.y) ? MyJavaCanvas.this.begP.y : MyJavaCanvas.this.curP.y;

            switch (MyJavaCanvas.this.shapeType)
            {
                case ShapeType.Line:
                case ShapeType.LineHorizontal:
                case ShapeType.LineVertical:
                    MyJavaCanvas.this.shapes.add(new SLine(begP, curP, LineColor, stroke, typeLine, startLineType, endLineType));
                    break;
                case ShapeType.PenLine:
                    if (MyJavaCanvas.this.freeLine != null)
                    {
                        MyJavaCanvas.this.shapes.add(new SPenLine(MyJavaCanvas.this.freeLine.Poins(), MyJavaCanvas.this.LineColor, MyJavaCanvas.this.stroke, MyJavaCanvas.this.typeLine, startLineType, endLineType));
                        MyJavaCanvas.this.freeLine = null;
                    }
                    break;
                case ShapeType.Ellipse:
                    MyJavaCanvas.this.shapes.add(new SEllipse(new Point(x1, y1), new Point(x2, y2), MyJavaCanvas.this.LineColor, MyJavaCanvas.this.stroke, MyJavaCanvas.this.typeLine));
                    break;
                case ShapeType.FillEllipse:
                    MyJavaCanvas.this.shapes.add(new SEllipse(new Point(x1, y1), new Point(x2, y2), LineColor, FillColor, stroke, typeLine));
                    break;
                case ShapeType.Rectangle:
                    MyJavaCanvas.this.shapes.add(new SRectangle(new Point(x1, y1), new Point(x2, y2), LineColor, stroke, typeLine));
                    break;
                case ShapeType.FillRectangle:
                    MyJavaCanvas.this.shapes.add(new SRectangle(new Point(x1, y1), new Point(x2, y2), LineColor, FillColor, stroke, typeLine));
                    break;
                case ShapeType.Table:
                    MyJavaCanvas.this.shapes.add(new STable(new Point(x1, y1), new Point(x2, y2), LineColor, stroke, typeLine, rows, columns));
                    break;

            }
            addShape(MyJavaCanvas.this.shapeType);

            //выполнить перерисовку как можно скорее
            MyJavaCanvas.this.repaint();
            //MyJavaCanvas.this.invalidate()// Компонент поменял размеры - требует перерисовки

        }

    }

    class MyMouseMotionListener extends MouseMotionAdapter
    {

        @Override
        public void mouseDragged(MouseEvent me)
        {

            if (MyJavaCanvas.this.isEdit && MyJavaCanvas.this.isManipulation)
            {

                int index = MyJavaCanvas.this.editTools.shapesList.getSelectedIndex();

                if (index < 0)
                {
                    return;
                }

                IShapeAction shapeEditable = MyJavaCanvas.this.shapes.get(index);
                switch (MyJavaCanvas.this.currentEditAction)
                {
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
                        // System.out.println("   sizeLineBeginPoint ");
                        EditAnchors = new AnchorsManipulations(shapeEditable.getType(),
                                shapeEditable.resize_moveRightBottom(me.getPoint().x - MyJavaCanvas.this.begP.x, me.getPoint().y - MyJavaCanvas.this.begP.y),
                                new Point(shapeEditable.getBegin().x + (me.getPoint().x - MyJavaCanvas.this.begP.x),
                                        shapeEditable.getBegin().y + (me.getPoint().y - shapeEditable.getBegin().y)), shapeEditable.getEnd());
                        break;
                    case EditAction.sizeLineEndPoint:
                        //  System.out.println("   sizeLineEndPoint ");
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
            if (MyJavaCanvas.this.isDraw)
            {

                MyJavaCanvas.this.select(MyCaretPos, MyCaretPos);
                switch (MyJavaCanvas.this.shapeType)
                {
                    case ShapeType.LineHorizontal:
                        MyJavaCanvas.this.curP = new Point(me.getX(), MyJavaCanvas.this.curP.y);
                        break;
                    case ShapeType.LineVertical:
                        MyJavaCanvas.this.curP = new Point(MyJavaCanvas.this.curP.x, me.getY());
                        break;
                    case ShapeType.PenLine:
                        if (MyJavaCanvas.this.freeLine == null)
                        {
                            MyJavaCanvas.this.freeLine = new SPenLine(MyJavaCanvas.this.begP, LineColor, stroke, typeLine, startLineType, endLineType);
                        } else
                        {
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
        public void mouseMoved(MouseEvent me)
        {

            if (MyJavaCanvas.this.isEdit && MyJavaCanvas.this.EditAnchors != null)
            {
                // назначение курсора по якорям
                Cursor c = null;
                c = MyJavaCanvas.this.EditAnchors.setCursorFromAnchor(me.getPoint());

                if (c != null)
                {
                    MyJavaCanvas.this.setCursor(c);
                } else
                {
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
    ActionListener clearboard = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            MyJavaCanvas.this.clearBoard();
        }
    };

    public ChangeListener FontSizeChanger = new ChangeListener()
    {

        @Override
        public void stateChanged(ChangeEvent e)
        {
            JSpinner fontSize = (JSpinner) e.getSource();

            fontHeight = (int) fontSize.getValue();
            float val = Float.parseFloat(fontSize.getValue().toString());

            MyJavaCanvas.this.setFont(F.deriveFont(val));
            MyJavaCanvas.this.getFontMetrics_HW();

            MyJavaCanvas.this.validate();
            MyJavaCanvas.this.repaint();
        }

    };

    /**
     * удаление выбранного графического объекта *
     */
    ActionListener deleteselect = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            MyJavaCanvas.this.deleteSelected();
        }
    };

    /**
     * копирование и вставка выбранного графического объекта *
     */
    ActionListener addselect = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            MyJavaCanvas.this.addSelect();
        }
    };

    //изменение количества строк
    public ActionListener rowCount = new ActionListener()
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            JComboBox cb = (JComboBox) e.getSource();
            rows = Byte.parseByte(cb.getSelectedItem().toString());
        }

    };

    //изменение количества столбцов
    public ActionListener colCount = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            JComboBox cb = (JComboBox) e.getSource();
            columns = Byte.parseByte(cb.getSelectedItem().toString());
        }
    };

    //установка типа начала линии
    public ActionListener startLineAL = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            JComboBox cb = (JComboBox) e.getSource();
            startLineType = Byte.parseByte(cb.getSelectedItem().toString());
        }
    };

    //установка типа конца линии
    public ActionListener endLineAL = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            JComboBox cb = (JComboBox) e.getSource();
            endLineType = Byte.parseByte(cb.getSelectedItem().toString());
        }
    };

    /**
     * изменился выбранный объект в перечне графических фигур
     */
    ListSelectionListener selectShape = new ListSelectionListener()
    {
        private int index = -1;

        @Override
        public void valueChanged(ListSelectionEvent e)
        {

            int i = MyJavaCanvas.this.editTools.shapesList.getSelectedIndex();
            //активация или деактивация кнопок 
            if (i < 0)
            {
                editTools.isSelectedOne(false);
            } else
            {
                editTools.isSelectedOne(true);
            }

            // проверка  изменился ли индекс
            if (this.index >= 0 && this.index < MyJavaCanvas.this.shapes.size() && this.index != i)
            {
                //если изменился, отменяем редактирование предыдущей фигуры                
                IShapeAction oldSelect = MyJavaCanvas.this.shapes.get(index);
                oldSelect.setEditable(false);
            }

            this.index = i;

            if (index < 0)
            {
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

    private final int left = 35;
    private final int top = 13;
    private final int right = 20;
    private final int bottom = 20;

    public MyJavaCanvas()
    {
        setOpaque(false);
        this.addMouseListener(new MyJavaCanvas.MyMouselistener());
        this.addMouseMotionListener(new MyJavaCanvas.MyMouseMotionListener());
        this.addKeyListener(new KeyListenerText());

        //this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
        // this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        // this.rowsCount = 30;
        getFontMetrics_HW();

        this.setMinimumSize(new Dimension(200, 200));
        this.setMargin(new Insets(top, left, bottom, right));

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
        this.addComponentListener(new ComponentListener()
        {

            @Override
            public void componentResized(ComponentEvent e)
            {

            }

            @Override
            public void componentMoved(ComponentEvent e)
            {
                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void componentShown(ComponentEvent e)
            {
                setTimeOn(true);
            }

            @Override
            public void componentHidden(ComponentEvent e)
            {
                setTimeOn(false);
            }
        });
        this.addCaretListener(new CaretListener()
        {
            @Override
            public void caretUpdate(CaretEvent ce)
            {
                int pos = MyJavaCanvas.this.getCaretPosition();
                Element map = MyJavaCanvas.this.getDocument().getDefaultRootElement();
                int row = map.getElementIndex(pos);
                Element lineElem = map.getElement(row);
                int col = pos - lineElem.getStartOffset();
                MyJavaCanvas.this.indexRow = (byte) row;
            }

        });

    }

    /**
     * считывание данных из файла.xml
     *
     * @param SC объект с данными из файла
     */
    public void Init(SettingsConfig SC)
    {
        this.sender = new Sender_UDP();
        this.setDrawColors(SC.LineColor, SC.FillColor);
        this.setColors(SC.Background, SC.Foreground);
        this.stroke = SC.thicknessLine;
        this.typeLine = (byte) SC.typeLine;
        
    }
    public void startSending()
    {       
        this.isStartSending=true;
        this.TST.start();
        this.TSG.start();
    }

    public void setTimeOn(boolean f)
    {
        if (f)
        {
            if(this.isVisible())
            {
                this.timeG = 100;
                this.timeT = 50;
            }
        }
        else
        {
            this.timeG = 3000;
            this.timeT = 3000;
        }

    }

    /**
     * Определение высоты строки в зависимости от размера шрифта
     *
     *
     * @return высоту строки
     */
    private void getFontMetrics_HW()
    {
        BufferedImage BI = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) BI.createGraphics();
        g2d.setFont(this.getFont());
        FontMetrics metrics = g2d.getFontMetrics(this.getFont());
        g2d.dispose();

        this.rowHeigth = (byte) metrics.getHeight();
        this.rowDescent = (byte) metrics.getMaxDescent();
        this.colWidth = (byte) metrics.charWidth('X');

        this.height = this.rowHeigth * (this.rowsCount + 1) + this.top + this.bottom;
        this.width = this.colWidth * (this.colsCount + 1) + this.left + this.right;

        this.setPreferredSize(new Dimension(this.width, this.height));
        this.setMaximumSize(new Dimension(this.width, this.height));
    }

    /**
     * Добавление выбранной фигуры
     */
    private void addSelect()
    {
        int index = this.editTools.shapesList.getSelectedIndex();
        IShapeAction newShape = this.shapes.get(index).copyShape(5, 5);
        this.shapes.add(index + 1, newShape);
        this.editTools.dlm.add(index + 1, ShapeType.toStr(newShape.getType()));
        this.editTools.shapesList.setSelectedIndex(index + 1);

    }

    /**
     * Удаление выбранной фигуры
     */
    private void deleteSelected()
    {

        int index = this.editTools.shapesList.getSelectedIndex();

        if (index < 0)
        {
            this.endEdit();
            return;
        }

        this.shapes.remove(index);
        this.editTools.dlm.remove(index);

        if (this.editTools.dlm.getSize() == 0)
        {
            this.endEdit();
            return;
        } else
        {
            // если список не пуст назначаем выбранным объектом следующий(если раньше был первый)или предыдущий (если не первый)
            if (this.editTools.dlm.getSize() == 1)
            {
                this.editTools.shapesList.setSelectedIndex(0);
            } else
            {
                this.editTools.shapesList.setSelectedIndex((index == 0) ? index++ : index--);
            }
        }
        this.repaint();

    }

    /**
     * Добавление фигуры в список
     *
     * @param name название фигуры
     */
    private void addShape(int type)
    {
        // System.out.println(" type   " + type);
        this.editTools.dlm.addElement(ShapeType.toStr(type));
    }

    /**
     * установка цвета текста и фона
     *
     * @param b цвет фона
     * @param f цвет текста
     */
    public void setColors(Color b, Color f)
    {
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
    public void setDrawColors(Color l, Color f)
    {
        this.LineColor = l;
        this.FillColor = f;
    }

    /**
     * обрезка контента, который не помещается в 30 строк
     */
    private void cutRows()
    {
        try
        {
            if (this.getText().length() == 0)
            {
                return;
            }

            boolean isCut = false;
            int lastIndex = this.getText().length() - 1;
            Rectangle R=null;
            while (R==null)
            {
                try
                {           
                    R = this.modelToView(lastIndex);
                }
                catch (BadLocationException ex)
                {
                    lastIndex--;
                    if(lastIndex<0)
                        return;
                }
            }
            
            while (lastIndex > 0 && R.getY() > (this.rowsCount + 10) * this.rowHeigth + 5)
            {
                lastIndex--;
                R = this.modelToView(lastIndex);
                isCut = true;
            }

            if (!isCut)
            {
                return;
            }

            this.setText(this.getText().substring(0, lastIndex++));

        } 
        catch (BadLocationException ex)
        {
         
            ReportException.write(this.getClass().getName() + "\t4\t" + ex.getMessage());
            Logger.getLogger(MyJavaCanvas.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * прорисовка номеров строк
     *
     * @param g Graphics доски
     */
    private void drawLinesNumber(Graphics2D g2D)
    {

        g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g2D.setFont(this.getFont());
        g2D.setColor(this.getForeground());
        AlphaComposite A1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        g2D.setComposite(A1);
        for (int i = 0; i < getRowsCount(); i++)
        {
            g2D.drawString((i + 1) + "", 5, (i + 1) * this.rowHeigth + this.top - this.rowDescent);
        }
        AlphaComposite A2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
        g2D.setComposite(A2);

    }

    private void drawEndLine(Graphics g)
    {
        switch (this.startLineType)
        {
            case EndLineType.ARROW:
                EndLineType.drawArrowStart(g, this.begP, this.curP);
                break;
            case EndLineType.CIRCLE:
                g.fillOval(this.begP.x - EndLineType.CIRCLE_SIZE, this.begP.y - EndLineType.CIRCLE_SIZE, EndLineType.CIRCLE_SIZE * 2, EndLineType.CIRCLE_SIZE * 2);
                break;
            case EndLineType.RECTANGLE:
                g.fillRect(this.begP.x - EndLineType.RECTANGLE_SIZE, this.begP.y - EndLineType.RECTANGLE_SIZE, EndLineType.RECTANGLE_SIZE * 2, EndLineType.RECTANGLE_SIZE * 2);
                break;

        }
        switch (this.endLineType)
        {
            case EndLineType.ARROW:
                EndLineType.drawArrowEnd(g, this.begP, this.curP);
                break;
            case EndLineType.CIRCLE:
                g.fillOval(this.curP.x - EndLineType.CIRCLE_SIZE, this.curP.y - EndLineType.CIRCLE_SIZE, EndLineType.CIRCLE_SIZE * 2, EndLineType.CIRCLE_SIZE * 2);
                break;
            case EndLineType.RECTANGLE:
                g.fillRect(this.curP.x - EndLineType.RECTANGLE_SIZE, this.curP.y - EndLineType.RECTANGLE_SIZE, EndLineType.RECTANGLE_SIZE * 2, EndLineType.RECTANGLE_SIZE * 2);
                break;

        }

    }

    /**
     * прорисовка текущей фигуры в режиме рисования
     *
     * @param g Graphics доски
     */
    private void draw_IsDraw(Graphics g)
    {
        if (!this.isDraw)
        {
            return;
        }
        g.setColor(this.LineColor);

        Graphics2D g2D = setProperties(g);

        int x1 = (this.begP.x < this.curP.x) ? this.begP.x : this.curP.x;

        int y1 = (this.begP.y < this.curP.y) ? this.begP.y : this.curP.y;

        int x2 = (this.begP.x > this.curP.x) ? this.begP.x : this.curP.x;
        int y2 = (this.begP.y > this.curP.y) ? this.begP.y : this.curP.y;

        switch (MyJavaCanvas.this.shapeType)
        {
            case ShapeType.Line:
            case ShapeType.LineHorizontal:
            case ShapeType.LineVertical:
                g2D.drawLine(this.begP.x, this.begP.y, this.curP.x, this.curP.y);
                drawEndLine(g);
                break;
            case ShapeType.PenLine:
                if (this.freeLine != null)
                {
                    this.freeLine.draw(g);
                }
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
            case ShapeType.Table:
                g2D.drawRect(x1, y1, x2 - x1, y2 - y1);
                int w = x2 - x1;
                int h = y2 - y1;

                for (int i = 1; i < rows; i++)
                {
                    g2D.drawLine(x1, y1 + h / rows * i, x1 + w, y1 + h / rows * i);
                }

                for (int i = 1; i < columns; i++)
                {
                    g2D.drawLine(x1 + w / columns * i, y1, x1 + w / columns * i, y1 + h);
                }

                break;
        }

    }

    @Override
    public void paintComponent(Graphics g)
    {
        // Рисование фона      
        // super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        if (b != null)
        {
            int x = (getWidth() - b.getWidth()) / 2;
            int y = (getHeight() - b.getHeight()) / 2;
            g2d.drawImage(b, 0, 0, this);
        } else
        {
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        for (IShapeAction R : this.shapes)
        {
            R.draw(g);
        }

        draw_IsDraw(g);
        if (this.EditAnchors != null)
        {
            this.EditAnchors.DrawAnchors(g);
        }

        drawLinesNumber(g2d);
        getUI().paint(g2d, this);
        g2d.setColor(this.getBackground());
        g2d.fillRect(0, this.rowHeigth * (this.rowsCount) + this.rowHeigth / 2, this.getWidth(), this.getHeight() - this.rowHeigth * this.rowsCount);
        g2d.dispose();
    }

    /**
     * очистка доски от графических объектов
     */
    public void clearBoard()
    {
        endEdit();
        this.shapes.clear();
        this.editTools.dlm.removeAllElements();
        this.repaint();
    }

    /**
     * выход из режима редактирования
     */
    private void endEdit()
    {
        this.isEdit = false;
        this.EditAnchors = null;
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        IShapeAction ShA = this.getCurrentShapeEditable();
        if (ShA != null)
        {
            ShA.setEditable(false);
        }
        this.editTools.shapesList.clearSelection();
        this.repaint();
    }

    protected Graphics2D setProperties(Graphics g)
    {
        Graphics2D g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setColor(this.LineColor);
        g2D.setStroke(DashArrays.getStrokeLine(stroke, this.typeLine));
        return g2D;
    }

    /**
     * формирование пакета для отправки - текст
     *
     * @return массив байт с текстом
     */
    private byte[] textToBytes()
    {
        try
        {
            ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
            DataOutputStream DOS = new DataOutputStream(BAOS);
            writeHead(DOS, TypeInfo.TEXT);
            synchronized (this.getText())
            {
                //  текст с доски
                byte[] body = getTextToBytes();
                DOS.writeInt(body.length);
                DOS.writeByte(this.indexRow);

//параметр для согласования размеров холста, шрифта, расположения и масштаба графики
                DOS.writeByte(this.fontHeight);

                DOS.write(body);

            }
            return BAOS.toByteArray();
        } catch (UnsupportedEncodingException ex)
        {
            ReportException.write(this.getClass().getName() + "\t2\t" + ex.getMessage());
            Logger.getLogger(MyJavaCanvas.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex)
        {
            ReportException.write(this.getClass().getName() + "\t3\t" + ex.getMessage());
            Logger.getLogger(MyJavaCanvas.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    /**
     * запись текста в массив байт для отправки по UDP
     */
    private byte[] getTextToBytes()
    {
        ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
        DataOutputStream DOS = new DataOutputStream(BAOS);
        byte[] body = null;
        try
        {
            ByteBuffer buffer = Charset.forName("UTF-8").encode(this.getText());
            DOS.writeUTF(new String(buffer.array(),"UTF-8"));
            body = BAOS.toByteArray();
            DOS.close();
            BAOS.close();
        } catch (Exception exc)
        {
            ReportException.write(this.getClass().getName() + "\t3\t" +exc.getMessage());
        }
        return body;
    }

    /**
     * формирование пакета графического содержимого для отправки по UDP
     *
     * @return массив байт с графикой
     */
    private byte[] graphToBytes()
    {
        ByteArrayOutputStream BAOS = null;
        DataOutputStream DOS = null;
        try
        {
            BAOS = new ByteArrayOutputStream();
            DOS = new DataOutputStream(BAOS);
            //формирование заголовка
            writeHead(DOS, TypeInfo.GRAPH);
            //формирование основного пакета
            byte[] body = this.writeBytesGraph();
            // запись длины основного пакета
            DOS.writeInt(body.length);
            // запись основного пакета
            DOS.write(body);
            byte[] b = BAOS.toByteArray();
            DOS.close();
            BAOS.close();
            return b;

        } catch (IOException ex)
        {
           
            ReportException.write("MyJavaCanvas.graphToBytes(..)" + ex.getMessage());
            return null;
        }

    }

    /**
     * Запись заголовка
     *
     * Protocol byte[0]- isRecord (0 - false / 1 - true) byte[1]-
     * nameGroup.length (length of string nameGroup) new byte[length] - название
     * группы byte [length+2] - номер доски byte[length+3]- тип данных
     * (TypeInfo.TEXT=1/TypeInfo.GRAPH=2 ) if (type == TypeInfo.TEXT)
     * byte[length+4] - номер строчки
     *
     * @param DOS поток для записи заголовка
     */
    private void writeHead(DataOutputStream DOS, int type)
    {
        try
        {
            DOS.write(this.recordHead.getHeadDesc());
            DOS.writeByte(this.numberPage);
            DOS.writeByte((byte) type);
        } catch (IOException ex)
        {
            ReportException.write(this.getClass().getName() + "\t1\t" + ex.getMessage());
        }

    }

    //<editor-fold defaultstate="collapsed" desc="Read Write Save board ">
    /**
     * запись в поток текста для сохранения
     */
    private void writeBytesText(DataOutputStream DOS)
    {

        try
        {
            byte[] text = this.getText().getBytes("UTF-8");
            DOS.writeInt(text.length);
            DOS.write(text, 0, text.length);
        } catch (IOException ex)
        {
            Logger.getLogger(MyJavaCanvas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * запись в поток графики для сохранения
     */
    private void writeBytesGraph(DataOutputStream DOS)
    {
        try
        {
            synchronized (this.shapes)
            {
                //размер массива
                DOS.writeInt(shapes.size());
                // запись всех фигур
                for (IShapeAction shape : shapes)
                {
                    switch (shape.getType())
                    {
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
                        case ShapeType.Table:
                            ((STable) shape).BinaryWrite(DOS);
                            break;
                    }
                }
            }
        } catch (IOException ex)
        {
            Logger.getLogger(MyJavaCanvas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * запись в поток графики для отправки по UDP
     */
    private byte[] writeBytesGraph()
    {
        ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
        DataOutputStream DOS = new DataOutputStream(BAOS);
        byte[] body = null;
        try
        {  synchronized (this.shapes)
            {
                DOS.writeInt(this.getWidth());
                DOS.writeInt(this.getHeight());
                //размер массива
                DOS.writeInt(shapes.size());
                // запись всех фигур
                for (IShapeAction shape : shapes)
                {
                    switch (shape.getType())
                    {
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
                        case ShapeType.Table:
                            ((STable) shape).BinaryWrite(DOS);
                            break;
                    }
                }
            }
            body = BAOS.toByteArray();
            DOS.close();
            BAOS.close();
        } catch (IOException ex)
        {
            ReportException.write(" Ошибка запись в поток графики для отправки по UDP " + ex.getMessage());
        }
        return body;
    }

    /**
     * Сохранение контента доски
     *
     * @param f файл для сохранения доски
     */
    public void save(File f)
    {
        if (!f.exists())
        {
            return;
        }
        FileOutputStream FOS = null;
        DataOutputStream DOS = null;
        try
        {
            FOS = new FileOutputStream(f);
            DOS = new DataOutputStream(FOS);
            this.writeBytesText(DOS);
            this.writeBytesGraph(DOS);

        } catch (FileNotFoundException ex)
        {
            ReportException.write(" Ошибка записи файла 1  " + f.getName());
        } finally
        {
            try
            {
                DOS.close();
                FOS.close();

            } catch (IOException ex)
            {
                ReportException.write(" Ошибка записи файла 3  " + f.getName());
            }

        }

    }

    /**
     * чтение текста из потока
     */
    private void readBitesText(DataInputStream DIS)
    {
        try
        {
            this.setText("");
            if (DIS.available() == 0)
            {
                return;
            }
            getFontMetrics_HW();
            int length = DIS.readInt();
            byte[] text = new byte[length];
            int cnt = DIS.read(text, 0, length);
          if(length!=0)
          {
            /*char c= (char)text[0];
            char z= (char)text[length-1];
            System.out.println(cnt == length);*/
            String str = new String(text,"UTF-8");
            this.setText(str);
          }

        } catch (IOException ex)
        {
            ReportException.write(" Ошибка чтения файла (текст) "+ex.getMessage());
        }

    }

    /**
     * чтение графики из потока
     */
    private void readBitesGraph(DataInputStream DIS)
    {
        try
        {
            shapes.clear();
            this.editTools.dlm.removeAllElements();
            if (DIS.available() == 0)
            {
                return;
            }

            int length = DIS.readInt();
            byte type = ShapeType.None;
            for (int i = 0; i < length; i++)
            {

                try
                {
                    IShapeAction sa = null;
                    type = DIS.readByte();
                    switch (type)
                    {
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
                        case ShapeType.Table:
                            sa = new STable(DIS, type);
                            break;
                    }
                    if (sa != null && sa.getType() > ShapeType.None)
                    {
                        this.shapes.add(sa);
                        this.editTools.dlm.addElement(ShapeType.toStr(type));
                    }

                } catch (IOException ex)
                {
                    ReportException.write(" Ошибка чтения файла (графика)1 " + ex.getMessage());
                }
            }

        } catch (IOException ex)
        {
            ReportException.write(" Ошибка чтения файла (графика) 2 " + ex.getMessage());
        }

    }

    /**
     * чтение доски из указанного файла
     */
    public void readBoard(File f)
    {
        try
        {
            String gr = f.getPath().substring(f.getPath().indexOf("_"), f.getPath().indexOf("\\"));
            System.out.println("    path " + f);
            this.recordHead.setNameGroup(gr);
        } catch (Exception exc)
        {
            ReportException.write(" Ошибка чтения доски из указанного файла " + exc.getMessage());
            this.recordHead.setNameGroup("???");
        }
        this.setText("");
        this.clearBoard();
        FileInputStream FIS = null;
        DataInputStream DIS = null;
        try
        {
            FIS = new FileInputStream(f);

            DIS = new DataInputStream(FIS);
            if (DIS.available() == 0)
            {
                return;
            }

            readBitesText(DIS);
            readBitesGraph(DIS);
            this.repaint();

        } catch (FileNotFoundException ex)
        {
            ReportException.write(" Ошибка чтения доски из указанного файла 1 " + f.getName() + "  " + ex.getMessage());
        } catch (IOException ex)
        {
            ReportException.write(" Ошибка чтения доски из указанного файла 2" + f.getName() + "  " + ex.getMessage());
        } finally
        {
            try
            {
                FIS.close();
                DIS.close();
            } catch (IOException ex)
            {
                ReportException.write(" Ошибка закрытия указанного файла " + f.getName() + "  " + ex.getMessage());
            }
        }

    }
    //</editor-fold>

}
