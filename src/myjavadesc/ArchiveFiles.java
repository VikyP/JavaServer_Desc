/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myjavadesc;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import masterPanel.ReportException;
import myjavadesc.events.EventChangeBoard;
import myjavadesc.events.IChBoard;
import userControl.ImageIconURL;
import userControl.SizeSketch;

/**
 *
 * @author Viky_Pa
 */
//класс для работы с арховом
//группа-> дата
//по указанной группе определяет список каталогов(дата занятия)
//при изменении переопределяет список каталогов
public class ArchiveFiles
{

    //текущая дата запуска приложения 
    //(используется для создания папки-Сегодня архива досок )
    private Calendar today;
    
    //папка-Сегодня
    private File todayDir;

    /**
     * флаг является ли текущая папка сегодняшней
     */
    public boolean isTodayDir = false;
    private String todayStr;
    /**
     * 
     */
    public String dayPath;

    // папка выбранной группы
    private File groupDir;
    private ArrayList<File> listBoard;

  
    /**
     * файл текущей доски(чтение-запись)
     */    
    public File currentBoard;

    // расширение файла доски   
    public static String ext = "jds";

    //перечень дат
    DefaultListModel lm;
    public JList archiveGroup;

    // перечень досок на выбранную дату
    DefaultComboBoxModel cbm;
    public JComboBox BoardsToday;

    // кнопки для работы с досками
    // новая
    public JButton newBoard;
    //удалить
    public JButton deleteBoard;
    //предыдущая
    public JButton previosBoard;
    //следущая
    public JButton nextBoard;
    

    //следущая
    public JButton themesBoard;
    public JButton colorText;
    public JSpinner fontSize;
    private final int MIN_Value=15;
    private final int MAX_Value=25;
    private final int STEP_Value=1;
    
    
    private JLabel fontSizeIcon;
    
    public JToggleButton rec;
    public int rowsClose=3;
    
    // панель всегда доступна
    public JPanel archPanel;
    //панель может быть скрыта
    public JPanel archPanelHide;
    
   

    
    // фильтр выбирает только файлы с заданным расширением
    private FileFilter FF = new FileFilter()
    {

        @Override
        public boolean accept(File pathname)
        {
            return getExtension(pathname).equals(ext);
        }

        private String getExtension(File pathname)
        {
            String filename = pathname.getPath();
            int i = filename.lastIndexOf('.');
            if (i > 0 && i < filename.length() - 1)
            {
                return filename.substring(i + 1).toLowerCase();
            }
            return "";
        }
    };
    public EventChangeBoard EvChBoard;

    public ArchiveFiles()
    {

        //дата запуска приложения
        this.today = Calendar.getInstance(); //LocalDateTime.now().toLocalDate();

        // cобытие смены текущей доски
        this.EvChBoard = new EventChangeBoard();

        //перечень занятий(дней-папок) выбранной группы
        this.lm = new DefaultListModel();
        this.archiveGroup = new JList(lm);

        // перечень досок на выбранный день
        this.cbm = new DefaultComboBoxModel();    // для  JComboBox   

        this.BoardsToday = new JComboBox(this.cbm); // JComboBox
        this.BoardsToday.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {

                int index = ArchiveFiles.this.BoardsToday.getSelectedIndex();
                if (index < 0)
                {
                    return;
                }
                ArchiveFiles.this.currentBoard = ArchiveFiles.this.listBoard.get(index);
                ArchiveFiles.this.changeBoard();

            }
        });
        this.listBoard = new ArrayList<File>();//контейнер для файлов

        //кнопка добавления новой доски
        this.newBoard = new JButton(ImageIconURL.get("resources/New20.png"));
        this.newBoard.setPressedIcon(ImageIconURL.get("resources/New20_press.png"));

        this.newBoard.setToolTipText("Добавление новой доски");

        this.newBoard.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                ArchiveFiles.this.addBoard();
                ArchiveFiles.this.changeBoard();
                
            }
        });

        this.setButtonPaintOff(this.newBoard);

        this.deleteBoard = new JButton(ImageIconURL.get("resources/delete20.png"));
        this.deleteBoard.setPressedIcon(ImageIconURL.get("resources/delete20_press.png"));
        this.deleteBoard.setToolTipText("Удаление текущей доски");

        this.deleteBoard.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ArchiveFiles.this.deleteBoard();
                ArchiveFiles.this.changeBoard();
            }
        });
        this.setButtonPaintOff(this.deleteBoard);

        this.previosBoard = new JButton(ImageIconURL.get("resources/back20.png"));
        this.previosBoard.setPressedIcon(ImageIconURL.get("resources/back20_press.png"));
        this.previosBoard.setToolTipText("Предыдущая доска");

        this.previosBoard.addActionListener(
                new ActionListener()
                {

                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        ArchiveFiles.this.previosBoard();
                        ArchiveFiles.this.changeBoard();
                    }
                });
        this.setButtonPaintOff(this.previosBoard);

        this.nextBoard = new JButton(ImageIconURL.get("resources/forward20.png"));
        this.nextBoard.setPressedIcon(ImageIconURL.get("resources/forward20_press.png"));
        this.nextBoard.setEnabled(false);
        this.nextBoard.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                ArchiveFiles.this.nextBoard();
                ArchiveFiles.this.changeBoard();
            }
        });
        this.nextBoard.setToolTipText("Следующая доска");
        this.setButtonPaintOff(this.nextBoard);

        this.themesBoard = new JButton(ImageIconURL.get("resources/Theme-icon25.png"));
        this.themesBoard.setPressedIcon(ImageIconURL.get("resources/Theme-icon25_press.png"));
        this.themesBoard.setToolTipText("Тема ");

        this.setButtonPaintOff(this.themesBoard);

        this.colorText = new JButton(ImageIconURL.get("resources/colorFont20.png"));
        this.colorText.setPressedIcon(ImageIconURL.get("resources/colorFont20_press.png"));
        this.colorText.setToolTipText("Тема ");

        this.setButtonPaintOff(this.colorText);
        this.todayStr = this.todayTostring();
        
        fontSizeIcon= new JLabel(ImageIconURL.get("resources/fontSize20.png"));
        fontSizeIcon.setPreferredSize(new Dimension(SizeSketch.BUTTON_WIDTH*2,SizeSketch.CONTROL_HEIGHT));
        SpinnerModel model =
        new SpinnerNumberModel(MIN_Value,MIN_Value,MAX_Value,STEP_Value);
        
        this.fontSize= new JSpinner();
        this.fontSize.setModel(model);
        this.fontSize.requestFocus(false);
        
        //запрет редактирования тексттового поля счетчика
        JTextField tf = ((JSpinner.DefaultEditor) this.fontSize.getEditor()).getTextField();
        tf.setFont(tf.getFont().deriveFont(20));
        tf.setEditable(false);
        setPaintOff(this.fontSize);
       toolsArchive();
       
    }
    
    public void addFontSizeChangeListener(ChangeListener listener)
    {
        this.fontSize.addChangeListener(listener);
    }

    /**
     * Изменение текущей доски
     */
    private void changeBoard()
    {
        IChBoard ICh = (IChBoard) this.EvChBoard.getListener();
        ICh.readBoard(this.currentBoard);
    }

    /**
     * Компановка инструмента на панели     
     * @return панель с инструментом
     */
    public void toolsArchive()
    {
      
        this.archPanel = new JPanel(); 
        GroupLayout layout = new GroupLayout(this.archPanel);
        this.archPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        this.BoardsToday.setPreferredSize(new Dimension(SizeSketch.COMBOBOX_WIDTH, SizeSketch.CONTROL_HEIGHT));
        
        layout.setHorizontalGroup(layout.createParallelGroup(LEADING) 
                .addGroup(layout.createSequentialGroup() 
                    .addComponent(this.newBoard)
                    .addComponent(this.deleteBoard)
                    .addComponent(this.previosBoard)
                    .addComponent(this.nextBoard)    
                )
                .addComponent(this.BoardsToday)
                );
        layout.setVerticalGroup(layout.createSequentialGroup()
                
                .addGroup(layout.createParallelGroup(BASELINE) 
                    .addComponent(this.newBoard)
                    .addComponent(this.deleteBoard)                   
                    .addComponent(this.previosBoard)
                    .addComponent(this.nextBoard)    
                )
                .addComponent(this.BoardsToday)
        );
        
       
        this.archPanelHide= new JPanel();
        layout = new GroupLayout(this.archPanelHide);
        this.archPanelHide.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        JScrollPane scroll_H = new JScrollPane(this.archiveGroup);
        scroll_H.setPreferredSize(new Dimension(SizeSketch.COMBOBOX_WIDTH, SizeSketch.LISTBOX_HEIGHT));
        
        layout.setHorizontalGroup(layout.createParallelGroup(LEADING) 
                .addComponent(scroll_H)
                .addGroup(layout.createSequentialGroup() 
                    .addComponent(this.themesBoard)
                    .addComponent(this.colorText)
                    .addComponent(this.fontSizeIcon)
                    .addComponent(this.fontSize)    
                )
                );
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(scroll_H)
                .addGroup(layout.createParallelGroup(BASELINE) 
                    .addComponent(this.themesBoard)
                    .addComponent(this.colorText)
                    .addComponent(this.fontSizeIcon)
                    .addComponent(this.fontSize) 
                )
        );
         
     
    }
    
    /**
     *
     * Заполнение списка заняний для выбранной группы
     *
     * @param groupName название группы(папки) из которой будет считан архив
     * занятий
     *
     */
    public void setGroup(String groupName)
    {
        //проверка названия группы
        if (groupName.trim().length() == 0)
        {
            return;
        }

        // при переходе на новую группу
        // активируем инструмент для работы с арховом    
        this.rec.setEnabled(true);
        this.newBoard.setEnabled(true);
        this.deleteBoard.setEnabled(true);
        this.previosBoard.setEnabled(true);
        this.nextBoard.setEnabled(true);
        this.themesBoard.setEnabled(true);
        this.colorText.setEnabled(true);
        //очищаем список
        this.lm.removeAllElements();
        this.cbm.removeAllElements();
        this.groupDir = new File(groupName);
        File[] days;
        //если папка группы существует
        if (this.groupDir.exists())
        {
            // заполняем список архива занятий

            days = this.groupDir.listFiles();
        } else
        {
            ReportException.write(" Группа " + this.groupDir.getName() + " не найдена");
            return;
        }
        //если архив пуст
        if (days.length == 0)
        {

            return;
        }
        // считаем, что папкиСегодня для группы нет
        this.todayDir = null;
        Arrays.sort(days);
        Collections.reverse(Arrays.asList(days));
        for (File f : days)
        {
            this.lm.addElement(f.getName());

           // System.out.println(i + "    " + f.getName());
          
            // проверка на наличие папки-Сегодня для выбранной группы
            if (f.getName().equals(this.todayTostring()))
            {
                this.todayDir = f;
            }

        }

        //перечне дат выбираем последнюю
        setDateLastSelected();

    }

    /**
     * заполнение архива досок для указанного дня
     *
     * @param day дата(имя папки) из которой будут считан архив досок и выбраны
     * файлы с расширенинем <code>ext</code>
     */
    public void setDay(String day)
    {
       // System.out.println(day);

        this.isTodayDir = false;
        //очищаем список
        this.listBoard.clear();
        this.cbm.removeAllElements();
        this.currentBoard = null;
        File F = new File(day);

        if (!F.exists())
        {
            return;
        }
        //Выставляем флаг для истории досок
        this.isTodayDir = this.todayStr.equals(day.substring(day.indexOf("/") + 1));

        //список файлов досок 
        File[] files = F.listFiles(this.FF);
        
        Arrays.sort(files);
       // Collections.reverse(Arrays.asList(files));

        //если папка выбранной даты пуста
        if (files.length == 0)
        {
            this.changeBoard();
            return;
        }

        for (File f : files)
        {
            this.listBoard.add(f);
            this.cbm.addElement(getNameBoard(f));

        }
        if (this.cbm.getSize() != 0)
        {
            this.BoardsToday.setSelectedIndex(this.cbm.getSize() - 1);
        } else
        {
            this.currentBoard = null;

        }

    }

    /**
     * отменяет прорисовку рамки, фона кнопки
     */
    private void setButtonPaintOff(JButton B)
    {
        B.setFocusPainted(false);
        B.setBorderPainted(false);
        B.setContentAreaFilled(false);
        B.setPreferredSize(new Dimension(SizeSketch.BUTTON_WIDTH, SizeSketch.CONTROL_HEIGHT));
        B.setEnabled(false);  
        B.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }
    
    /**
     * отменяет прорисовку рамки
     */
    private void setPaintOff(JSpinner B)
    {
        B.setBorder(null);
    }

    /**
     * дата запуска приложения для названия папки
     */
    private String todayTostring()
    {
        String day = this.today.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + this.today.get(Calendar.DAY_OF_MONTH) : "" + this.today.get(Calendar.DAY_OF_MONTH);
        String month = (this.today.get(Calendar.MONTH) + 1) < 10 ? "0" + (this.today.get(Calendar.MONTH) + 1) : "" + (this.today.get(Calendar.MONTH) + 1);
        return this.today.get(Calendar.YEAR) + "_" + month + "_" + day;
    }

    /**
     * путь к архиву по дате
     *
     * @return путь к папке архива
     */
    private String getPathArchive()
    {
        return (this.groupDir.getName() + "/" + this.todayDir.getName());
    }

    /**
     * Создание новой доски
     *
     * @param index номер доски
     * @return файл новой доски
     */
    private File createNewBoard(int index)
    {
        String path = getPathArchive();
        String tmp="";
       if(index<10)
        tmp= "/" + "Board_0" + index + "." + this.ext;
       else
        tmp= "/" + "Board_" + index + "." + this.ext;
       File f = new File(path +tmp);
       
        if (!f.exists())
        {
           
            try
            {
                f.createNewFile();
                return f;
            }
            catch (IOException ex)
            {
                ReportException.write(" Доска " + f.getName() + " cуществует ");
                Logger.getLogger(ArchiveFiles.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        } 
        else
        {
            return f;
        }

    }

    /**
     * Назначение последней даты выбранной
     */
    private void setDateLastSelected()
    {
        //если в перечне есть даты, выбираем последнюю(первую в списке)
        if (this.lm.getSize() != 0)
        {
            this.archiveGroup.setSelectedIndex(0);
        }
        //выставляем флаг для истории
        this.isTodayDir = this.todayStr.equals(this.archiveGroup.getSelectedValue().toString());
    }

    /**
     * Добавление новой доски
     */
    private void addBoard()
    {
        // индекс файла текущей доски
        int index = -1;
        String path=this.groupDir.getName() + "/" + this.todayTostring();
        File f = new File(path);
        //если папки-Сегодня нет        
        if (this.todayDir == null || !f.exists())
        {
           
            // создаем папку
            this.todayDir = new File(path);
            this.todayDir.mkdir();

            // создаем файл первой доски
            // очищаем списки досок
            this.listBoard.clear();
            this.cbm.removeAllElements();

            // назначаем файл текущей доски и добавляем его в списки
            this.currentBoard = this.createNewBoard(1);
            if (this.currentBoard == null)
            {
                ReportException.write(" Ошибка создания файла доски ");
                return;
            }
            this.listBoard.add(this.currentBoard);
            this.cbm.addElement(getNameBoard(this.currentBoard));
            this.lm.add(0, this.todayDir.getName());

        } 
      // папка-Сегодня уже есть
        else
        {
            index = this.listBoard.size() + 1;
           // System.out.println("    index " +index);
            this.currentBoard = this.createNewBoard(index);
            this.listBoard.add(this.currentBoard);
            this.cbm.addElement(getNameBoard(this.currentBoard));
            this.BoardsToday.setSelectedIndex(this.cbm.getSize() - 1);
            
        }
        //перечне дат выбираем последнюю
        setDateLastSelected();
    }

    /**
     * удаляет расширение файла
     *
     * @param f файл доски для добавления в список досок
     * @return название доски
     */
    private String getNameBoard(File f)
    {
        return f.getName().substring(0, f.getName().indexOf("."));
    }

    /**
     * удаление текущей доски
     */
    private void deleteBoard()
    {
        int index = this.BoardsToday.getSelectedIndex();
        File f = this.listBoard.get(index);
        if ( this.listBoard.size()==1)
        {            
           if( 
                JOptionPane.showConfirmDialog(  archiveGroup, "Удалить файл"+ f.getName()+"  и  папку \r\n \t\tДень:  "
                +this.archiveGroup.getSelectedValue().toString()+" ?","Удаление папки",
                JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION
              )
           {
              
               if(this.lm.size()!=0)
               {                  
                File dir=f.getParentFile();
                f.delete();
                dir.delete();                  
                setGroup(this.groupDir.getName());
               }
           }                    
            return;
        } 
        
                
        String boardName =this.BoardsToday.getSelectedItem().toString();
       
        if( JOptionPane.showConfirmDialog(  archiveGroup, "Удалить доску  "
            +boardName+" ?","Удаление доски",
            JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION)        
        {            
            return;
        }
        
        String path = this.dayPath;
        System.out.println("  index   " + index);
        
        this.listBoard.remove(index);
        this.BoardsToday.removeItemAt(index);
        f.delete();
        for (int i = index; i < this.listBoard.size(); i++)
        {
            String path_boadr="";
            if(i<9)
                path_boadr= "/" + "Board_0" + (i+1) + "." + this.ext;
                else
                path_boadr= "/" + "Board_" + (i+1) + "." + this.ext;
            File newNameFile = new File(path + path_boadr);
            this.listBoard.get(i).renameTo(newNameFile);
            this.cbm.removeElementAt(i);
            this.cbm.insertElementAt(getNameBoard(newNameFile), i);
        }
        this.setDay(this.dayPath);
        if (!this.listBoard.isEmpty())
        {          
            this.BoardsToday.setSelectedIndex(--index);
            System.out.println("  index   " + index);
            this.currentBoard = this.listBoard.get(index);
        }
      
    }

    /**
     * переход на следующую доску, если она есть если нет, на первую
     */
    private void nextBoard()
    {
        int index = -1;
        if (this.BoardsToday.getSelectedIndex() == this.cbm.getSize() - 1)
        {
            index = 0;
        } else
        {
            index = this.BoardsToday.getSelectedIndex() + 1;
        }
        this.BoardsToday.setSelectedIndex(index);
        this.currentBoard = this.listBoard.get(index);

    }

    /**
     * переход на предыдущую доску если сейчас первая, то на последнюю
     */
    private void previosBoard()
    {
        int index = -1;
        if (this.BoardsToday.getSelectedIndex() == 0)
        {
            index = this.cbm.getSize() - 1;
        } else
        {
            index = this.BoardsToday.getSelectedIndex() - 1;
        }
        this.BoardsToday.setSelectedIndex(index);
        this.currentBoard = this.listBoard.get(index);
    }

}
