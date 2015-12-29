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
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import userControl.ImageIconURL;

/**
 *
 * @author Viky_Pa
 */
//создается при запуске приложения
//определяет перечень групп в папке приложения по ключу
//добавляет новую группу(если ее нет в списке)
//возвращет каталог выбранной группы
//
public class Groups {

    File rootAppFile;
    String groupKey = "group_";
    
    DefaultComboBoxModel cbm;    
    public JComboBox Groups;
    private JTextField tb= new JTextField();
    private JButton addGroup;
    public JPanel toolPanel;
    public Groups()
    {
        File F = new File("");
        rootAppFile = new File(F.getAbsolutePath());
        this.cbm = new DefaultComboBoxModel();        
        File[] listF = rootAppFile.listFiles();
        // заполнение списка групп
        if (listF.length != 0) {
            for (File f : listF) {
                if (f.getName().startsWith(groupKey))
                {
                    //выделяем имя файла без ключевого слова
                    this.cbm.addElement(f.getName().substring(groupKey.length()));
                }
            }

        }
        
        //выпадающий список групп
        this.Groups = new JComboBox(cbm);
        this.Groups.setSelectedIndex(-1);
        
        // текстовое поле добавления новой группы
        this.tb= new JTextField();
        tb.setEditable(true);
        this.tb.setPreferredSize( new Dimension(90,20));
        
        
        //кнопка добавления новой группы
        this.addGroup= new JButton(ImageIconURL.get("resources/New20.png"));        
        this.addGroup.setFocusPainted(false);
        this.addGroup.setBorderPainted(false);
        this.addGroup.setContentAreaFilled(false);
        this.addGroup.setPressedIcon(ImageIconURL.get("resources/New20_press.png"));
        this.addGroup.setPreferredSize(new Dimension(20,20));
        
        this.addGroup.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e)
            {  
                String nameNewGroup=Groups.this.tb.getText();
                String dirName = Groups.this.groupKey + nameNewGroup;
                File newGroup = new File(dirName);
               
              if(nameNewGroup.trim().length()!=0 && !newGroup.exists())  
              {
                    newGroup.mkdir();
                    Groups.this.cbm.addElement(newGroup.getName().substring(groupKey.length()));
              }              
              Groups.this.tb.setText("");
            }
        });
        
        toolPanel= new JPanel();
        this.toolPanel.add(tb);
        this.toolPanel.add(this.addGroup);
        
    }
    
    public String getSelectedGroup()
    { 
        return groupKey+cbm.getSelectedItem().toString();
    }
    
    
}
