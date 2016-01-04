/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myjavadesc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import masterPanel.RecordInfo;
import masterPanel.SettingsConfig;
import myjavadesc.events.IChBoard;
import myjavadesc.events.IChShapeType;
import userControl.DashLineComboBox;

import userControl.DrawTools;
import userControl.SettingTools;
import userControl.SizeSketch;
import userControl.ToolsPanel;
import userControl.WidthLineComboBox;

/**
 *
 * @author 06585
 */
public class MasterBoard extends JPanel {

    public JPanel MasterPane;
    private JPanel canvasPanel;
    private JScrollPane scrollPane;
    private JScrollPane scrollPaneTools;
    private JSplitPane mainPanel;
    private JTextArea plan;
    private SettingsConfig styles;
    
    
    public File fileCurrentBoard;

    private MyJavaCanvas myCanvas;
    JColorChooser colorChooser ;
    
    private ToolsPanel tools;
    
    private DrawTools DT;
    private Groups listGour;
    private SettingTools settings;
    private ArchiveFiles archF;
    private Dimension BoardDim= new Dimension(800,800);

    IChShapeType IShType = new IChShapeType() {
        @Override
        public void setShapeType(int ShapeType) 
        {
            MasterBoard.this.myCanvas.shapeType = ShapeType;
        }
    };

    IChBoard IChbrd =  new IChBoard()
    {

        @Override
        public void readBoard(File f)
        {
           
            if(f==null)
            {                
                MasterBoard.this.myCanvas.clearBoard();
                MasterBoard.this.myCanvas.setText("");
                return;
                
            }
            if(MasterBoard.this.fileCurrentBoard== null)
            {
               MasterBoard.this.fileCurrentBoard=f;                  
            }
            else
            {
               System.out.println(" MasterBoard.this.fileCurrentBoard " +MasterBoard.this.fileCurrentBoard.getName());
               MasterBoard.this.saveCurrentBoard();
               MasterBoard.this.fileCurrentBoard=f;            
            }
            
            MasterBoard.this.readCurrentBoard();
            
        }
    };
/**
 * конструктор создания доски
 */
    public MasterBoard(SettingsConfig SC, RecordInfo r, JToggleButton b) 
    {    
        
        this.styles=SC;
        this.MasterPane = new JPanel();
        this.setSize(BoardDim);
        this.setLayout(new BorderLayout());
        JPanel leftPanel= new JPanel();
        JPanel status = new JPanel();
        status.setBackground(Color.red);
        
        status.setPreferredSize(new Dimension(200,50));
        leftPanel.add(status, BorderLayout.SOUTH);
        leftPanel.setLayout(new BorderLayout());
        this.canvasPanel = new JPanel(); 
        
        
        leftPanel.add(this.canvasPanel,BorderLayout.CENTER);
        
        
        this.myCanvas = new MyJavaCanvas(); 
        
        this.myCanvas.Init(SC);
        this.myCanvas.recordHead=r; 
        this.myCanvas.setVisible(false);        
        this.canvasPanel.add(this.myCanvas);
        this.canvasPanel.setBackground(getPanelBackground(this.myCanvas.getBackground()));
      
        this.plan = new JTextArea();
        this.MasterPane.setLayout(new BorderLayout());
        this.scrollPane = new JScrollPane(this.canvasPanel);
        JSplitPane SP= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,this.scrollPane,new JTextArea());
        SP.setDividerLocation(600);
        SP.setResizeWeight(1);
        this.tools = new ToolsPanel();
        this.MasterPane.add(SP, BorderLayout.CENTER);
        this.settings= new SettingTools (SC.thicknessLine,SC.typeLine);
        initTools();
        settings.currFill.setBackground(SC.FillColor);
        settings.currLine.setBackground(SC.Background);
        settings.currLine.setProperties(SC.LineColor, SC.thicknessLine, SC.typeLine);
        this.archF.rec=b;
        this.archF.fontSize.setValue(SC.fontSize);
        this.add(this.MasterPane, BorderLayout.CENTER);
        
    }
    
    public void setVisibleCanvas(boolean flag)
    {
        this.myCanvas.setVisible(flag);    
    }
    
    
    private void initTools()
    {
        //контейнер инструментов
        
        this.scrollPaneTools = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.scrollPaneTools.setViewportView(this.tools);  
        this.MasterPane.add(this.scrollPaneTools, BorderLayout.WEST);
        
        this.tools.setPreferredSize(new Dimension(SizeSketch.TOOLPANEL_WIDTH,this.BoardDim.height));
        
        this.colorChooser= new JColorChooser(this.myCanvas.getForeground());
        
        //<editor-fold defaultstate="collapsed" desc=" Группа ">
       //заполняем список папок групп( заполнение в конструкторе)
       this.listGour = new Groups();
        // инструменты работы с группой
       this.tools.Group.setPanel(this.listGour.groupPanel,this.listGour.rowsClose  );
        //событие изменения выбранной группы
       this.listGour.setActionGroups(new ActionListener()
       {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                JComboBox Groups= (JComboBox)e.getSource();
                if(Groups.getSelectedIndex()<0)
                    return;
                MasterBoard.this.archF.setGroup(MasterBoard.this.listGour.getSelectedGroup() );
            }
        });
//</editor-fold>         
        
        //<editor-fold defaultstate="collapsed" desc=" Архив группы ">
       // создаем объект для раборы с архивом выбранной группы
        this.archF = new ArchiveFiles();
        
        //событие изменения текущей доски
        this.archF.EvChBoard.ChangeBoardAdd(IChbrd);
        //событие изменения выбранной даты
        this.archF.archiveGroup.addListSelectionListener
        (
                new ListSelectionListener() {

                    @Override
                    public void valueChanged(ListSelectionEvent e)
                    {
                        if(MasterBoard.this.archF.archiveGroup.getSelectedIndex()<0)
                        return;
                        String groupName=MasterBoard.this.listGour.getSelectedGroup();
                        String day=MasterBoard.this.archF.archiveGroup.getSelectedValue().toString();                        
                        String path = MasterBoard.this.listGour.getSelectedGroup()+"/" + MasterBoard.this.archF.archiveGroup.getSelectedValue().toString();                        
                        MasterBoard.this.archF.setDay(path);
                        
                    }
         });
        
        //инструменты работы с архивом ( добавить счетчик !!!)
       this.tools.Hystory.setPanel(this.archF.toolsArchive(),this.archF.rowsClose);
       
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
                            Color c=colorChooser.getSelectionModel().getSelectedColor();
                            myCanvas.setForeground(c);
                            myCanvas.setCaretColor(c);
                        }

                    },  null) .setVisible(true);    
                       
            }

        });
        //кнопка изменения фона
        this.archF.themesBoard.addActionListener(new ActionListener() {

            Color c;
            public void actionPerformed(ActionEvent e) 
            {
                JColorChooser.createDialog(MasterBoard.this.archF.themesBoard,
                    "Новый цвет фона", true, colorChooser,
                    new ActionListener() {                           
                    public void actionPerformed(ActionEvent e) 
                    {
                        c=colorChooser.getSelectionModel().getSelectedColor();
                         myCanvas.setBackground(c);
                    }

                    },  null) .setVisible(true); 
                if(c!=null)
                {

                    MasterBoard.this.canvasPanel.setBackground( MasterBoard.this.getPanelBackground(c));
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
                            myCanvas.LineColor=colorChooser.getSelectionModel().getSelectedColor();
                            settings.currLine.setProperty(myCanvas.LineColor);
                            
                        }

                    },  null) .setVisible(true);    
                       
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
                            myCanvas.FillColor=colorChooser.getSelectionModel().getSelectedColor();
                            settings.currFill.setBackground(myCanvas.FillColor);
                        }

                    },  null) .setVisible(true);    
               
            }

        });
        this.tools.Setting.setPanel(this.settings,this.settings.rowsClose);
        this.settings.thicnessLine.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                WidthLineComboBox wlcb=(WidthLineComboBox)ae.getSource();               
                myCanvas.stroke =(float)wlcb.getSelectedValue();
                settings.currLine.setProperty(myCanvas.stroke);
            }
        });
        this.settings.styleLine.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                DashLineComboBox dlcb=(DashLineComboBox)ae.getSource();
                myCanvas.typeLine=dlcb.getSelectedValue();
                settings.currLine.setProperty(myCanvas.typeLine);
            }
        }
);
        
        
//</editor-fold>  
        
        //<editor-fold defaultstate="collapsed" desc=" Типы графических элементов ">
        // патель изменения типа графическиго инструмента
        this.DT= new DrawTools();        
        this.tools.setDrawTools(this.DT);     
        //событие смены типа текущего графического элемента
        this.DT.EvShapeType.ShapeTypeChangedAdd(IShType);        
        //</editor-fold>  
        
        //<editor-fold defaultstate="collapsed" desc=" Редактирование графических элементов ">       
        //добавление на панель инструментов блока редактирования графики
        this.tools.setEditTools(this.myCanvas.editTools);
        //</editor-fold>    
        
    
    }
    
    /**
     * 
     * @param c
     * @return 
     */
    private Color getPanelBackground(Color c)
    {       
        int delta=30;
        int r=c.getRed()>150?c.getRed()-delta:c.getRed()+delta;
        int g=c.getGreen()>150?c.getGreen()-delta:c.getGreen()+delta;
        int b=c.getBlue()>150?c.getBlue()-delta:c.getBlue()+delta;
        Color p = new Color(r,g,b);                

        return p;
    }
    
    /**
     * Сохранение текущей доски,если назначен файл для сохранения
     */
    public void saveCurrentBoard()
    {
        if(this.fileCurrentBoard!=null)
        {
            this.myCanvas.save(fileCurrentBoard);
            this.styles.saveSettingsThemes(this.myCanvas.getForeground(),this.myCanvas.getBackground(),(int)this.archF.fontSize.getValue());
            this.styles.saveColorsDraw(this.myCanvas.LineColor, this.myCanvas.FillColor);
            this.styles.saveLineSetting(this.myCanvas.stroke, this.myCanvas.typeLine);
        }        
    }
    
     /**
     * Чтение контента из назначенного файла текущей доски
     */
    private void readCurrentBoard()
    {
        if(this.fileCurrentBoard!=null)
        {
            System.out.println(" file" + fileCurrentBoard.getName());
            
            this.myCanvas.readBoard(fileCurrentBoard);
            this.myCanvas.numberPage=this.archF.isTodayDir?
                    (this.archF.cbm.getIndexOf(this.archF.cbm.getSelectedItem())+1):
                    (this.archF.cbm.getIndexOf(this.archF.cbm.getSelectedItem())+1)*(-1);
            if(!this.myCanvas.isVisible())
            {
                this.myCanvas.setVisible(true);
                this.settings.setEnabledAll();
            }
            this.myCanvas.requestFocus();
        }
    }
    

}
