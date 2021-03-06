/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myjavadesc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import masterPanel.MasterFrame;
import masterPanel.RecordInfo;
import masterPanel.SettingsConfig;
import myjavadesc.events.EventStart;
import myjavadesc.events.IChBoard;
import myjavadesc.events.IChShapeType;
import myjavadesc.events.IStart;
import userControl.DashLineComboBox;

import userControl.DrawTools;
import userControl.SettingTools;
import userControl.SizeSketch;
import userControl.StartEndLine_ComboBox;
import userControl.ToolsPanel;
import userControl.WidthLineComboBox;

/**
 *
 * @author 06585
 */
public class MasterBoard extends JPanel
{

    public JPanel MasterPane;
    private JPanel canvasPanel;
    private JScrollPane scrollPane;
    private JScrollPane scrollPaneTools;
    private SettingsConfig styles;

    public File fileCurrentBoard;

    MyJavaCanvas myCanvas;
    JColorChooser colorChooser;

    private ToolsPanel tools;

    private DrawTools DT;
    private GroupsTools listGour;
    private SettingTools settings;
    private ArchiveFiles archF;
    private Dimension BoardDim = new Dimension(800, 800);
    
    public EventStart ES;

    IChShapeType IShType = new IChShapeType()
    {
        @Override
        public void setShapeType(byte ShapeType)
        {
            MasterBoard.this.myCanvas.shapeType = ShapeType;
        }
    };

    IChBoard IChbrd = new IChBoard()
    {

        @Override
        public void readBoard(File f)
        {

            if (f == null)
            {
                MasterBoard.this.myCanvas.clearBoard();
                MasterBoard.this.myCanvas.setText("");
                return;

            }
            if (MasterBoard.this.fileCurrentBoard == null)
            {
                MasterBoard.this.fileCurrentBoard = f;
            } else
            {
                // System.out.println(" MasterBoard.this.fileCurrentBoard " +MasterBoard.this.fileCurrentBoard.getName());
                MasterBoard.this.saveCurrentBoard();
                MasterBoard.this.fileCurrentBoard = f;
            }

            MasterBoard.this.readCurrentBoard();

        }
    };

    /**
     * конструктор создания доски
     *
     * @param SC
     * @param r
     * @param b
     */
    public MasterBoard(SettingsConfig SC, RecordInfo r, JToggleButton b)
    {

        this.styles = SC;
        this.MasterPane = new JPanel();
        ES= new EventStart();
        this.setSize(BoardDim);
        this.setLayout(new BorderLayout());
        JPanel leftPanel = new JPanel();
        JPanel status = new JPanel();
        status.setBackground(Color.red);

        status.setPreferredSize(new Dimension(200, 50));
        leftPanel.add(status, BorderLayout.SOUTH);
        leftPanel.setLayout(new BorderLayout());
        this.canvasPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        leftPanel.add(this.canvasPanel, BorderLayout.CENTER);

        this.myCanvas = new MyJavaCanvas();

        this.myCanvas.Init(SC);
        this.myCanvas.recordHead = r;
        this.myCanvas.setVisible(false);
        this.canvasPanel.add(this.myCanvas);
        this.canvasPanel.setBackground(getPanelBackground(this.myCanvas.getBackground()));

        this.scrollPane = new JScrollPane(this.canvasPanel);
        JSplitPane SP = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.scrollPane, new JTextArea());
        SP.setDividerLocation(600);
        SP.setResizeWeight(1);
        this.tools = new ToolsPanel();
        this.settings = new SettingTools(SC.thicknessLine, SC.typeLine);
        this.listGour = new GroupsTools();
        
        initTools();
        settings.currFill.setBackground(SC.FillColor);
        settings.currLine.setBackground(SC.Background);
        settings.currLine.setProperties(SC.LineColor, SC.thicknessLine, SC.typeLine);
        this.archF.rec = b;
        this.archF.fontSize.setValue(SC.fontSize);

        this.MasterPane.setLayout(new BorderLayout());
        this.MasterPane.add(SP, BorderLayout.CENTER);
        this.MasterPane.add(this.scrollPaneTools, BorderLayout.WEST);

        this.add(this.MasterPane, BorderLayout.CENTER);

    }

    public void setVisibleCanvas(boolean flag)
    {
        this.myCanvas.setVisible(flag);
    }
    
    public void setEnableAll()
    {
        this.archF.startCommection();
    }

    private void initTools()
    {
        //контейнер инструментов

        this.scrollPaneTools = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.scrollPaneTools.setViewportView(this.tools);

        this.tools.setPreferredSize(new Dimension(SizeSketch.TOOLPANEL_WIDTH, this.BoardDim.height));

        this.colorChooser = new JColorChooser(this.myCanvas.getForeground());

        //<editor-fold defaultstate="collapsed" desc=" Группа ">
        //заполняем список папок групп( заполнение в конструкторе)
        
        // инструменты работы с группой
        this.tools.Group.setPanels(this.listGour.groupPanel, this.listGour.groupPanelHide);
        //событие изменения выбранной группы
        this.listGour.setActionGroups(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JComboBox Groups = (JComboBox) e.getSource();
                if (Groups.getSelectedIndex() < 0)
                {
                    return;
                }               
                MasterBoard.this.archF.setGroup(Groups.getSelectedItem().toString());
                // потоки отправки еще незапущены
                if (!MasterBoard.this.myCanvas.isStartSending)
                {
                    MasterBoard.this.myCanvas.startSending();
                    MasterBoard.this.listGour.setNotEditable();
                    IStart  S= (IStart)MasterBoard.this.ES.getListener();
                    S.startConnection();
                }
                
            }
        });
//</editor-fold>         

        //<editor-fold defaultstate="collapsed" desc=" Архив группы ">
        // создаем объект для раборы с архивом выбранной группы
        this.archF = new ArchiveFiles();

        //событие изменения текущей доски
        this.archF.EvChBoard.ChangeBoardAdd(IChbrd);
     
        //инструменты работы с архивом ( добавить счетчик !!!)
        this.tools.Hystory.setPanels(this.archF.archPanel, this.archF.archPanelHide);

        //кнопка изменения цвета текста
        this.archF.colorText.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JColorChooser.createDialog(MasterBoard.this.archF.colorText,
                        "Новый цвет текста", true, colorChooser,
                        new ActionListener()
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                Color c = colorChooser.getSelectionModel().getSelectedColor();
                                myCanvas.setForeground(c);
                                myCanvas.setCaretColor(c);
                            }

                        }, null).setVisible(true);

            }

        });
        //кнопка изменения фона
        this.archF.themesBoard.addActionListener(new ActionListener()
        {

            Color c;

            public void actionPerformed(ActionEvent e)
            {
                JColorChooser.createDialog(MasterBoard.this.archF.themesBoard,
                        "Новый цвет фона", true, colorChooser,
                        new ActionListener()
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                c = colorChooser.getSelectionModel().getSelectedColor();
                                myCanvas.setBackground(c);
                            }

                        }, null).setVisible(true);
                if (c != null)
                {

                    MasterBoard.this.canvasPanel.setBackground(MasterBoard.this.getPanelBackground(c));
                    settings.currLine.setBackground(c);
                }
            }

        });
        this.archF.fontSize.addChangeListener(this.myCanvas.FontSizeChanger);

//</editor-fold>    
        //<editor-fold defaultstate="collapsed" desc=" Насторйки ">
        this.settings.colorLine.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JColorChooser.createDialog(MasterBoard.this.settings.colorLine,
                        "Новый цвет линии", true, colorChooser,
                        new ActionListener()
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                myCanvas.LineColor = colorChooser.getSelectionModel().getSelectedColor();
                                settings.currLine.setProperty(myCanvas.LineColor);

                            }

                        }, null).setVisible(true);

            }

        });

        this.settings.colorFill.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JColorChooser.createDialog(MasterBoard.this.settings.colorFill,
                        "Новый цвет заливки", true, colorChooser,
                        new ActionListener()
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                myCanvas.FillColor = colorChooser.getSelectionModel().getSelectedColor();
                                settings.currFill.setBackground(myCanvas.FillColor);
                            }

                        }, null).setVisible(true);

            }

        });
        this.tools.Setting.setPanels(this.settings.settingPanel, this.settings.settingPanelHide);
        this.settings.thicnessLine.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                WidthLineComboBox wlcb = (WidthLineComboBox) ae.getSource();
                myCanvas.stroke = (float) wlcb.getSelectedValue();
                settings.currLine.setProperty(myCanvas.stroke);
            }
        });
        this.settings.styleLine.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                DashLineComboBox dlcb = (DashLineComboBox) ae.getSource();
                myCanvas.typeLine = (byte) dlcb.getSelectedValue();
                settings.currLine.setProperty(myCanvas.typeLine);
            }
        }
        );
        this.settings.startLineCB.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                StartEndLine_ComboBox slcb = (StartEndLine_ComboBox) e.getSource();
                myCanvas.startLineType = (byte) slcb.getSelectedValue();

            }
        }
        );

        this.settings.endLineCB.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                StartEndLine_ComboBox slcb = (StartEndLine_ComboBox) e.getSource();
                myCanvas.endLineType = (byte) slcb.getSelectedValue();
            }
        }
        );

//</editor-fold>  
        //<editor-fold defaultstate="collapsed" desc=" Типы графических элементов ">
        // патель изменения типа графическиго инструмента
        this.DT = new DrawTools();
        this.tools.setDrawTools(this.DT);
        this.DT.setActionRowCount(myCanvas.rowCount);
        this.DT.setActionColumnCount(myCanvas.colCount);
        //событие смены типа текущего графического элемента
        this.DT.EvShapeType.ShapeTypeChangedAdd(IShType);
        //</editor-fold>  

        //<editor-fold defaultstate="collapsed" desc=" Редактирование графических элементов ">       
        //добавление на панель инструментов блока редактирования графики
        this.tools.setEditTools(this.myCanvas.editTools);
        //</editor-fold>   
    }

    /**
     * Определение цвета фона компонента, содержащего доску
     * @param c фон доски
     * @return цвет фона компонента
     */
    private Color getPanelBackground(Color c)
    {
        int delta = 30;
        int r = c.getRed() > 150 ? c.getRed() - delta : c.getRed() + delta;
        int g = c.getGreen() > 150 ? c.getGreen() - delta : c.getGreen() + delta;
        int b = c.getBlue() > 150 ? c.getBlue() - delta : c.getBlue() + delta;
        Color p = new Color(r, g, b);

        return p;
    }

    /**
     * Сохранение текущей доски,если назначен файл для сохранения
     */
    public void saveCurrentBoard()
    {
        if (this.fileCurrentBoard != null)
        {
            this.myCanvas.save(fileCurrentBoard);
            this.styles.saveSettingsThemes(this.myCanvas.getForeground(), this.myCanvas.getBackground(), (int) this.archF.fontSize.getValue());
            this.styles.saveColorsDraw(this.myCanvas.LineColor, this.myCanvas.FillColor);
            this.styles.saveLineSetting(this.myCanvas.stroke, this.myCanvas.typeLine);
        }
    }

    /**
     * Чтение контента из назначенного файла текущей доски
     */
    private void readCurrentBoard()
    {
        if (this.fileCurrentBoard != null)
        {
            this.myCanvas.readBoard(fileCurrentBoard);
            this.myCanvas.numberPage = this.archF.isTodayDir()
                    ? (byte) (this.archF.cbm.getIndexOf(this.archF.cbm.getSelectedItem()) + 1)
                    : (byte) ((this.archF.cbm.getIndexOf(this.archF.cbm.getSelectedItem()) + 1) * (-1));
            if (!this.myCanvas.isVisible())
            {
                this.myCanvas.setVisible(true);
                this.settings.setEnabledAll();
            }
            
            this.myCanvas.requestFocus();
        }
    }

    
    
    public void setTimeOn(boolean f)
    {
        this.myCanvas.setTimeOn(f);
    }
    

}
