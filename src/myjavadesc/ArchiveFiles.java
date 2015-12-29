/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myjavadesc;

import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import masterPanel.ReportException;
import myjavadesc.events.EventChangeBoard;
import myjavadesc.events.IChBoard;
import userControl.ImageIconURL;

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

        this.themesBoard = new JButton(ImageIconURL.get("resources/Theme-icon20.png"));
        this.themesBoard.setPressedIcon(ImageIconURL.get("resources/Theme-icon20_press.png"));
        this.themesBoard.setToolTipText("Тема ");

        this.setButtonPaintOff(this.themesBoard);

        this.colorText = new JButton(ImageIconURL.get("resources/colorFont20.png"));
        this.colorText.setPressedIcon(ImageIconURL.get("resources/colorFont20_press.png"));
        this.colorText.setToolTipText("Тема ");

        this.setButtonPaintOff(this.colorText);
        this.todayStr = this.todayTostring();
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
     * Компановка инструмента на панали
     *
     * @param w ширина панели, в которую вставляются инструменты
     * @return панель с инструментом
     */
    public JPanel toolsArchive(int w)
    {
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));
        p.setPreferredSize(new Dimension(w+10, 160));
        p.add(this.newBoard);
        p.add(this.deleteBoard);
        p.add(this.previosBoard);
        p.add(this.nextBoard);

        this.BoardsToday.setPreferredSize(new Dimension(w, 20));
        p.add(this.BoardsToday);

        JScrollPane scroll_H = new JScrollPane(this.archiveGroup);
        scroll_H.setPreferredSize(new Dimension(w, 80));
        p.add(scroll_H);
        p.add(this.themesBoard);
        p.add(this.colorText);
        return p;

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
        int i = 1;
        Arrays.sort(days);
        Collections.reverse(Arrays.asList(days));
        for (File f : days)
        {
            this.lm.addElement(f.getName());

            System.out.println(i + "    " + f.getName());
            i++;
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
        System.out.println(day);

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
      ////////////////////////////// System.out.println("  Dir day  set day "+this.isTodayDir);

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
        B.setPreferredSize(new Dimension(20, 20));
        B.setEnabled(false);
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
            return null;
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
        ///////////////////System.out.println("  Dir day  " + this.archiveGroup.getSelectedValue().toString());
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
        this.listBoard.remove(index);
        this.BoardsToday.removeItemAt(index);
        f.delete();

        String path = getPathArchive();

        for (int i = index; i < this.listBoard.size(); i++)
        {
            File ff = new File(path + "/" + "Board_" + (i + 1) + "." + this.ext);
            this.listBoard.get(i).renameTo(ff);
            this.cbm.removeElementAt(i);
            this.cbm.insertElementAt(getNameBoard(ff), i);
        }
        if (!this.listBoard.isEmpty())

        {
            this.BoardsToday.setSelectedIndex(index);
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
