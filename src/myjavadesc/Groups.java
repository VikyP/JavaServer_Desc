/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myjavadesc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import userControl.ImageIconURL;
import userControl.SizeSketch;

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
    private JComboBox Groups;
    private JTextField tb= new JTextField();
    private final JButton addGroup;
    
    // панель всегда доступна
    public JPanel groupPanel;
    //панель может быть скрыта
    public JPanel groupPanelHide;
    
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
        this.Groups.setPreferredSize(new Dimension(SizeSketch.COMBOBOX_WIDTH,SizeSketch.CONTROL_HEIGHT));
        this.Groups.setSelectedIndex(-1);
        
        // текстовое поле добавления новой группы
        this.tb= new JTextField();
        tb.setEditable(true);
        this.tb.setPreferredSize( new Dimension(SizeSketch.TEXTBOX_WIDTH,SizeSketch.CONTROL_HEIGHT));
        
        
        //кнопка добавления новой группы
        this.addGroup= new JButton(ImageIconURL.get("resources/New20.png"));        
        this.addGroup.setFocusPainted(false);
        this.addGroup.setBorderPainted(false);
        this.addGroup.setContentAreaFilled(false);
        this.addGroup.setPressedIcon(ImageIconURL.get("resources/New20_press.png"));
        this.addGroup.setPreferredSize(new Dimension(SizeSketch.BUTTON_WIDTH,SizeSketch.CONTROL_HEIGHT));
        
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
        this.addGroup.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
       
        groupPanel= new JPanel(new FlowLayout(FlowLayout.CENTER));           
        this.groupPanel.add(this.Groups);
        
        groupPanelHide= new JPanel();
        GroupLayout layout = new GroupLayout(groupPanelHide);
        groupPanelHide.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        
        JLabel label = new JLabel("Новая группа");
        label.setPreferredSize(new Dimension(SizeSketch.TEXTBOX_WIDTH, SizeSketch.CONTROL_HEIGHT));
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER) 
                .addComponent(label)
                .addGroup(layout.createSequentialGroup() 
                    .addComponent(this.tb)
                    .addComponent(this.addGroup))
                );
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(label)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE) 
                    .addComponent(this.tb)
                    .addComponent(this.addGroup))
        );
          
       
        
    }
    
    public String getSelectedGroup()
    { 
        return groupKey+cbm.getSelectedItem().toString();
    }
    
    public void setActionGroups(ActionListener AL)
    {
        this.Groups.addActionListener(AL);    
    }
    
    
}
